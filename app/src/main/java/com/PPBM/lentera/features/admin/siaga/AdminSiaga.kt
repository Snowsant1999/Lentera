package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.admin.model.LogTindakanItem
import com.PPBM.lentera.backend.mahasiswa.model.BuktiLaporanItem
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem

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
    initialRoute: AdminSiagaRoute = AdminSiagaRoute.LAPORAN_MASUK,
    buktiList: List<BuktiLaporanItem> = emptyList(),
    logList: List<LogTindakanItem> = emptyList(),
    permintaanList: List<PermintaanInformasiItem> = emptyList(),
    dokumenHasilList: List<DokumenHasilItem> = emptyList(),
    isUpdating: Boolean = false,
    onSelectLaporanId: (String) -> Unit = {},
    onUpdateStatusLaporan: (laporanId: String, status: String, unit: String?, jalur: String?, catatan: String?, onSuccess: () -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    onMintaInformasi: (laporanId: String, pesanKePelapor: String, onSuccess: () -> Unit) -> Unit = { _, _, _ -> },
    onAddCatatanTindakan: (laporanId: String, judul: String, catatan: String, onSuccess: () -> Unit) -> Unit = { _, _, _, _ -> },
    onSelesaikanLaporan: (laporanId: String, ringkasan: String, dipublikasi: Boolean, onSuccess: () -> Unit) -> Unit = { _, _, _, _ -> },
    onSelesaikanLaporanLengkap: ((laporanId: String, ringkasan: String, dipublikasi: Boolean, attachments: List<Triple<String, ByteArray, Boolean>>, onSuccess: () -> Unit) -> Unit)? = null,
    onOpenBukti: (BuktiLaporanItem) -> Unit = {},
    onOpenDokumen: (DokumenHasilItem) -> Unit = {},
    onBackToDashboard: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var currentScreen by remember(initialRoute) { mutableStateOf(initialRoute) }

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
                buktiList = buktiList,
                logList = logList,
                permintaanList = permintaanList,
                onOpenBukti = onOpenBukti,
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
                isUpdating = isUpdating,
                onValidasiPenugasan = {
                    val lap = selectedLaporan ?: return@VerifikasiKelengkapanScreen
                    onUpdateStatusLaporan(lap.id, "diperiksa", null, null, null) {
                        currentScreen = AdminSiagaRoute.PENUGASAN_LAPORAN
                    }
                },
                onMintaInformasi = {
                    if (selectedLaporan != null) {
                        currentScreen = AdminSiagaRoute.MINTA_INFORMASI
                    }
                },
                onTidakDapatDiproses = {
                    val lap = selectedLaporan ?: return@VerifikasiKelengkapanScreen
                    onUpdateStatusLaporan(lap.id, "ditolak", null, null, "Laporan tidak dapat diproses.") {
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
                isUpdating = isUpdating,
                onKirimPermintaan = { pesan ->
                    val lap = selectedLaporan ?: return@MintaInformasiScreen
                    onMintaInformasi(lap.id, pesan) {
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
                isUpdating = isUpdating,
                onSimpanPenugasan = { unit, jalur, catatan ->
                    val lap = selectedLaporan ?: return@PenugasanLaporanScreen
                    onUpdateStatusLaporan(lap.id, "diproses", unit, jalur, catatan) {
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
                logTindakanList = logList,
                permintaanList = permintaanList,
                isUpdating = isUpdating,
                onHasilPenyelesaian = { currentScreen = AdminSiagaRoute.HASIL_PENYELESAIAN },
                onSimpanCatatan = { catatan ->
                    val lap = selectedLaporan ?: return@CatatanTindakanScreen
                    onAddCatatanTindakan(lap.id, "Catatan Tindakan Penanganan", catatan) {
                        currentScreen = AdminSiagaRoute.DETAIL_LAPORAN
                    }
                },
                onBackToDetail = { currentScreen = AdminSiagaRoute.DETAIL_LAPORAN },
                onNavClick = handleNavClick
            )
        }
        AdminSiagaRoute.HASIL_PENYELESAIAN -> {
            HasilPenyelesaianScreen(
                laporan = selectedLaporan,
                dokumenHasilList = dokumenHasilList,
                isUpdating = isUpdating,
                onOpenDokumen = onOpenDokumen,
                onSimpanDanSelesaikan = { ringkasan, isBagikan ->
                    val lap = selectedLaporan ?: return@HasilPenyelesaianScreen
                    onSelesaikanLaporan(lap.id, ringkasan, isBagikan) {
                        currentScreen = AdminSiagaRoute.LAPORAN_MASUK
                    }
                },
                onSimpanLengkap = onSelesaikanLaporanLengkap?.let { handler ->
                    { ringkasan, isBagikan, attachments ->
                        val lap = selectedLaporan
                        if (lap != null) {
                            handler(lap.id, ringkasan, isBagikan, attachments) {
                                currentScreen = AdminSiagaRoute.LAPORAN_MASUK
                            }
                        }
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
