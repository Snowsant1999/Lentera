package com.PPBM.lentera.backend.mahasiswa.repository

import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.mahasiswa.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage

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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
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
            Result.failure(e)
        }
    }

    /**
     * Mengajukan lamaran magang baru.
     */
    suspend fun submitLamaran(payload: SubmitLamaranPayload): Result<LamaranSaya> {
        return try {
            val inserted = client.from("lamaran").insert(payload) {
                select()
            }.decodeSingle<LamaranSaya>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengunggah berkas dokumen lamaran (CV, Portofolio, Transkrip, dll) ke Supabase Storage
     * bucket `dokumen-magang` dan mencatatnya ke tabel `dokumen_lamaran`.
     */
    suspend fun uploadDokumenLamaran(
        lamaranId: String,
        jenis: String,
        fileName: String,
        fileBytes: ByteArray
    ): Result<DokumenLamaranItem> {
        return try {
            val cleanFileName = fileName.replace(" ", "_")
            val storagePath = "$lamaranId/${jenis}_${System.currentTimeMillis()}_$cleanFileName"

            val bucket = storage.from("dokumen-magang")
            bucket.upload(storagePath, fileBytes) {
                upsert = true
            }

            val docRecord = DokumenLamaranItem(
                lamaranId = lamaranId,
                jenis = jenis,
                namaFile = cleanFileName,
                filePath = storagePath,
                ukuranBytes = fileBytes.size.toLong()
            )

            val inserted = client.from("dokumen_lamaran").insert(docRecord) {
                select()
            }.decodeSingle<DokumenLamaranItem>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(e)
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
                Result.success(false) // Unbookmarked
            } else {
                client.from("bookmark_lowongan").insert(
                    BookmarkLowongan(mahasiswaId = currentUserId, lowonganId = lowonganId)
                )
                Result.success(true) // Bookmarked
            }
        } catch (e: Exception) {
            Result.failure(e)
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
            Result.failure(e)
        }
    }
}

