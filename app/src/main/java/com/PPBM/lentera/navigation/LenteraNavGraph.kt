package com.PPBM.lentera.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.PPBM.lentera.R
import com.PPBM.lentera.features.auth.*
import com.PPBM.lentera.features.siaga.*
import com.PPBM.lentera.features.magang.*
import com.PPBM.lentera.ui.theme.*

/**
 * Navigation graph utama untuk alur autentikasi.
 * Splash → Login → (Lupa Password → Verifikasi → Reset Password)
 *                → Dashboard Placeholder
 */
@Composable
fun LenteraNavGraph(navController: NavHostController) {
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
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD_PLACEHOLDER) {
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
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD_PLACEHOLDER) { inclusive = true }
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

        // Lentera Siaga Mahasiswa
        composable(Routes.SIAGA) {
            MahasiswaSiagaScreen(
                onCreateReportClick = {
                    navController.navigate(Routes.BUAT_LAPORAN)
                },
                onViewAllReportsClick = {
                    navController.navigate(Routes.RIWAYAT_LAPORAN)
                },
                onReportClick = { item ->
                    if (item.status == LaporanStatus.SELESAI) {
                        navController.navigate(Routes.DETAIL_LAPORAN)
                    } else {
                        navController.navigate(Routes.DETAIL_LAPORAN_TRACKING)
                    }
                },
                onNavTabClick = { tab ->
                    when (tab) {
                        SiagaNavTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
                        SiagaNavTab.BERANDA -> navController.navigate(Routes.DASHBOARD_PLACEHOLDER)
                        else -> {}
                    }
                }
            )
        }

        // Halaman Buat Laporan (Langkah 2 dari 4)
        composable(Routes.BUAT_LAPORAN) {
            BuatLaporanScreen(
                onBackClick = { navController.popBackStack() },
                onKembaliKeKategoriClick = { navController.popBackStack() },
                onLanjutKeBuktiClick = { data ->
                    navController.navigate(Routes.UPLOAD_BUKTI)
                }
            )
        }

        // Halaman Upload Bukti (Langkah 3 dari 4)
        composable(Routes.UPLOAD_BUKTI) {
            UploadBuktiScreen(
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onLanjutClick = { files ->
                    navController.navigate(Routes.TINJAU_LAPORAN)
                }
            )
        }

        // Halaman Tinjau Laporan (Langkah 4 dari 4)
        composable(Routes.TINJAU_LAPORAN) {
            TinjauLaporanScreen(
                onBackClick = { navController.popBackStack() },
                onUbahKategori = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahJudul = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahTanggal = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahLokasi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahDeskripsi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahBukti = { navController.popBackStack(Routes.UPLOAD_BUKTI, false) },
                onKirimLaporan = { finalData ->
                    navController.navigate(Routes.LAPORAN_TERKIRIM) {
                        popUpTo(Routes.SIAGA) { inclusive = false }
                    }
                }
            )
        }

        // Halaman Konfirmasi Laporan Terkirim
        composable(Routes.LAPORAN_TERKIRIM) {
            LaporanTerkirimScreen(
                onLihatStatusClick = {
                    navController.navigate(Routes.DETAIL_LAPORAN_TRACKING) {
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
        composable(Routes.DETAIL_LAPORAN_TRACKING) {
            DetailLaporanTrackingScreen(
                onBackClick = { navController.popBackStack() },
                onLengkapiInformasiClick = {
                    navController.navigate(Routes.LENGKAPI_INFORMASI)
                }
            )
        }

        // Halaman Lengkapi Informasi Tambahan
        composable(Routes.LENGKAPI_INFORMASI) {
            LengkapiInformasiScreen(
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onKirimInformasiClick = { tambahan ->
                    navController.navigate(Routes.INFORMASI_TERKIRIM) {
                        popUpTo(Routes.DETAIL_LAPORAN_TRACKING) { inclusive = false }
                    }
                }
            )
        }

        // Halaman Konfirmasi Informasi Tambahan Berhasil Dikirim
        composable(Routes.INFORMASI_TERKIRIM) {
            InformasiTerkirimScreen(
                onBackClick = { navController.popBackStack() },
                onLihatStatusClick = {
                    navController.navigate(Routes.DETAIL_LAPORAN_TRACKING) {
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
            HasilPenangananScreen(
                onBackClick = { navController.popBackStack() },
                onTinjauDokumentasiClick = {
                    navController.navigate(Routes.DOKUMENTASI)
                },
                onKembaliKeRiwayatClick = { navController.popBackStack() }
            )
        }

        // Halaman Riwayat Laporan (Laporan Saya)
        composable(Routes.RIWAYAT_LAPORAN) {
            RiwayatLaporanScreen(
                onBackClick = { navController.popBackStack() },
                onReportClick = { item ->
                    if (item.isSelesai) {
                        navController.navigate(Routes.DETAIL_LAPORAN)
                    } else {
                        navController.navigate(Routes.DETAIL_LAPORAN_TRACKING)
                    }
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
            DetailLaporanScreen(
                onBackClick = { navController.popBackStack() },
                onLihatHasilPenangananClick = {
                    navController.navigate(Routes.HASIL_PENANGANAN)
                }
            )
        }

        // ====================================================================
        // Fitur Magang
        // ====================================================================

        // Halaman Utama Magang
        composable(Routes.MAGANG) {
            MagangScreen(
                onSearchClick = { navController.navigate(Routes.LOWONGAN_MAGANG) },
                onViewAllLowonganClick = { navController.navigate(Routes.LOWONGAN_MAGANG) },
                onLowonganClick = { navController.navigate(Routes.DETAIL_MAGANG) },
                onNavTabClick = { tab ->
                    when (tab) {
                        MagangNavTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        MagangNavTab.BERANDA -> navController.navigate(Routes.DASHBOARD_PLACEHOLDER)
                        else -> {}
                    }
                }
            )
        }

        // Halaman Daftar Lowongan Magang
        composable(Routes.LOWONGAN_MAGANG) {
            LowonganScreen(
                onBackClick = { navController.popBackStack() },
                onLowonganClick = { navController.navigate(Routes.DETAIL_MAGANG) }
            )
        }

        // Halaman Detail Magang
        composable(Routes.DETAIL_MAGANG) {
            DetailMagangScreen(
                onBackClick = { navController.popBackStack() },
                onLamarSekarangClick = {
                    // Tindakan saat tombol Lamar Sekarang ditekan
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
                painter = painterResource(id = R.drawable.lenteralogo),
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
