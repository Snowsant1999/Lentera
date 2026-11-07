package com.PPBM.lentera.backend.mahasiswa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KategoriLaporan(
    val id: Long,
    val nama: String
)

/**
 * DTO hasil query view `v_laporan_saya`
 */
@Serializable
data class LaporanSaya(
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
    val isAnonim: Boolean = false,
    val status: String, // baru, verifikasi, investigasi, penanganan, selesai, ditolak
    @SerialName("tanggal_penyelesaian")
    val tanggalPenyelesaian: String? = null,
    @SerialName("hasil_penanganan")
    val hasilPenanganan: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

/**
 * DTO timeline progres penanganan laporan dari view `v_timeline_laporan_saya`
 */
@Serializable
data class TimelineLaporanSaya(
    val id: String,
    @SerialName("laporan_id")
    val laporanId: String,
    @SerialName("judul_tindakan")
    val judulTindakan: String,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * DTO bukti lampiran laporan
 */
@Serializable
data class BuktiLaporanItem(
    val id: String? = null,
    @SerialName("laporan_id")
    val laporanId: String,
    @SerialName("nama_file")
    val namaFile: String,
    @SerialName("tipe_file")
    val tipeFile: String,
    @SerialName("ukuran_bytes")
    val ukuranBytes: Long,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

/**
 * Payload insert laporan baru ke tabel `laporan`
 */
@Serializable
data class CreateLaporanPayload(
    @SerialName("pelapor_id")
    val pelaporId: String,
    @SerialName("kategori_id")
    val kategoriId: Long,
    val judul: String,
    val kronologi: String,
    @SerialName("lokasi_kejadian")
    val lokasiKejadian: String,
    @SerialName("tanggal_kejadian")
    val tanggalKejadian: String,
    @SerialName("is_anonim")
    val isAnonim: Boolean = false,
    val status: String = "baru"
)

/**
 * DTO permintaan informasi tambahan dari admin ke pelapor
 */
@Serializable
data class PermintaanInformasiItem(
    val id: String,
    @SerialName("laporan_id")
    val laporanId: String,
    @SerialName("pesan_ke_pelapor")
    val pesanKePelapor: String,
    @SerialName("balasan_pelapor")
    val balasanPelapor: String? = null,
    @SerialName("lampiran_balasan")
    val lampiranBalasan: String? = null,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * DTO berkas berita acara / dokumen hasil penyelesaian kasus
 */
@Serializable
data class DokumenHasilItem(
    val id: String,
    @SerialName("laporan_id")
    val laporanId: String,
    @SerialName("nama_file")
    val namaFile: String,
    @SerialName("tipe_file")
    val tipeFile: String,
    @SerialName("ukuran_bytes")
    val ukuranBytes: Long,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("boleh_dibagikan")
    val bolehDibagikan: Boolean = false,
    @SerialName("created_at")
    val createdAt: String
)

