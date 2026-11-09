package com.PPBM.lentera.backend.auth.repository

import android.content.Context
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.mapSupabaseError
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromFragment
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(context: Context) {
    private val client = SupabaseConfig.client
    private val sessionManager = SessionManager.getInstance(context)

    /**
     * Login menggunakan NIM atau Email Kampus + Password.
     * Jika pengguna memasukkan NIM, sistem mencari email terkait via fungsi RPC `get_email_by_nim`.
     */
    suspend fun login(identifier: String, password: String): Result<UserProfile> {
        return try {
            val cleanIdentifier = identifier.trim()
            val targetEmail: String = when {
                cleanIdentifier.contains("@") -> cleanIdentifier.lowercase()
                else -> {
                    // Resolusi NIM -> Email via RPC
                    val rpcParams = buildJsonObject {
                        put("p_nim", cleanIdentifier)
                    }
                    val resolved: String? = try {
                        client.postgrest.rpc("get_email_by_nim", rpcParams).decodeAs<String>()
                    } catch (e: Exception) {
                        null
                    }
                    if (resolved.isNullOrBlank()) {
                        return Result.failure(Exception("NIM atau akun '$cleanIdentifier' tidak terdaftar dalam sistem."))
                    }
                    resolved.lowercase()
                }
            }

            // Autentikasi dengan Supabase Auth
            client.auth.signInWith(Email) {
                this.email = targetEmail
                this.password = password
            }

            // Ambil profile pengguna
            val userId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Gagal mengidentifikasi akun pengguna."))

            val profile = client.from("profiles").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<UserProfile>()

            if (profile.statusAkun.equals("nonaktif", ignoreCase = true)) {
                try {
                    client.auth.signOut()
                } catch (_: Exception) {}
                sessionManager.clearSession()
                return Result.failure(Exception("Akun dinonaktifkan oleh admin kampus."))
            }

            // Simpan sesi lokal
            sessionManager.saveSession(profile)
            Result.success(profile)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Invalid login credentials", ignoreCase = true) == true ->
                    "NIM/Email atau password yang kamu masukkan salah."
                e.message?.contains("tidak terdaftar", ignoreCase = true) == true ->
                    e.message ?: "Akun tidak terdaftar."
                else -> mapSupabaseError(e, "Terjadi kesalahan saat login.")
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Mengambil sesi login saat ini (jika ada).
     */
    fun getCurrentProfile(): UserProfile? {
        return sessionManager.getProfile()
    }

    /**
     * Cek apakah pengguna sudah login.
     */
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * Logout dan bersihkan sesi lokal.
     */
    suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (_: Exception) {}
        sessionManager.clearSession()
    }

    /**
     * Meminta pengiriman kode OTP pemulihan 6 digit ke email mahasiswa.
     */
    suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val cleanEmail = email.trim().lowercase()
            client.auth.signInWith(OTP) {
                this.email = cleanEmail
                this.createUser = false
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengirim kode OTP pemulihan.")))
        }
    }

    /**
     * Memverifikasi kode OTP pemulihan 6 digit.
     */
    suspend fun verifyRecoveryOtp(email: String, token: String): Result<Unit> {
        return try {
            sessionManager.clearSession()
            client.auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email.trim().lowercase(),
                token = token.trim()
            )
            val userId = client.auth.currentUserOrNull()?.id
            if (userId != null) {
                val profile = runCatching {
                    client.from("profiles").select {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeSingle<UserProfile>()
                }.getOrNull()
                if (profile != null && profile.statusAkun.equals("nonaktif", ignoreCase = true)) {
                    try {
                        client.auth.signOut()
                    } catch (_: Exception) {}
                    sessionManager.clearSession()
                    return Result.failure(Exception("Akun dinonaktifkan oleh admin kampus."))
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Kode verifikasi salah atau kedaluwarsa.")))
        }
    }

    /**
     * Mengimpor sesi pemulihan dari fragment deep link Supabase ke Auth client.
     */
    suspend fun importRecoverySessionFromFragment(fragment: String): Result<String> {
        return try {
            runCatching { client.auth.awaitInitialization() }
            val parsedSession = client.auth.parseSessionFromFragment(fragment)
            val user = client.auth.retrieveUser(parsedSession.accessToken)
            sessionManager.clearSession()
            client.auth.importSession(
                session = parsedSession.copy(user = user),
                autoRefresh = true,
                source = SessionSource.External
            )
            val profile = runCatching {
                client.from("profiles").select {
                    filter {
                        eq("id", user.id)
                    }
                }.decodeSingle<UserProfile>()
            }.getOrNull()
            if (profile != null && profile.statusAkun.equals("nonaktif", ignoreCase = true)) {
                try {
                    client.auth.signOut()
                } catch (_: Exception) {}
                sessionManager.clearSession()
                return Result.failure(Exception("Akun dinonaktifkan oleh admin kampus."))
            }
            Result.success(user.email ?: profile?.email ?: "")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val raw = e.message.orEmpty()
            val message = if (
                raw.contains("JWT", ignoreCase = true) ||
                raw.contains("expired", ignoreCase = true) ||
                raw.contains("invalid", ignoreCase = true)
            ) {
                "Tautan aktivasi atau reset password tidak valid atau sudah kedaluwarsa. Minta admin mengirim ulang tautan baru."
            } else {
                mapSupabaseError(e, "Gagal memverifikasi tautan pemulihan.")
            }
            Result.failure(Exception(message))
        }
    }

    /**
     * Memperbarui password setelah terverifikasi sekaligus mengaktifkan akun via RPC `aktivasi_akun_saya`.
     */
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            client.auth.updateUser {
                this.password = newPassword
            }
            val userId = client.auth.currentUserOrNull()?.id
            if (userId != null) {
                client.postgrest.rpc("aktivasi_akun_saya")
                val refreshedProfile = try {
                    client.from("profiles").select {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeSingle<UserProfile>()
                } catch (_: Exception) {
                    sessionManager.getProfile()?.copy(statusAkun = "aktif")
                }
                if (refreshedProfile != null) {
                    sessionManager.saveSession(refreshedProfile)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui password.")))
        }
    }
}

