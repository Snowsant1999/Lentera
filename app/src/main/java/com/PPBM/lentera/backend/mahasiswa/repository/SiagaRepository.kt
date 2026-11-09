package com.PPBM.lentera.backend.mahasiswa.repository

import com.PPBM.lentera.backend.core.BuatLaporanRpcResult
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.parseWitaIsoTimestamp
import com.PPBM.lentera.backend.mahasiswa.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Duration.Companion.minutes

class SiagaRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

    companion object {
        private const val MAX_BUKTI_BYTES = 20L * 1024L * 1024L
    }

    /**
     * Mengambil daftar master kategori pengaduan (Perundungan, Pelecehan, dll).
     */
    suspend fun getKategoriList(): Result<List<KategoriLaporan>> {
        return try {
            val list = client.from("kategori_laporan")
                .select()
                .decodeList<KategoriLaporan>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat kategori laporan.")))
        }
    }

    /**
     * Mengambil riwayat laporan milik mahasiswa yang sedang login via view `v_laporan_saya`.
     */
    suspend fun getLaporanSaya(): Result<List<LaporanSaya>> {
        return try {
            val list = client.from("v_laporan_saya")
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<LaporanSaya>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat riwayat laporan.")))
        }
    }

    /**
     * Mengambil detail satu laporan mahasiswa berdasarkan ID.
     */
    suspend fun getDetailLaporan(laporanId: String): Result<LaporanSaya> {
        return try {
            val item = client.from("v_laporan_saya")
                .select {
                    filter {
                        eq("id", laporanId)
                    }
                }
                .decodeSingle<LaporanSaya>()
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat detail laporan.")))
        }
    }

    /**
     * Mengambil riwayat linimasa/tindakan progres penanganan laporan via view `v_timeline_laporan_saya`.
     */
    suspend fun getTimelineLaporan(laporanId: String): Result<List<TimelineLaporanSaya>> {
        return try {
            val list = client.from("v_timeline_laporan_saya")
                .select {
                    filter {
                        eq("laporan_id", laporanId)
                    }
                    order("created_at", order = Order.ASCENDING)
                }
                .decodeList<TimelineLaporanSaya>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat linimasa laporan.")))
        }
    }

    /**
     * Mengambil daftar berkas bukti terkait suatu laporan.
     */
    suspend fun getBuktiLaporan(laporanId: String): Result<List<BuktiLaporanItem>> {
        return try {
            val list = client.from("bukti_laporan")
                .select {
                    filter {
                        eq("laporan_id", laporanId)
                        eq("is_excluded", false)
                    }
                }
                .decodeList<BuktiLaporanItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat lampiran bukti.")))
        }
    }

    /**
     * Membuat laporan pengaduan baru melalui RPC `buat_laporan`.
     */
    suspend fun createLaporan(
        kategoriId: String,
        judul: String,
        kronologi: String,
        lokasi: String,
        tanggalKejadian: String,
        isAnonim: Boolean,
        kategoriNama: String = "Pengaduan"
    ): Result<LaporanSaya> {
        return try {
            client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login tidak valid. Silakan login kembali."))

            if (kategoriId.isBlank()) {
                return Result.failure(Exception("Kategori laporan belum dipilih."))
            }

            val isoTanggal = parseWitaIsoTimestamp(tanggalKejadian)
            val rpcParams = buildJsonObject {
                put("p_kategori_id", kategoriId.trim())
                put("p_judul", judul.trim())
                put("p_kronologi", kronologi.trim())
                put("p_lokasi", lokasi.trim())
                put("p_tanggal", isoTanggal)
                put("p_anonim", isAnonim)
            }

            val createdRef = client.postgrest.rpc("buat_laporan", rpcParams)
                .decodeList<BuatLaporanRpcResult>()
                .firstOrNull()
                ?: return Result.failure(Exception("Server tidak mengembalikan nomor referensi laporan."))

            val detail = getDetailLaporan(createdRef.id).getOrElse {
                LaporanSaya(
                    id = createdRef.id,
                    nomorLaporan = createdRef.nomorLaporan,
                    kategori = kategoriNama,
                    judul = judul.trim(),
                    kronologi = kronologi.trim(),
                    lokasiKejadian = lokasi.trim(),
                    tanggalKejadian = isoTanggal,
                    isAnonim = isAnonim,
                    status = "baru",
                    createdAt = isoTanggal,
                    updatedAt = isoTanggal
                )
            }

            Result.success(detail)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengirim laporan.")))
        }
    }

    /**
     * Mengunggah berkas bukti laporan ke Supabase Storage (bucket `bukti-laporan`)
     * dan mencatatnya ke tabel `bukti_laporan`.
     */
    suspend fun uploadBukti(
        laporanId: String,
        fileName: String,
        fileBytes: ByteArray,
        tipeFile: String
    ): Result<BuktiLaporanItem> {
        return try {
            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas bukti '$fileName' kosong atau gagal dibaca."))
            }
            if (fileBytes.size.toLong() > MAX_BUKTI_BYTES) {
                return Result.failure(Exception("Ukuran berkas '$fileName' melebihi batas maksimal 20 MB."))
            }

            val cleanFileName = fileName.trim()
                .ifBlank { "bukti_laporan" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            val storagePath = "$laporanId/${System.currentTimeMillis()}_$cleanFileName"

            val bucket = storage.from("bukti-laporan")
            bucket.upload(storagePath, fileBytes) {
                upsert = true
            }

            val shortType = extractShortFileType(cleanFileName, tipeFile).lowercase()
            val buktiRecord = BuktiLaporanItem(
                laporanId = laporanId,
                namaFile = cleanFileName,
                tipeFile = shortType,
                ukuranBytes = fileBytes.size.toLong(),
                filePath = storagePath
            )

            val inserted = client.from("bukti_laporan").insert(buktiRecord) {
                select()
            }.decodeSingle<BuktiLaporanItem>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah bukti '$fileName'.")))
        }
    }

    /**
     * Mengambil permintaan informasi tambahan dari admin untuk laporan tertentu.
     */
    suspend fun getPermintaanInformasi(laporanId: String): Result<List<PermintaanInformasiItem>> {
        return try {
            val list = client.from("permintaan_informasi")
                .select {
                    filter {
                        eq("laporan_id", laporanId)
                    }
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<PermintaanInformasiItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat permintaan informasi.")))
        }
    }

    /**
     * Mengirim balasan klarifikasi/informasi tambahan dari pelapor beserta lampiran opsional.
     */
    suspend fun balasPermintaanInformasi(
        permintaanId: String,
        balasan: String,
        lampiranPath: String? = null,
        laporanId: String? = null,
        attachmentFileName: String? = null,
        attachmentBytes: ByteArray? = null
    ): Result<Unit> {
        return try {
            if (permintaanId.isBlank()) {
                return Result.failure(Exception("Tidak ada permintaan informasi aktif untuk dibalas."))
            }
            if (balasan.trim().isBlank()) {
                return Result.failure(Exception("Isi informasi tambahan wajib diisi."))
            }

            var finalLampiranPath = lampiranPath
            if (attachmentBytes != null) {
                if (attachmentBytes.isEmpty()) {
                    return Result.failure(Exception("Berkas lampiran kosong atau gagal dibaca."))
                }
                if (attachmentBytes.size.toLong() > MAX_BUKTI_BYTES) {
                    return Result.failure(Exception("Ukuran lampiran melebihi batas maksimal 20 MB."))
                }
                val targetLaporanId = laporanId?.takeIf { it.isNotBlank() }
                    ?: return Result.failure(Exception("ID laporan tidak valid untuk unggah lampiran."))
                val cleanName = (attachmentFileName ?: "lampiran").trim()
                    .ifBlank { "lampiran" }
                    .replace(Regex("[^A-Za-z0-9._-]"), "_")
                val storagePath = "$targetLaporanId/balasan_${System.currentTimeMillis()}_$cleanName"
                storage.from("bukti-laporan").upload(storagePath, attachmentBytes) {
                    upsert = true
                }
                finalLampiranPath = storagePath
            }

            val payload = buildJsonObject {
                put("balasan_pelapor", balasan.trim())
                if (!finalLampiranPath.isNullOrBlank()) put("lampiran_balasan", finalLampiranPath)
            }
            client.from("permintaan_informasi").update(payload) {
                filter {
                    eq("id", permintaanId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengirim balasan informasi.")))
        }
    }

    /**
     * Mengambil dokumen berita acara / hasil penyelesaian kasus yang boleh dibagikan.
     */
    suspend fun getDokumenHasil(laporanId: String): Result<List<DokumenHasilItem>> {
        return try {
            val list = client.from("dokumen_hasil")
                .select {
                    filter {
                        eq("laporan_id", laporanId)
                        eq("boleh_dibagikan", true)
                    }
                }
                .decodeList<DokumenHasilItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat dokumen hasil penanganan.")))
        }
    }

    /**
     * Membuat signed URL sementara untuk melihat/mengunduh berkas dari bucket privat.
     */
    suspend fun createSignedUrl(bucketName: String, filePath: String): Result<String> {
        return try {
            val url = storage.from(bucketName).createSignedUrl(filePath, expiresIn = 10.minutes)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal membuka tautan berkas.")))
        }
    }
}

