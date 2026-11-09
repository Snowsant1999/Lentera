package com.PPBM.lentera.backend.mahasiswa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO lowongan magang dari view `v_lowongan_terbit`
 */
@Serializable
data class LowonganTerbit(
    val id: String,
    val posisi: String,
    val kategori: String? = null,
    val lokasi: String,
    @SerialName("sistem_kerja")
    val sistemKerja: String, // On-site, Remote, Hybrid
    @SerialName("deskripsi_pekerjaan")
    val deskripsiPekerjaan: String,
    val persyaratan: String,
    @SerialName("periode_magang")
    val periodeMagang: String,
    @SerialName("batas_pendaftaran")
    val batasPendaftaran: String,
    val kuota: Int,
    @SerialName("tempat_magang_id")
    val tempatMagangId: String,
    @SerialName("nama_perusahaan")
    val namaPerusahaan: String,
    @SerialName("bidang_usaha")
    val bidangUsaha: String,
    val kota: String,
    val provinsi: String,
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("situs_web")
    val situsWeb: String? = null
)

/**
 * DTO lamaran yang diajukan mahasiswa
 */
@Serializable
data class LamaranSaya(
    val id: String,
    @SerialName("nomor_lamaran")
    val nomorLamaran: String,
    @SerialName("mahasiswa_id")
    val mahasiswaId: String,
    @SerialName("lowongan_id")
    val lowonganId: String,
    @SerialName("nama_lengkap")
    val namaLengkap: String,
    val nim: String,
    @SerialName("tempat_tanggal_lahir")
    val tempatTanggalLahir: String,
    @SerialName("alamat_ktp")
    val alamatKtp: String,
    @SerialName("alamat_domisili")
    val alamatDomisili: String,
    val agama: String,
    @SerialName("no_telepon")
    val noTelepon: String,
    val email: String,
    @SerialName("golongan_darah")
    val golonganDarah: String,
    val status: String, // terkirim, pemeriksaan, perlu_perbaikan, seleksi, diterima, ditolak
    @SerialName("catatan_admin")
    val catatanAdmin: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

/**
 * Payload pengajuan lamaran baru
 */
@Serializable
data class SubmitLamaranPayload(
    @SerialName("mahasiswa_id")
    val mahasiswaId: String,
    @SerialName("lowongan_id")
    val lowonganId: String,
    @SerialName("nama_lengkap")
    val namaLengkap: String,
    val nim: String,
    @SerialName("tempat_tanggal_lahir")
    val tempatTanggalLahir: String,
    @SerialName("alamat_ktp")
    val alamatKtp: String,
    @SerialName("alamat_domisili")
    val alamatDomisili: String,
    val agama: String,
    @SerialName("no_telepon")
    val noTelepon: String,
    val email: String,
    @SerialName("golongan_darah")
    val golonganDarah: String,
    val status: String = "terkirim"
)

/**
 * DTO dokumen lampiran lamaran
 */
@Serializable
data class DokumenLamaranItem(
    val id: String? = null,
    @SerialName("lamaran_id")
    val lamaranId: String,
    val jenis: String, // CV, Surat Pengantar, Transkrip, KTM, Portofolio, Sertifikat
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
 * DTO riwayat status lamaran
 */
@Serializable
data class LamaranRiwayatItem(
    val id: String,
    @SerialName("lamaran_id")
    val lamaranId: String,
    val status: String,
    val catatan: String? = null,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * Bookmark lowongan
 */
@Serializable
data class BookmarkLowongan(
    @SerialName("mahasiswa_id")
    val mahasiswaId: String,
    @SerialName("lowongan_id")
    val lowonganId: String
)

