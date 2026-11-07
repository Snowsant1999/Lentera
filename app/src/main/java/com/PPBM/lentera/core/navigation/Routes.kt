package com.PPBM.lentera.core.navigation

/**
 * Definisi semua route navigasi dalam aplikasi Lentera.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val LUPA_PASSWORD = "lupa_password"
    const val VERIFIKASI_PEMULIHAN = "verifikasi_pemulihan/{email}"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val DASHBOARD_PLACEHOLDER = "dashboard_placeholder"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val MAHASISWA_DASHBOARD = "mahasiswa_dashboard"
    const val SIAGA = "siaga"
    const val BUAT_LAPORAN = "buat_laporan"
    const val UPLOAD_BUKTI = "upload_bukti"
    const val TINJAU_LAPORAN = "tinjau_laporan"
    const val LAPORAN_TERKIRIM = "laporan_terkirim"
    const val LAPORAN_TERKIRIM_ROUTE = "laporan_terkirim?ref={ref}"
    const val DETAIL_LAPORAN = "detail_laporan"
    const val DETAIL_LAPORAN_TRACKING = "detail_laporan_tracking"
    const val DETAIL_LAPORAN_TRACKING_ROUTE = "detail_laporan_tracking?id={id}"
    const val DETAIL_LAPORAN_LENGKAP = "detail_laporan" // Alias untuk DETAIL_LAPORAN
    const val LENGKAPI_INFORMASI = "lengkapi_informasi"
    const val DOKUMENTASI = "dokumentasi"
    const val INFORMASI_TERKIRIM = "informasi_terkirim"
    const val HASIL_PENANGANAN = "hasil_penanganan"
    const val RIWAYAT_LAPORAN = "riwayat_laporan"
    
    // Mahasiswa Profil & Settings Routes
    const val MAHASISWA_PROFIL = "mahasiswa_profil"
    const val MAHASISWA_DATA_DIRI = "mahasiswa_data_diri"
    const val MAHASISWA_DOKUMEN_SAYA = "mahasiswa_dokumen_saya"
    const val MAHASISWA_NOTIFIKASI = "mahasiswa_notifikasi"
    const val MAHASISWA_BANTUAN = "mahasiswa_bantuan"
    const val MAHASISWA_TENTANG = "mahasiswa_tentang"

    // Magang Hub Routes
    const val MAGANG = "magang"
    const val LOWONGAN_MAGANG = "lowongan_magang"
    const val DETAIL_MAGANG = "detail_magang"
    const val DETAIL_MAGANG_ROUTE = "detail_magang?id={id}"
    const val FORMULIR_LAMARAN = "formulir_lamaran"
    const val LAMARAN_SAYA = "lamaran_saya"
    const val STATUS_LAMARAN = "status_lamaran"
    const val STATUS_LAMARAN_ROUTE = "status_lamaran?id={id}"
    const val STATUS_LAMARAN_FINAL = "status_lamaran_final"
    const val INFORMASI_PENERIMAAN = "informasi_penerimaan"
    const val INFORMASI_PENERIMAAN_FINAL = "informasi_penerimaan_final"
    const val PERBAIKAN_DOKUMEN = "perbaikan_dokumen"
    const val PERBAIKAN_TERKIRIM = "perbaikan_terkirim"

    // Helper untuk route dengan argumen
    fun verifikasiPemulihan(email: String) = "verifikasi_pemulihan/$email"
    fun resetPassword(email: String) = "reset_password/$email"
    fun laporanTerkirim(nomorReferensi: String) = "laporan_terkirim?ref=$nomorReferensi"
    fun detailLaporanTracking(id: String) = "detail_laporan_tracking?id=$id"
    fun detailMagang(id: String) = "detail_magang?id=$id"
    fun statusLamaran(id: String) = "status_lamaran?id=$id"
}
