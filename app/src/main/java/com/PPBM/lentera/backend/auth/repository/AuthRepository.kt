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

class SessionConflictException(message: String) : Exception(message)

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
                    return Result.failure(
                        SessionConflictException(
                            if (pesan.isNotBlank()) pesan else "Akun sedang aktif digunakan di perangkat lain."
                        )
                    )
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

    private fun isPermanentAuthRevocation(error: Throwable): Boolean {
        val msg = generateSequence(error) { it.cause }
            .mapNotNull { it.message }
            .joinToString(" | ")
        return msg.contains("invalid_grant", ignoreCase = true) ||
            msg.contains("refresh_token_not_found", ignoreCase = true) ||
            msg.contains("invalid refresh token", ignoreCase = true) ||
            msg.contains("session_not_found", ignoreCase = true)
    }

    private suspend fun clearInvalidAuthSession() {
        try {
            client.auth.clearSession()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {}
        sessionManager.clearSession()
    }

    /**
     * Memastikan Supabase Auth sudah terinisialisasi dan JWT masih berlaku (refresh bila <= 60 detik atau dipaksa).
     * Mengembalikan false jika tidak ada sesi atau refresh token dicabut permanen;
     * tetap mengembalikan true saat error jaringan sementara agar pengguna offline tidak langsung dikeluarkan.
     */
    suspend fun ensureValidAuthSession(forceRefresh: Boolean = false): Boolean {
        runCatching { client.auth.awaitInitialization() }
            .onFailure { if (it is CancellationException) throw it }

        val session = client.auth.currentSessionOrNull() ?: return false
        val isExpiringSoon = session.expiresAt.toEpochMilliseconds() <= System.currentTimeMillis() + 60_000L

        if (forceRefresh || isExpiringSoon) {
            try {
                client.auth.refreshCurrentSession()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return !isPermanentAuthRevocation(e)
            }
        }

        return client.auth.currentSessionOrNull() != null
    }

    /**
     * Mengirim detak sesi (heartbeat) aktif ke server setiap 60 detik (C1).
     */
    suspend fun sendHeartbeat(): Boolean = sendHeartbeatWithDetail().first

    suspend fun sendHeartbeatWithDetail(): Pair<Boolean, String?> {
        if (!isLoggedIn()) return true to null
        if (!ensureValidAuthSession()) {
            clearInvalidAuthSession()
            return false to "Sesi login Anda telah berakhir. Silakan masuk kembali."
        }

        val deviceId = sessionManager.getDeviceId()
        return try {
            suspend fun callDetakSesi(): JsonObject {
                val rpcParams = buildJsonObject {
                    put("p_device_id", deviceId)
                }
                return client.postgrest.rpc("detak_sesi", rpcParams).decodeAs<JsonObject>()
            }

            var res = callDetakSesi()
            var isSuccess = res["success"]?.jsonPrimitive?.booleanOrNull ?: true
            var message = res["message"]?.jsonPrimitive?.contentOrNull

            if (!isSuccess && message?.contains("Sesi autentikasi tidak valid", ignoreCase = true) == true) {
                val refreshed = ensureValidAuthSession(forceRefresh = true)
                if (!refreshed) {
                    clearInvalidAuthSession()
                    return false to "Sesi login Anda telah berakhir. Silakan masuk kembali."
                }
                res = callDetakSesi()
                isSuccess = res["success"]?.jsonPrimitive?.booleanOrNull ?: true
                message = res["message"]?.jsonPrimitive?.contentOrNull
            }

            isSuccess to message
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            true to null // Jangan putus sesi hanya karena fluktuasi jaringan sementara
        }
    }

    /**
     * Memvalidasi atau mengklaim ulang sesi pada cold start atau resume aplikasi (C1).
     */
    suspend fun validateOrClaimSessionOnStartup(): Pair<Boolean, String?> {
        if (!isLoggedIn()) return false to null
        if (!ensureValidAuthSession()) {
            clearInvalidAuthSession()
            return false to null
        }

        val deviceId = sessionManager.getDeviceId()
        val deviceInfo = sessionManager.getDeviceInfo()
        return try {
            suspend fun callKlaimSesi(): JsonObject {
                val rpcParams = buildJsonObject {
                    put("p_device_id", deviceId)
                    put("p_device_info", deviceInfo)
                }
                return client.postgrest.rpc("klaim_sesi", rpcParams).decodeAs<JsonObject>()
            }

            var res = callKlaimSesi()
            var isSuccess = res["success"]?.jsonPrimitive?.booleanOrNull ?: true
            var message = res["message"]?.jsonPrimitive?.contentOrNull

            if (!isSuccess && message?.contains("Sesi autentikasi tidak valid", ignoreCase = true) == true) {
                val refreshed = ensureValidAuthSession(forceRefresh = true)
                if (!refreshed) {
                    clearInvalidAuthSession()
                    return false to null
                }
                res = callKlaimSesi()
                isSuccess = res["success"]?.jsonPrimitive?.booleanOrNull ?: true
                message = res["message"]?.jsonPrimitive?.contentOrNull
            }

            if (!isSuccess) {
                try {
                    client.auth.signOut()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {}
                sessionManager.clearSession()
                false to (message?.takeIf { it.isNotBlank() } ?: "Akun sedang aktif digunakan di perangkat lain.")
            } else {
                true to null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            true to null // Jangan memblokir akses jika offline sementara
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

