package com.PPBM.lentera.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.features.admin.AdminViewModel
import com.PPBM.lentera.features.admin.akunmahasiswa.AdminMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.BuatAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.DetailAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.Mahasiswa
import com.PPBM.lentera.features.admin.akunmahasiswa.StatusAkun
import com.PPBM.lentera.features.admin.dashboard.AdminDashboardScreen
import com.PPBM.lentera.features.admin.dashboard.DashboardLaporanItem
import com.PPBM.lentera.features.admin.dashboard.DashboardLowonganItem
import com.PPBM.lentera.features.admin.profil.*
import com.PPBM.lentera.features.admin.notifikasi.AdminNotifikasiScreen
import com.PPBM.lentera.features.admin.siaga.*
import com.PPBM.lentera.features.admin.siaga.components.AdminLaporanItem
import com.PPBM.lentera.features.auth.*
import com.PPBM.lentera.features.mahasiswa.dashboard.MahasiswaDashboard
import com.PPBM.lentera.features.mahasiswa.dashboard.MahasiswaDashboardViewModel
import com.PPBM.lentera.features.mahasiswa.siaga.*
import com.PPBM.lentera.features.mahasiswa.notifikasi.MahasiswaNotifikasiScreen
import com.PPBM.lentera.features.mahasiswa.profil.*
import com.PPBM.lentera.features.mahasiswa.magang.*
import com.PPBM.lentera.core.designsystem.theme.*

/**
 * Navigation graph utama untuk alur autentikasi.
 * Splash → Login → (Lupa Password → Verifikasi → Reset Password)
 *                → Dashboard Placeholder
 */
@Composable
fun LenteraNavGraph(navController: NavHostController) {
    val siagaViewModel: SiagaViewModel = viewModel()
    val siagaState by siagaViewModel.uiState.collectAsState()
    val profilViewModel: ProfilViewModel = viewModel()
    val profilState by profilViewModel.uiState.collectAsState()
    val magangViewModel: MagangViewModel = viewModel()
    val magangState by magangViewModel.uiState.collectAsState()
    val adminViewModel: AdminViewModel = viewModel()
    val adminState by adminViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // Splash Screen
        composable(
            route = Routes.SPLASH,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SplashScreen(
                onSplashFinished = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role.equals("admin", ignoreCase = true)) {
                        Routes.ADMIN_DASHBOARD
                    } else {
                        Routes.MAHASISWA_DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(Routes.LUPA_PASSWORD)
                }
            )
        }

        // Lupa Password Screen
        composable(Routes.LUPA_PASSWORD) {
            LupaPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSendReset = { email ->
                    navController.navigate(Routes.verifikasiPemulihan(email))
                }
            )
        }

        // Verifikasi Pemulihan Screen
        composable(
            route = Routes.VERIFIKASI_PEMULIHAN,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifikasiPemulihanScreen(
                email = email,
                onBackToLupaPassword = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Routes.resetPassword(email)) {
                        popUpTo(Routes.LUPA_PASSWORD) { inclusive = true }
                    }
                },
                onChangeEmail = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Reset Password Screen
        composable(
            route = Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            ResetPasswordScreen(
                onBackToVerifikasi = { navController.popBackStack() },
                onPasswordSaved = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard Placeholder
        composable(Routes.DASHBOARD_PLACEHOLDER) {
            DashboardPlaceholderScreen(
                onLogout = {
                    val session = SessionManager.getInstance(navController.context)
                    session.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenSiaga = {
                    navController.navigate(Routes.SIAGA)
                },
                onOpenMagang = {
                    navController.navigate(Routes.MAGANG)
                }
            )
        }

        // Mahasiswa Dashboard
        composable(Routes.MAHASISWA_DASHBOARD) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()
            val dashboardViewModel: MahasiswaDashboardViewModel = viewModel()
            val dashboardState by dashboardViewModel.uiState.collectAsState()

            MahasiswaDashboard().mhsDashboard(
                userName = profile?.namaLengkap ?: "Budi Mahasiswa",
                totalLaporan = dashboardState.totalLaporan,
                laporanDiperiksa = dashboardState.laporanDiperiksa,
                laporanSelesai = dashboardState.laporanSelesai,
                totalLamaran = dashboardState.totalLamaran,
                lamaranStatusText = dashboardState.lamaranStatusSummary,
                aktivitasList = dashboardState.aktivitasTerbaru,
                lowonganList = dashboardState.rekomendasiLowongan,
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> { /* tetap di beranda */ }
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                },
                onProfileClick = { navController.navigate(Routes.MAHASISWA_PROFIL) },
                onNotificationClick = { navController.navigate(Routes.MAHASISWA_NOTIFIKASI) },
                onSiagaClick = { navController.navigate(Routes.SIAGA) },
                onMagangClick = { navController.navigate(Routes.MAGANG) },
                onAktivitasLihatSemuaClick = { navController.navigate(Routes.SIAGA) },
                onAktivitasItemClick = { laporanId ->
                    navController.navigate(Routes.detailLaporanTracking(laporanId))
                },
                onLowonganLihatSemuaClick = { navController.navigate(Routes.MAGANG) },
                onLowonganItemClick = { lowonganId ->
                    navController.navigate(Routes.detailMagang(lowonganId))
                }
            )
        }

        // Admin Dashboard
        composable(Routes.ADMIN_DASHBOARD) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            LaunchedEffect(Unit) {
                adminViewModel.loadDashboardData()
            }

            AdminDashboardScreen(
                userName = profile?.namaLengkap ?: "Admin Lentera",
                onNavigateToMahasiswa = { navController.navigate(Routes.ADMIN_MAHASISWA) },
                onNavigateToSiaga = { navController.navigate(Routes.ADMIN_SIAGA) },
                onNavigateToMagang = { navController.navigate(Routes.ADMIN_MAGANG) },
                onNavigateToLowongan = { navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN) },
                onNavigateToProfil = { navController.navigate(Routes.ADMIN_PROFIL) },
                onNavigateToNotifikasi = { navController.navigate(Routes.ADMIN_NOTIFIKASI) },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> {}
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // ====================================================================
        // Fitur Kelola Akun Mahasiswa (Admin)
        // ====================================================================

        // Daftar Akun Mahasiswa
        composable(Routes.ADMIN_MAHASISWA) {
            LaunchedEffect(Unit) {
                adminViewModel.loadMahasiswaList()
            }

            val studentsTriple = adminState.mahasiswaList.map { mhs ->
                val statusStr = if (mhs.statusAkun.equals("aktif", ignoreCase = true)) "Aktif" else "Belum Aktivasi"
                Triple(mhs.namaLengkap, mhs.nim ?: "-", statusStr)
            }

            AdminMahasiswaScreen(
                studentList = studentsTriple,
                onNavigateToBuatAkun = { navController.navigate(Routes.ADMIN_BUAT_AKUN_MAHASISWA) },
                onNavigateToDetail = { nim ->
                    adminViewModel.selectMahasiswa(nim)
                    navController.navigate(Routes.adminDetailMahasiswa(nim))
                },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> {}
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Buat Akun Mahasiswa Baru
        composable(Routes.ADMIN_BUAT_AKUN_MAHASISWA) {
            BuatAkunMahasiswaScreen(
                onBackClick = { navController.popBackStack() },
                onSimpanSuccess = { navController.popBackStack() },
                onSimpanAkun = { nama, nim, email, jurusan, prodi, angkatan, onSuccess ->
                    adminViewModel.buatAkunMahasiswa(
                        namaLengkap = nama,
                        nim = nim,
                        email = email,
                        jurusan = jurusan,
                        programStudi = prodi,
                        angkatan = angkatan,
                        onSuccess = { onSuccess() }
                    )
                },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Detail Akun Mahasiswa
        composable(
            route = Routes.ADMIN_DETAIL_MAHASISWA_ROUTE,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id").orEmpty()
            LaunchedEffect(idArg) {
                if (idArg.isNotBlank()) {
                    adminViewModel.selectMahasiswa(idArg)
                }
            }

            val mhs = adminState.selectedMahasiswa
            val mahasiswaObj = if (mhs != null) {
                val statusEnum = if (mhs.statusAkun.equals("aktif", ignoreCase = true)) StatusAkun.AKTIF else StatusAkun.BELUM_AKTIVASI
                Mahasiswa(
                    nama = mhs.namaLengkap,
                    nim = mhs.nim ?: "-",
                    prodi = mhs.programStudi ?: "Teknik Informatika",
                    status = statusEnum
                )
            } else {
                Mahasiswa(
                    nama = "Mahasiswa Lentera",
                    nim = idArg.ifBlank { "2024010001" },
                    prodi = "Teknik Informatika",
                    status = StatusAkun.AKTIF
                )
            }

            DetailAkunMahasiswaScreen(
                mahasiswa = mahasiswaObj,
                jurusan = mhs?.jurusan ?: "Teknologi Informasi",
                angkatan = mhs?.angkatan ?: "2026",
                emailKampus = mhs?.email ?: "mahasiswa@lentera.ac.id",
                metodeAkses = "Akun aktif terdaftar di database Supabase",
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onKirimUlangTautanClick = {
                    mhs?.id?.let { uid ->
                        adminViewModel.toggleStatusMahasiswa(uid, "aktif")
                    }
                },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Fallback route detail akun mahasiswa tanpa query
        composable(Routes.ADMIN_DETAIL_MAHASISWA) {
            DetailAkunMahasiswaScreen(
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // ====================================================================
        // Fitur Pengaturan & Profil Admin
        // ====================================================================

        // Halaman Utama Profil Admin
        composable(Routes.ADMIN_PROFIL) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            ProfilAdminScreen(
                nama = profile?.namaLengkap ?: "Rina Wulandari",
                peran = "${profile?.nim ?: "NIP. 198803152015042001"} · Admin Kampus",
                badge = if (profile?.statusAkun.equals("aktif", ignoreCase = true)) "Admin aktif" else "Admin terdaftar",
                onMenuClick = { menu ->
                    when (menu.judul) {
                        "Data Akun" -> navController.navigate(Routes.ADMIN_DATA_AKUN)
                        "Notifikasi" -> navController.navigate(Routes.ADMIN_NOTIFIKASI)
                        "Bantuan Admin" -> navController.navigate(Routes.ADMIN_BANTUAN)
                        "Tentang Aplikasi" -> navController.navigate(Routes.MAHASISWA_TENTANG)
                    }
                },
                onKeluarClick = {
                    session.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> {}
                    }
                }
            )
        }

        // Data Akun Admin
        composable(Routes.ADMIN_DATA_AKUN) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            DataAkunAdminScreen(
                nama = profile?.namaLengkap ?: "Rina Wulandari",
                peran = "Admin Kampus",
                email = profile?.email ?: "admin@lentera.ac.id",
                nomorTelepon = profile?.noTelepon ?: "081234567890",
                onBackClick = { navController.popBackStack() },
                onUbahClick = { navController.navigate(Routes.ADMIN_KEAMANAN_AKUN) },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Keamanan Akun Admin
        composable(Routes.ADMIN_KEAMANAN_AKUN) {
            KeamananAkunScreen(
                onBackClick = { navController.popBackStack() },
                onPerangkatTerhubungClick = { navController.navigate(Routes.ADMIN_PERANGKAT_TERHUBUNG) },
                onRiwayatLoginClick = { navController.navigate(Routes.ADMIN_RIWAYAT_LOGIN) },
                onEmailPemulihanClick = { navController.navigate(Routes.ADMIN_EMAIL_PEMULIHAN) },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Perangkat Terhubung
        composable(Routes.ADMIN_PERANGKAT_TERHUBUNG) {
            PerangkatTerhubungScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Riwayat Login
        composable(Routes.ADMIN_RIWAYAT_LOGIN) {
            RiwayatLoginScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Email Pemulihan
        composable(Routes.ADMIN_EMAIL_PEMULIHAN) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            EmailPemulihanScreen(
                currentEmail = profile?.email ?: "admin@lentera.ac.id",
                onBackClick = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Bantuan Admin
        composable(Routes.ADMIN_BANTUAN) {
            BantuanAdminScreen(
                onBackClick = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Notifikasi Admin
        composable(Routes.ADMIN_NOTIFIKASI) {
            AdminNotifikasiScreen(
                onBackClick = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // ====================================================================
        // Fitur Lentera Siaga Admin (Penanganan Laporan)
        // ====================================================================

        // Alur Terpadu Admin Siaga
        composable(Routes.ADMIN_SIAGA) {
            LaunchedEffect(Unit) {
                adminViewModel.loadSemuaLaporan()
            }

            AdminSiagaFlowScreen(
                onBackToDashboard = { navController.navigate(Routes.ADMIN_DASHBOARD) },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> {}
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Halaman Detail Laporan Admin Spesifik
        composable(
            route = Routes.ADMIN_DETAIL_LAPORAN_ROUTE,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id").orEmpty()
            LaunchedEffect(idArg) {
                if (idArg.isNotBlank()) {
                    adminViewModel.selectLaporan(idArg)
                }
            }

            AdminDetailLaporanScreen(
                onNavigateToVerifikasi = { navController.navigate(Routes.ADMIN_SIAGA) },
                onNavigateToMintaInformasi = { navController.navigate(Routes.ADMIN_MINTA_INFORMASI) },
                onNavigateToPenugasan = { navController.navigate(Routes.ADMIN_PENUGASAN_LAPORAN) },
                onNavigateToCatatanTindakan = { navController.navigate(Routes.ADMIN_CATATAN_TINDAKAN) },
                onNavigateToHasilPenyelesaian = { navController.navigate(Routes.ADMIN_HASIL_PENYELESAIAN) },
                onBackToList = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Catatan Tindakan Laporan
        composable(Routes.ADMIN_CATATAN_TINDAKAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            CatatanTindakanScreen(
                onHasilPenyelesaian = { navController.navigate(Routes.ADMIN_HASIL_PENYELESAIAN) },
                onSimpanCatatan = { catatan ->
                    selectedLaporan?.id?.let { lapId ->
                        adminViewModel.addCatatanTindakan(
                            laporanId = lapId,
                            judul = "Catatan Penanganan Admin",
                            deskripsi = catatan
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Penugasan Laporan
        composable(Routes.ADMIN_PENUGASAN_LAPORAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            PenugasanLaporanScreen(
                onSimpanPenugasan = {
                    selectedLaporan?.id?.let { lapId ->
                        adminViewModel.updateStatusLaporan(
                            laporanId = lapId,
                            status = "penanganan",
                            unitPenanganan = "Tim Advokasi Kampus",
                            jalurTindakLanjut = "Alihkan ke pihak berwenang",
                            catatanPenugasan = "Koordinasikan pendampingan dan pemeriksaan dengan menjaga kerahasiaan pelapor."
                        ) {
                            navController.navigate(Routes.ADMIN_CATATAN_TINDAKAN)
                        }
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Hasil & Penyelesaian Laporan
        composable(Routes.ADMIN_HASIL_PENYELESAIAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            HasilPenyelesaianScreen(
                onSimpanDanSelesaikan = {
                    selectedLaporan?.id?.let { lapId ->
                        adminViewModel.selesaikanLaporan(
                            laporanId = lapId,
                            hasilPenanganan = "Laporan telah selesai ditangani secara resmi oleh Tim Advokasi dan Administrasi Kampus.",
                            hasilDipublikasi = true
                        ) {
                            navController.navigate(Routes.ADMIN_SIAGA) {
                                popUpTo(Routes.ADMIN_SIAGA) { inclusive = true }
                            }
                        }
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item ->
                    when (item) {
                        LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
                        LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
                        LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
                        LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
                    }
                }
            )
        }

        // Lentera Siaga Mahasiswa
        composable(Routes.SIAGA) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            LaunchedEffect(Unit) {
                siagaViewModel.loadLaporanSaya()
            }

            val laporanItemList = siagaState.laporanList.map { item ->
                val statusEnum = when (item.status.lowercase()) {
                    "selesai" -> LaporanStatus.SELESAI
                    "ditolak" -> LaporanStatus.DITOLAK
                    "penanganan", "investigasi" -> LaporanStatus.DIPROSES
                    else -> LaporanStatus.DIPERIKSA
                }
                LaporanItem(
                    id = item.id,
                    title = item.judul,
                    date = item.tanggalKejadian,
                    status = statusEnum
                )
            }

            MahasiswaSiagaScreen(
                userName = profile?.namaLengkap ?: "Mahasiswa Lentera",
                laporanList = if (siagaState.laporanList.isEmpty()) sampleLaporanList else laporanItemList,
                onCreateReportClick = {
                    navController.navigate(Routes.BUAT_LAPORAN)
                },
                onViewAllReportsClick = {
                    navController.navigate(Routes.RIWAYAT_LAPORAN)
                },
                onReportClick = { item ->
                    navController.navigate(Routes.detailLaporanTracking(item.id))
                },
                onProfileClick = { navController.navigate(Routes.MAHASISWA_PROFIL) },
                onNotificationClick = { navController.navigate(Routes.MAHASISWA_NOTIFIKASI) },
                onNavTabClick = { tab ->
                    when (tab) {
                        SiagaNavTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        SiagaNavTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        SiagaNavTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                        else -> {}
                    }
                }
            )
        }

        // Halaman Buat Laporan (Langkah 2 dari 4)
        composable(Routes.BUAT_LAPORAN) {
            BuatLaporanScreen(
                initialData = BuatLaporanFormData(
                    kategori = siagaState.selectedKategoriNama,
                    judul = siagaState.formJudul.ifBlank { siagaState.selectedKategoriNama },
                    tanggalWaktu = siagaState.formTanggalWaktu.ifBlank { "12 Oktober 2026, 14.30" },
                    lokasi = siagaState.formLokasi.ifBlank { "Koridor Kampus" },
                    deskripsi = siagaState.formKronologi,
                    isAnonim = siagaState.formIsAnonim
                ),
                onBackClick = { navController.popBackStack() },
                onKembaliKeKategoriClick = { navController.popBackStack() },
                onLanjutKeBuktiClick = { data ->
                    siagaViewModel.updateFormData(
                        judul = data.judul,
                        tanggalWaktu = data.tanggalWaktu,
                        lokasi = data.lokasi,
                        kronologi = data.deskripsi,
                        isAnonim = data.isAnonim
                    )
                    navController.navigate(Routes.UPLOAD_BUKTI)
                }
            )
        }

        // Halaman Upload Bukti (Langkah 3 dari 4)
        composable(Routes.UPLOAD_BUKTI) {
            val buktiItems = if (siagaState.uploadedBuktiList.isEmpty()) {
                sampleBuktiList
            } else {
                siagaState.uploadedBuktiList.mapIndexed { index, item ->
                    BuktiFileItem(
                        id = index.toString(),
                        fileName = item.fileName,
                        fileType = item.fileType,
                        fileSize = item.fileSizeFormatted,
                        isExcluded = item.isExcluded
                    )
                }
            }

            UploadBuktiScreen(
                initialFiles = buktiItems,
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onDeleteFile = { item ->
                    item.id.toIntOrNull()?.let { siagaViewModel.toggleExcludeBukti(it) }
                },
                onRestoreFile = { item ->
                    item.id.toIntOrNull()?.let { siagaViewModel.toggleExcludeBukti(it) }
                },
                onLanjutClick = { files ->
                    navController.navigate(Routes.TINJAU_LAPORAN)
                }
            )
        }

        // Halaman Tinjau Laporan (Langkah 4 dari 4)
        composable(Routes.TINJAU_LAPORAN) {
            val reportData = TinjauLaporanData(
                kategori = siagaState.selectedKategoriNama,
                judul = siagaState.formJudul.ifBlank { siagaState.selectedKategoriNama },
                tanggalWaktu = siagaState.formTanggalWaktu.ifBlank { "12 Oktober 2026, 14.30" },
                lokasi = siagaState.formLokasi.ifBlank { "Koridor Kampus" },
                deskripsi = siagaState.formKronologi.ifBlank { "Kronologi pengaduan kejadian" },
                buktiSummary = "${siagaState.uploadedBuktiList.count { !it.isExcluded }} file bukti disertakan",
                isAnonim = siagaState.formIsAnonim
            )

            TinjauLaporanScreen(
                reportData = reportData,
                onBackClick = { navController.popBackStack() },
                onUbahKategori = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahJudul = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahTanggal = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahLokasi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahDeskripsi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahBukti = { navController.popBackStack(Routes.UPLOAD_BUKTI, false) },
                onKirimLaporan = { finalData ->
                    siagaViewModel.submitLaporan { nomorRef ->
                        navController.navigate(Routes.laporanTerkirim(nomorRef)) {
                            popUpTo(Routes.SIAGA) { inclusive = false }
                        }
                    }
                }
            )
        }

        // Halaman Konfirmasi Laporan Terkirim
        composable(
            route = Routes.LAPORAN_TERKIRIM_ROUTE,
            arguments = listOf(navArgument("ref") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val refArg = backStackEntry.arguments?.getString("ref").orEmpty()
            val finalRef = if (refArg.isNotBlank()) refArg else (siagaState.submitSuccessNomorLaporan ?: "LTR-2026-00001")

            LaporanTerkirimScreen(
                nomorReferensi = finalRef,
                onLihatStatusClick = {
                    val targetId = siagaState.laporanList.firstOrNull()?.id ?: ""
                    navController.navigate(Routes.detailLaporanTracking(targetId)) {
                        popUpTo(Routes.SIAGA) { inclusive = false }
                    }
                },
                onKembaliKeBerandaClick = {
                    navController.navigate(Routes.SIAGA) {
                        popUpTo(Routes.SIAGA) { inclusive = true }
                    }
                }
            )
        }

        // Fallback route lama tanpa query
        composable(Routes.LAPORAN_TERKIRIM) {
            LaporanTerkirimScreen(
                nomorReferensi = siagaState.submitSuccessNomorLaporan ?: "LTR-2026-00001",
                onLihatStatusClick = {
                    val targetId = siagaState.laporanList.firstOrNull()?.id ?: ""
                    navController.navigate(Routes.detailLaporanTracking(targetId)) {
                        popUpTo(Routes.SIAGA) { inclusive = false }
                    }
                },
                onKembaliKeBerandaClick = {
                    navController.navigate(Routes.SIAGA) {
                        popUpTo(Routes.SIAGA) { inclusive = true }
                    }
                }
            )
        }

        // Halaman Detail Laporan Tracking (Sedang Ditindaklanjuti)
        composable(
            route = Routes.DETAIL_LAPORAN_TRACKING_ROUTE,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id").orEmpty()
            val targetId = if (idArg.isNotBlank()) idArg else (siagaState.laporanList.firstOrNull()?.id ?: "")

            LaunchedEffect(targetId) {
                if (targetId.isNotBlank()) {
                    siagaViewModel.loadDetailLaporan(targetId)
                }
            }

            val detail = siagaState.detailLaporan
            val hasPendingAdmin = siagaState.permintaanInfoList.any { it.balasanPelapor.isNullOrBlank() }
            val adminMsg = siagaState.permintaanInfoList.firstOrNull { it.balasanPelapor.isNullOrBlank() }?.pesanKePelapor
                ?: "Mohon jelaskan ciri pelaku dan apakah ada saksi saat kejadian."

            val trackingData = if (detail != null) {
                val stepState = when (detail.status.lowercase()) {
                    "selesai" -> Pair(TimelineStepState.COMPLETED, TimelineStepState.COMPLETED)
                    "penanganan", "investigasi" -> Pair(TimelineStepState.COMPLETED, TimelineStepState.ACTIVE)
                    "verifikasi" -> Pair(TimelineStepState.ACTIVE, TimelineStepState.PENDING)
                    else -> Pair(TimelineStepState.ACTIVE, TimelineStepState.PENDING)
                }
                DetailLaporanTrackingData(
                    nomorReferensi = detail.nomorLaporan,
                    judul = detail.judul,
                    status = detail.status.replaceFirstChar { it.uppercase() },
                    tanggalPengiriman = "Dikirim ${detail.createdAt.take(10)} · ${if (detail.isAnonim) "Laporan anonim" else "Kerahasiaan terjamin"}",
                    hasAdminRequest = hasPendingAdmin,
                    adminMessage = adminMsg,
                    timeline = listOf(
                        TimelineStepItem(
                            title = "Dikirim",
                            description = "${detail.createdAt.take(10)} · Laporan diterima sistem",
                            state = TimelineStepState.COMPLETED
                        ),
                        TimelineStepItem(
                            title = "Diperiksa",
                            description = "Tim Satgas meninjau laporan",
                            state = stepState.first
                        ),
                        TimelineStepItem(
                            title = "Ditindaklanjuti",
                            description = "Proses penanganan kasus",
                            state = stepState.second
                        ),
                        TimelineStepItem(
                            title = "Selesai",
                            description = detail.hasilPenanganan ?: "Ringkasan penyelesaian laporan",
                            state = if (detail.status == "selesai") TimelineStepState.COMPLETED else TimelineStepState.PENDING
                        )
                    )
                )
            } else {
                DetailLaporanTrackingData()
            }

            DetailLaporanTrackingScreen(
                reportData = trackingData,
                onBackClick = { navController.popBackStack() },
                onLengkapiInformasiClick = {
                    navController.navigate(Routes.LENGKAPI_INFORMASI)
                }
            )
        }

        // Fallback route lama tanpa query
        composable(Routes.DETAIL_LAPORAN_TRACKING) {
            val targetId = siagaState.laporanList.firstOrNull()?.id ?: ""
            LaunchedEffect(targetId) {
                if (targetId.isNotBlank()) {
                    siagaViewModel.loadDetailLaporan(targetId)
                }
            }
            DetailLaporanTrackingScreen(
                reportData = DetailLaporanTrackingData(),
                onBackClick = { navController.popBackStack() },
                onLengkapiInformasiClick = {
                    navController.navigate(Routes.LENGKAPI_INFORMASI)
                }
            )
        }

        // Halaman Lengkapi Informasi Tambahan
        composable(Routes.LENGKAPI_INFORMASI) {
            val pendingPermintaan = siagaState.permintaanInfoList.firstOrNull { it.balasanPelapor.isNullOrBlank() }
            val pesan = pendingPermintaan?.pesanKePelapor
                ?: "Mohon jelaskan kronologi lebih rinci dan apakah ada saksi saat kejadian."

            LengkapiInformasiScreen(
                kodeLaporan = siagaState.detailLaporan?.nomorLaporan ?: "LTR-2026-00123",
                pesanAdmin = pesan,
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onKirimInformasiClick = { balasan ->
                    val reqId = pendingPermintaan?.id ?: ""
                    siagaViewModel.kirimBalasanInformasi(reqId, balasan) {
                        navController.navigate(Routes.INFORMASI_TERKIRIM) {
                            popUpTo(Routes.DETAIL_LAPORAN_TRACKING) { inclusive = false }
                        }
                    }
                }
            )
        }

        // Halaman Konfirmasi Informasi Tambahan Berhasil Dikirim
        composable(Routes.INFORMASI_TERKIRIM) {
            InformasiTerkirimScreen(
                nomorLaporan = siagaState.detailLaporan?.nomorLaporan ?: "LTR-2026-00123",
                onBackClick = { navController.popBackStack() },
                onLihatStatusClick = {
                    val targetId = siagaState.detailLaporan?.id ?: ""
                    navController.navigate(Routes.detailLaporanTracking(targetId)) {
                        popUpTo(Routes.SIAGA) { inclusive = false }
                    }
                },
                onKembaliKeLaporanClick = {
                    navController.navigate(Routes.SIAGA) {
                        popUpTo(Routes.SIAGA) { inclusive = true }
                    }
                }
            )
        }

        // Halaman Dokumentasi Penanganan Laporan
        composable(Routes.DOKUMENTASI) {
            DokumentasiScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Halaman Hasil Penanganan
        composable(Routes.HASIL_PENANGANAN) {
            val detail = siagaState.detailLaporan
            val hasilData = if (detail != null) {
                HasilPenangananData(
                    nomorLaporan = detail.nomorLaporan,
                    judul = detail.judul,
                    status = detail.status.replaceFirstChar { it.uppercase() },
                    tanggalLaporan = detail.tanggalKejadian,
                    kategori = detail.kategori,
                    ringkasanPenanganan = detail.hasilPenanganan
                        ?: "Laporan telah selesai ditangani secara tuntas dan sesuai prosedur penanganan kampus.",
                    tanggalPenyelesaian = detail.tanggalPenyelesaian ?: "${detail.updatedAt.take(10)}, 10:30 WITA"
                )
            } else {
                HasilPenangananData()
            }

            HasilPenangananScreen(
                data = hasilData,
                onBackClick = { navController.popBackStack() },
                onTinjauDokumentasiClick = {
                    navController.navigate(Routes.DOKUMENTASI)
                },
                onKembaliKeRiwayatClick = { navController.popBackStack() }
            )
        }

        // Halaman Riwayat Laporan (Laporan Saya)
        composable(Routes.RIWAYAT_LAPORAN) {
            val riwayatItems = if (siagaState.laporanList.isEmpty()) {
                sampleRiwayatLaporan
            } else {
                siagaState.laporanList.map { item ->
                    val isSelesai = item.status.lowercase() == "selesai"
                    val badgeBg: Color
                    val badgeText: Color
                    when (item.status.lowercase()) {
                        "selesai" -> {
                            badgeBg = Color(0xFFE3F5EB)
                            badgeText = Color(0xFF287D4A)
                        }
                        "ditolak" -> {
                            badgeBg = Color(0xFFFFEBEE)
                            badgeText = Color(0xFFC62828)
                        }
                        else -> {
                            badgeBg = Color(0xFFE6B363)
                            badgeText = Color.White
                        }
                    }
                    RiwayatLaporanItem(
                        id = item.id,
                        nomorLaporan = item.nomorLaporan,
                        judul = item.judul,
                        kategori = item.kategori,
                        waktu = item.tanggalKejadian,
                        statusBadge = item.status.replaceFirstChar { it.uppercase() },
                        badgeBgColor = badgeBg,
                        badgeTextColor = badgeText,
                        isSelesai = isSelesai
                    )
                }
            }

            RiwayatLaporanScreen(
                reportList = riwayatItems,
                onBackClick = { navController.popBackStack() },
                onReportClick = { item ->
                    navController.navigate(Routes.detailLaporanTracking(item.id))
                },
                onKembaliKeDashboard = {
                    navController.navigate(Routes.SIAGA) {
                        popUpTo(Routes.SIAGA) { inclusive = true }
                    }
                }
            )
        }

        // Halaman Detail Laporan Lengkap (Scrollable)
        composable(Routes.DETAIL_LAPORAN) {
            val detail = siagaState.detailLaporan
            val fullData = if (detail != null) {
                DetailLaporanFullData(
                    nomorLaporan = detail.nomorLaporan,
                    judul = detail.judul,
                    status = detail.status.replaceFirstChar { it.uppercase() },
                    waktuKirim = "Dikirim ${detail.createdAt.take(10)}",
                    pelaporNama = if (detail.isAnonim) "Anonim" else "Pelapor Terverifikasi",
                    pelaporDeskripsi = if (detail.isAnonim) "Identitas pelapor dirahasiakan" else "Data identitas terjaga aman",
                    kategori = detail.kategori,
                    tanggalKejadian = detail.tanggalKejadian,
                    lokasiKejadian = detail.lokasiKejadian,
                    ringkasanLaporan = detail.kronologi,
                    listLampiran = if (siagaState.buktiList.isEmpty()) {
                        emptyList()
                    } else {
                        siagaState.buktiList.map { b ->
                            LampiranBuktiDetail(
                                nama = b.namaFile,
                                ukuran = "${b.ukuranBytes / 1024} KB",
                                tipe = when (b.tipeFile.uppercase()) {
                                    "JPG", "PNG", "JPEG" -> "PHOTO"
                                    "MP4", "MKV" -> "DOC"
                                    else -> "DOC"
                                }
                            )
                        }
                    }
                )
            } else {
                DetailLaporanFullData()
            }

            MahasiswaDetailLaporanScreen(
                data = fullData,
                onBackClick = { navController.popBackStack() },
                onLihatHasilPenangananClick = {
                    navController.navigate(Routes.HASIL_PENANGANAN)
                }
            )
        }

        // ====================================================================
        // Fitur Magang
        // ====================================================================

        // Halaman Utama Magang (Magang.png)
        composable(Routes.MAGANG) {
            val totalLam = magangState.lamaranSayaList.size
            val dtr = magangState.lamaranSayaList.count { it.status == "diterima" }
            val dtl = magangState.lamaranSayaList.count { it.status == "ditolak" }
            val drv = magangState.lamaranSayaList.count {
                it.status == "terkirim" || it.status == "verifikasi_berkas" || it.status == "wawancara" || it.status == "perlu_perbaikan"
            }

            MagangScreen(
                userName = profilState.profile?.namaLengkap ?: "Budi Santoso",
                totalLamaran = totalLam,
                jumlahDiterima = dtr,
                jumlahDitolak = dtl,
                jumlahDireview = drv,
                bookmarkedIds = magangState.bookmarkedIds,
                onBookmarkClick = { lowongan ->
                    magangViewModel.toggleBookmark(lowongan.id)
                },
                onSearchClick = { navController.navigate(Routes.LOWONGAN_MAGANG) },
                onViewAllLowonganClick = { navController.navigate(Routes.LOWONGAN_MAGANG) },
                onLowonganClick = { item ->
                    navController.navigate(Routes.detailMagang(item.id))
                },
                onLihatLamaranClick = { navController.navigate(Routes.LAMARAN_SAYA) },
                onStatusClick = { navController.navigate(Routes.LAMARAN_SAYA) },
                onLamaranItemClick = { item ->
                    navController.navigate(Routes.statusLamaran(item.id))
                },
                onProfileClick = { navController.navigate(Routes.MAHASISWA_PROFIL) },
                onNotificationClick = { navController.navigate(Routes.MAHASISWA_NOTIFIKASI) },
                onNavTabClick = { tab ->
                    when (tab) {
                        MagangNavTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        MagangNavTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        MagangNavTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                        else -> {}
                    }
                }
            )
        }

        // Halaman Daftar Lowongan Magang (Lowongan.png)
        composable(Routes.LOWONGAN_MAGANG) {
            LowonganScreen(
                bookmarkedIds = magangState.bookmarkedIds,
                onBookmarkToggle = { lowongan ->
                    magangViewModel.toggleBookmark(lowongan.id)
                },
                onBackClick = { navController.popBackStack() },
                onLowonganClick = { item ->
                    navController.navigate(Routes.detailMagang(item.id))
                }
            )
        }

        // Halaman Detail Magang (Detail Magang.png)
        composable(
            route = Routes.DETAIL_MAGANG_ROUTE,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id").orEmpty()
            val targetId = if (idArg.isNotBlank()) idArg else (magangState.lowonganList.firstOrNull()?.id ?: "")

            LaunchedEffect(targetId) {
                if (targetId.isNotBlank()) {
                    magangViewModel.loadDetailLowongan(targetId)
                }
            }

            val lowongan = magangState.selectedLowongan ?: magangState.lowonganList.find { it.id == targetId }
            val detailData = if (lowongan != null) {
                DetailMagangData(
                    id = lowongan.id,
                    posisi = lowongan.posisi.uppercase(),
                    perusahaan = lowongan.namaPerusahaan,
                    lokasi = lowongan.kota,
                    durasi = lowongan.periodeMagang,
                    status = "Available",
                    deskripsiPekerjaan = lowongan.deskripsiPekerjaan,
                    persyaratan = lowongan.persyaratan.split("\n", ",").map { it.trim() }.filter { it.isNotEmpty() },
                    periodePendaftaran = "s/d ${lowongan.batasPendaftaran}"
                )
            } else {
                DetailMagangData()
            }

            DetailMagangScreen(
                data = detailData,
                isBookmarked = magangState.bookmarkedIds.contains(detailData.id),
                onBackClick = { navController.popBackStack() },
                onBookmarkToggle = { magangViewModel.toggleBookmark(detailData.id) },
                onLamarSekarangClick = { navController.navigate(Routes.FORMULIR_LAMARAN) }
            )
        }

        // Fallback route lama tanpa query
        composable(Routes.DETAIL_MAGANG) {
            val targetId = magangState.lowonganList.firstOrNull()?.id ?: ""
            LaunchedEffect(targetId) {
                if (targetId.isNotBlank()) {
                    magangViewModel.loadDetailLowongan(targetId)
                }
            }
            DetailMagangScreen(
                onBackClick = { navController.popBackStack() },
                onLamarSekarangClick = { navController.navigate(Routes.FORMULIR_LAMARAN) }
            )
        }

        // Formulir Lamaran Magang (Step 1, 2, 3, 4)
        composable(Routes.FORMULIR_LAMARAN) {
            val lowongan = magangState.selectedLowongan
            val posisi = lowongan?.posisi ?: "UI/UX Designer Intern"
            val perusahaan = lowongan?.namaPerusahaan ?: "PT Teknologi Nusantara"

            FormulirLamaranScreen(
                posisiJudul = posisi,
                perusahaan = perusahaan,
                onBackClick = { navController.popBackStack() },
                onCekStatusLamaran = {
                    navController.navigate(Routes.STATUS_LAMARAN) {
                        popUpTo(Routes.MAGANG) { inclusive = false }
                    }
                },
                onKembaliKeBeranda = {
                    navController.navigate(Routes.MAGANG) {
                        popUpTo(Routes.MAGANG) { inclusive = true }
                    }
                }
            )
        }

        // Riwayat Lamaran Saya (Lamaran Saya.png)
        composable(Routes.LAMARAN_SAYA) {
            LamaranSayaScreen(
                onBackClick = { navController.popBackStack() },
                onStatusClick = { item ->
                    if (item.status == "Diterima") {
                        navController.navigate(Routes.STATUS_LAMARAN_FINAL)
                    } else {
                        navController.navigate(Routes.STATUS_LAMARAN)
                    }
                }
            )
        }

        // Status Lamaran
        composable(Routes.STATUS_LAMARAN) {
            val lamaran = magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()
            val isPerluPerbaikan = lamaran?.status == "perlu_perbaikan"
            val isDiterima = lamaran?.status == "diterima"

            StatusLamaranScreen(
                posisi = lamaran?.namaLengkap ?: "UI/UX Designer Intern",
                nomorLamaran = lamaran?.nomorLamaran ?: "LNT-MG-2026-0891",
                needsCorrection = isPerluPerbaikan,
                isFinalAccepted = isDiterima,
                onBackClick = { navController.popBackStack() },
                onPerbaikiDokumenClick = { navController.navigate(Routes.PERBAIKAN_DOKUMEN) },
                onInformasiPenerimaanClick = { navController.navigate(Routes.INFORMASI_PENERIMAAN) }
            )
        }

        // Status Lamaran - Final Diterima (Status Lamaran Final.png)
        composable(Routes.STATUS_LAMARAN_FINAL) {
            StatusLamaranScreen(
                needsCorrection = false,
                isFinalAccepted = true,
                onBackClick = { navController.popBackStack() },
                onInformasiPenerimaanClick = { navController.navigate(Routes.INFORMASI_PENERIMAAN_FINAL) }
            )
        }

        // Informasi Penerimaan Sementara (Informasi Penerimaan.png)
        composable(Routes.INFORMASI_PENERIMAAN) {
            InformasiPenerimaanScreen(
                isFinal = false,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Informasi Penerimaan Final (Informasi Penerimaan Final.png)
        composable(Routes.INFORMASI_PENERIMAAN_FINAL) {
            InformasiPenerimaanScreen(
                isFinal = true,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Perbaikan Dokumen (Perbaikan Dokumen.png)
        composable(Routes.PERBAIKAN_DOKUMEN) {
            PerbaikanDokumenScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { navController.navigate(Routes.PERBAIKAN_TERKIRIM) }
            )
        }

        // Perbaikan Dokumen Terkirim (Perbaikan Terkirim.png)
        composable(Routes.PERBAIKAN_TERKIRIM) {
            PerbaikanTerkirimScreen(
                onKembaliKeStatus = {
                    navController.navigate(Routes.STATUS_LAMARAN) {
                        popUpTo(Routes.STATUS_LAMARAN) { inclusive = true }
                    }
                }
            )
        }

        // ====================================================================
        // Fitur Pengaturan & Profil Mahasiswa
        // ====================================================================

        // Halaman Utama Profil & Pengaturan (MahasiswaSettings.kt)
        composable(Routes.MAHASISWA_PROFIL) {
            val user = profilState.profile
            val nama = user?.namaLengkap ?: "Budi Santoso"
            val nim = user?.nim ?: "2024010001"
            val pt = user?.perguruanTinggi ?: "Universitas Lentera"
            val prodi = user?.programStudi ?: "Teknik Informatika"
            val status = "Mahasiswa ${user?.statusAkun ?: "aktif"}"

            MahasiswaSettings().mhsProfilScreen(
                namaLengkap = nama,
                nim = nim,
                kampusProdi = "$pt - $prodi",
                statusAkun = status,
                onNavigateToDataDiri = { navController.navigate(Routes.MAHASISWA_DATA_DIRI) },
                onNavigateToDokumen = { navController.navigate(Routes.MAHASISWA_DOKUMEN_SAYA) },
                onNavigateToNotifikasi = { navController.navigate(Routes.MAHASISWA_NOTIFIKASI) },
                onNavigateToBantuan = { navController.navigate(Routes.MAHASISWA_BANTUAN) },
                onNavigateToTentang = { navController.navigate(Routes.MAHASISWA_TENTANG) },
                onLogoutConfirmed = {
                    profilViewModel.logout {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> { /* Tetap di halaman profil */ }
                    }
                }
            )
        }

        // Halaman Data Diri (DataDiriScreen.kt)
        composable(Routes.MAHASISWA_DATA_DIRI) {
            DataDiriScreen(
                userProfile = profilState.profile,
                onBackClick = { navController.popBackStack() },
                onSimpanDataDiri = { nama, telp ->
                    profilViewModel.updateProfil(nama, telp)
                },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                }
            )
        }

        // Halaman Dokumen Saya (DokumenSayaScreen.kt)
        composable(Routes.MAHASISWA_DOKUMEN_SAYA) {
            DokumenSayaScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                }
            )
        }

        // Halaman Notifikasi Mahasiswa (NotifikasiScreen.kt)
        composable(Routes.MAHASISWA_NOTIFIKASI) {
            MahasiswaNotifikasiScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                }
            )
        }

        // Halaman Bantuan (BantuanScreen.kt)
        composable(Routes.MAHASISWA_BANTUAN) {
            BantuanScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                }
            )
        }

        // Halaman Tentang Aplikasi (TentangAplikasiScreen.kt)
        composable(Routes.MAHASISWA_TENTANG) {
            TentangAplikasiScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardPlaceholderScreen(
    onLogout: () -> Unit = {},
    onOpenSiaga: () -> Unit = {},
    onOpenMagang: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_lentera_logo),
                contentDescription = "Logo Lentera",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Selamat Datang di Lentera!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dashboard sedang dalam pengembangan.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = SlateGray
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onOpenSiaga,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SiagaTealPrimary
                )
            ) {
                Text("Buka Lentera Siaga")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenMagang,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MagangAmberPrimary
                )
            ) {
                Text("Buka Magang Hub", color = NavyDark)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyMedium
                )
            ) {
                Text("Keluar (Logout)")
            }
        }
    }
}
