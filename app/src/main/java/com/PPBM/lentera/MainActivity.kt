package com.PPBM.lentera

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// Design System & Components
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme

// Auth Features
import com.PPBM.lentera.features.auth.LoginScreen
import com.PPBM.lentera.features.auth.LupaPasswordScreen
import com.PPBM.lentera.features.auth.ResetPasswordScreen
import com.PPBM.lentera.features.auth.SplashScreen
import com.PPBM.lentera.features.auth.VerifikasiPemulihanScreen

// Mahasiswa Features
import com.PPBM.lentera.features.mahasiswa.dashboard.MahasiswaDashboard
import com.PPBM.lentera.features.mahasiswa.notifikasi.MahasiswaNotifikasiScreen
import com.PPBM.lentera.features.mahasiswa.profil.BantuanScreen
import com.PPBM.lentera.features.mahasiswa.profil.DataDiriScreen
import com.PPBM.lentera.features.mahasiswa.profil.DokumenSayaScreen
import com.PPBM.lentera.features.mahasiswa.profil.MahasiswaSettings
import com.PPBM.lentera.features.mahasiswa.profil.TentangAplikasiScreen
import com.PPBM.lentera.features.mahasiswa.siaga.BuatLaporanScreen
import com.PPBM.lentera.features.mahasiswa.siaga.DetailLaporanTrackingScreen
import com.PPBM.lentera.features.mahasiswa.siaga.DokumentasiScreen
import com.PPBM.lentera.features.mahasiswa.siaga.HasilPenangananScreen
import com.PPBM.lentera.features.mahasiswa.siaga.InformasiTerkirimScreen
import com.PPBM.lentera.features.mahasiswa.siaga.LaporanStatus
import com.PPBM.lentera.features.mahasiswa.siaga.LaporanTerkirimScreen
import com.PPBM.lentera.features.mahasiswa.siaga.LengkapiInformasiScreen
import com.PPBM.lentera.features.mahasiswa.siaga.MahasiswaDetailLaporanScreen
import com.PPBM.lentera.features.mahasiswa.siaga.MahasiswaSiagaScreen
import com.PPBM.lentera.features.mahasiswa.siaga.RiwayatLaporanScreen
import com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab
import com.PPBM.lentera.features.mahasiswa.siaga.TinjauLaporanScreen
import com.PPBM.lentera.features.mahasiswa.siaga.UploadBuktiScreen
import com.PPBM.lentera.features.mahasiswa.magang.DetailMagangScreen
import com.PPBM.lentera.features.mahasiswa.magang.FormulirLamaranScreen
import com.PPBM.lentera.features.mahasiswa.magang.InformasiPenerimaanScreen
import com.PPBM.lentera.features.mahasiswa.magang.LamaranSayaScreen
import com.PPBM.lentera.features.mahasiswa.magang.LowonganScreen
import com.PPBM.lentera.features.mahasiswa.magang.MagangNavTab
import com.PPBM.lentera.features.mahasiswa.magang.MagangScreen
import com.PPBM.lentera.features.mahasiswa.magang.PerbaikanDokumenScreen
import com.PPBM.lentera.features.mahasiswa.magang.PerbaikanTerkirimScreen
import com.PPBM.lentera.features.mahasiswa.magang.StatusLamaranScreen

// Admin Features
import com.PPBM.lentera.features.admin.dashboard.AdminDashboardScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.AdminMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.BuatAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.DetailAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.siaga.AdminSiagaFlowScreen
import com.PPBM.lentera.features.admin.magang.DaftarLowonganScreen
import com.PPBM.lentera.features.admin.magang.DetailLowonganScreen
import com.PPBM.lentera.features.admin.magang.DetailTempatMagangScreen
import com.PPBM.lentera.features.admin.magang.FormLowonganScreen
import com.PPBM.lentera.features.admin.magang.KelolaStatusTempatScreen
import com.PPBM.lentera.features.admin.magang.TambahTempatMagangScreen
import com.PPBM.lentera.features.admin.magang.TempatMagangScreen
import com.PPBM.lentera.features.admin.notifikasi.AdminNotifikasiScreen
import com.PPBM.lentera.features.admin.profil.BantuanAdminScreen
import com.PPBM.lentera.features.admin.profil.DataAkunAdminScreen
import com.PPBM.lentera.features.admin.profil.KonfirmasiLogoutAdminScreen
import com.PPBM.lentera.features.admin.profil.ProfilAdminScreen

enum class ScreenState {
    // Auth Flow
    SPLASH,
    LOGIN,
    LUPA_PASSWORD,
    VERIFIKASI_PEMULIHAN,
    RESET_PASSWORD,

    // Mahasiswa Main Tabs
    BERANDA,
    PENGADUAN,
    MAGANG_HUB,
    PROFIL,
    NOTIFIKASI,

    // Mahasiswa Profil Sub-Screens
    DATA_DIRI,
    DOKUMEN_SAYA,
    BANTUAN,
    TENTANG_APLIKASI,

    // Mahasiswa Siaga Sub-Screens
    SIAGA_BUAT_LAPORAN,
    SIAGA_UPLOAD_BUKTI,
    SIAGA_TINJAU_LAPORAN,
    SIAGA_LAPORAN_TERKIRIM,
    SIAGA_DETAIL_TRACKING,
    SIAGA_LENGKAPI_INFORMASI,
    SIAGA_INFORMASI_TERKIRIM,
    SIAGA_RIWAYAT_LAPORAN,
    SIAGA_DETAIL_LAPORAN,
    SIAGA_HASIL_PENANGANAN,
    SIAGA_DOKUMENTASI,

    // Mahasiswa Magang Sub-Screens
    MAGANG_LOWONGAN,
    MAGANG_DETAIL,
    MAGANG_FORMULIR,
    MAGANG_LAMARAN_SAYA,
    MAGANG_STATUS,
    MAGANG_STATUS_FINAL,
    MAGANG_INFORMASI,
    MAGANG_INFORMASI_FINAL,
    MAGANG_PERBAIKAN_DOKUMEN,
    MAGANG_PERBAIKAN_TERKIRIM,

    // Admin Flow
    ADMIN_DASHBOARD,
    ADMIN_MAHASISWA,
    ADMIN_BUAT_AKUN_MAHASISWA,
    ADMIN_DETAIL_AKUN_MAHASISWA,
    ADMIN_SIAGA,
    ADMIN_MAGANG_TEMPAT,
    ADMIN_MAGANG_TAMBAH_TEMPAT,
    ADMIN_MAGANG_DETAIL_TEMPAT,
    ADMIN_MAGANG_KELOLA_STATUS,
    ADMIN_MAGANG_LOWONGAN,
    ADMIN_MAGANG_TAMBAH_LOWONGAN,
    ADMIN_MAGANG_DETAIL_LOWONGAN,
    ADMIN_PROFIL,
    ADMIN_DATA_AKUN,
    ADMIN_BANTUAN,
    ADMIN_LOGOUT,
    ADMIN_NOTIFIKASI
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
    // Memulai dari SPLASH untuk pengalaman demo autentik
    var currentScreen by remember { mutableStateOf(ScreenState.SPLASH) }
    var currentTab by remember { mutableStateOf(LenteraTab.BERANDA) }
    var recoveryEmail by remember { mutableStateOf("mahasiswa@lentera.ac.id") }
    var siagaRiwayatOrigin by remember { mutableStateOf(ScreenState.BERANDA) }
    var siagaDetailOrigin by remember { mutableStateOf(ScreenState.PENGADUAN) }
    var magangLowonganOrigin by remember { mutableStateOf(ScreenState.BERANDA) }
    var magangDetailOrigin by remember { mutableStateOf(ScreenState.MAGANG_HUB) }
    var notifikasiOrigin by remember { mutableStateOf(ScreenState.BERANDA) }
    var userProfilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var userNamaLengkap by remember { mutableStateOf("Budi Santoso") }

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

    val handleAdminNavClick: (LenteraNavItem) -> Unit = { navItem ->
        currentScreen = when (navItem) {
            LenteraNavItem.BERANDA -> ScreenState.ADMIN_DASHBOARD
            LenteraNavItem.MAHASISWA -> ScreenState.ADMIN_MAHASISWA
            LenteraNavItem.LAPORAN -> ScreenState.ADMIN_SIAGA
            LenteraNavItem.MAGANG -> ScreenState.ADMIN_MAGANG_TEMPAT
            LenteraNavItem.PROFIL -> ScreenState.ADMIN_PROFIL
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            // ================================================================
            // AUTH FLOW
            // ================================================================
            ScreenState.SPLASH -> {
                SplashScreen(
                    onSplashFinished = { currentScreen = ScreenState.LOGIN }
                )
            }
            ScreenState.LOGIN -> {
                LoginScreen(
                    onLoginMahasiswa = {
                        currentTab = LenteraTab.BERANDA
                        currentScreen = ScreenState.BERANDA
                    },
                    onLoginAdmin = {
                        currentScreen = ScreenState.ADMIN_DASHBOARD
                    },
                    onForgotPassword = {
                        currentScreen = ScreenState.LUPA_PASSWORD
                    }
                )
            }
            ScreenState.LUPA_PASSWORD -> {
                LupaPasswordScreen(
                    onBackToLogin = { currentScreen = ScreenState.LOGIN },
                    onSendReset = { email ->
                        recoveryEmail = if (email.isNotBlank()) email else "mahasiswa@lentera.ac.id"
                        currentScreen = ScreenState.VERIFIKASI_PEMULIHAN
                    }
                )
            }
            ScreenState.VERIFIKASI_PEMULIHAN -> {
                VerifikasiPemulihanScreen(
                    email = recoveryEmail,
                    onBackToLupaPassword = { currentScreen = ScreenState.LUPA_PASSWORD },
                    onVerified = { currentScreen = ScreenState.RESET_PASSWORD },
                    onChangeEmail = { currentScreen = ScreenState.LUPA_PASSWORD },
                    onBackToLogin = { currentScreen = ScreenState.LOGIN }
                )
            }
            ScreenState.RESET_PASSWORD -> {
                ResetPasswordScreen(
                    onBackToVerifikasi = { currentScreen = ScreenState.VERIFIKASI_PEMULIHAN },
                    onPasswordSaved = { currentScreen = ScreenState.LOGIN },
                    onBackToLogin = { currentScreen = ScreenState.LOGIN }
                )
            }

            // ================================================================
            // MAHASISWA MAIN FLOW
            // ================================================================
            ScreenState.BERANDA -> {
                mahasiswaDashboard.mhsDashboard(
                    photoUri = userProfilePhotoUri,
                    userName = userNamaLengkap,
                    onNotificationClick = {
                        notifikasiOrigin = ScreenState.BERANDA
                        currentScreen = ScreenState.NOTIFIKASI
                    },
                    onProfileClick = {
                        currentTab = LenteraTab.PROFIL
                        currentScreen = ScreenState.PROFIL
                    },
                    onSiagaClick = {
                        currentTab = LenteraTab.PENGADUAN
                        currentScreen = ScreenState.PENGADUAN
                    },
                    onMagangClick = {
                        currentTab = LenteraTab.MAGANG_HUB
                        currentScreen = ScreenState.MAGANG_HUB
                    },
                    onAktivitasLihatSemuaClick = {
                        siagaRiwayatOrigin = ScreenState.BERANDA
                        currentScreen = ScreenState.SIAGA_RIWAYAT_LAPORAN
                    },
                    onAktivitasItemClick = { itemTitle ->
                        siagaDetailOrigin = ScreenState.BERANDA
                        if (itemTitle == "Keracunan Makanan") {
                            currentScreen = ScreenState.SIAGA_DETAIL_TRACKING
                        } else {
                            currentScreen = ScreenState.SIAGA_DETAIL_LAPORAN
                        }
                    },
                    onLowonganLihatSemuaClick = {
                        magangLowonganOrigin = ScreenState.BERANDA
                        currentScreen = ScreenState.MAGANG_LOWONGAN
                    },
                    onLowonganItemClick = {
                        magangDetailOrigin = ScreenState.BERANDA
                        currentScreen = ScreenState.MAGANG_DETAIL
                    },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.PROFIL -> {
                mahasiswaSettings.mhsProfilScreen(
                    photoUri = userProfilePhotoUri,
                    namaLengkap = userNamaLengkap,
                    onNavigateToDataDiri = { currentScreen = ScreenState.DATA_DIRI },
                    onNavigateToDokumen = { currentScreen = ScreenState.DOKUMEN_SAYA },
                    onNavigateToNotifikasi = {
                        notifikasiOrigin = ScreenState.PROFIL
                        currentScreen = ScreenState.NOTIFIKASI
                    },
                    onNavigateToBantuan = { currentScreen = ScreenState.BANTUAN },
                    onNavigateToTentang = { currentScreen = ScreenState.TENTANG_APLIKASI },
                    onLogoutConfirmed = {
                        currentScreen = ScreenState.LOGIN
                    },
                    onTabSelected = handleTabSelected
                )
            }
            ScreenState.DATA_DIRI -> {
                DataDiriScreen(
                    initialPhotoUri = userProfilePhotoUri,
                    onPhotoUpdated = { userProfilePhotoUri = it },
                    initialNama = userNamaLengkap,
                    onNamaUpdated = { userNamaLengkap = it },
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
                MahasiswaNotifikasiScreen(
                    onBackClick = {
                        currentScreen = notifikasiOrigin
                        currentTab = when (notifikasiOrigin) {
                            ScreenState.MAGANG_HUB -> LenteraTab.MAGANG_HUB
                            ScreenState.PENGADUAN -> LenteraTab.PENGADUAN
                            ScreenState.PROFIL -> LenteraTab.PROFIL
                            else -> LenteraTab.BERANDA
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
            // MAHASISWA PENGADUAN (SIAGA) FLOW
            // ================================================================
            ScreenState.PENGADUAN -> {
                MahasiswaSiagaScreen(
                    onNotificationClick = {
                        notifikasiOrigin = ScreenState.PENGADUAN
                        currentScreen = ScreenState.NOTIFIKASI
                    },
                    onProfileClick = {
                        currentTab = LenteraTab.PROFIL
                        currentScreen = ScreenState.PROFIL
                    },
                    onCreateReportClick = {
                        currentScreen = ScreenState.SIAGA_BUAT_LAPORAN
                    },
                    onViewAllReportsClick = {
                        siagaRiwayatOrigin = ScreenState.PENGADUAN
                        currentScreen = ScreenState.SIAGA_RIWAYAT_LAPORAN
                    },
                    onReportClick = { item ->
                        siagaDetailOrigin = ScreenState.PENGADUAN
                        if (item.status == LaporanStatus.SELESAI) {
                            currentScreen = ScreenState.SIAGA_DETAIL_LAPORAN
                        } else {
                            currentScreen = ScreenState.SIAGA_DETAIL_TRACKING
                        }
                    },
                    onNavTabClick = { tab ->
                        when (tab) {
                            SiagaNavTab.BERANDA -> {
                                currentTab = LenteraTab.BERANDA
                                currentScreen = ScreenState.BERANDA
                            }
                            SiagaNavTab.MAGANG_HUB -> {
                                currentTab = LenteraTab.MAGANG_HUB
                                currentScreen = ScreenState.MAGANG_HUB
                            }
                            SiagaNavTab.PROFIL -> {
                                currentTab = LenteraTab.PROFIL
                                currentScreen = ScreenState.PROFIL
                            }
                            SiagaNavTab.PENGADUAN -> {}
                        }
                    }
                )
            }
            ScreenState.SIAGA_BUAT_LAPORAN -> {
                BuatLaporanScreen(
                    onBackClick = { currentScreen = ScreenState.PENGADUAN },
                    onKembaliKeKategoriClick = { currentScreen = ScreenState.PENGADUAN },
                    onLanjutKeBuktiClick = {
                        currentScreen = ScreenState.SIAGA_UPLOAD_BUKTI
                    }
                )
            }
            ScreenState.SIAGA_UPLOAD_BUKTI -> {
                UploadBuktiScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onKembaliClick = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onLanjutClick = {
                        currentScreen = ScreenState.SIAGA_TINJAU_LAPORAN
                    }
                )
            }
            ScreenState.SIAGA_TINJAU_LAPORAN -> {
                TinjauLaporanScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_UPLOAD_BUKTI },
                    onUbahKategori = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onUbahJudul = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onUbahTanggal = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onUbahLokasi = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onUbahDeskripsi = { currentScreen = ScreenState.SIAGA_BUAT_LAPORAN },
                    onUbahBukti = { currentScreen = ScreenState.SIAGA_UPLOAD_BUKTI },
                    onKirimLaporan = {
                        currentScreen = ScreenState.SIAGA_LAPORAN_TERKIRIM
                    }
                )
            }
            ScreenState.SIAGA_LAPORAN_TERKIRIM -> {
                LaporanTerkirimScreen(
                    onLihatStatusClick = {
                        currentScreen = ScreenState.SIAGA_DETAIL_TRACKING
                    },
                    onKembaliKeBerandaClick = {
                        currentScreen = ScreenState.PENGADUAN
                    }
                )
            }
            ScreenState.SIAGA_DETAIL_TRACKING -> {
                DetailLaporanTrackingScreen(
                    onBackClick = {
                        currentScreen = siagaDetailOrigin
                        if (siagaDetailOrigin == ScreenState.BERANDA) {
                            currentTab = LenteraTab.BERANDA
                        } else if (siagaDetailOrigin == ScreenState.PENGADUAN) {
                            currentTab = LenteraTab.PENGADUAN
                        }
                    },
                    onLengkapiInformasiClick = {
                        currentScreen = ScreenState.SIAGA_LENGKAPI_INFORMASI
                    }
                )
            }
            ScreenState.SIAGA_LENGKAPI_INFORMASI -> {
                LengkapiInformasiScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_DETAIL_TRACKING },
                    onKembaliClick = { currentScreen = ScreenState.SIAGA_DETAIL_TRACKING },
                    onKirimInformasiClick = {
                        currentScreen = ScreenState.SIAGA_INFORMASI_TERKIRIM
                    }
                )
            }
            ScreenState.SIAGA_INFORMASI_TERKIRIM -> {
                InformasiTerkirimScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_DETAIL_TRACKING },
                    onLihatStatusClick = {
                        currentScreen = ScreenState.SIAGA_DETAIL_TRACKING
                    },
                    onKembaliKeLaporanClick = {
                        currentScreen = ScreenState.PENGADUAN
                    }
                )
            }
            ScreenState.SIAGA_RIWAYAT_LAPORAN -> {
                RiwayatLaporanScreen(
                    onBackClick = {
                        currentScreen = siagaRiwayatOrigin
                        if (siagaRiwayatOrigin == ScreenState.BERANDA) {
                            currentTab = LenteraTab.BERANDA
                        } else {
                            currentTab = LenteraTab.PENGADUAN
                        }
                    },
                    onReportClick = { item ->
                        siagaDetailOrigin = ScreenState.SIAGA_RIWAYAT_LAPORAN
                        if (item.isSelesai) {
                            currentScreen = ScreenState.SIAGA_DETAIL_LAPORAN
                        } else {
                            currentScreen = ScreenState.SIAGA_DETAIL_TRACKING
                        }
                    },
                    onKembaliKeDashboard = {
                        currentTab = LenteraTab.PENGADUAN
                        currentScreen = ScreenState.PENGADUAN
                    }
                )
            }
            ScreenState.SIAGA_DETAIL_LAPORAN -> {
                MahasiswaDetailLaporanScreen(
                    onBackClick = {
                        currentScreen = siagaDetailOrigin
                        if (siagaDetailOrigin == ScreenState.BERANDA) {
                            currentTab = LenteraTab.BERANDA
                        } else if (siagaDetailOrigin == ScreenState.PENGADUAN) {
                            currentTab = LenteraTab.PENGADUAN
                        }
                    },
                    onLihatHasilPenangananClick = {
                        currentScreen = ScreenState.SIAGA_HASIL_PENANGANAN
                    }
                )
            }
            ScreenState.SIAGA_HASIL_PENANGANAN -> {
                HasilPenangananScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_DETAIL_LAPORAN },
                    onTinjauDokumentasiClick = {
                        currentScreen = ScreenState.SIAGA_DOKUMENTASI
                    },
                    onKembaliKeRiwayatClick = {
                        currentScreen = ScreenState.SIAGA_RIWAYAT_LAPORAN
                    }
                )
            }
            ScreenState.SIAGA_DOKUMENTASI -> {
                DokumentasiScreen(
                    onBackClick = { currentScreen = ScreenState.SIAGA_HASIL_PENANGANAN }
                )
            }

            // ================================================================
            // MAGANG HUB FLOW
            // ================================================================
            ScreenState.MAGANG_HUB -> {
                MagangScreen(
                    onNotificationClick = {
                        notifikasiOrigin = ScreenState.MAGANG_HUB
                        currentScreen = ScreenState.NOTIFIKASI
                    },
                    onProfileClick = {
                        currentTab = LenteraTab.PROFIL
                        currentScreen = ScreenState.PROFIL
                    },
                    onSearchClick = {
                        magangLowonganOrigin = ScreenState.MAGANG_HUB
                        currentScreen = ScreenState.MAGANG_LOWONGAN
                    },
                    onViewAllLowonganClick = {
                        magangLowonganOrigin = ScreenState.MAGANG_HUB
                        currentScreen = ScreenState.MAGANG_LOWONGAN
                    },
                    onLowonganClick = {
                        magangDetailOrigin = ScreenState.MAGANG_HUB
                        currentScreen = ScreenState.MAGANG_DETAIL
                    },
                    onLihatLamaranClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onStatusClick = { currentScreen = ScreenState.MAGANG_LAMARAN_SAYA },
                    onLamaranItemClick = { currentScreen = ScreenState.MAGANG_STATUS },
                    selectedNavTab = MagangNavTab.MAGANG_HUB,
                    onNavTabClick = { tab ->
                        when (tab) {
                            MagangNavTab.BERANDA -> {
                                currentTab = LenteraTab.BERANDA
                                currentScreen = ScreenState.BERANDA
                            }
                            MagangNavTab.PENGADUAN -> {
                                currentTab = LenteraTab.PENGADUAN
                                currentScreen = ScreenState.PENGADUAN
                            }
                            MagangNavTab.PROFIL -> {
                                currentTab = LenteraTab.PROFIL
                                currentScreen = ScreenState.PROFIL
                            }
                            MagangNavTab.MAGANG_HUB -> {}
                        }
                    }
                )
            }
            ScreenState.MAGANG_LOWONGAN -> {
                LowonganScreen(
                    onBackClick = {
                        currentScreen = magangLowonganOrigin
                        if (magangLowonganOrigin == ScreenState.BERANDA) {
                            currentTab = LenteraTab.BERANDA
                        } else {
                            currentTab = LenteraTab.MAGANG_HUB
                        }
                    },
                    onLowonganClick = {
                        magangDetailOrigin = ScreenState.MAGANG_LOWONGAN
                        currentScreen = ScreenState.MAGANG_DETAIL
                    }
                )
            }
            ScreenState.MAGANG_DETAIL -> {
                DetailMagangScreen(
                    onBackClick = {
                        currentScreen = magangDetailOrigin
                        if (magangDetailOrigin == ScreenState.BERANDA) {
                            currentTab = LenteraTab.BERANDA
                        } else if (magangDetailOrigin == ScreenState.MAGANG_HUB) {
                            currentTab = LenteraTab.MAGANG_HUB
                        }
                    },
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

            // ================================================================
            // ADMIN FLOW
            // ================================================================
            ScreenState.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    onNavigateToMahasiswa = { currentScreen = ScreenState.ADMIN_MAHASISWA },
                    onNavigateToSiaga = { currentScreen = ScreenState.ADMIN_SIAGA },
                    onNavigateToMagang = { currentScreen = ScreenState.ADMIN_MAGANG_TEMPAT },
                    onNavigateToProfil = { currentScreen = ScreenState.ADMIN_PROFIL },
                    onNavigateToNotifikasi = { currentScreen = ScreenState.ADMIN_NOTIFIKASI },
                    onTambahMahasiswa = { currentScreen = ScreenState.ADMIN_BUAT_AKUN_MAHASISWA },
                    onTambahTempatMagang = { currentScreen = ScreenState.ADMIN_MAGANG_TAMBAH_TEMPAT },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_MAHASISWA -> {
                AdminMahasiswaScreen(
                    onNavigateToBuatAkun = { currentScreen = ScreenState.ADMIN_BUAT_AKUN_MAHASISWA },
                    onNavigateToDetail = { currentScreen = ScreenState.ADMIN_DETAIL_AKUN_MAHASISWA },
                    onBack = { currentScreen = ScreenState.ADMIN_DASHBOARD },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_BUAT_AKUN_MAHASISWA -> {
                BuatAkunMahasiswaScreen(
                    onBackClick = { currentScreen = ScreenState.ADMIN_MAHASISWA },
                    onSimpanSuccess = { currentScreen = ScreenState.ADMIN_MAHASISWA },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_DETAIL_AKUN_MAHASISWA -> {
                DetailAkunMahasiswaScreen(
                    onBackClick = { currentScreen = ScreenState.ADMIN_MAHASISWA },
                    onKembaliClick = { currentScreen = ScreenState.ADMIN_MAHASISWA },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_SIAGA -> {
                AdminSiagaFlowScreen(
                    onBackToDashboard = { currentScreen = ScreenState.ADMIN_DASHBOARD }
                )
            }
            ScreenState.ADMIN_MAGANG_TEMPAT -> {
                TempatMagangScreen(
                    onNavigateToDetailTempat = { currentScreen = ScreenState.ADMIN_MAGANG_DETAIL_TEMPAT },
                    onNavigateToTambahTempat = { currentScreen = ScreenState.ADMIN_MAGANG_TAMBAH_TEMPAT },
                    onNavigateToLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_LOWONGAN },
                    onBack = { currentScreen = ScreenState.ADMIN_DASHBOARD }
                )
            }
            ScreenState.ADMIN_MAGANG_TAMBAH_TEMPAT -> {
                TambahTempatMagangScreen(
                    onSimpanSuccess = { currentScreen = ScreenState.ADMIN_MAGANG_TEMPAT },
                    onBackToTempatMagang = { currentScreen = ScreenState.ADMIN_MAGANG_TEMPAT }
                )
            }
            ScreenState.ADMIN_MAGANG_DETAIL_TEMPAT -> {
                DetailTempatMagangScreen(
                    onNavigateToKelolaStatus = { currentScreen = ScreenState.ADMIN_MAGANG_KELOLA_STATUS },
                    onBackToTempatMagang = { currentScreen = ScreenState.ADMIN_MAGANG_TEMPAT }
                )
            }
            ScreenState.ADMIN_MAGANG_KELOLA_STATUS -> {
                KelolaStatusTempatScreen(
                    onSimpanStatus = { currentScreen = ScreenState.ADMIN_MAGANG_DETAIL_TEMPAT },
                    onBackToDetailTempat = { currentScreen = ScreenState.ADMIN_MAGANG_DETAIL_TEMPAT }
                )
            }
            ScreenState.ADMIN_MAGANG_LOWONGAN -> {
                DaftarLowonganScreen(
                    onNavigateToTambahLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_TAMBAH_LOWONGAN },
                    onNavigateToFormLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_TAMBAH_LOWONGAN },
                    onNavigateToDetailLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_DETAIL_LOWONGAN },
                    onBackToTempatMagang = { currentScreen = ScreenState.ADMIN_MAGANG_TEMPAT }
                )
            }
            ScreenState.ADMIN_MAGANG_TAMBAH_LOWONGAN -> {
                FormLowonganScreen(
                    onSimpanLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_LOWONGAN },
                    onBackToDaftarLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_LOWONGAN }
                )
            }
            ScreenState.ADMIN_MAGANG_DETAIL_LOWONGAN -> {
                DetailLowonganScreen(
                    onBackToDaftarLowongan = { currentScreen = ScreenState.ADMIN_MAGANG_LOWONGAN }
                )
            }
            ScreenState.ADMIN_PROFIL -> {
                ProfilAdminScreen(
                    onMenuClick = { menuItem ->
                        when (menuItem.judul) {
                            "Data Akun" -> currentScreen = ScreenState.ADMIN_DATA_AKUN
                            "Bantuan Admin" -> currentScreen = ScreenState.ADMIN_BANTUAN
                            "Notifikasi" -> currentScreen = ScreenState.ADMIN_NOTIFIKASI
                        }
                    },
                    onKeluarClick = { currentScreen = ScreenState.ADMIN_LOGOUT },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_DATA_AKUN -> {
                DataAkunAdminScreen(
                    onBackClick = { currentScreen = ScreenState.ADMIN_PROFIL },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_BANTUAN -> {
                BantuanAdminScreen(
                    onBackClick = { currentScreen = ScreenState.ADMIN_PROFIL },
                    onNavClick = handleAdminNavClick
                )
            }
            ScreenState.ADMIN_LOGOUT -> {
                KonfirmasiLogoutAdminScreen(
                    onDismiss = { currentScreen = ScreenState.ADMIN_PROFIL },
                    onConfirm = { currentScreen = ScreenState.LOGIN }
                )
            }
            ScreenState.ADMIN_NOTIFIKASI -> {
                AdminNotifikasiScreen(
                    onBackClick = { currentScreen = ScreenState.ADMIN_DASHBOARD },
                    onNavClick = handleAdminNavClick
                )
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
