package com.PPBM.lentera.backend.mahasiswa.repository

import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeDokumenLamaranJenis
import com.PPBM.lentera.backend.mahasiswa.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Duration.Companion.minutes

private const val MAX_LAMARAN_DOC_BYTES = 20L * 1024L * 1024L

class MagangRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

    /**
     * Mengambil daftar lowongan magang yang sedang aktif/terbit via view `v_lowongan_terbit`.
     */
    suspend fun getLowonganTerbit(): Result<List<LowonganTerbit>> {
        return try {
            val list = client.from("v_lowongan_terbit")
                .select {
                    order("batas_pendaftaran", order = Order.ASCENDING)
                }
                .decodeList<LowonganTerbit>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat daftar lowongan.")))
        }
    }

    /**
     * Mengambil detail satu lowongan magang berdasarkan ID.
     */
    suspend fun getDetailLowongan(lowonganId: String): Result<LowonganTerbit> {
        return try {
            val item = client.from("v_lowongan_terbit")
                .select {
                    filter {
                        eq("id", lowonganId)
                    }
                }
                .decodeSingle<LowonganTerbit>()
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat detail lowongan.")))
        }
    }

    /**
     * Mengambil daftar lamaran yang telah diajukan oleh mahasiswa.
     */
    suspend fun getLamaranSaya(): Result<List<LamaranSaya>> {
        return try {
            val list = client.from("lamaran")
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<LamaranSaya>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat riwayat lamaran.")))
        }
    }

    /**
     * Mengambil detail lamaran magang tertentu.
     */
    suspend fun getDetailLamaran(lamaranId: String): Result<LamaranSaya> {
        return try {
            val item = client.from("lamaran")
                .select {
                    filter {
                        eq("id", lamaranId)
                    }
                }
                .decodeSingle<LamaranSaya>()
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat detail lamaran.")))
        }
    }

    /**
     * Mengambil riwayat status lamaran untuk timeline status.
     */
    suspend fun getRiwayatLamaran(lamaranId: String): Result<List<LamaranRiwayatItem>> {
        return try {
            val list = client.from("lamaran_riwayat")
                .select {
                    filter {
                        eq("lamaran_id", lamaranId)
                    }
                    order("created_at", order = Order.ASCENDING)
                }
                .decodeList<LamaranRiwayatItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat riwayat status lamaran.")))
        }
    }

    /**
     * Mengambil dokumen berkas yang diunggah untuk lamaran tertentu.
     */
    suspend fun getDokumenLamaran(lamaranId: String): Result<List<DokumenLamaranItem>> {
        return try {
            val list = client.from("dokumen_lamaran")
                .select {
                    filter {
                        eq("lamaran_id", lamaranId)
                    }
                }
                .decodeList<DokumenLamaranItem>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat dokumen lamaran.")))
        }
    }

    /**
     * Mengajukan lamaran magang baru.
     */
    suspend fun submitLamaran(payload: SubmitLamaranPayload): Result<LamaranSaya> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val sanitized = payload.copy(
                mahasiswaId = currentUserId,
                namaLengkap = payload.namaLengkap.trim(),
                nim = payload.nim.trim(),
                tempatTanggalLahir = payload.tempatTanggalLahir.trim(),
                alamatKtp = payload.alamatKtp.trim(),
                alamatDomisili = payload.alamatDomisili.trim(),
                agama = payload.agama.trim(),
                noTelepon = payload.noTelepon.trim(),
                email = payload.email.trim(),
                golonganDarah = payload.golonganDarah.trim(),
                status = "terkirim"
            )

            val inserted = client.from("lamaran").insert(sanitized) {
                select()
            }.decodeSingle<LamaranSaya>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengajukan lamaran magang.")))
        }
    }

    /**
     * Mengunggah berkas dokumen lamaran (CV, Surat Pengantar, Transkrip, KTM, Portofolio, Sertifikat)
     * ke Supabase Storage bucket `dokumen-lamaran` dan mencatatnya ke tabel `dokumen_lamaran`.
     */
    suspend fun uploadDokumenLamaran(
        lamaranId: String,
        jenis: String,
        fileName: String,
        fileBytes: ByteArray
    ): Result<DokumenLamaranItem> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas kosong atau tidak dapat dibaca."))
            }
            if (fileBytes.size > MAX_LAMARAN_DOC_BYTES) {
                return Result.failure(Exception("Ukuran berkas melebihi batas 20 MB."))
            }

            val normalizedJenis = normalizeDokumenLamaranJenis(jenis)
            val jenisSlug = normalizedJenis.lowercase().replace(" ", "_")
            val cleanFileName = fileName.trim()
                .ifBlank { "dokumen.pdf" }
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
            val storagePath = "$currentUserId/$lamaranId/${jenisSlug}_${System.currentTimeMillis()}_$cleanFileName"

            val bucket = storage.from("dokumen-lamaran")
            bucket.upload(storagePath, fileBytes) {
                upsert = true
            }

            val docRecord = DokumenLamaranItem(
                lamaranId = lamaranId,
                jenis = normalizedJenis,
                namaFile = cleanFileName,
                filePath = storagePath,
                ukuranBytes = fileBytes.size.toLong()
            )

            val inserted = client.from("dokumen_lamaran").insert(docRecord) {
                select()
            }.decodeSingle<DokumenLamaranItem>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah berkas lamaran.")))
        }
    }

    /**
     * Mengajukan kembali berkas perbaikan lamaran yang diminta admin.
     * Mengubah status lamaran dari 'perlu_perbaikan' kembali ke 'pemeriksaan'.
     */
    suspend fun ajukanPerbaikanLamaran(lamaranId: String): Result<Unit> {
        return try {
            val rpcParams = kotlinx.serialization.json.buildJsonObject {
                put("p_lamaran_id", lamaranId)
            }
            client.postgrest.rpc("mahasiswa_ajukan_perbaikan_lamaran", rpcParams)
            Result.success(Unit)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui status perbaikan lamaran.")))
        }
    }

    /**
     * Membuat signed URL sementara (10 menit) untuk membuka berkas dari private storage bucket.
     */
    suspend fun createSignedUrl(bucketName: String, filePath: String): Result<String> {
        return try {
            val signedUrl = storage.from(bucketName).createSignedUrl(
                path = filePath,
                expiresIn = 10.minutes
            )
            Result.success(signedUrl)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal membuat tautan berkas.")))
        }
    }

    /**
     * Toggle bookmark/simpan lowongan oleh mahasiswa.
     */
    suspend fun toggleBookmark(lowonganId: String): Result<Boolean> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val existing = client.from("bookmark_lowongan").select {
                filter {
                    eq("mahasiswa_id", currentUserId)
                    eq("lowongan_id", lowonganId)
                }
            }.decodeList<BookmarkLowongan>()

            if (existing.isNotEmpty()) {
                client.from("bookmark_lowongan").delete {
                    filter {
                        eq("mahasiswa_id", currentUserId)
                        eq("lowongan_id", lowonganId)
                    }
                }
                Result.success(false)
            } else {
                client.from("bookmark_lowongan").insert(
                    BookmarkLowongan(mahasiswaId = currentUserId, lowonganId = lowonganId)
                )
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memperbarui bookmark.")))
        }
    }

    /**
     * Mengambil daftar ID lowongan yang di-bookmark oleh mahasiswa.
     */
    suspend fun getBookmarkedLowonganIds(): Result<Set<String>> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val list = client.from("bookmark_lowongan").select {
                filter {
                    eq("mahasiswa_id", currentUserId)
                }
            }.decodeList<BookmarkLowongan>()

            Result.success(list.map { it.lowonganId }.toSet())
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal memuat bookmark lowongan.")))
        }
    }
}

