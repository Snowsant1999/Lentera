package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.AdminDashboardStats
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeStatusAkun
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminMahasiswaRepository {
    private val client = SupabaseConfig.client

    /**
     * Mengambil seluruh daftar mahasiswa terdaftar.
     */
    suspend fun getAllMahasiswa(): Result<List<UserProfile>> {
        return try {
            val list = client.from("profiles").select {
                filter {
                    eq("role", "mahasiswa")
                }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<UserProfile>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mendaftarkan akun mahasiswa baru oleh Admin melalui RPC `admin_create_mahasiswa`.
     */
    suspend fun createMahasiswa(
        email: String,
        password: String,
        namaLengkap: String,
        nim: String,
        noTelepon: String?,
        perguruanTinggi: String?,
        jurusan: String?,
        programStudi: String?,
        angkatan: String?
    ): Result<String> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_email", email.trim().lowercase())
                put("p_password", password)
                put("p_nama_lengkap", namaLengkap.trim())
                put("p_nim", nim.trim())
                if (noTelepon != null) put("p_no_telepon", noTelepon.trim())
                if (perguruanTinggi != null) put("p_perguruan_tinggi", perguruanTinggi.trim())
                if (jurusan != null) put("p_jurusan", jurusan.trim())
                if (programStudi != null) put("p_program_studi", programStudi.trim())
                if (angkatan != null) put("p_angkatan", angkatan.trim())
            }

            val newUserId = client.postgrest.rpc("admin_create_mahasiswa_v2", rpcParams).decodeAs<String>()
            Result.success(newUserId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengubah status akun mahasiswa (status_akun: 'aktif', 'belum_aktivasi', 'lulus', 'nonaktif').
     */
    suspend fun toggleStatusAkun(userId: String, newStatus: String): Result<Unit> {
        return try {
            val normalized = normalizeStatusAkun(newStatus)
            val payload = buildJsonObject {
                put("status_akun", normalized)
            }
            client.from("profiles").update(payload) {
                filter {
                    eq("id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengubah status akun mahasiswa.")))
        }
    }

    /**
     * Mengirim ulang tautan pemulihan / aktivasi password ke email mahasiswa.
     */
    suspend fun resendActivationLink(email: String): Result<Unit> {
        return try {
            val cleanEmail = email.trim().lowercase()
            if (cleanEmail.isBlank()) {
                return Result.failure(Exception("Email mahasiswa belum terdaftar."))
            }
            client.auth.resetPasswordForEmail(cleanEmail)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengirim ulang tautan aktivasi.")))
        }
    }

    /**
     * Mengambil statistik ringkasan dashboard admin langsung melalui RPC `get_admin_dashboard_stats`.
     */
    suspend fun getDashboardStats(): Result<AdminDashboardStats> {
        return try {
            val stats = client.postgrest.rpc("get_admin_dashboard_stats").decodeAs<AdminDashboardStats>()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat statistik dashboard admin.")))
        }
    }

    /**
     * Memperbarui data identitas & akademik mahasiswa oleh Admin melalui RPC `admin_update_mahasiswa`.
     * Jika migrasi RPC belum terpasang dan email tidak berubah, fallback ke update langsung tabel `profiles`.
     */
    suspend fun updateMahasiswaProfile(
        userId: String,
        namaLengkap: String,
        nim: String,
        email: String,
        perguruanTinggi: String?,
        programStudi: String?,
        angkatan: String?,
        emailChanged: Boolean = true
    ): Result<Unit> {
        val cleanNama = namaLengkap.trim()
        val cleanNim = nim.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPt = perguruanTinggi?.trim()?.takeIf { it.isNotBlank() }
        val cleanProdi = programStudi?.trim()?.takeIf { it.isNotBlank() }
        val cleanAngkatan = angkatan?.trim()?.takeIf { it.isNotBlank() }

        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", userId)
                put("p_nama_lengkap", cleanNama)
                put("p_nim", cleanNim)
                put("p_email", cleanEmail)
                if (cleanPt != null) put("p_perguruan_tinggi", cleanPt) else put("p_perguruan_tinggi", JsonNull)
                if (cleanProdi != null) put("p_program_studi", cleanProdi) else put("p_program_studi", JsonNull)
                if (cleanAngkatan != null) put("p_angkatan", cleanAngkatan) else put("p_angkatan", JsonNull)
            }
            client.postgrest.rpc("admin_update_mahasiswa", rpcParams)
            Result.success(Unit)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            val missingRpc = msg.contains("PGRST202", ignoreCase = true) ||
                (msg.contains("Could not find the function", ignoreCase = true) &&
                    msg.contains("admin_update_mahasiswa", ignoreCase = true))
            if (missingRpc) {
                if (emailChanged) {
                    return Result.failure(
                        Exception("Fungsi server admin_update_mahasiswa belum dijalankan di Supabase sehingga perubahan email login belum dapat disimpan.")
                    )
                }
                return try {
                    val payload = buildJsonObject {
                        put("nama_lengkap", cleanNama)
                        put("nim", cleanNim)
                        if (cleanPt != null) put("perguruan_tinggi", cleanPt) else put("perguruan_tinggi", JsonNull)
                        if (cleanProdi != null) put("program_studi", cleanProdi) else put("program_studi", JsonNull)
                        if (cleanAngkatan != null) put("angkatan", cleanAngkatan) else put("angkatan", JsonNull)
                    }
                    client.from("profiles").update(payload) {
                        filter {
                            eq("id", userId)
                        }
                    }
                    Result.success(Unit)
                } catch (fallbackErr: kotlinx.coroutines.CancellationException) {
                    throw fallbackErr
                } catch (fallbackErr: Exception) {
                    Result.failure(Exception(mapSupabaseError(fallbackErr, "Gagal memperbarui profil mahasiswa.")))
                }
            }
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui profil mahasiswa.")))
        }
    }

    /**
     * Menghapus akun mahasiswa melalui RPC `admin_delete_mahasiswa`.
     */
    suspend fun deleteMahasiswa(userId: String): Result<Unit> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", userId)
            }
            client.postgrest.rpc("admin_delete_mahasiswa", rpcParams)
            Result.success(Unit)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            val missingRpc = msg.contains("PGRST202", ignoreCase = true) ||
                (msg.contains("Could not find the function", ignoreCase = true) &&
                    msg.contains("admin_delete_mahasiswa", ignoreCase = true))
            if (missingRpc) {
                return Result.failure(Exception("Fungsi server admin_delete_mahasiswa belum dijalankan di Supabase."))
            }
            Result.failure(Exception(mapSupabaseError(e, "Gagal menghapus akun mahasiswa.")))
        }
    }

    /**
     * Memperbarui profil akun admin (nama lengkap, email, no telepon).
     */
    suspend fun updateAdminProfile(
        userId: String,
        namaLengkap: String,
        email: String,
        noTelepon: String
    ): Result<UserProfile> {
        return try {
            val cleanEmail = email.trim().lowercase()
            val currentAuthEmail = client.auth.currentUserOrNull()?.email?.trim()?.lowercase()
            if (cleanEmail.isNotBlank() && cleanEmail != currentAuthEmail) {
                client.auth.updateUser {
                    this.email = cleanEmail
                }
            }

            client.from("profiles").update(
                buildJsonObject {
                    put("nama_lengkap", namaLengkap.trim())
                    if (cleanEmail.isNotBlank()) put("email", cleanEmail)
                    put("no_telepon", noTelepon.trim())
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }

            val updatedProfile = client.from("profiles").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<UserProfile>()

            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui profil admin.")))
        }
    }
}

