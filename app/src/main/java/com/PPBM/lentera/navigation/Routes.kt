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

    // Helper untuk route dengan argumen
    fun verifikasiPemulihan(email: String) = "verifikasi_pemulihan/$email"
    fun resetPassword(email: String) = "reset_password/$email"
}
