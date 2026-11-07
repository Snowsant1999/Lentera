package com.PPBM.lentera.backend.admin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO laporan pengaduan untuk admin dari view `v_laporan_admin`
 */
@Serializable
data class AdminLaporan(
    val id: String,
    @SerialName("nomor_laporan")
    val nomorLaporan: String,
    val kategori: String,
    val judul: String,
    val kronologi: String,
    @SerialName("lokasi_kejadian")
    val lokasiKejadian: String,
    @SerialName("tanggal_kejadian")
    val tanggalKejadian: String,
    @SerialName("is_anonim")
    val isAnonim: Boolean,
    val status: String, // baru, verifikasi, investigasi, penanganan, selesai, ditolak
    @SerialName("jalur_tindak_lanjut")
    val jalurTindakLanjut: String? = null,
    @SerialName("unit_penanganan")
    val unitPenanganan: String? = null,
    @SerialName("catatan_penugasan")
    val catatanPenugasan: String? = null,
    @SerialName("hasil_penanganan")
    val hasilPenanganan: String? = null,
    @SerialName("tanggal_penyelesaian")
    val tanggalPenyelesaian: String? = null,
    @SerialName("hasil_dipublikasi")
    val hasilDipublikasi: Boolean = false,
    @SerialName("ditangani_oleh")
    val ditanganiOleh: String? = null,
    @SerialName("pelapor_id")
    val pelaporId: String? = null,
    @SerialName("pelapor_nama")
    val pelaporNama: String? = null,
    @SerialName("pelapor_nim")
    val pelaporNim: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

/**
 * Log tindakan penanganan kasus oleh admin
 */
@Serializable
data class LogTindakanItem(
    val id: String? = null,
    @SerialName("laporan_id")
    val laporanId: String,
    @SerialName("admin_id")
    val adminId: String,
    @SerialName("judul_tindakan")
    val judulTindakan: String,
    val deskripsi: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

/**
 * Entitas mitra / Tempat Magang
 */
@Serializable
data class TempatMagangEntity(
    val id: String? = null,
    @SerialName("nama_perusahaan")
    val namaPerusahaan: String,
    @SerialName("bidang_usaha")
    val bidangUsaha: String,
    @SerialName("nama_cabang")
    val namaCabang: String = "Pusat",
    val deskripsi: String,
    @SerialName("alamat_lengkap")
    val alamatLengkap: String,
    val kota: String,
    val provinsi: String,
    @SerialName("sistem_kerja")
    val sistemKerja: String = "hybrid",
    @SerialName("nama_kontak")
    val namaKontak: String,
    @SerialName("email_kontak")
    val emailKontak: String,
    @SerialName("telepon_kontak")
    val teleponKontak: String,
    @SerialName("situs_web")
    val situsWeb: String? = null,
    @SerialName("logo_path")
    val logoPath: String? = null,
    val status: String = "aktif" // draf, aktif, nonaktif
)

/**
 * Entitas Lowongan Magang
 */
@Serializable
data class LowonganEntity(
    val id: String? = null,
    @SerialName("tempat_magang_id")
    val tempatMagangId: String,
    val posisi: String,
    val kategori: String? = null,
    val lokasi: String,
    @SerialName("sistem_kerja")
    val sistemKerja: String, // wfo, wfh, hybrid
    @SerialName("deskripsi_pekerjaan")
    val deskripsiPekerjaan: String,
    val persyaratan: String,
    @SerialName("periode_magang")
    val periodeMagang: String,
    @SerialName("batas_pendaftaran")
    val batasPendaftaran: String,
    val kuota: Int,
    val status: String = "terbit", // draf, terbit, ditutup, arsip
    @SerialName("catatan_internal")
    val catatanInternal: String? = null
)

/**
 * Ringkasan statistik dashboard admin
 */
data class AdminDashboardStats(
    val totalLaporan: Int = 0,
    val laporanPerluTindakan: Int = 0,
    val totalTempatMagang: Int = 0,
    val totalLowonganAktif: Int = 0,
    val totalLamaranMasuk: Int = 0,
    val totalMahasiswaAktif: Int = 0
)

