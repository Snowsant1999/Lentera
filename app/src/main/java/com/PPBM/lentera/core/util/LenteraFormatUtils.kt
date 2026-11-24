package com.PPBM.lentera.core.util

object LenteraFormatUtils {
    /**
     * Menurunkan tahun angkatan dari NIM mahasiswa.
     * Contoh: "246651001" -> "2024", atau "202411001" -> "2024".
     */
    fun deriveAngkatanFromNim(nim: String): String {
        val clean = nim.trim()
        if (clean.length >= 4 && clean.startsWith("20") && clean.take(4).all { it.isDigit() }) {
            return clean.take(4)
        }
        if (clean.length >= 2 && clean.take(2).all { it.isDigit() }) {
            return "20" + clean.take(2)
        }
        return ""
    }

    /**
     * Validasi format nomor telepon / WhatsApp Indonesia:
     * - Diawali 08, +628, atau 628
     * - Panjang 10 - 14 karakter numerik
     */
    fun isValidIndonesianPhone(phone: String): Boolean {
        val clean = phone.trim().replace("-", "").replace(" ", "")
        return clean.matches(Regex("^(\\+62|62|0)8[0-9]{8,11}$"))
    }
}

