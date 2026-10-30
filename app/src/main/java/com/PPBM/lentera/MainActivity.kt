package com.PPBM.lentera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.PPBM.lentera.features.dashboard.MahasiswaDashboard
import com.PPBM.lentera.features.dashboard.NotifikasiScreen
import com.PPBM.lentera.features.magang.*
import com.PPBM.lentera.features.settings.BantuanScreen
import com.PPBM.lentera.features.settings.DataDiriScreen
import com.PPBM.lentera.features.settings.DokumenSayaScreen
import com.PPBM.lentera.features.settings.MahasiswaSettings
import com.PPBM.lentera.features.settings.TentangAplikasiScreen
import com.PPBM.lentera.navigation.LenteraNavGraph
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraTheme
import com.PPBM.lentera.ui.theme.PlusJakartaSans
import com.PPBM.lentera.ui.theme.ScreenBackground

enum class ScreenState {
    BERANDA,
    PENGADUAN,
    MAGANG_HUB,
    PROFIL,
    DATA_DIRI,
    DOKUMEN_SAYA,
    NOTIFIKASI,
    BANTUAN,
    TENTANG_APLIKASI,
    // Magang Hub Sub-Screens
    MAGANG_LOWONGAN,
    MAGANG_DETAIL,
    MAGANG_FORMULIR,
    MAGANG_LAMARAN_SAYA,
    MAGANG_STATUS,
    MAGANG_STATUS_FINAL,
    MAGANG_INFORMASI,
    MAGANG_INFORMASI_FINAL,
    MAGANG_PERBAIKAN_DOKUMEN,
    MAGANG_PERBAIKAN_TERKIRIM
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LenteraTheme {
                LenteraApp()
            }
        }
    }
}

@Composable
fun LenteraApp() {
    var currentScreen by remember { mutableStateOf(ScreenState.BERANDA) }
    var currentTab by remember { mutableStateOf(LenteraTab.BERANDA) }

    val mahasiswaDashboard = remember { MahasiswaDashboard() }
    val mahasiswaSettings = remember { MahasiswaSettings() }

    val handleTabSelected: (LenteraTab) -> Unit = { tab ->
        currentTab = tab
        currentScreen = when (tab) {
            LenteraTab.BERANDA -> ScreenState.BERANDA
            LenteraTab.PENGADUAN -> ScreenState.PENGADUAN
            LenteraTab.MAGANG_HUB -> ScreenState.MAGANG_HUB
            LenteraTab.PROFIL -> ScreenState.PROFIL
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            ScreenState.BERANDA -> {
                mahasiswaDashboard.mhsDashboard(
                    onNotificationClick = { currentScreen = ScreenState.NOTIFIKASI },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.PROFIL -> {
                mahasiswaSettings.mhsProfilScreen(
                    onNavigateToDataDiri = { currentScreen = ScreenState.DATA_DIRI },
                    onNavigateToDokumen = { currentScreen = ScreenState.DOKUMEN_SAYA },
                    onNavigateToNotifikasi = { currentScreen = ScreenState.NOTIFIKASI },
                    onNavigateToBantuan = { currentScreen = ScreenState.BANTUAN },
                    onNavigateToTentang = { currentScreen = ScreenState.TENTANG_APLIKASI },
                    onLogoutConfirmed = {
                        currentScreen = ScreenState.BERANDA
                        currentTab = LenteraTab.BERANDA
                    },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.DATA_DIRI -> {
                DataDiriScreen(
                    onBackClick = { currentScreen = ScreenState.PROFIL },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.DOKUMEN_SAYA -> {
                DokumenSayaScreen(
                    onBackClick = { currentScreen = ScreenState.PROFIL },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.NOTIFIKASI -> {
                NotifikasiScreen(
                    onBackClick = {
                        if (currentTab == LenteraTab.PROFIL) {
                            currentScreen = ScreenState.PROFIL
                        } else {
                            currentScreen = ScreenState.BERANDA
                        }
                    },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.BANTUAN -> {
                BantuanScreen(
                    onBackClick = { currentScreen = ScreenState.PROFIL },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.TENTANG_APLIKASI -> {
                TentangAplikasiScreen(
                    onBackClick = { currentScreen = ScreenState.PROFIL },
                    onTabSelected = handleTabSelected
                )
            }
            // ================================================================
            // MAGANG HUB FLOW
            // ================================================================
            ScreenState.MAGANG_HUB -> {
                MagangScreen(
                    onNotificationClick = { currentScreen = ScreenState.NOTIFIKASI },
                    onProfileClick = {
                        currentScreen = ScreenState.PROFIL
                        currentTab = LenteraTab.PROFIL
                    },
                    onSearchClick = { currentScreen = ScreenState.MAGANG_LOWONGAN },
                    onViewAllLowonganClick = { currentScreen = ScreenState.MAGANG_LOWONGAN },
                    onLowonganClick = { currentScreen = ScreenState.MAGANG_DETAIL },
                    onLihatLamaranClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onStatusClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onLamaranItemClick = { currentScreen = ScreenState.MAGANG_STATUS },
                    selectedNavTab = MagangNavTab.MAGANG_HUB,
                    onNavTabClick = { tab ->
                        when (tab) {
                            MagangNavTab.BERANDA -> {
                                currentScreen = ScreenState.BERANDA
                                currentTab = LenteraTab.BERANDA
                            }
                            MagangNavTab.PENGADUAN -> {
                                currentScreen = ScreenState.PENGADUAN
                                currentTab = LenteraTab.PENGADUAN
                            }
                            MagangNavTab.PROFIL -> {
                                currentScreen = ScreenState.PROFIL
                                currentTab = LenteraTab.PROFIL
                            }
                            MagangNavTab.MAGANG_HUB -> {}
                        }
                    }
                )
            }
            ScreenState.MAGANG_LOWONGAN -> {
                LowonganScreen(
                    onBackClick = { currentScreen = ScreenState.MAGANG_HUB },
                    onLowonganClick = { currentScreen = ScreenState.MAGANG_DETAIL }
                )
            }
            ScreenState.MAGANG_DETAIL -> {
                DetailMagangScreen(
                    onBackClick = { currentScreen = ScreenState.MAGANG_HUB },
                    onLamarSekarangClick = { currentScreen = ScreenState.MAGANG_FORMULIR }
                )
            }
            ScreenState.MAGANG_FORMULIR -> {
                FormulirLamaranScreen(
                    onBackClick = { currentScreen = ScreenState.MAGANG_DETAIL },
                    onCekStatusLamaran = { currentScreen = ScreenState.MAGANG_STATUS },
                    onKembaliKeBeranda = { currentScreen = ScreenState.MAGANG_HUB }
                )
            }
            ScreenState.MAGANG_LAMARAN_SAYA -> {
                LamaranSayaScreen(
                    onBackClick = { currentScreen = ScreenState.MAGANG_HUB },
                    onStatusClick = { item ->
                        if (item.status == "Diterima") {
                            currentScreen = ScreenState.MAGANG_STATUS_FINAL
                        } else {
                            currentScreen = ScreenState.MAGANG_STATUS
                        }
                    }
                )
            }
            ScreenState.MAGANG_STATUS -> {
                StatusLamaranScreen(
                    needsCorrection = true,
                    isFinalAccepted = false,
                    onBackClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onPerbaikiDokumenClick = { currentScreen = ScreenState.MAGANG_PERBAIKAN_DOKUMEN },
                    onInformasiPenerimaanClick = { currentScreen = ScreenState.MAGANG_INFORMASI }
                )
            }
            ScreenState.MAGANG_STATUS_FINAL -> {
                StatusLamaranScreen(
                    needsCorrection = false,
                    isFinalAccepted = true,
                    onBackClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onInformasiPenerimaanClick = { currentScreen = ScreenState.MAGANG_INFORMASI_FINAL }
                )
            }
            ScreenState.MAGANG_INFORMASI -> {
                InformasiPenerimaanScreen(
                    isFinal = false,
                    onBackClick = { currentScreen = ScreenState.MAGANG_STATUS }
                )
            }
            ScreenState.MAGANG_INFORMASI_FINAL -> {
                InformasiPenerimaanScreen(
                    isFinal = true,
                    onBackClick = { currentScreen = ScreenState.MAGANG_STATUS_FINAL }
                )
            }
            ScreenState.MAGANG_PERBAIKAN_DOKUMEN -> {
                PerbaikanDokumenScreen(
                    onBackClick = { currentScreen = ScreenState.MAGANG_STATUS },
                    onSubmitSuccess = { currentScreen = ScreenState.MAGANG_PERBAIKAN_TERKIRIM }
                )
            }
            ScreenState.MAGANG_PERBAIKAN_TERKIRIM -> {
                PerbaikanTerkirimScreen(
                    onKembaliKeStatus = { currentScreen = ScreenState.MAGANG_STATUS }
                )
            }
            ScreenState.PENGADUAN -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LenteraBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Halaman Pengaduan",
                        fontFamily = PlusJakartaSans,
                        fontSize = 18.sp,
                        color = LenteraNavy
                    )
                    LenteraBottomBar(
                        currentTab = currentTab,
                        onTabSelected = handleTabSelected,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    LenteraTheme {
        LenteraApp()
    }
}
