package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.model.LogTindakanItem
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeLaporanStatus
import com.PPBM.lentera.backend.mahasiswa.model.BuktiLaporanItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Duration.Companion.minutes

class AdminSiagaRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

    companion object {
        private val UUID_REGEX = Regex(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
    }

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
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat daftar laporan.")))
        }
    }

    /**
     * Mengambil detail laporan admin berdasarkan UUID atau nomor laporan.
     */
    suspend fun getDetailLaporan(idOrNomor: String): Result<AdminLaporan> {
        return try {
            val trimmed = idOrNomor.trim()
            val col = if (UUID_REGEX.matches(trimmed)) "id" else "nomor_laporan"
            val item = client.from("v_laporan_admin").select {
                filter {
                    eq(col, trimmed)
                }
            }.decodeSingle<AdminLaporan>()
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat detail laporan.")))
        }
    }

    /**
     * Memperbarui status laporan dan penugasan unit melalui RPC `admin_update_laporan`.
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
                ?: return Result.failure(Exception("Sesi admin tidak valid."))
            val normalizedStatus = normalizeLaporanStatus(status)
            val params = buildJsonObject {
                put("p_id", laporanId)
                put("p_status", normalizedStatus)
                if (!jalurTindakLanjut.isNullOrBlank()) put("p_jalur", jalurTindakLanjut.trim())
                if (!unitPenanganan.isNullOrBlank()) put("p_unit", unitPenanganan.trim())
                if (!catatanPenugasan.isNullOrBlank()) put("p_catatan", catatanPenugasan.trim())
                put("p_ditangani", currentAdminId)
            }

            client.postgrest.rpc("admin_update_laporan", params)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui status laporan.")))
        }
    }

    /**
     * Mengirim permintaan informasi tambahan ke pelapor dan mengubah status menjadi `diperiksa`.
     */
    suspend fun mintaInformasi(laporanId: String, pesanKePelapor: String): Result<Unit> {
        return try {
            val currentAdminId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi admin tidak valid."))
            if (pesanKePelapor.trim().isBlank()) {
                return Result.failure(Exception("Pesan permintaan informasi wajib diisi."))
            }

            val payload = buildJsonObject {
                put("laporan_id", laporanId)
                put("admin_id", currentAdminId)
                put("pesan_ke_pelapor", pesanKePelapor.trim())
            }
            client.from("permintaan_informasi").insert(payload)

            updateStatusLaporan(laporanId = laporanId, status = "diperiksa").getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengirim permintaan informasi.")))
        }
    }

    /**
     * Mengambil daftar permintaan informasi tambahan pada laporan tertentu.
     */
    suspend fun getPermintaanInformasi(laporanId: String): Result<List<PermintaanInformasiItem>> {
        return try {
            val list = client.from("permintaan_informasi").select {
                filter { eq("laporan_id", laporanId) }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<PermintaanInformasiItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat riwayat permintaan informasi.")))
        }
    }

    /**
     * Mengambil daftar bukti lampiran laporan untuk admin.
     */
    suspend fun getBuktiLaporan(laporanId: String): Result<List<BuktiLaporanItem>> {
        return try {
            val list = client.from("bukti_laporan").select {
                filter {
                    eq("laporan_id", laporanId)
                    eq("is_excluded", false)
                }
                order("created_at", order = Order.ASCENDING)
            }.decodeList<BuktiLaporanItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat bukti laporan.")))
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
            if (deskripsi.trim().isBlank()) {
                return Result.failure(Exception("Deskripsi catatan tindakan wajib diisi."))
            }

            val log = LogTindakanItem(
                laporanId = laporanId,
                adminId = currentAdminId,
                judulTindakan = judul.trim().ifBlank { "Catatan Tindakan Penanganan" },
                deskripsi = deskripsi.trim()
            )

            client.from("log_tindakan").insert(log)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal menyimpan log tindakan.")))
        }
    }

    /**
     * Mengambil daftar log riwayat tindakan penanganan kasus.
     */
    suspend fun getLogTindakan(laporanId: String): Result<List<LogTindakanItem>> {
        return try {
            val list = client.from("log_tindakan").select {
                filter { eq("laporan_id", laporanId) }
                order("created_at", order = Order.ASCENDING)
            }.decodeList<LogTindakanItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat log tindakan.")))
        }
    }

    /**
     * Mengambil daftar dokumen hasil penanganan laporan.
     */
    suspend fun getDokumenHasil(laporanId: String): Result<List<DokumenHasilItem>> {
        return try {
            val list = client.from("dokumen_hasil").select {
                filter { eq("laporan_id", laporanId) }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<DokumenHasilItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat dokumen hasil.")))
        }
    }

    /**
     * Mengunggah dokumen hasil penyelesaian ke bucket `dokumen-hasil`.
     */
    suspend fun uploadDokumenHasil(
        laporanId: String,
        fileName: String,
        fileBytes: ByteArray,
        tipeFile: String,
        bolehDibagikan: Boolean
    ): Result<DokumenHasilItem> {
        return try {
            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas dokumen hasil kosong atau gagal dibaca."))
            }
            val cleanName = fileName.trim()
                .ifBlank { "dokumen_hasil.pdf" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            val storagePath = "$laporanId/hasil_${System.currentTimeMillis()}_$cleanName"

            storage.from("dokumen-hasil").upload(storagePath, fileBytes) {
                upsert = true
            }

            val payload = buildJsonObject {
                put("laporan_id", laporanId)
                put("nama_file", cleanName)
                put("tipe_file", extractShortFileType(cleanName, tipeFile).lowercase())
                put("ukuran_bytes", fileBytes.size.toLong())
                put("file_path", storagePath)
                put("boleh_dibagikan", bolehDibagikan)
            }
            val inserted = client.from("dokumen_hasil").insert(payload) {
                select()
            }.decodeSingle<DokumenHasilItem>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah dokumen hasil penanganan.")))
        }
    }

    /**
     * Menyelesaikan laporan dan mencatat hasil akhir penanganan melalui RPC `admin_update_laporan`.
     */
    suspend fun selesaikanLaporan(
        laporanId: String,
        hasilPenanganan: String,
        hasilDipublikasi: Boolean
    ): Result<Unit> {
        return try {
            val currentAdminId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi admin tidak valid."))
            if (hasilPenanganan.trim().isBlank()) {
                return Result.failure(Exception("Ringkasan hasil penanganan wajib diisi."))
            }

            val params = buildJsonObject {
                put("p_id", laporanId)
                put("p_status", "selesai")
                put("p_hasil", hasilPenanganan.trim())
                put("p_dipublikasi", hasilDipublikasi)
                put("p_ditangani", currentAdminId)
            }

            client.postgrest.rpc("admin_update_laporan", params)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal menyelesaikan laporan.")))
        }
    }

    /**
     * Membuat signed URL sementara untuk melihat lampiran bukti atau dokumen hasil.
     */
    suspend fun createSignedUrl(bucketName: String, filePath: String): Result<String> {
        return try {
            val url = storage.from(bucketName).createSignedUrl(filePath, expiresIn = 10.minutes)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal membuka berkas.")))
        }
    }
}

