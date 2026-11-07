package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.model.LogTindakanItem
import com.PPBM.lentera.backend.core.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminSiagaRepository {
    private val client = SupabaseConfig.client

    /**
     * Mengambil semua laporan dari view `v_laporan_admin`.
     */
    suspend fun getAllLaporan(): Result<List<AdminLaporan>> {
        return try {
            val list = client.from("v_laporan_admin").select {
                order("created_at", order = Order.DESCENDING)
            }.decodeList<AdminLaporan>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengambil detail laporan admin berdasarkan ID.
     */
    suspend fun getDetailLaporan(laporanId: String): Result<AdminLaporan> {
        return try {
            val item = client.from("v_laporan_admin").select {
                filter {
                    eq("id", laporanId)
                }
            }.decodeSingle<AdminLaporan>()
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Memperbarui status laporan dan penugasan unit.
     */
    suspend fun updateStatusLaporan(
        laporanId: String,
        status: String,
        unitPenanganan: String? = null,
        jalurTindakLanjut: String? = null,
        catatanPenugasan: String? = null
    ): Result<Unit> {
        return try {
            val currentAdminId = client.auth.currentUserOrNull()?.id
            val payload = buildJsonObject {
                put("status", status)
                if (unitPenanganan != null) put("unit_penanganan", unitPenanganan)
                if (jalurTindakLanjut != null) put("jalur_tindak_lanjut", jalurTindakLanjut)
                if (catatanPenugasan != null) put("catatan_penugasan", catatanPenugasan)
                if (currentAdminId != null) put("ditangani_oleh", currentAdminId)
            }

            client.from("laporan").update(payload) {
                filter {
                    eq("id", laporanId)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menambahkan entri log riwayat tindakan penanganan kasus.
     */
    suspend fun addLogTindakan(
        laporanId: String,
        judul: String,
        deskripsi: String
    ): Result<Unit> {
        return try {
            val currentAdminId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi admin tidak valid."))

            val log = LogTindakanItem(
                laporanId = laporanId,
                adminId = currentAdminId,
                judulTindakan = judul.trim(),
                deskripsi = deskripsi.trim()
            )

            client.from("log_tindakan").insert(log)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menyelesaikan laporan dan mencatat hasil akhir penanganan.
     */
    suspend fun selesaikanLaporan(
        laporanId: String,
        hasilPenanganan: String,
        hasilDipublikasi: Boolean
    ): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("status", "selesai")
                put("hasil_penanganan", hasilPenanganan.trim())
                put("hasil_dipublikasi", hasilDipublikasi)
            }

            client.from("laporan").update(payload) {
                filter {
                    eq("id", laporanId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

