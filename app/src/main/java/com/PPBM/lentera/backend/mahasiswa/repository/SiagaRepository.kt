package com.PPBM.lentera.backend.mahasiswa.repository

import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.mahasiswa.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SiagaRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
        }
    }

    /**
     * Membuat laporan pengaduan baru.
     */
    suspend fun createLaporan(
        kategoriId: Long,
        judul: String,
        kronologi: String,
        lokasi: String,
        tanggalKejadian: String,
        isAnonim: Boolean
    ): Result<LaporanSaya> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login tidak valid. Silakan login kembali."))

            val payload = CreateLaporanPayload(
                pelaporId = currentUserId,
                kategoriId = kategoriId,
                judul = judul.trim(),
                kronologi = kronologi.trim(),
                lokasiKejadian = lokasi.trim(),
                tanggalKejadian = tanggalKejadian.trim(),
                isAnonim = isAnonim,
                status = "baru"
            )

            // Insert ke tabel laporan
            val inserted = client.from("laporan").insert(payload) {
                select()
            }.decodeSingle<LaporanSaya>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengunggah berkas bukti laporan ke Supabase Storage (bucket `laporan-bukti`)
     * dan mencatatnya ke tabel `bukti_laporan`.
     */
    suspend fun uploadBukti(
        laporanId: String,
        fileName: String,
        fileBytes: ByteArray,
        tipeFile: String
    ): Result<BuktiLaporanItem> {
        return try {
            val cleanFileName = fileName.replace(" ", "_")
            val storagePath = "$laporanId/${System.currentTimeMillis()}_$cleanFileName"

            // Upload ke Supabase Storage
            val bucket = storage.from("laporan-bukti")
            bucket.upload(storagePath, fileBytes) {
                upsert = true
            }

            // Catat ke database tabel bukti_laporan
            val buktiRecord = BuktiLaporanItem(
                laporanId = laporanId,
                namaFile = cleanFileName,
                tipeFile = tipeFile.lowercase(),
                ukuranBytes = fileBytes.size.toLong(),
                filePath = storagePath
            )

            val inserted = client.from("bukti_laporan").insert(buktiRecord) {
                select()
            }.decodeSingle<BuktiLaporanItem>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.failure(e)
        }
    }

    /**
     * Mengirim balasan klarifikasi/informasi tambahan dari pelapor.
     */
    suspend fun balasPermintaanInformasi(
        permintaanId: String,
        balasan: String,
        lampiranPath: String? = null
    ): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("balasan_pelapor", balasan.trim())
                if (lampiranPath != null) put("lampiran_balasan", lampiranPath)
            }
            client.from("permintaan_informasi").update(payload) {
                filter {
                    eq("id", permintaanId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.failure(e)
        }
    }
}

