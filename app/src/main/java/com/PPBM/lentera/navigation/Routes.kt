package com.PPBM.lentera.navigation

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
    const val SIAGA = "siaga"
    const val BUAT_LAPORAN = "buat_laporan"
    const val UPLOAD_BUKTI = "upload_bukti"
    const val TINJAU_LAPORAN = "tinjau_laporan"
    const val LAPORAN_TERKIRIM = "laporan_terkirim"
    const val DETAIL_LAPORAN = "detail_laporan"
    const val DETAIL_LAPORAN_TRACKING = "detail_laporan_tracking"
    const val DETAIL_LAPORAN_LENGKAP = "detail_laporan" // Alias untuk DETAIL_LAPORAN
    const val LENGKAPI_INFORMASI = "lengkapi_informasi"
    const val DOKUMENTASI = "dokumentasi"
    const val INFORMASI_TERKIRIM = "informasi_terkirim"
    const val HASIL_PENANGANAN = "hasil_penanganan"
    const val RIWAYAT_LAPORAN = "riwayat_laporan"
    
    // Magang Hub Routes
    const val MAGANG = "magang"
    const val LOWONGAN_MAGANG = "lowongan_magang"
    const val DETAIL_MAGANG = "detail_magang"
    const val FORMULIR_LAMARAN = "formulir_lamaran"
    const val LAMARAN_SAYA = "lamaran_saya"
    const val STATUS_LAMARAN = "status_lamaran"
    const val STATUS_LAMARAN_FINAL = "status_lamaran_final"
    const val INFORMASI_PENERIMAAN = "informasi_penerimaan"
    const val INFORMASI_PENERIMAAN_FINAL = "informasi_penerimaan_final"
    const val PERBAIKAN_DOKUMEN = "perbaikan_dokumen"
    const val PERBAIKAN_TERKIRIM = "perbaikan_terkirim"

    // Helper untuk route dengan argumen
    fun verifikasiPemulihan(email: String) = "verifikasi_pemulihan/$email"
    fun resetPassword(email: String) = "reset_password/$email"
}
