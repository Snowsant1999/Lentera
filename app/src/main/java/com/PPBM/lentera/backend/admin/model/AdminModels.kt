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
    val status: String, // baru, diperiksa, diproses, selesai, ditolak
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
    val sistemKerja: String = "Hybrid", // On-site, Remote, Hybrid
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
    val status: String = "draf", // draf, aktif, arsip
    @SerialName("catatan_internal")
    val catatanInternal: String? = null
)

/**
 * Dokumen MOU / lampiran mitra dari tabel `dokumen_tempat_magang`
 */
@Serializable
data class DokumenTempatMagangItem(
    val id: String? = null,
    @SerialName("tempat_magang_id")
    val tempatMagangId: String,
    @SerialName("nama_file")
    val namaFile: String,
    @SerialName("ukuran_bytes")
    val ukuranBytes: Long,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("created_at")
    val createdAt: String? = null
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
    val sistemKerja: String, // On-site, Remote, Hybrid
    @SerialName("deskripsi_pekerjaan")
    val deskripsiPekerjaan: String,
    val persyaratan: String,
    @SerialName("periode_magang")
    val periodeMagang: String,
    @SerialName("batas_pendaftaran")
    val batasPendaftaran: String,
    val kuota: Int,
    val status: String = "draf", // draf, terbit, ditutup
    @SerialName("catatan_internal")
    val catatanInternal: String? = null
)

/**
 * Ringkasan statistik dashboard admin
 */
@Serializable
data class AdminDashboardStats(
    @SerialName("total_laporan")
    val totalLaporan: Int = 0,
    @SerialName("laporan_perlu_tindakan")
    val laporanPerluTindakan: Int = 0,
    @SerialName("total_tempat_magang")
    val totalTempatMagang: Int = 0,
    @SerialName("total_lowongan_aktif")
    val totalLowonganAktif: Int = 0,
    @SerialName("total_lamaran_masuk")
    val totalLamaranMasuk: Int = 0,
    @SerialName("total_mahasiswa_aktif")
    val totalMahasiswaAktif: Int = 0
)

