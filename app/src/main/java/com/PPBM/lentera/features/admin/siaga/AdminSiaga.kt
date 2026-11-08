package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

/**
 * Controller Router Interaktif untuk Alur Admin Siaga
 * Menghubungkan 7 frame desain UI:
 * 1. LaporanMasukScreen.kt (Frame: Laporan Masuk)
 * 2. DetailLaporanScreen.kt (Frame: Detail Laporan)
 * 3. VerifikasiKelengkapanScreen.kt (Frame: Verifikasi Kelengkapan)
 * 4. MintaInformasiScreen.kt (Frame: Minta Informasi)
 * 5. PenugasanLaporanScreen.kt (Frame: Penugasan Laporan)
 * 6. CatatanTindakanScreen.kt (Frame: Catatan Tindakan)
 * 7. HasilPenyelesaianScreen.kt (Frame: Hasil & Penyelesaian)
 */
@Composable
fun AdminSiagaFlowScreen(
    laporanList: List<AdminLaporanItem> = emptyList(),
    selectedLaporan: AdminLaporan? = null,
    onSelectLaporanId: (String) -> Unit = {},
    onUpdateStatusLaporan: (laporanId: String, status: String, unit: String?, jalur: String?, catatan: String?, onSuccess: () -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    onAddCatatanTindakan: (laporanId: String, judul: String, catatan: String, onSuccess: () -> Unit) -> Unit = { _, _, _, _ -> },
    onSelesaikanLaporan: (laporanId: String, ringkasan: String, dipublikasi: Boolean, onSuccess: () -> Unit) -> Unit = { _, _, _, _ -> },
    onBackToDashboard: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(AdminSiagaRoute.LAPORAN_MASUK) }

    val handleNavClick: (LenteraNavItem) -> Unit = { item ->
        if (item == LenteraNavItem.LAPORAN) {
            currentScreen = AdminSiagaRoute.LAPORAN_MASUK
        } else {
            onNavClick(item)
        }
    }

    when (currentScreen) {
        AdminSiagaRoute.LAPORAN_MASUK -> {
            LaporanMasukScreen(
                laporanList = laporanList,
                onNavigateToDetail = { lapId ->
                    onSelectLaporanId(lapId)
                    currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                },
                onBack = onBackToDashboard,
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.DETAIL_LAPORAN -> {
            AdminDetailLaporanScreen(
                laporan = selectedLaporan,
                onNavigateToVerifikasi = { currentScreen = AdminSiagaRoute.VERIFIKASI_KELENGKAPAN },
                onNavigateToMintaInformasi = { currentScreen = AdminSiagaRoute.MINTA_INFORMASI },
                onNavigateToPenugasan = { currentScreen = AdminSiagaRoute.PENUGASAN_LAPORAN },
                onNavigateToCatatanTindakan = { currentScreen = AdminSiagaRoute.CATATAN_TINDAKAN },
                onNavigateToHasilPenyelesaian = { currentScreen = AdminSiagaRoute.HASIL_PENYELESAIAN },
                onBackToList = { currentScreen = AdminSiagaRoute.LAPORAN_MASUK },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.VERIFIKASI_KELENGKAPAN -> {
            VerifikasiKelengkapanScreen(
                laporan = selectedLaporan,
                onValidasiPenugasan = {
                    selectedLaporan?.let { lap ->
                        onUpdateStatusLaporan(lap.id, "diperiksa", null, null, null) {
                            currentScreen = AdminSiagaRoute.PENUGASAN_LAPORAN
                        }
                    } ?: run {
                        currentScreen = AdminSiagaRoute.PENUGASAN_LAPORAN
                    }
                },
                onMintaInformasi = { currentScreen = AdminSiagaRoute.MINTA_INFORMASI },
                onTidakDapatDiproses = {
                    selectedLaporan?.let { lap ->
                        onUpdateStatusLaporan(lap.id, "ditolak", null, null, "Laporan tidak dapat diproses.") {
                            currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                        }
                    } ?: run {
                        currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.MINTA_INFORMASI -> {
            MintaInformasiScreen(
                laporan = selectedLaporan,
                onKirimPermintaan = { pesan ->
                    selectedLaporan?.let { lap ->
                        onAddCatatanTindakan(lap.id, "Permintaan Klarifikasi Informasi", pesan) {
                            currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                        }
                    } ?: run {
                        currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.PENUGASAN_LAPORAN -> {
            PenugasanLaporanScreen(
                laporan = selectedLaporan,
                onSimpanPenugasan = { unit, jalur, catatan ->
                    selectedLaporan?.let { lap ->
                        onUpdateStatusLaporan(lap.id, "diproses", unit, jalur, catatan) {
                            currentScreen = AdminSiagaRoute.CATATAN_TINDAKAN
                        }
                    } ?: run {
                        currentScreen = AdminSiagaRoute.CATATAN_TINDAKAN
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.CATATAN_TINDAKAN -> {
            CatatanTindakanScreen(
                laporan = selectedLaporan,
                onHasilPenyelesaian = { currentScreen = AdminSiagaRoute.HASIL_PENYELESAIAN },
                onSimpanCatatan = { catatan ->
                    selectedLaporan?.let { lap ->
                        onAddCatatanTindakan(lap.id, "Catatan Tindakan Penanganan", catatan) {
                            currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                        }
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.HASIL_PENYELESAIAN -> {
            HasilPenyelesaianScreen(
                laporan = selectedLaporan,
                onSimpanDanSelesaikan = { ringkasan, isBagikan ->
                    selectedLaporan?.let { lap ->
                        onSelesaikanLaporan(lap.id, ringkasan, isBagikan) {
                            currentScreen = AdminSiagaRoute.LAPORAN_MASUK
                        }
                    } ?: run {
                        currentScreen = AdminSiagaRoute.LAPORAN_MASUK
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AdminSiagaFlowPreview() {
    AdminSiagaFlowScreen()
}
