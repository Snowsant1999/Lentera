package com.PPBM.lentera.backend.mahasiswa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO dokumen arsip mahasiswa (CV, Transkrip, Portofolio)
 */
@Serializable
data class DokumenMahasiswaItem(
    val id: String? = null,
    @SerialName("mahasiswa_id")
    val mahasiswaId: String,
    val jenis: String, // CV, Transkrip, Portofolio
    @SerialName("nama_file")
    val namaFile: String,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("ukuran_bytes")
    val ukuranBytes: Long,
    @SerialName("created_at")
    val createdAt: String? = null
)

/**
 * DTO notifikasi pengguna
 */
@Serializable
data class NotifikasiItem(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val judul: String,
    val isi: String,
    val kategori: String, // siaga, magang, sistem
    val tautan: String? = null,
    val dibaca: Boolean = false,
    @SerialName("created_at")
    val createdAt: String
)

