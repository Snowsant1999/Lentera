package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.mahasiswa.model.LamaranRiwayatItem
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminMagangRepository {
    private val client = SupabaseConfig.client

    /**
     * Mengambil daftar tempat magang / mitra.
     */
    suspend fun getTempatMagangList(): Result<List<TempatMagangEntity>> {
        return try {
            val list = client.from("tempat_magang").select {
                order("created_at", order = Order.DESCENDING)
            }.decodeList<TempatMagangEntity>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menambah tempat magang / mitra baru.
     */
    suspend fun createTempatMagang(entity: TempatMagangEntity): Result<TempatMagangEntity> {
        return try {
            val inserted = client.from("tempat_magang").insert(entity) {
                select()
            }.decodeSingle<TempatMagangEntity>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengubah status operasional tempat magang (draf, aktif, nonaktif).
     */
    suspend fun updateStatusTempatMagang(tempatId: String, status: String): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("status", status)
            }
            client.from("tempat_magang").update(payload) {
                filter {
                    eq("id", tempatId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengambil seluruh lowongan magang untuk dikelola admin.
     */
    suspend fun getLowonganList(): Result<List<LowonganEntity>> {
        return try {
            val list = client.from("lowongan").select {
                order("created_at", order = Order.DESCENDING)
            }.decodeList<LowonganEntity>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menambah lowongan magang baru.
     */
    suspend fun createLowongan(entity: LowonganEntity): Result<LowonganEntity> {
        return try {
            val inserted = client.from("lowongan").insert(entity) {
                select()
            }.decodeSingle<LowonganEntity>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengubah status publikasi lowongan (draf, terbit, ditutup, arsip).
     */
    suspend fun updateStatusLowongan(lowonganId: String, status: String): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("status", status)
            }
            client.from("lowongan").update(payload) {
                filter {
                    eq("id", lowonganId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengambil daftar semua berkas lamaran mahasiswa yang masuk.
     */
    suspend fun getAllLamaranMasuk(): Result<List<LamaranSaya>> {
        return try {
            val list = client.from("lamaran").select {
                order("created_at", order = Order.DESCENDING)
            }.decodeList<LamaranSaya>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Memvalidasi berkas lamaran mahasiswa (verifikasi_berkas, wawancara, diterima, ditolak, perlu_perbaikan)
     * dan mencatatnya ke riwayat lamaran.
     */
    suspend fun validasiLamaran(
        lamaranId: String,
        status: String,
        catatanAdmin: String? = null
    ): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("status", status)
                if (catatanAdmin != null) put("catatan_admin", catatanAdmin.trim())
            }

            client.from("lamaran").update(payload) {
                filter {
                    eq("id", lamaranId)
                }
            }

            // Catat ke riwayat
            val riwayat = LamaranRiwayatItem(
                id = "",
                lamaranId = lamaranId,
                status = status,
                catatan = catatanAdmin,
                createdAt = ""
            )
            val riwayatPayload = buildJsonObject {
                put("lamaran_id", lamaranId)
                put("status", status)
                if (catatanAdmin != null) put("catatan", catatanAdmin)
            }
            client.from("lamaran_riwayat").insert(riwayatPayload)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

