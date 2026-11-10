package com.PPBM.lentera.features.admin.akunmahasiswa

enum class StatusAkun(val label: String) {
    AKTIF("Aktif"),
    BELUM_AKTIVASI("Belum Aktivasi"),
    LULUS("Lulus"),
    NONAKTIF("Nonaktif")
}

data class Mahasiswa(
    val nama: String,
    val nim: String,
    val prodi: String,
    val status: StatusAkun
)