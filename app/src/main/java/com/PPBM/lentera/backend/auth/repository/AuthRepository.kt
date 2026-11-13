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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull

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

            // Validasi dan klaim satu sesi aktif per akun (C1)
            val deviceId = sessionManager.getDeviceId()
            val deviceInfo = sessionManager.getDeviceInfo()
            val klaimResult = runCatching {
                val rpcParams = buildJsonObject {
                    put("p_device_id", deviceId)
                    put("p_device_info", deviceInfo)
                }
                client.postgrest.rpc("klaim_sesi", rpcParams).decodeAs<JsonObject>()
            }.getOrNull()

            if (klaimResult != null) {
                val isSuccess = klaimResult["success"]?.jsonPrimitive?.booleanOrNull ?: true
                val pesan = klaimResult["message"]?.jsonPrimitive?.contentOrNull.orEmpty()
                if (!isSuccess) {
                    try {
                        client.auth.signOut()
                    } catch (_: Exception) {}
                    sessionManager.clearSession()
                    return Result.failure(Exception(if (pesan.isNotBlank()) pesan else "Akun sedang aktif digunakan di perangkat lain."))
                }
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
        val deviceId = sessionManager.getDeviceId()
        try {
            val rpcParams = buildJsonObject {
                put("p_device_id", deviceId)
            }
            client.postgrest.rpc("lepas_sesi", rpcParams)
        } catch (_: Exception) {}
        try {
            client.auth.signOut()
        } catch (_: Exception) {}
        sessionManager.clearSession()
    }

    /**
     * Mengirim detak sesi (heartbeat) aktif ke server setiap 60 detik (C1).
     */
    suspend fun sendHeartbeat(): Boolean {
        if (!isLoggedIn()) return false
        val deviceId = sessionManager.getDeviceId()
        return try {
            val rpcParams = buildJsonObject {
                put("p_device_id", deviceId)
            }
            val res = client.postgrest.rpc("detak_sesi", rpcParams).decodeAs<JsonObject>()
            res["success"]?.jsonPrimitive?.booleanOrNull ?: true
        } catch (_: Exception) {
            true // Jangan putus sesi hanya karena fluktuasi jaringan sementara
        }
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
            val cleanEmail = email.trim().lowercase()
            val cleanToken = token.trim()
            try {
                client.auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = cleanEmail,
                    token = cleanToken
                )
            } catch (_: Exception) {
                client.auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = cleanEmail,
                    token = cleanToken
                )
            }
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

