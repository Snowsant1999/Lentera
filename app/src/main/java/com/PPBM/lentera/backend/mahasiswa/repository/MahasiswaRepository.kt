package com.PPBM.lentera.backend.mahasiswa.repository

import com.PPBM.lentera.backend.auth.model.UserProfile
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.core.mapSupabaseError
import com.PPBM.lentera.backend.core.normalizeDokumenMahasiswaJenis
import com.PPBM.lentera.backend.mahasiswa.model.DokumenMahasiswaItem
import com.PPBM.lentera.backend.mahasiswa.model.NotifikasiItem
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MahasiswaRepository {
    private val client = SupabaseConfig.client
    private val storage = client.storage

    /**
     * Mengambil profil lengkap mahasiswa yang sedang login.
     */
    suspend fun getProfilSaya(): Result<UserProfile> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val profile = client.from("profiles").select {
                filter {
                    eq("id", currentUserId)
                }
            }.decodeSingle<UserProfile>()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Memperbarui profil mahasiswa (nama lengkap, no telepon, prodi, angkatan).
     * Kolom role, nim, dan email dijaga oleh database trigger `guard_profile_update`.
     */
    suspend fun updateProfil(
        namaLengkap: String,
        noTelepon: String?,
        perguruanTinggi: String?,
        jurusan: String?,
        programStudi: String?,
        angkatan: String?
    ): Result<UserProfile> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val updatePayload = buildJsonObject {
                put("nama_lengkap", namaLengkap.trim())
                if (noTelepon != null) put("no_telepon", noTelepon.trim())
                if (perguruanTinggi != null) put("perguruan_tinggi", perguruanTinggi.trim())
                if (jurusan != null) put("jurusan", jurusan.trim())
                if (programStudi != null) put("program_studi", programStudi.trim())
                if (angkatan != null) put("angkatan", angkatan.trim())
            }

            val updated = client.from("profiles").update(updatePayload) {
                filter {
                    eq("id", currentUserId)
                }
                select()
            }.decodeSingle<UserProfile>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengambil daftar dokumen arsip mahasiswa (CV, Portofolio, Transkrip).
     */
    suspend fun getDokumenMahasiswa(): Result<List<DokumenMahasiswaItem>> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val list = client.from("dokumen_mahasiswa").select {
                filter {
                    eq("mahasiswa_id", currentUserId)
                }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<DokumenMahasiswaItem>()

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengunggah dokumen profil mahasiswa ke storage bucket `dokumen-mahasiswa`.
     */
    suspend fun uploadDokumenMahasiswa(
        jenis: String,
        fileName: String,
        fileBytes: ByteArray
    ): Result<DokumenMahasiswaItem> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            if (fileBytes.isEmpty()) {
                return Result.failure(Exception("Berkas kosong atau gagal dibaca."))
            }

            val normalizedJenis = normalizeDokumenMahasiswaJenis(jenis)
            val cleanFileName = fileName.trim().ifBlank { "dokumen.pdf" }.replace(" ", "_")
            val storagePath = "$currentUserId/${normalizedJenis}_${System.currentTimeMillis()}_$cleanFileName"

            val bucket = storage.from("dokumen-mahasiswa")
            bucket.upload(storagePath, fileBytes) {
                upsert = true
            }

            val doc = DokumenMahasiswaItem(
                mahasiswaId = currentUserId,
                jenis = normalizedJenis,
                namaFile = cleanFileName,
                filePath = storagePath,
                ukuranBytes = fileBytes.size.toLong()
            )

            val inserted = client.from("dokumen_mahasiswa").insert(doc) {
                select()
            }.decodeSingle<DokumenMahasiswaItem>()

            Result.success(inserted)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunggah dokumen profil.")))
        }
    }

    /**
     * Mengunduh isi bytes dokumen dari bucket `dokumen-mahasiswa`.
     */
    suspend fun downloadDokumenBytes(filePath: String): Result<ByteArray> {
        return try {
            val bytes = storage.from("dokumen-mahasiswa").downloadAuthenticated(filePath)
            Result.success(bytes)
        } catch (e: Exception) {
            Result.failure(Exception(mapSupabaseError(e, "Gagal mengunduh dokumen dari server.")))
        }
    }

    /**
     * Menghapus dokumen profil mahasiswa.
     */
    suspend fun deleteDokumenMahasiswa(docId: String, filePath: String): Result<Unit> {
        return try {
            val bucket = storage.from("dokumen-mahasiswa")
            try {
                bucket.delete(filePath)
            } catch (_: Exception) {}

            client.from("dokumen_mahasiswa").delete {
                filter {
                    eq("id", docId)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengambil daftar notifikasi mahasiswa.
     */
    suspend fun getNotifikasi(): Result<List<NotifikasiItem>> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Sesi login berakhir."))

            val list = client.from("notifikasi").select {
                filter {
                    eq("user_id", currentUserId)
                }
                order("created_at", order = Order.DESCENDING)
            }.decodeList<NotifikasiItem>()

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menandai notifikasi telah dibaca.
     */
    suspend fun markNotifikasiRead(notifId: String): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("is_dibaca", true)
            }
            client.from("notifikasi").update(payload) {
                filter {
                    eq("id", notifId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

