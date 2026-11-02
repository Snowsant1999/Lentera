package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem

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
                onNavigateToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onBack = onBackToDashboard,
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.DETAIL_LAPORAN -> {
            AdminDetailLaporanScreen(
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
                onValidasiPenugasan = { currentScreen = AdminSiagaRoute.PENUGASAN_LAPORAN },
                onMintaInformasi = { currentScreen = AdminSiagaRoute.MINTA_INFORMASI },
                onTidakDapatDiproses = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.MINTA_INFORMASI -> {
            MintaInformasiScreen(
                onKirimPermintaan = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.PENUGASAN_LAPORAN -> {
            PenugasanLaporanScreen(
                onSimpanPenugasan = { currentScreen = AdminSiagaRoute.CATATAN_TINDAKAN },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.CATATAN_TINDAKAN -> {
            CatatanTindakanScreen(
                onHasilPenyelesaian = { currentScreen = AdminSiagaRoute.HASIL_PENYELESAIAN },
                onSimpanCatatan = { /* Simpan catatan tindakan */ },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.HASIL_PENYELESAIAN -> {
            HasilPenyelesaianScreen(
                onSimpanDanSelesaikan = { currentScreen = AdminSiagaRoute.LAPORAN_MASUK },
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
