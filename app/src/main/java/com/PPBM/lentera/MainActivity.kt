package com.PPBM.lentera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.PPBM.lentera.features.dashboard.MahasiswaDashboard
import com.PPBM.lentera.features.dashboard.NotifikasiScreen
import com.PPBM.lentera.features.settings.BantuanScreen
import com.PPBM.lentera.features.settings.DataDiriScreen
import com.PPBM.lentera.features.settings.DokumenSayaScreen
import com.PPBM.lentera.features.settings.MahasiswaSettings
import com.PPBM.lentera.features.settings.TentangAplikasiScreen
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraTheme
import com.PPBM.lentera.ui.theme.PlusJakartaSans

enum class ScreenState {
    BERANDA,
    PENGADUAN,
    MAGANG_HUB,
    PROFIL,
    DATA_DIRI,
    DOKUMEN_SAYA,
    NOTIFIKASI,
    BANTUAN,
    TENTANG_APLIKASI
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
                        // Kembali ke beranda atau login saat logout dikonfirmasi
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
            ScreenState.PENGADUAN, ScreenState.MAGANG_HUB -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LenteraBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentScreen == ScreenState.PENGADUAN) "Halaman Pengaduan" else "Halaman Magang Hub",
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