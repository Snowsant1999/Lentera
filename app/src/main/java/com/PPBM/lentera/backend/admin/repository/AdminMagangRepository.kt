package com.PPBM.lentera.backend.admin.repository

import com.PPBM.lentera.backend.admin.model.DokumenTempatMagangItem
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeLamaranStatus
import com.PPBM.lentera.backend.core.normalizeLowonganStatus
import com.PPBM.lentera.backend.core.normalizeSistemKerja
import com.PPBM.lentera.backend.core.normalizeTempatStatus
import com.PPBM.lentera.backend.core.parseLocalDateFlexible
import com.PPBM.lentera.backend.mahasiswa.model.DokumenLamaranItem
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes

class AdminMagangRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

    companion object {
        private const val MAX_FILE_BYTES = 20L * 1024L * 1024L
        private const val MAX_LOGO_BYTES = 10L * 1024L * 1024L
    }

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
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat daftar tempat magang.")))
        }
    }

    /**
     * Mengunggah logo mitra ke bucket privat `logo-perusahaan`.
     */
    suspend fun uploadLogoPerusahaan(fileName: String, fileBytes: ByteArray): Result<String> {
        return try {
            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas logo kosong atau gagal dibaca."))
            }
            if (fileBytes.size.toLong() > MAX_LOGO_BYTES) {
                return Result.failure(Exception("Ukuran logo maksimal 10 MB."))
            }
            val cleanName = fileName.trim()
                .ifBlank { "logo.png" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            val storagePath = "mitra/${System.currentTimeMillis()}_$cleanName"
            storage.from("logo-perusahaan").upload(storagePath, fileBytes) {
                upsert = true
            }
            Result.success(storagePath)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah logo perusahaan.")))
        }
    }

    /**
     * Mengunggah dokumen MOU / legalitas mitra ke bucket `dokumen-mitra`
     * dan mencatatnya ke tabel `dokumen_tempat_magang`.
     */
    suspend fun uploadDokumenTempatMagang(
        tempatMagangId: String,
        fileName: String,
        fileBytes: ByteArray
    ): Result<DokumenTempatMagangItem> {
        return try {
            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas dokumen mitra kosong atau gagal dibaca."))
            }
            if (fileBytes.size.toLong() > MAX_FILE_BYTES) {
                return Result.failure(Exception("Ukuran dokumen mitra maksimal 20 MB."))
            }
            val cleanName = fileName.trim()
                .ifBlank { "dokumen_mitra.pdf" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            val storagePath = "$tempatMagangId/${System.currentTimeMillis()}_$cleanName"
            storage.from("dokumen-mitra").upload(storagePath, fileBytes) {
                upsert = true
            }

            val payload = DokumenTempatMagangItem(
                tempatMagangId = tempatMagangId,
                namaFile = cleanName,
                ukuranBytes = fileBytes.size.toLong(),
                filePath = storagePath
            )
            val inserted = client.from("dokumen_tempat_magang").insert(payload) {
                select()
            }.decodeSingle<DokumenTempatMagangItem>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah dokumen kemitraan.")))
        }
    }

    /**
     * Mengambil daftar dokumen MOU / legalitas tempat magang.
     */
    suspend fun getDokumenTempatMagang(tempatId: String): Result<List<DokumenTempatMagangItem>> {
        return try {
            val list = client.from("dokumen_tempat_magang").select {
                filter { eq("tempat_magang_id", tempatId) }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<DokumenTempatMagangItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat dokumen tempat magang.")))
        }
    }

    /**
     * Menambah tempat magang / mitra baru dengan normalisasi enum backend.
     */
    suspend fun createTempatMagang(entity: TempatMagangEntity): Result<TempatMagangEntity> {
        return try {
            val sanitized = entity.copy(
                namaPerusahaan = entity.namaPerusahaan.trim(),
                bidangUsaha = entity.bidangUsaha.trim(),
                namaCabang = entity.namaCabang.trim().ifBlank { "Pusat" },
                deskripsi = entity.deskripsi.trim(),
                alamatLengkap = entity.alamatLengkap.trim(),
                kota = entity.kota.trim(),
                provinsi = entity.provinsi.trim(),
                sistemKerja = normalizeSistemKerja(entity.sistemKerja, "Hybrid"),
                namaKontak = entity.namaKontak.trim(),
                emailKontak = entity.emailKontak.trim(),
                teleponKontak = entity.teleponKontak.trim(),
                situsWeb = entity.situsWeb?.trim()?.ifBlank { null },
                logoPath = entity.logoPath?.trim()?.ifBlank { null },
                status = normalizeTempatStatus(entity.status),
                catatanInternal = entity.catatanInternal?.trim()?.ifBlank { null }
            )
            val inserted = client.from("tempat_magang").insert(sanitized) {
                select()
            }.decodeSingle<TempatMagangEntity>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal menambah tempat magang.")))
        }
    }

    /**
     * Mengubah status operasional tempat magang (`draf`, `aktif`, `arsip`) dan catatan internal opsional.
     */
    suspend fun updateStatusTempatMagang(
        tempatId: String,
        status: String,
        catatanInternal: String? = null
    ): Result<Unit> {
        return try {
            val normalizedStatus = normalizeTempatStatus(status)
            val payload = buildJsonObject {
                put("status", normalizedStatus)
                if (catatanInternal != null) {
                    put("catatan_internal", catatanInternal.trim())
                }
            }
            client.from("tempat_magang").update(payload) {
                filter {
                    eq("id", tempatId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengubah status tempat magang.")))
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
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat daftar lowongan.")))
        }
    }

    /**
     * Menambah lowongan magang baru dengan validasi tanggal, kuota, dan enum.
     */
    suspend fun createLowongan(entity: LowonganEntity): Result<LowonganEntity> {
        return try {
            val sanitized = sanitizeLowonganEntity(entity).getOrThrow()
            val inserted = client.from("lowongan").insert(sanitized) {
                select()
            }.decodeSingle<LowonganEntity>()
            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal membuat lowongan magang.")))
        }
    }

    /**
     * Memperbarui data lowongan magang yang sudah ada.
     */
    suspend fun updateLowongan(lowonganId: String, entity: LowonganEntity): Result<LowonganEntity> {
        return try {
            val sanitized = sanitizeLowonganEntity(entity).getOrThrow()
            val payload = buildJsonObject {
                put("tempat_magang_id", sanitized.tempatMagangId)
                put("posisi", sanitized.posisi)
                sanitized.kategori?.let { put("kategori", it) }
                put("lokasi", sanitized.lokasi)
                put("sistem_kerja", sanitized.sistemKerja)
                put("deskripsi_pekerjaan", sanitized.deskripsiPekerjaan)
                put("persyaratan", sanitized.persyaratan)
                put("periode_magang", sanitized.periodeMagang)
                put("batas_pendaftaran", sanitized.batasPendaftaran)
                put("kuota", sanitized.kuota)
                put("status", sanitized.status)
                sanitized.catatanInternal?.let { put("catatan_internal", it) }
            }
            val updated = client.from("lowongan").update(payload) {
                filter { eq("id", lowonganId) }
                select()
            }.decodeSingle<LowonganEntity>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui lowongan magang.")))
        }
    }

    private fun sanitizeLowonganEntity(entity: LowonganEntity): Result<LowonganEntity> {
        if (entity.tempatMagangId.isBlank()) {
            return Result.failure(Exception("Tempat magang mitra wajib dipilih."))
        }
        if (entity.posisi.isBlank()) {
            return Result.failure(Exception("Posisi / judul lowongan wajib diisi."))
        }
        if (entity.kuota <= 0) {
            return Result.failure(Exception("Kuota magang harus lebih dari 0."))
        }
        val parsedDeadline = parseLocalDateFlexible(entity.batasPendaftaran)
            ?: return Result.failure(Exception("Format batas pendaftaran tidak valid (gunakan YYYY-MM-DD)."))
        val isoDate = parsedDeadline.format(DateTimeFormatter.ISO_LOCAL_DATE)

        return Result.success(
            entity.copy(
                tempatMagangId = entity.tempatMagangId.trim(),
                posisi = entity.posisi.trim(),
                kategori = entity.kategori?.trim()?.ifBlank { null },
                lokasi = entity.lokasi.trim(),
                sistemKerja = normalizeSistemKerja(entity.sistemKerja, "Hybrid"),
                deskripsiPekerjaan = entity.deskripsiPekerjaan.trim(),
                persyaratan = entity.persyaratan.trim(),
                periodeMagang = entity.periodeMagang.trim(),
                batasPendaftaran = isoDate,
                status = normalizeLowonganStatus(entity.status),
                catatanInternal = entity.catatanInternal?.trim()?.ifBlank { null }
            )
        )
    }

    /**
     * Mengubah status publikasi lowongan (`draf`, `terbit`, `ditutup`).
     */
    suspend fun updateStatusLowongan(lowonganId: String, status: String): Result<Unit> {
        return try {
            val normalizedStatus = normalizeLowonganStatus(status)
            val payload = buildJsonObject {
                put("status", normalizedStatus)
            }
            client.from("lowongan").update(payload) {
                filter {
                    eq("id", lowonganId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengubah status lowongan.")))
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
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat daftar lamaran masuk.")))
        }
    }

    /**
     * Mengambil daftar dokumen lampiran untuk sebuah lamaran mahasiswa.
     */
    suspend fun getDokumenLamaran(lamaranId: String): Result<List<DokumenLamaranItem>> {
        return try {
            val list = client.from("dokumen_lamaran").select {
                filter { eq("lamaran_id", lamaranId) }
                order("created_at", order = Order.ASCENDING)
            }.decodeList<DokumenLamaranItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat berkas lamaran.")))
        }
    }

    /**
     * Memvalidasi berkas lamaran mahasiswa (`terkirim`, `pemeriksaan`, `perlu_perbaikan`, `seleksi`, `diterima`, `ditolak`).
     * Riwayat pada `lamaran_riwayat` dicatat otomatis oleh trigger PostgreSQL `trg_lamaran_riwayat`.
     */
    suspend fun validasiLamaran(
        lamaranId: String,
        status: String,
        catatanAdmin: String? = null
    ): Result<Unit> {
        return try {
            val normalizedStatus = normalizeLamaranStatus(status)
            if (normalizedStatus == "perlu_perbaikan" && catatanAdmin.isNullOrBlank()) {
                return Result.failure(Exception("Catatan perbaikan wajib diisi saat meminta revisi berkas."))
            }

            val payload = buildJsonObject {
                put("status", normalizedStatus)
                if (!catatanAdmin.isNullOrBlank()) {
                    put("catatan_admin", catatanAdmin.trim())
                }
            }

            client.from("lamaran").update(payload) {
                filter {
                    eq("id", lamaranId)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memvalidasi lamaran.")))
        }
    }

    /**
     * Membuat signed URL sementara untuk melihat berkas di bucket privat.
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


