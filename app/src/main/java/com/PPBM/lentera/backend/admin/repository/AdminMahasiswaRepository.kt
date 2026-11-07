package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.AdminDashboardStats
import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.core.SupabaseConfig
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
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

            val newUserId = client.postgrest.rpc("admin_create_mahasiswa", rpcParams).decodeAs<String>()
            Result.success(newUserId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengaktifkan atau menonaktifkan akun mahasiswa (status_akun: 'aktif' / 'nonaktif').
     */
    suspend fun toggleStatusAkun(userId: String, newStatus: String): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("status_akun", newStatus)
            }
            client.from("profiles").update(payload) {
                filter {
                    eq("id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menghitung statistik ringkasan dashboard admin.
     */
    suspend fun getDashboardStats(): Result<AdminDashboardStats> {
        return try {
            val totalLaporan = client.from("laporan").select().decodeList<kotlinx.serialization.json.JsonObject>().size
            val totalTempat = client.from("tempat_magang").select().decodeList<kotlinx.serialization.json.JsonObject>().size
            val totalLowongan = client.from("lowongan").select().decodeList<kotlinx.serialization.json.JsonObject>().size
            val totalLamaran = client.from("lamaran").select().decodeList<kotlinx.serialization.json.JsonObject>().size
            val totalMhs = client.from("profiles").select {
                filter { eq("role", "mahasiswa") }
            }.decodeList<kotlinx.serialization.json.JsonObject>().size

            Result.success(
                AdminDashboardStats(
                    totalLaporan = totalLaporan,
                    laporanPerluTindakan = totalLaporan,
                    totalTempatMagang = totalTempat,
                    totalLowonganAktif = totalLowongan,
                    totalLamaranMasuk = totalLamaran,
                    totalMahasiswaAktif = totalMhs
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

