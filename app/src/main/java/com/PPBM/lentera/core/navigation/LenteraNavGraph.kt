package com.PPBM.lentera.core.navigation

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.core.*
import com.PPBM.lentera.backend.mahasiswa.model.SubmitLamaranPayload
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.features.admin.AdminViewModel
import com.PPBM.lentera.features.admin.akunmahasiswa.AdminMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.BuatAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.DetailAkunMahasiswaScreen
import com.PPBM.lentera.features.admin.akunmahasiswa.Mahasiswa
import com.PPBM.lentera.features.admin.akunmahasiswa.StatusAkun
import com.PPBM.lentera.features.admin.dashboard.AdminDashboardScreen
import com.PPBM.lentera.features.admin.dashboard.DashboardLaporanItem
import com.PPBM.lentera.features.admin.dashboard.DashboardLowonganItem
import com.PPBM.lentera.features.admin.magang.*
import com.PPBM.lentera.features.admin.notifikasi.AdminNotifikasiScreen
import com.PPBM.lentera.features.admin.notifikasi.KategoriNotifikasi as AdminNotifKategori
import com.PPBM.lentera.features.admin.notifikasi.NotifikasiItem as AdminNotifItem
import com.PPBM.lentera.features.admin.profil.*
import com.PPBM.lentera.features.admin.siaga.*
import com.PPBM.lentera.features.admin.siaga.components.AdminLaporanItem
import com.PPBM.lentera.features.auth.*
import com.PPBM.lentera.features.mahasiswa.dashboard.MahasiswaDashboard
import com.PPBM.lentera.features.mahasiswa.dashboard.MahasiswaDashboardViewModel
import com.PPBM.lentera.features.mahasiswa.magang.*
import com.PPBM.lentera.features.mahasiswa.notifikasi.MahasiswaNotifikasiScreen
import com.PPBM.lentera.features.mahasiswa.profil.*
import com.PPBM.lentera.features.mahasiswa.siaga.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Navigation graph utama aplikasi LENTERA untuk alur autentikasi, mahasiswa, dan admin.
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
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.uiState.collectAsState()

    val navContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var lastPerbaikanMeta by remember { mutableStateOf(Triple("", "", "")) }

    fun showToast(msg: String, isLong: Boolean = true) {
        Toast.makeText(
            navContext,
            msg,
            if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }

    fun openSignedUrl(getUrl: suspend () -> Result<String>) {
        coroutineScope.launch {
            getUrl().onSuccess { url ->
                runCatching {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    navContext.startActivity(intent)
                }.onFailure { err ->
                    showToast("Tidak dapat membuka berkas: ${err.message ?: "periksa aplikasi pembuka berkas."}")
                }
            }.onFailure { err ->
                showToast(err.message ?: "Gagal membuka dokumen.")
            }
        }
    }

    fun navigateAdminTab(item: LenteraNavItem, current: LenteraNavItem? = null) {
        if (item == current) return
        when (item) {
            LenteraNavItem.BERANDA -> navController.navigate(Routes.ADMIN_DASHBOARD)
            LenteraNavItem.MAHASISWA -> navController.navigate(Routes.ADMIN_MAHASISWA)
            LenteraNavItem.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
            LenteraNavItem.MAGANG -> navController.navigate(Routes.ADMIN_MAGANG)
            LenteraNavItem.PROFIL -> navController.navigate(Routes.ADMIN_PROFIL)
        }
    }

    fun navigateMahasiswaTab(tab: LenteraTab, current: LenteraTab? = null) {
        if (tab == current) return
        when (tab) {
            LenteraTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
            LenteraTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
            LenteraTab.MAGANG_HUB -> navController.navigate(Routes.MAGANG)
            LenteraTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
        }
    }

    @Composable
    fun RenderDetailLaporanTracking(targetId: String) {
        LaunchedEffect(targetId) {
            if (targetId.isNotBlank()) {
                siagaViewModel.loadDetailLaporan(targetId)
            }
        }

        val detail = siagaState.detailLaporan?.takeIf { targetId.isBlank() || it.id == targetId || it.nomorLaporan == targetId }
            ?: siagaState.detailLaporan
        val hasPendingAdmin = siagaState.permintaanInfoList.any { it.balasanPelapor.isNullOrBlank() }
        val adminMsg = siagaState.permintaanInfoList
            .firstOrNull { it.balasanPelapor.isNullOrBlank() }
            ?.pesanKePelapor
            .orEmpty()

        val trackingData = if (detail != null) {
            val norm = normalizeLaporanStatus(detail.status)
            val statusLabel = when (norm) {
                "baru" -> "Baru"
                "diperiksa" -> "Diperiksa"
                "diproses" -> "Diproses"
                "selesai" -> "Selesai"
                "ditolak" -> "Ditolak"
                else -> detail.status.replaceFirstChar { it.uppercase() }
            }
            val (diperiksaState, diprosesState) = when (norm) {
                "selesai" -> TimelineStepState.COMPLETED to TimelineStepState.COMPLETED
                "ditolak" -> TimelineStepState.COMPLETED to TimelineStepState.COMPLETED
                "diproses" -> TimelineStepState.COMPLETED to TimelineStepState.ACTIVE
                "diperiksa" -> TimelineStepState.ACTIVE to TimelineStepState.PENDING
                else -> TimelineStepState.ACTIVE to TimelineStepState.PENDING
            }
            val baseSteps = listOf(
                TimelineStepItem(
                    title = "Dikirim",
                    description = "${detail.createdAt.take(10)} · Laporan diterima sistem",
                    state = TimelineStepState.COMPLETED
                ),
                TimelineStepItem(
                    title = "Diperiksa",
                    description = "Tim Satgas meninjau kelengkapan laporan",
                    state = diperiksaState
                ),
                TimelineStepItem(
                    title = if (norm == "ditolak") "Keputusan Verifikasi" else "Ditindaklanjuti",
                    description = if (norm == "ditolak") {
                        "Laporan tidak memenuhi kriteria penanganan lebih lanjut"
                    } else {
                        "Proses penanganan dan koordinasi kasus"
                    },
                    state = diprosesState
                )
            )
            val dynamicSteps = siagaState.timelineList.map { log ->
                TimelineStepItem(
                    title = log.judulTindakan,
                    description = log.createdAt.take(16).replace("T", " "),
                    state = TimelineStepState.COMPLETED
                )
            }
            val closingStep = TimelineStepItem(
                title = if (norm == "ditolak") "Ditutup" else "Selesai",
                description = detail.hasilPenanganan?.takeIf { it.isNotBlank() }
                    ?: "Menunggu ringkasan penyelesaian laporan",
                state = if (norm == "selesai" || norm == "ditolak") {
                    TimelineStepState.COMPLETED
                } else {
                    TimelineStepState.PENDING
                }
            )

            DetailLaporanTrackingData(
                nomorReferensi = detail.nomorLaporan,
                judul = detail.judul,
                status = statusLabel,
                tanggalPengiriman = "Dikirim ${detail.createdAt.take(10)} · ${if (detail.isAnonim) "Laporan anonim" else "Kerahasiaan terjamin"}",
                adminMessage = adminMsg,
                hasAdminRequest = hasPendingAdmin,
                hasilPenanganan = detail.hasilPenanganan,
                isSelesai = norm == "selesai",
                timeline = baseSteps + dynamicSteps + closingStep
            )
        } else {
            DetailLaporanTrackingData()
        }

        DetailLaporanTrackingScreen(
            reportData = trackingData,
            onBackClick = { navController.popBackStack() },
            onLengkapiInformasiClick = {
                navController.navigate(Routes.LENGKAPI_INFORMASI)
            },
            onLihatHasilClick = {
                navController.navigate(Routes.HASIL_PENANGANAN)
            }
        )
    }

    @Composable
    fun RenderDetailMagang(targetId: String) {
        LaunchedEffect(targetId) {
            if (targetId.isNotBlank()) {
                magangViewModel.loadDetailLowongan(targetId)
            }
        }

        val lowongan = magangState.selectedLowongan?.takeIf { targetId.isBlank() || it.id == targetId }
            ?: magangState.lowonganList.find { it.id == targetId }
            ?: magangState.selectedLowongan

        val hasApplied = targetId.isNotBlank() && magangState.lamaranSayaList.any { it.lowonganId == targetId }
        val deadlineNotPassed = lowongan?.batasPendaftaran?.let { raw ->
            parseLocalDateFlexible(raw)?.let { !it.isBefore(LocalDate.now(WITA_ZONE)) } ?: true
        } ?: false
        val canApply = lowongan != null && lowongan.kuota > 0 && deadlineNotPassed

        val detailData = if (lowongan != null) {
            DetailMagangData(
                id = lowongan.id,
                posisi = lowongan.posisi.uppercase(),
                perusahaan = lowongan.namaPerusahaan,
                lokasi = lowongan.kota,
                tipeWaktu = lowongan.sistemKerja,
                durasi = lowongan.periodeMagang,
                status = if (canApply) "Available" else "Tutup",
                batasHari = daysUntilDeadline(lowongan.batasPendaftaran),
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
            hasApplied = hasApplied,
            canApply = canApply,
            onBackClick = { navController.popBackStack() },
            onBookmarkToggle = {
                if (detailData.id.isNotBlank()) {
                    magangViewModel.toggleBookmark(detailData.id)
                }
            },
            onLamarSekarangClick = { navController.navigate(Routes.FORMULIR_LAMARAN) }
        )
    }

    @Composable
    fun RenderStatusLamaran(idArg: String) {
        LaunchedEffect(idArg) {
            if (idArg.isNotBlank()) {
                magangViewModel.loadDetailLamaran(idArg)
            } else {
                magangState.selectedLamaran?.id?.takeIf { it.isNotBlank() }?.let {
                    magangViewModel.loadDetailLamaran(it)
                }
            }
        }

        val lamaran = idArg.takeIf { it.isNotBlank() }?.let { id ->
            magangState.selectedLamaran?.takeIf { it.id == id }
                ?: magangState.lamaranSayaList.find { it.id == id }
        } ?: magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()

        val lowongan = lamaran?.let { lam ->
            magangState.lowonganList.find { it.id == lam.lowonganId }
        }

        StatusLamaranScreen(
            lamaran = lamaran,
            lowongan = lowongan,
            riwayatList = magangState.riwayatStatusList,
            dokumenList = magangState.dokumenLamaranList,
            onOpenDokumen = { doc ->
                openSignedUrl { magangViewModel.getSignedUrl("dokumen-lamaran", doc.filePath) }
            },
            onBackClick = { navController.popBackStack() },
            onPerbaikiDokumenClick = { navController.navigate(Routes.PERBAIKAN_DOKUMEN) },
            onInformasiPenerimaanClick = { navController.navigate(Routes.INFORMASI_PENERIMAAN) }
        )
    }

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
                onLoginSuccess = { role, statusAkun ->
                    val session = SessionManager.getInstance(navController.context)
                    val destination = if (role.equals("admin", ignoreCase = true)) {
                        Routes.ADMIN_DASHBOARD
                    } else if (normalizeStatusAkun(statusAkun) == "belum_aktivasi") {
                        val email = session.getProfile()?.email ?: ""
                        Routes.resetPassword(email)
                    } else {
                        Routes.MAHASISWA_DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    loginViewModel.clearError()
                    navController.navigate(Routes.LUPA_PASSWORD)
                }
            )
        }

        // Lupa Password Screen
        composable(Routes.LUPA_PASSWORD) {
            LupaPasswordScreen(
                isLoading = loginState.isLoading,
                errorMessage = loginState.errorMessage,
                onClearError = { loginViewModel.clearError() },
                onBackToLogin = { navController.popBackStack() },
                onSendReset = { email ->
                    loginViewModel.requestPasswordReset(email) {
                        navController.navigate(Routes.verifikasiPemulihan(email))
                    }
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
                isLoading = loginState.isLoading,
                errorMessage = loginState.errorMessage,
                onClearError = { loginViewModel.clearError() },
                onBackToLupaPassword = { navController.popBackStack() },
                onVerified = { token ->
                    loginViewModel.verifyRecoveryOtp(email, token) {
                        navController.navigate(Routes.resetPassword(email)) {
                            popUpTo(Routes.LUPA_PASSWORD) { inclusive = true }
                        }
                    }
                },
                onResendOtp = {
                    loginViewModel.requestPasswordReset(email)
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
        ) {
            ResetPasswordScreen(
                isLoading = loginState.isLoading,
                serverErrorMessage = loginState.errorMessage,
                onClearError = { loginViewModel.clearError() },
                onBackToVerifikasi = { navController.popBackStack() },
                onPasswordSaved = { newPassword ->
                    loginViewModel.updatePassword(newPassword) {
                        val session = SessionManager.getInstance(navController.context)
                        val profile = session.getProfile()
                        val destination = if (profile != null) {
                            if (profile.role.equals("admin", ignoreCase = true)) {
                                Routes.ADMIN_DASHBOARD
                            } else {
                                Routes.MAHASISWA_DASHBOARD
                            }
                        } else {
                            Routes.LOGIN
                        }
                        navController.navigate(destination) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
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

            LaunchedEffect(Unit) {
                dashboardViewModel.loadDashboardData()
            }

            MahasiswaDashboard().mhsDashboard(
                userName = profile?.namaLengkap?.ifBlank { "Mahasiswa" } ?: "Mahasiswa",
                totalLaporan = dashboardState.totalLaporan,
                laporanDiperiksa = dashboardState.laporanDiperiksa,
                laporanSelesai = dashboardState.laporanSelesai,
                totalLamaran = dashboardState.totalLamaran,
                lamaranStatusText = dashboardState.lamaranStatusSummary,
                aktivitasList = dashboardState.aktivitasTerbaru,
                lowonganList = dashboardState.rekomendasiLowongan,
                onTabSelected = { tab -> navigateMahasiswaTab(tab, LenteraTab.BERANDA) },
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

            val laporanDashboard = adminState.laporanTerbaruList.map { item ->
                val norm = normalizeLaporanStatus(item.status)
                val (bg, txt, label) = when (norm) {
                    "baru" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Baru")
                    "diperiksa" -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "Diperiksa")
                    "diproses" -> Triple(Color(0xFFFFE4E6), Color(0xFFE11D48), "Diproses")
                    "selesai" -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "Selesai")
                    "ditolak" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Ditolak")
                    else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), item.status.replaceFirstChar { it.uppercase() })
                }
                DashboardLaporanItem(
                    id = item.nomorLaporan.ifEmpty { item.id },
                    judul = item.judul,
                    waktu = item.createdAt.take(10),
                    status = label,
                    statusBg = bg,
                    statusColor = txt
                )
            }

            val lowonganDashboard = adminState.lowonganAdminList.take(3).map { item ->
                val mitraName = adminState.tempatMagangList
                    .find { it.id == item.tempatMagangId }
                    ?.namaPerusahaan
                    ?.takeIf { it.isNotBlank() }
                    ?: item.lokasi
                val applicantCount = adminState.lamaranMasukList.count { it.lowonganId == item.id }
                val normLowongan = normalizeLowonganStatus(item.status)
                val (lwgBg, lwgColor, lwgLabel) = when (normLowongan) {
                    "terbit" -> Triple(Color(0xFFE8F4F1), Color(0xFF2F7078), "Terbit")
                    "ditutup" -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Ditutup")
                    else -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Draf")
                }
                DashboardLowonganItem(
                    posisi = item.posisi,
                    mitra = mitraName,
                    totalPelamar = applicantCount,
                    status = lwgLabel,
                    statusBg = lwgBg,
                    statusColor = lwgColor
                )
            }

            AdminDashboardScreen(
                userName = profile?.namaLengkap?.ifBlank { "Admin Lentera" } ?: "Admin Lentera",
                totalMahasiswa = adminState.stats.totalMahasiswaAktif,
                totalLaporan = adminState.stats.totalLaporan,
                totalMitra = adminState.stats.totalTempatMagang,
                totalLowongan = adminState.stats.totalLowonganAktif,
                laporanList = laporanDashboard,
                lowonganList = lowonganDashboard,
                onNavigateToMahasiswa = { navController.navigate(Routes.ADMIN_MAHASISWA) },
                onNavigateToSiaga = { navController.navigate(Routes.ADMIN_SIAGA) },
                onNavigateToMagang = { navController.navigate(Routes.ADMIN_MAGANG) },
                onNavigateToLowongan = { navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN) },
                onNavigateToProfil = { navController.navigate(Routes.ADMIN_PROFIL) },
                onNavigateToNotifikasi = { navController.navigate(Routes.ADMIN_NOTIFIKASI) },
                onLaporanClick = { item ->
                    adminViewModel.selectLaporan(item.id)
                    navController.navigate(Routes.adminDetailLaporan(item.id))
                },
                onLowonganItemClick = { clicked ->
                    val target = adminState.lowonganAdminList.find { it.posisi == clicked.posisi }
                    if (target != null) {
                        adminViewModel.selectLowongan(target)
                        navController.navigate(Routes.ADMIN_DETAIL_LOWONGAN)
                    } else {
                        navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN)
                    }
                },
                onNavClick = { item -> navigateAdminTab(item, LenteraNavItem.BERANDA) }
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
                val statusStr = when (normalizeStatusAkun(mhs.statusAkun)) {
                    "aktif" -> "Aktif"
                    "nonaktif" -> "Nonaktif"
                    "lulus" -> "Lulus"
                    else -> "Belum Aktivasi"
                }
                Triple(mhs.namaLengkap, mhs.nim ?: "-", statusStr)
            }

            AdminMahasiswaScreen(
                studentList = studentsTriple,
                onNavigateToBuatAkun = { navController.navigate(Routes.ADMIN_BUAT_AKUN_MAHASISWA) },
                onNavigateToDetail = { nim ->
                    adminViewModel.selectMahasiswa(nim)
                    navController.navigate(Routes.adminDetailMahasiswa(nim))
                },
                onNavClick = { item -> navigateAdminTab(item, LenteraNavItem.MAHASISWA) }
            )
        }

        // Buat Akun Mahasiswa Baru
        composable(Routes.ADMIN_BUAT_AKUN_MAHASISWA) {
            BuatAkunMahasiswaScreen(
                isSubmitting = adminState.isCreatingMahasiswa,
                onBackClick = { navController.popBackStack() },
                onSimpanSuccess = { navController.popBackStack() },
                onSimpanAkun = { nama, nim, email, jurusan, prodi, angkatan, onSuccess, onError ->
                    adminViewModel.buatAkunMahasiswa(
                        namaLengkap = nama,
                        nim = nim,
                        email = email,
                        jurusan = jurusan,
                        programStudi = prodi,
                        angkatan = angkatan,
                        onError = onError,
                        onSuccess = { _, tempPass -> onSuccess(tempPass) }
                    )
                },
                onNavClick = { item -> navigateAdminTab(item) }
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
                if (adminState.mahasiswaList.isEmpty()) {
                    adminViewModel.loadMahasiswaList()
                }
                if (idArg.isNotBlank()) {
                    adminViewModel.selectMahasiswa(idArg)
                }
            }

            var lastKnownMhs by remember(idArg) {
                mutableStateOf<com.PPBM.lentera.backend.auth.model.UserProfile?>(null)
            }
            val foundMhs = adminState.selectedMahasiswa?.takeIf {
                idArg.isBlank() || it.id == idArg || it.nim == idArg
            } ?: adminState.mahasiswaList.find { it.id == idArg || it.nim == idArg }
                ?: adminState.selectedMahasiswa
            if (foundMhs != null) {
                lastKnownMhs = foundMhs
            }
            val mhs = foundMhs ?: lastKnownMhs

            val statusEnum = when (normalizeStatusAkun(mhs?.statusAkun.orEmpty())) {
                "aktif" -> StatusAkun.AKTIF
                "nonaktif" -> StatusAkun.NONAKTIF
                "lulus" -> StatusAkun.LULUS
                else -> StatusAkun.BELUM_AKTIVASI
            }
            val mahasiswaObj = if (mhs != null) {
                Mahasiswa(
                    nama = mhs.namaLengkap,
                    nim = mhs.nim ?: "-",
                    prodi = mhs.programStudi?.ifBlank { "-" } ?: "-",
                    status = statusEnum
                )
            } else {
                Mahasiswa(
                    nama = "-",
                    nim = idArg.ifBlank { "-" },
                    prodi = "-",
                    status = StatusAkun.BELUM_AKTIVASI
                )
            }

            DetailAkunMahasiswaScreen(
                mahasiswa = mahasiswaObj,
                perguruanTinggi = mhs?.perguruanTinggi?.ifBlank { "-" } ?: "-",
                programStudi = mhs?.programStudi?.ifBlank { "-" } ?: "-",
                angkatan = mhs?.angkatan?.ifBlank { "-" } ?: "-",
                emailKampus = mhs?.email?.ifBlank { "-" } ?: "-",
                metodeAkses = "Kirim tautan aktivasi ke email",
                isProcessing = adminState.isLoading,
                onBackClick = { navController.popBackStack() },
                onKirimUlangTautanClick = { onSuccess, onError ->
                    val em = mhs?.email.orEmpty()
                    if (em.isNotBlank()) {
                        adminViewModel.resendActivationLink(em, onError, onSuccess)
                    } else {
                        onError("Email mahasiswa belum terdaftar.")
                    }
                },
                onEditSave = { nama, nim, email, pt, prodi, angk, onSuccess, onError ->
                    val uid = mhs?.id
                    if (!uid.isNullOrBlank()) {
                        adminViewModel.editMahasiswa(
                            userId = uid,
                            namaLengkap = nama,
                            nim = nim,
                            email = email,
                            perguruanTinggi = pt,
                            programStudi = prodi,
                            angkatan = angk,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    } else {
                        onError("ID akun mahasiswa tidak ditemukan.")
                    }
                },
                onToggleStatusClick = { nextStatus, onSuccess, onError ->
                    val uid = mhs?.id
                    if (!uid.isNullOrBlank()) {
                        adminViewModel.toggleStatusMahasiswa(uid, nextStatus, onError, onSuccess)
                    } else {
                        onError("ID akun mahasiswa tidak ditemukan.")
                    }
                },
                onDeleteClick = { onSuccess, onError ->
                    val uid = mhs?.id
                    if (!uid.isNullOrBlank()) {
                        adminViewModel.deleteMahasiswa(uid, onError, onSuccess)
                    } else {
                        onError("ID akun mahasiswa tidak ditemukan.")
                    }
                },
                onAfterDelete = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
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
            val identifier = profile?.nim?.takeIf { it.isNotBlank() }
                ?: profile?.email?.takeIf { it.isNotBlank() }
                ?: "-"

            ProfilAdminScreen(
                nama = profile?.namaLengkap?.ifBlank { "Admin Lentera" } ?: "Admin Lentera",
                peran = "$identifier · Admin Kampus",
                badge = if (normalizeStatusAkun(profile?.statusAkun.orEmpty()) == "aktif") {
                    "Admin aktif"
                } else {
                    "Admin terdaftar"
                },
                onMenuClick = { menu ->
                    when (menu.judul) {
                        "Data Akun" -> navController.navigate(Routes.ADMIN_DATA_AKUN)
                        "Notifikasi" -> navController.navigate(Routes.ADMIN_NOTIFIKASI)
                        "Bantuan Admin" -> navController.navigate(Routes.ADMIN_BANTUAN)
                        "Tentang Aplikasi" -> navController.navigate(Routes.MAHASISWA_TENTANG)
                    }
                },
                onKeluarClick = {
                    adminViewModel.logout(context) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavClick = { item -> navigateAdminTab(item, LenteraNavItem.PROFIL) }
            )
        }

        // Data Akun Admin
        composable(Routes.ADMIN_DATA_AKUN) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            var currentProfile by remember { mutableStateOf(session.getProfile()) }

            DataAkunAdminScreen(
                nama = currentProfile?.namaLengkap?.ifBlank { "Admin Lentera" } ?: "Admin Lentera",
                peran = "Admin Kampus",
                email = currentProfile?.email,
                nomorTelepon = currentProfile?.noTelepon,
                isSaving = adminState.isLoading,
                onBackClick = { navController.popBackStack() },
                onDataUpdated = { newNama, newEmail, newTelepon, onSuccess, onError ->
                    adminViewModel.updateAdminProfile(
                        context = context,
                        namaLengkap = newNama,
                        email = newEmail,
                        noTelepon = newTelepon,
                        onError = onError,
                        onSuccess = { updated ->
                            currentProfile = updated
                            onSuccess()
                        }
                    )
                },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Keamanan Akun Admin
        composable(Routes.ADMIN_KEAMANAN_AKUN) {
            KeamananAkunScreen(
                onBackClick = { navController.popBackStack() },
                onPerangkatTerhubungClick = { navController.navigate(Routes.ADMIN_PERANGKAT_TERHUBUNG) },
                onRiwayatLoginClick = { navController.navigate(Routes.ADMIN_RIWAYAT_LOGIN) },
                onEmailPemulihanClick = { navController.navigate(Routes.ADMIN_EMAIL_PEMULIHAN) },
                onNavClick = { item -> navigateAdminTab(item) }
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
                currentEmail = profile?.email.orEmpty(),
                onBackClick = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Bantuan Admin
        composable(Routes.ADMIN_BANTUAN) {
            BantuanAdminScreen(
                onBackClick = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Notifikasi Admin (diturunkan langsung dari status data operasional real)
        composable(Routes.ADMIN_NOTIFIKASI) {
            LaunchedEffect(Unit) {
                adminViewModel.loadDashboardData()
            }

            val hariIniItems = buildList {
                val baruCount = adminState.semuaLaporanList.count { normalizeLaporanStatus(it.status) == "baru" }
                if (baruCount > 0) {
                    add(
                        AdminNotifItem(
                            judul = "Laporan masuk perlu verifikasi",
                            isi = "$baruCount laporan baru menunggu peninjauan tim LENTERA Siaga.",
                            meta = "LENTERA Siaga · Perlu Tindakan",
                            kategori = AdminNotifKategori.LAPORAN,
                            iconRes = R.drawable.ic_category,
                            belumDibaca = true
                        )
                    )
                }
                val lamaranPending = adminState.lamaranMasukList.count {
                    val norm = normalizeLamaranStatus(it.status)
                    norm == "terkirim" || norm == "pemeriksaan"
                }
                if (lamaranPending > 0) {
                    add(
                        AdminNotifItem(
                            judul = "Lamaran magang perlu validasi",
                            isi = "$lamaranPending berkas lamaran mahasiswa menunggu verifikasi admin.",
                            meta = "Magang Hub · Lamaran Masuk",
                            kategori = AdminNotifKategori.MAGANG,
                            iconRes = R.drawable.ic_category_2,
                            belumDibaca = true
                        )
                    )
                }
                val lowonganDraf = adminState.lowonganAdminList.count { normalizeLowonganStatus(it.status) == "draf" }
                if (lowonganDraf > 0) {
                    add(
                        AdminNotifItem(
                            judul = "Draf lowongan belum diterbitkan",
                            isi = "$lowonganDraf draf lowongan siap ditinjau untuk publikasi ke mahasiswa.",
                            meta = "Magang Hub · Draf Lowongan",
                            kategori = AdminNotifKategori.MAGANG,
                            iconRes = R.drawable.ic_category_2,
                            belumDibaca = true
                        )
                    )
                }
            }

            val sebelumnyaItems = buildList {
                val aktifLaporan = adminState.semuaLaporanList.count {
                    val norm = normalizeLaporanStatus(it.status)
                    norm == "diperiksa" || norm == "diproses"
                }
                if (aktifLaporan > 0) {
                    add(
                        AdminNotifItem(
                            judul = "Laporan sedang ditangani",
                            isi = "$aktifLaporan laporan berada pada tahap pemeriksaan atau proses penyelesaian.",
                            meta = "LENTERA Siaga · Aktif",
                            kategori = AdminNotifKategori.LAPORAN,
                            iconRes = R.drawable.ic_category_3,
                            belumDibaca = false
                        )
                    )
                }
                val mitraAktif = adminState.tempatMagangList.count { normalizeTempatStatus(it.status) == "aktif" }
                if (mitraAktif > 0) {
                    add(
                        AdminNotifItem(
                            judul = "Mitra tempat magang aktif",
                            isi = "$mitraAktif mitra tempat magang terdaftar dalam status aktif.",
                            meta = "Magang Hub · Mitra Terdaftar",
                            kategori = AdminNotifKategori.MAGANG,
                            iconRes = R.drawable.ic_category_2,
                            belumDibaca = false
                        )
                    )
                }
            }

            AdminNotifikasiScreen(
                hariIni = hariIniItems,
                sebelumnya = sebelumnyaItems,
                onBackClick = { navController.popBackStack() },
                onAvatarClick = { navController.navigate(Routes.ADMIN_PROFIL) },
                onNotifikasiClick = { notif ->
                    when (notif.kategori) {
                        AdminNotifKategori.LAPORAN -> navController.navigate(Routes.ADMIN_SIAGA)
                        AdminNotifKategori.MAGANG -> {
                            when {
                                notif.judul.contains("lamaran", ignoreCase = true) ->
                                    navController.navigate(Routes.ADMIN_KELOLA_LAMARAN)
                                notif.judul.contains("lowongan", ignoreCase = true) ->
                                    navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN)
                                else -> navController.navigate(Routes.ADMIN_MAGANG)
                            }
                        }
                        AdminNotifKategori.SEMUA -> {}
                    }
                },
                onNavClick = { item -> navigateAdminTab(item) }
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

            val laporanAdminItems = adminState.semuaLaporanList.map { lap ->
                val norm = normalizeLaporanStatus(lap.status)
                val (bgCol, txtCol, label) = when (norm) {
                    "baru" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Baru")
                    "diperiksa" -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "Diperiksa")
                    "diproses" -> Triple(Color(0xFFFFE4E6), Color(0xFFE11D48), "Diproses")
                    "selesai" -> Triple(Color(0xFFE8F4F1), Color(0xFF2F7078), "Selesai")
                    "ditolak" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Ditolak")
                    else -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), lap.status.replaceFirstChar { it.uppercase() })
                }
                AdminLaporanItem(
                    id = lap.nomorLaporan,
                    judul = lap.judul,
                    tanggal = "${lap.tanggalKejadian} · ${if (lap.isAnonim) "Anonim" else "Terverifikasi"}",
                    status = label,
                    statusBgColor = bgCol,
                    statusTextColor = txtCol,
                    uuid = lap.id
                )
            }

            AdminSiagaFlowScreen(
                laporanList = laporanAdminItems,
                selectedLaporan = adminState.selectedLaporan,
                buktiList = adminState.selectedLaporanBukti,
                logList = adminState.selectedLaporanLogs,
                permintaanList = adminState.selectedLaporanPermintaan,
                dokumenHasilList = adminState.selectedLaporanDokumenHasil,
                isUpdating = adminState.isUpdatingLaporan,
                onSelectLaporanId = { nomorOrId ->
                    adminViewModel.selectLaporan(nomorOrId)
                },
                onUpdateStatusLaporan = { lapId, st, unit, jalur, cat, onSuccess ->
                    adminViewModel.updateStatusLaporan(
                        laporanId = lapId,
                        status = st,
                        unitPenanganan = unit,
                        jalurTindakLanjut = jalur,
                        catatanPenugasan = cat,
                        onError = { showToast(it) },
                        onSuccess = onSuccess
                    )
                },
                onMintaInformasi = { lapId, pesan, onSuccess ->
                    adminViewModel.mintaInformasi(
                        laporanId = lapId,
                        pesanKePelapor = pesan,
                        onError = { showToast(it) },
                        onSuccess = onSuccess
                    )
                },
                onAddCatatanTindakan = { lapId, judul, cat, onSuccess ->
                    adminViewModel.addCatatanTindakan(
                        laporanId = lapId,
                        judul = judul,
                        deskripsi = cat,
                        onError = { showToast(it) },
                        onSuccess = onSuccess
                    )
                },
                onSelesaikanLaporan = { lapId, ringkasan, pub, onSuccess ->
                    adminViewModel.selesaikanLaporan(
                        laporanId = lapId,
                        hasilPenanganan = ringkasan,
                        hasilDipublikasi = pub,
                        onError = { showToast(it) },
                        onSuccess = onSuccess
                    )
                },
                onSelesaikanLaporanLengkap = { lapId, ringkasan, pub, attachments, onSuccess ->
                    adminViewModel.selesaikanLaporan(
                        laporanId = lapId,
                        hasilPenanganan = ringkasan,
                        hasilDipublikasi = pub,
                        attachments = attachments,
                        onError = { showToast(it) },
                        onSuccess = onSuccess
                    )
                },
                onOpenBukti = { item ->
                    openSignedUrl { adminViewModel.getSignedUrl("bukti-laporan", item.filePath) }
                },
                onOpenDokumen = { item ->
                    openSignedUrl { adminViewModel.getSignedUrl("dokumen-hasil", item.filePath) }
                },
                onBackToDashboard = { navController.navigate(Routes.ADMIN_DASHBOARD) },
                onNavClick = { item -> navigateAdminTab(item, LenteraNavItem.LAPORAN) }
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
                laporan = adminState.selectedLaporan,
                buktiList = adminState.selectedLaporanBukti,
                logList = adminState.selectedLaporanLogs,
                permintaanList = adminState.selectedLaporanPermintaan,
                onOpenBukti = { item ->
                    openSignedUrl { adminViewModel.getSignedUrl("bukti-laporan", item.filePath) }
                },
                onNavigateToVerifikasi = { navController.navigate(Routes.ADMIN_SIAGA) },
                onNavigateToMintaInformasi = { navController.navigate(Routes.ADMIN_MINTA_INFORMASI) },
                onNavigateToPenugasan = { navController.navigate(Routes.ADMIN_PENUGASAN_LAPORAN) },
                onNavigateToCatatanTindakan = { navController.navigate(Routes.ADMIN_CATATAN_TINDAKAN) },
                onNavigateToHasilPenyelesaian = { navController.navigate(Routes.ADMIN_HASIL_PENYELESAIAN) },
                onBackToList = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Minta Informasi Tambahan ke Pelapor
        composable(Routes.ADMIN_MINTA_INFORMASI) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            MintaInformasiScreen(
                laporan = selectedLaporan,
                isUpdating = adminState.isUpdatingLaporan,
                onKirimPermintaan = { pesan ->
                    val lapId = selectedLaporan?.id
                    if (!lapId.isNullOrBlank()) {
                        adminViewModel.mintaInformasi(
                            laporanId = lapId,
                            pesanKePelapor = pesan,
                            onError = { showToast(it) },
                            onSuccess = { navController.popBackStack() }
                        )
                    } else {
                        showToast("Data laporan tidak ditemukan.", isLong = false)
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Catatan Tindakan Laporan
        composable(Routes.ADMIN_CATATAN_TINDAKAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            CatatanTindakanScreen(
                laporan = selectedLaporan,
                logTindakanList = adminState.selectedLaporanLogs,
                permintaanList = adminState.selectedLaporanPermintaan,
                isUpdating = adminState.isUpdatingLaporan,
                onHasilPenyelesaian = { navController.navigate(Routes.ADMIN_HASIL_PENYELESAIAN) },
                onSimpanCatatan = { catatan ->
                    val lapId = selectedLaporan?.id
                    if (!lapId.isNullOrBlank()) {
                        adminViewModel.addCatatanTindakan(
                            laporanId = lapId,
                            judul = "Catatan Penanganan Admin",
                            deskripsi = catatan,
                            onError = { showToast(it) },
                            onSuccess = { navController.popBackStack() }
                        )
                    } else {
                        showToast("Data laporan tidak ditemukan.", isLong = false)
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Penugasan Laporan
        composable(Routes.ADMIN_PENUGASAN_LAPORAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            PenugasanLaporanScreen(
                laporan = selectedLaporan,
                isUpdating = adminState.isUpdatingLaporan,
                onSimpanPenugasan = { unit, jalur, catatan ->
                    val lapId = selectedLaporan?.id
                    if (!lapId.isNullOrBlank()) {
                        adminViewModel.updateStatusLaporan(
                            laporanId = lapId,
                            status = "diproses",
                            unitPenanganan = unit.ifBlank { "Tim Advokasi Kampus" },
                            jalurTindakLanjut = jalur.ifBlank { "Pemeriksaan dan Pendampingan Tim Advokasi" },
                            catatanPenugasan = catatan.ifBlank { null },
                            onError = { showToast(it) },
                            onSuccess = { navController.navigate(Routes.ADMIN_CATATAN_TINDAKAN) }
                        )
                    } else {
                        showToast("Data laporan tidak ditemukan.", isLong = false)
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Hasil & Penyelesaian Laporan
        composable(Routes.ADMIN_HASIL_PENYELESAIAN) {
            val selectedLaporan = adminState.selectedLaporan ?: adminState.semuaLaporanList.firstOrNull()
            HasilPenyelesaianScreen(
                laporan = selectedLaporan,
                dokumenHasilList = adminState.selectedLaporanDokumenHasil,
                isUpdating = adminState.isUpdatingLaporan,
                onOpenDokumen = { doc ->
                    openSignedUrl { adminViewModel.getSignedUrl("dokumen-hasil", doc.filePath) }
                },
                onSimpanDanSelesaikan = { ringkasan, isBagikan ->
                    val lapId = selectedLaporan?.id
                    if (!lapId.isNullOrBlank()) {
                        adminViewModel.selesaikanLaporan(
                            laporanId = lapId,
                            hasilPenanganan = ringkasan.ifBlank {
                                "Laporan telah selesai ditangani secara resmi oleh Tim Advokasi dan Administrasi Kampus."
                            },
                            hasilDipublikasi = isBagikan,
                            onError = { showToast(it) },
                            onSuccess = {
                                navController.navigate(Routes.ADMIN_SIAGA) {
                                    popUpTo(Routes.ADMIN_SIAGA) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        showToast("Data laporan tidak ditemukan.", isLong = false)
                    }
                },
                onSimpanLengkap = { ringkasan, isBagikan, attachments ->
                    val lapId = selectedLaporan?.id
                    if (!lapId.isNullOrBlank()) {
                        adminViewModel.selesaikanLaporan(
                            laporanId = lapId,
                            hasilPenanganan = ringkasan.ifBlank {
                                "Laporan telah selesai ditangani secara resmi oleh Tim Advokasi dan Administrasi Kampus."
                            },
                            hasilDipublikasi = isBagikan,
                            attachments = attachments,
                            onError = { showToast(it) },
                            onSuccess = {
                                navController.navigate(Routes.ADMIN_SIAGA) {
                                    popUpTo(Routes.ADMIN_SIAGA) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        showToast("Data laporan tidak ditemukan.", isLong = false)
                    }
                },
                onBackToDetail = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // ====================================================================
        // Fitur Magang Hub Admin (Mitra, Lowongan, Validasi Lamaran)
        // ====================================================================

        // Halaman Utama Admin Magang / Tempat Magang
        composable(Routes.ADMIN_MAGANG) {
            LaunchedEffect(Unit) {
                adminViewModel.loadTempatMagangList()
                adminViewModel.loadLowonganAdminList()
                adminViewModel.loadLamaranMasukList()
            }

            TempatMagangScreen(
                mitraList = adminState.tempatMagangList,
                lowonganList = adminState.lowonganAdminList,
                lamaranCount = adminState.lamaranMasukList.size,
                onNavigateToDetailTempat = { tempat ->
                    adminViewModel.selectTempatMagang(tempat)
                    navController.navigate(Routes.ADMIN_DETAIL_TEMPAT_MAGANG)
                },
                onNavigateToTambahTempat = { navController.navigate(Routes.ADMIN_TAMBAH_TEMPAT_MAGANG) },
                onNavigateToLowongan = { navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN) },
                onNavigateToLamaran = { navController.navigate(Routes.ADMIN_KELOLA_LAMARAN) },
                onBack = { navController.navigate(Routes.ADMIN_DASHBOARD) },
                onNavClick = { item -> navigateAdminTab(item, LenteraNavItem.MAGANG) }
            )
        }

        // Tambah Tempat Magang
        composable(Routes.ADMIN_TAMBAH_TEMPAT_MAGANG) {
            TambahTempatMagangScreen(
                isSubmitting = adminState.isSubmittingMagang,
                onSimpanSuccess = { navController.popBackStack() },
                onSimpanTempatMagang = {
                    nama, bidang, cabang, desk, alamat, kota, prov, sk, kontak, email, telp, web,
                    logoFileName, logoBytes, dokumenFileName, dokumenBytes, onError, onSuccess ->
                    val entity = TempatMagangEntity(
                        namaPerusahaan = nama,
                        bidangUsaha = bidang,
                        namaCabang = cabang.ifBlank { "Pusat" },
                        deskripsi = desk,
                        alamatLengkap = alamat,
                        kota = kota,
                        provinsi = prov,
                        sistemKerja = normalizeSistemKerja(sk),
                        namaKontak = kontak,
                        emailKontak = email,
                        teleponKontak = telp,
                        situsWeb = web,
                        status = "aktif"
                    )
                    adminViewModel.tambahTempatMagang(
                        entity = entity,
                        logoFileName = logoFileName,
                        logoBytes = logoBytes,
                        dokumenFileName = dokumenFileName,
                        dokumenBytes = dokumenBytes,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                },
                onBackToTempatMagang = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Detail Tempat Magang
        composable(Routes.ADMIN_DETAIL_TEMPAT_MAGANG) {
            DetailTempatMagangScreen(
                tempat = adminState.selectedTempatMagang,
                dokumenList = adminState.selectedTempatDokumenList,
                onOpenDokumen = { doc ->
                    openSignedUrl { adminViewModel.getSignedUrl("dokumen-mitra", doc.filePath) }
                },
                onNavigateToKelolaStatus = { navController.navigate(Routes.ADMIN_KELOLA_STATUS_TEMPAT) },
                onBackToTempatMagang = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Kelola Status Tempat Magang
        composable(Routes.ADMIN_KELOLA_STATUS_TEMPAT) {
            KelolaStatusTempatScreen(
                tempat = adminState.selectedTempatMagang,
                isSubmitting = adminState.isSubmittingMagang,
                onSimpanStatus = { newStatus, catatan ->
                    val tempatId = adminState.selectedTempatMagang?.id
                    if (tempatId != null) {
                        adminViewModel.updateStatusTempatMagang(
                            tempatId = tempatId,
                            status = newStatus,
                            catatanInternal = catatan,
                            onError = { showToast(it) },
                            onSuccess = { navController.popBackStack() }
                        )
                    } else {
                        showToast("Data tempat magang tidak ditemukan.", isLong = false)
                    }
                },
                onBackToDetailTempat = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Daftar Lowongan Admin
        composable(Routes.ADMIN_DAFTAR_LOWONGAN) {
            LaunchedEffect(Unit) {
                adminViewModel.loadLowonganAdminList()
            }

            val companyNamesById = remember(adminState.tempatMagangList) {
                adminState.tempatMagangList.mapNotNull { t ->
                    t.id?.let { id -> id to t.namaPerusahaan }
                }.toMap()
            }

            DaftarLowonganScreen(
                lowonganList = adminState.lowonganAdminList,
                companyNamesById = companyNamesById,
                isUpdating = adminState.isSubmittingMagang,
                onNavigateToDetailLowongan = { lowongan ->
                    adminViewModel.selectLowongan(lowongan)
                    navController.navigate(Routes.ADMIN_DETAIL_LOWONGAN)
                },
                onNavigateToTambahLowongan = {
                    adminViewModel.clearSelectedLowongan()
                    navController.navigate(Routes.ADMIN_FORM_LOWONGAN)
                },
                onNavigateToUbahLowongan = { lowongan ->
                    adminViewModel.selectLowongan(lowongan)
                    navController.navigate(Routes.ADMIN_FORM_LOWONGAN)
                },
                onTogglePublish = { lowongan, newStatus ->
                    lowongan.id?.let { id ->
                        adminViewModel.updateStatusLowongan(
                            lowonganId = id,
                            status = newStatus,
                            onError = { showToast(it) }
                        )
                    }
                },
                onTutupLowongan = { lowongan ->
                    lowongan.id?.let { id ->
                        adminViewModel.updateStatusLowongan(
                            lowonganId = id,
                            status = "ditutup",
                            onError = { showToast(it) }
                        )
                    }
                },
                onNavigateToLamaran = { navController.navigate(Routes.ADMIN_KELOLA_LAMARAN) },
                onBackToTempatMagang = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Form Tambah / Edit Lowongan
        composable(Routes.ADMIN_FORM_LOWONGAN) {
            LaunchedEffect(Unit) {
                if (adminState.tempatMagangList.isEmpty()) {
                    adminViewModel.loadTempatMagangList()
                }
            }

            val existing = adminState.selectedLowonganAdmin
            FormLowonganScreen(
                tempatMagangList = adminState.tempatMagangList,
                existingLowongan = existing,
                isSubmitting = adminState.isSubmittingMagang,
                onSimpanLowongan = {
                    adminViewModel.clearSelectedLowongan()
                    navController.popBackStack()
                },
                onSubmitLowongan = { tempatId, pos, kat, lok, sk, desk, persy, per, bat, kuo, st, onError, onSuccess ->
                    val entity = LowonganEntity(
                        id = existing?.id,
                        tempatMagangId = tempatId,
                        posisi = pos,
                        kategori = kat,
                        lokasi = lok,
                        sistemKerja = sk,
                        deskripsiPekerjaan = desk,
                        persyaratan = persy,
                        periodeMagang = per,
                        batasPendaftaran = bat,
                        kuota = kuo,
                        status = st
                    )
                    val existingId = existing?.id
                    if (!existingId.isNullOrBlank()) {
                        adminViewModel.updateLowongan(
                            lowonganId = existingId,
                            entity = entity,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    } else {
                        adminViewModel.tambahLowongan(
                            entity = entity,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    }
                },
                onBackToDaftarLowongan = {
                    adminViewModel.clearSelectedLowongan()
                    navController.popBackStack()
                },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Detail Lowongan
        composable(Routes.ADMIN_DETAIL_LOWONGAN) {
            val selectedLowongan = adminState.selectedLowonganAdmin
            val companyName = adminState.tempatMagangList
                .find { it.id == selectedLowongan?.tempatMagangId }
                ?.namaPerusahaan

            DetailLowonganScreen(
                lowongan = selectedLowongan,
                companyName = companyName,
                isSubmitting = adminState.isSubmittingMagang,
                onToggleStatus = { newStatus ->
                    val lowonganId = selectedLowongan?.id
                    if (lowonganId != null) {
                        adminViewModel.updateStatusLowongan(
                            lowonganId = lowonganId,
                            status = newStatus,
                            onError = { showToast(it) },
                            onSuccess = {
                                showToast("Status lowongan diperbarui.", isLong = false)
                                navController.popBackStack()
                            }
                        )
                    } else {
                        showToast("Data lowongan tidak ditemukan.", isLong = false)
                    }
                },
                onNavigateToFormLowongan = { navController.navigate(Routes.ADMIN_FORM_LOWONGAN) },
                onBackToDaftarLowongan = { navController.popBackStack() },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Kelola Lamaran Masuk Mahasiswa
        composable(Routes.ADMIN_KELOLA_LAMARAN) {
            LaunchedEffect(Unit) {
                adminViewModel.loadLamaranMasukList()
                adminViewModel.loadLowonganAdminList()
                adminViewModel.loadMahasiswaList()
            }

            val daftarPelamar = adminState.lamaranMasukList.map { lam ->
                val lowongan = adminState.lowonganAdminList.find { it.id == lam.lowonganId }
                val tempat = adminState.tempatMagangList.find { it.id == lowongan?.tempatMagangId }
                val mhs = adminState.mahasiswaList.find {
                    it.id == lam.mahasiswaId || (!lam.nim.isBlank() && it.nim == lam.nim)
                }
                val statusLabel = when (normalizeLamaranStatus(lam.status)) {
                    "terkirim" -> "Menunggu Verifikasi"
                    "pemeriksaan" -> "Pemeriksaan Berkas"
                    "perlu_perbaikan" -> "Perlu Perbaikan"
                    "seleksi" -> "Seleksi / Wawancara"
                    "diterima" -> "Diterima"
                    "ditolak" -> "Ditolak"
                    else -> "Menunggu Verifikasi"
                }
                PelamarLamaranItem(
                    id = lam.nomorLamaran.ifBlank { lam.id.take(8) },
                    lamaranUuid = lam.id,
                    namaMahasiswa = lam.namaLengkap,
                    nim = lam.nim,
                    jurusan = mhs?.jurusan?.takeIf { it.isNotBlank() } ?: "-",
                    prodi = mhs?.programStudi?.takeIf { it.isNotBlank() } ?: "-",
                    posisi = lowongan?.posisi ?: "-",
                    mitra = tempat?.namaPerusahaan ?: lowongan?.lokasi ?: "-",
                    tanggalLamar = lam.createdAt.take(10),
                    status = statusLabel,
                    catatanRevisi = lam.catatanAdmin.orEmpty()
                )
            }

            KelolaLamaranScreen(
                lamaranList = daftarPelamar,
                onNavigateToDetailValidasi = { pelamar ->
                    adminViewModel.selectPelamar(pelamar)
                    navController.navigate(Routes.ADMIN_DETAIL_VALIDASI_LAMARAN)
                },
                onNavigateToMitra = { navController.navigate(Routes.ADMIN_MAGANG) },
                onNavigateToLowongan = { navController.navigate(Routes.ADMIN_DAFTAR_LOWONGAN) },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // Detail Validasi Berkas Lamaran
        composable(Routes.ADMIN_DETAIL_VALIDASI_LAMARAN) {
            val pelamar = adminState.selectedPelamar ?: PelamarLamaranItem()
            DetailValidasiLamaranScreen(
                pelamar = pelamar,
                dokumenList = adminState.selectedPelamarDokumenList,
                isUpdating = adminState.isSubmittingMagang,
                onOpenDokumen = { doc ->
                    openSignedUrl { adminViewModel.getSignedUrl("dokumen-lamaran", doc.filePath) }
                },
                onBackClick = { navController.popBackStack() },
                onStatusUpdated = { newStatus, newCatatan, onError, onSuccess ->
                    val lamId = pelamar.lamaranUuid.ifBlank { pelamar.id }
                    if (lamId.isNotBlank()) {
                        adminViewModel.validasiLamaranMahasiswa(
                            lamaranId = lamId,
                            status = normalizeLamaranStatus(newStatus),
                            catatanAdmin = newCatatan.ifBlank { null },
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    } else {
                        onError("ID lamaran tidak valid.")
                    }
                },
                onNavClick = { item -> navigateAdminTab(item) }
            )
        }

        // ====================================================================
        // Fitur Lentera Siaga Mahasiswa
        // ====================================================================

        // Lentera Siaga Mahasiswa
        composable(Routes.SIAGA) {
            val context = LocalContext.current
            val session = SessionManager.getInstance(context)
            val profile = session.getProfile()

            LaunchedEffect(Unit) {
                siagaViewModel.loadLaporanSaya()
            }

            val laporanItemList = siagaState.laporanList.map { item ->
                val statusEnum = when (normalizeLaporanStatus(item.status)) {
                    "selesai" -> LaporanStatus.SELESAI
                    "ditolak" -> LaporanStatus.DITOLAK
                    "diproses" -> LaporanStatus.DIPROSES
                    "baru" -> LaporanStatus.BARU
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
                userName = profile?.namaLengkap?.ifBlank { "Mahasiswa" } ?: "Mahasiswa",
                laporanList = laporanItemList,
                onEmergencyHelpClick = {
                    runCatching {
                        navContext.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                    }.onFailure {
                        showToast("Hubungi layanan darurat kampus atau 112 jika membutuhkan bantuan segera.")
                    }
                },
                onCreateReportClick = { kategori ->
                    siagaViewModel.setKategoriByName(kategori.displayName)
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
                        SiagaNavTab.PENGADUAN -> {}
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
                    tanggalWaktu = siagaState.formTanggalWaktu,
                    lokasi = siagaState.formLokasi,
                    deskripsi = siagaState.formKronologi,
                    isAnonim = siagaState.formIsAnonim
                ),
                onBackClick = { navController.popBackStack() },
                onUbahKategoriClick = { navController.popBackStack() },
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
            var pickerError by remember { mutableStateOf<String?>(null) }

            val buktiPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri == null) return@rememberLauncherForActivityResult
                pickerError = null
                try {
                    var fileName = "bukti_${System.currentTimeMillis()}"
                    navContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIdx >= 0) {
                            fileName = cursor.getString(nameIdx) ?: fileName
                        }
                    }
                    val bytes = navContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes == null) {
                        pickerError = "Gagal membaca berkas '$fileName' dari perangkat."
                    } else {
                        siagaViewModel.addBuktiFile(
                            fileName = fileName,
                            fileType = extractShortFileType(fileName),
                            fileBytes = bytes,
                            uri = uri,
                            onError = { err -> pickerError = err }
                        )
                    }
                } catch (e: Exception) {
                    pickerError = "Gagal membaca berkas: ${e.message ?: "kesalahan sistem"}"
                }
            }

            val buktiItems = siagaState.uploadedBuktiList.mapIndexed { index, item ->
                BuktiFileItem(
                    id = index.toString(),
                    fileName = item.fileName,
                    fileType = item.fileType,
                    fileSize = item.fileSizeFormatted,
                    isExcluded = item.isExcluded
                )
            }

            UploadBuktiScreen(
                initialFiles = buktiItems,
                errorMessage = pickerError ?: siagaState.errorMessage,
                onBackClick = { navController.popBackStack() },
                onUploadBoxClick = {
                    pickerError = null
                    siagaViewModel.clearError()
                    buktiPicker.launch("*/*")
                },
                onKembaliClick = { navController.popBackStack() },
                onDeleteFile = { item ->
                    item.id.toIntOrNull()?.let { siagaViewModel.toggleExcludeBukti(it) }
                },
                onRestoreFile = { item ->
                    item.id.toIntOrNull()?.let { siagaViewModel.toggleExcludeBukti(it) }
                },
                onLanjutClick = {
                    navController.navigate(Routes.TINJAU_LAPORAN)
                }
            )
        }

        // Halaman Tinjau Laporan (Langkah 4 dari 4)
        composable(Routes.TINJAU_LAPORAN) {
            val activeBuktiCount = siagaState.uploadedBuktiList.count { !it.isExcluded }
            val reportData = TinjauLaporanData(
                kategori = siagaState.selectedKategoriNama,
                judul = siagaState.formJudul.ifBlank { siagaState.selectedKategoriNama },
                tanggalWaktu = siagaState.formTanggalWaktu,
                lokasi = siagaState.formLokasi,
                deskripsi = siagaState.formKronologi,
                buktiSummary = if (activeBuktiCount == 0) {
                    "Tidak ada lampiran"
                } else {
                    "$activeBuktiCount file bukti disertakan"
                },
                isAnonim = siagaState.formIsAnonim
            )

            TinjauLaporanScreen(
                reportData = reportData,
                isSubmitting = siagaState.isSubmitting,
                errorMessage = siagaState.errorMessage,
                onBackClick = { navController.popBackStack() },
                onUbahKategori = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahJudul = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahTanggal = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahLokasi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahDeskripsi = { navController.popBackStack(Routes.BUAT_LAPORAN, false) },
                onUbahBukti = { navController.popBackStack(Routes.UPLOAD_BUKTI, false) },
                onKirimLaporan = {
                    siagaViewModel.submitLaporan(
                        onError = { err -> showToast(err) },
                        onUploadWarning = { warn -> showToast(warn) }
                    ) { nomorRef ->
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
            val finalRef = refArg.ifBlank { siagaState.submitSuccessNomorLaporan ?: "-" }

            LaporanTerkirimScreen(
                nomorReferensi = finalRef,
                onLihatStatusClick = {
                    val targetId = siagaState.submitSuccessLaporanId
                        ?: siagaState.laporanList.firstOrNull()?.id.orEmpty()
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
                nomorReferensi = siagaState.submitSuccessNomorLaporan ?: "-",
                onLihatStatusClick = {
                    val targetId = siagaState.submitSuccessLaporanId
                        ?: siagaState.laporanList.firstOrNull()?.id.orEmpty()
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
            val targetId = idArg.ifBlank { siagaState.laporanList.firstOrNull()?.id.orEmpty() }
            RenderDetailLaporanTracking(targetId = targetId)
        }

        // Fallback route lama tanpa query
        composable(Routes.DETAIL_LAPORAN_TRACKING) {
            val targetId = siagaState.detailLaporan?.id
                ?: siagaState.laporanList.firstOrNull()?.id.orEmpty()
            RenderDetailLaporanTracking(targetId = targetId)
        }

        // Halaman Lengkapi Informasi Tambahan
        composable(Routes.LENGKAPI_INFORMASI) {
            val pendingPermintaan = siagaState.permintaanInfoList.firstOrNull { it.balasanPelapor.isNullOrBlank() }
            val pesan = pendingPermintaan?.pesanKePelapor.orEmpty()

            LengkapiInformasiScreen(
                kodeLaporan = siagaState.detailLaporan?.nomorLaporan.orEmpty(),
                pesanAdmin = pesan,
                isSubmitting = siagaState.isSubmitting,
                errorMessage = siagaState.errorMessage,
                onBackClick = { navController.popBackStack() },
                onKembaliClick = { navController.popBackStack() },
                onKirimLengkapClick = { balasan, fileName, fileBytes ->
                    val reqId = pendingPermintaan?.id.orEmpty()
                    if (reqId.isBlank()) {
                        showToast("Tidak ada permintaan informasi aktif untuk dibalas.", isLong = false)
                    } else {
                        siagaViewModel.kirimBalasanInformasi(
                            permintaanId = reqId,
                            balasan = balasan,
                            attachmentFileName = fileName,
                            attachmentBytes = fileBytes,
                            onError = { err -> showToast(err) }
                        ) {
                            navController.navigate(Routes.INFORMASI_TERKIRIM) {
                                popUpTo(Routes.DETAIL_LAPORAN_TRACKING) { inclusive = false }
                            }
                        }
                    }
                }
            )
        }

        // Halaman Konfirmasi Informasi Tambahan Berhasil Dikirim
        composable(Routes.INFORMASI_TERKIRIM) {
            InformasiTerkirimScreen(
                nomorLaporan = siagaState.detailLaporan?.nomorLaporan.orEmpty().ifBlank { "-" },
                onBackClick = { navController.popBackStack() },
                onLihatStatusClick = {
                    val targetId = siagaState.detailLaporan?.id.orEmpty()
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
            val detail = siagaState.detailLaporan
            DokumentasiScreen(
                data = DokumentasiData(
                    judul = detail?.judul.orEmpty(),
                    tanggal = detail?.tanggalPenyelesaian ?: detail?.updatedAt?.take(10).orEmpty(),
                    totalFoto = siagaState.dokumenHasilList.size
                ),
                dokumenList = siagaState.dokumenHasilList,
                onBackClick = { navController.popBackStack() },
                onOpenDokumenClick = { doc ->
                    openSignedUrl { siagaViewModel.getDokumenHasilSignedUrl(doc.filePath) }
                }
            )
        }

        // Halaman Hasil Penanganan
        composable(Routes.HASIL_PENANGANAN) {
            val detail = siagaState.detailLaporan
            val dokumentasiItems = siagaState.dokumenHasilList.map { doc ->
                DokumentasiTindakLanjutItem(
                    tanggal = doc.createdAt.take(10),
                    judul = doc.namaFile,
                    filePath = doc.filePath,
                    fileType = doc.tipeFile.uppercase(Locale.US),
                    fileSizeFormatted = if (doc.ukuranBytes >= 1024 * 1024) {
                        String.format(Locale.US, "%.1f MB", doc.ukuranBytes / (1024.0 * 1024.0))
                    } else {
                        "${(doc.ukuranBytes / 1024).coerceAtLeast(1)} KB"
                    }
                )
            }
            val hasilData = if (detail != null) {
                HasilPenangananData(
                    nomorLaporan = detail.nomorLaporan,
                    judul = detail.judul,
                    status = detail.status.replaceFirstChar { it.uppercase() },
                    tanggalLaporan = detail.tanggalKejadian,
                    kategori = detail.kategori,
                    ringkasanPenanganan = detail.hasilPenanganan.orEmpty(),
                    tanggalPenyelesaian = detail.tanggalPenyelesaian ?: detail.updatedAt.take(10),
                    listDokumentasi = dokumentasiItems
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
                onItemClick = { item ->
                    if (item.filePath.isNotBlank()) {
                        openSignedUrl { siagaViewModel.getDokumenHasilSignedUrl(item.filePath) }
                    } else {
                        navController.navigate(Routes.DOKUMENTASI)
                    }
                },
                onKembaliKeRiwayatClick = { navController.popBackStack() }
            )
        }

        // Halaman Riwayat Laporan (Laporan Saya)
        composable(Routes.RIWAYAT_LAPORAN) {
            LaunchedEffect(Unit) {
                siagaViewModel.loadLaporanSaya()
            }

            val riwayatItems = siagaState.laporanList.map { item ->
                val norm = normalizeLaporanStatus(item.status)
                val isSelesai = norm == "selesai"
                val isDitolak = norm == "ditolak"
                val (badgeLabel, badgeBg, badgeText) = when (norm) {
                    "selesai" -> Triple("Selesai", Color(0xFFE3F5EB), Color(0xFF287D4A))
                    "ditolak" -> Triple("Ditolak", Color(0xFFFFEBEE), Color(0xFFC62828))
                    "diproses" -> Triple("Diproses", Color(0xFFE0F2FE), Color(0xFF0369A1))
                    "diperiksa" -> Triple("Diperiksa", Color(0xFFFEF3C7), Color(0xFFB45309))
                    else -> Triple("Baru", Color(0xFFE6B363), Color.White)
                }
                RiwayatLaporanItem(
                    id = item.id,
                    nomorLaporan = item.nomorLaporan,
                    judul = item.judul,
                    kategori = item.kategori,
                    waktu = item.tanggalKejadian,
                    statusBadge = badgeLabel,
                    badgeBgColor = badgeBg,
                    badgeTextColor = badgeText,
                    isSelesai = isSelesai,
                    isDitolak = isDitolak
                )
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
                    listLampiran = siagaState.buktiList.map { b ->
                        LampiranBuktiDetail(
                            nama = b.namaFile,
                            ukuran = "${(b.ukuranBytes / 1024).coerceAtLeast(1)} KB",
                            tipe = when (b.tipeFile.uppercase(Locale.US)) {
                                "JPG", "PNG", "JPEG", "WEBP" -> "PHOTO"
                                "MP3", "WAV", "M4A", "AAC" -> "AUDIO"
                                else -> "DOC"
                            },
                            filePath = b.filePath
                        )
                    },
                    listTimeline = siagaState.timelineList.map { log ->
                        log.judulTindakan to log.createdAt.take(16).replace("T", " ")
                    }
                )
            } else {
                DetailLaporanFullData()
            }

            MahasiswaDetailLaporanScreen(
                data = fullData,
                onBackClick = { navController.popBackStack() },
                onAttachmentClick = { item ->
                    if (item.filePath.isNotBlank()) {
                        openSignedUrl { siagaViewModel.getBuktiSignedUrl(item.filePath) }
                    }
                },
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
            LaunchedEffect(Unit) {
                magangViewModel.loadMagangData()
                profilViewModel.loadData()
            }

            val profile = profilState.profile
            val completionChecks = listOf(
                !profile?.namaLengkap.isNullOrBlank(),
                !profile?.nim.isNullOrBlank(),
                !profile?.noTelepon.isNullOrBlank(),
                !profile?.programStudi.isNullOrBlank(),
                profilState.dokumenList.any { it.jenis.equals("CV", ignoreCase = true) },
                profilState.dokumenList.any { it.jenis.equals("Transkrip", ignoreCase = true) }
            )
            val profilCompletionPct = (completionChecks.count { it } * 100) / completionChecks.size

            val totalLam = magangState.lamaranSayaList.size
            val dtr = magangState.lamaranSayaList.count { normalizeLamaranStatus(it.status) == "diterima" }
            val dtl = magangState.lamaranSayaList.count { normalizeLamaranStatus(it.status) == "ditolak" }
            val drv = magangState.lamaranSayaList.count {
                val norm = normalizeLamaranStatus(it.status)
                norm == "terkirim" || norm == "pemeriksaan" || norm == "perlu_perbaikan" || norm == "seleksi"
            }

            val mappedLamaran = magangState.lamaranSayaList.map { item ->
                val norm = normalizeLamaranStatus(item.status)
                val (label, bg, txt) = when (norm) {
                    "diterima" -> Triple("Diterima", MagangPillGreenBg, MagangPillGreenText)
                    "ditolak" -> Triple("Ditolak", MagangPillRedBg, MagangPillRedText)
                    "perlu_perbaikan" -> Triple("Perlu Perbaikan", MagangAmberPillBg, MagangAmberPillText)
                    "seleksi" -> Triple("Seleksi", MagangPillOrangeBg, MagangPillOrangeText)
                    "pemeriksaan" -> Triple("Pemeriksaan", MagangPillOrangeBg, MagangPillOrangeText)
                    else -> Triple("Terkirim", MagangPillOrangeBg, MagangPillOrangeText)
                }
                val lwg = magangState.lowonganList.find { it.id == item.lowonganId }
                LamaranItemData(
                    id = item.id,
                    posisi = lwg?.posisi ?: "Lamaran #${item.nomorLamaran}",
                    perusahaan = lwg?.namaPerusahaan ?: "Mitra Magang",
                    status = label,
                    statusBg = bg,
                    statusText = txt
                )
            }

            val mappedLowongan = magangState.lowonganList.map { item ->
                LowonganCardData(
                    id = item.id,
                    posisi = item.posisi.uppercase(),
                    perusahaan = item.namaPerusahaan,
                    lokasi = item.kota,
                    durasi = item.periodeMagang,
                    tipe = item.sistemKerja.uppercase(),
                    batasHari = daysUntilDeadline(item.batasPendaftaran),
                    logoDrawable = R.drawable.ic_avatar_2,
                    isBookmarked = magangState.bookmarkedIds.contains(item.id)
                )
            }

            MagangScreen(
                userName = profile?.namaLengkap?.ifBlank { "Mahasiswa" } ?: "Mahasiswa",
                profilCompletion = profilCompletionPct,
                totalLamaran = totalLam,
                jumlahDiterima = dtr,
                jumlahDitolak = dtl,
                jumlahDireview = drv,
                lamaranList = mappedLamaran,
                lowonganList = mappedLowongan,
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
                onStatusClick = { _ -> navController.navigate(Routes.LAMARAN_SAYA) },
                onLamaranItemClick = { item ->
                    val target = magangState.lamaranSayaList.find { it.id == item.id }
                    if (target != null) {
                        magangViewModel.selectLamaran(target)
                    }
                    navController.navigate(Routes.statusLamaran(item.id))
                },
                onProfileClick = { navController.navigate(Routes.MAHASISWA_PROFIL) },
                onNotificationClick = { navController.navigate(Routes.MAHASISWA_NOTIFIKASI) },
                onNavTabClick = { tab ->
                    when (tab) {
                        MagangNavTab.PENGADUAN -> navController.navigate(Routes.SIAGA)
                        MagangNavTab.BERANDA -> navController.navigate(Routes.MAHASISWA_DASHBOARD)
                        MagangNavTab.PROFIL -> navController.navigate(Routes.MAHASISWA_PROFIL)
                        MagangNavTab.MAGANG_HUB -> {}
                    }
                }
            )
        }

        // Halaman Daftar Lowongan Magang (Lowongan.png)
        composable(Routes.LOWONGAN_MAGANG) {
            LaunchedEffect(Unit) {
                magangViewModel.loadMagangData()
            }

            val todayWita = LocalDate.now(WITA_ZONE)
            val items = magangState.lowonganList.map { lw ->
                val isOpen = lw.kuota > 0 && (
                    parseLocalDateFlexible(lw.batasPendaftaran)?.let { !it.isBefore(todayWita) } ?: true
                )
                LowonganItem(
                    id = lw.id,
                    posisi = lw.posisi,
                    perusahaan = lw.namaPerusahaan,
                    lokasi = lw.kota,
                    durasi = lw.periodeMagang,
                    kategori = lw.kategori ?: "Umum",
                    sistemKerja = lw.sistemKerja,
                    status = if (isOpen) "Available" else "Tutup",
                    batasHari = daysUntilDeadline(lw.batasPendaftaran),
                    logoDrawable = R.drawable.ic_avatar_2,
                    isBookmarked = magangState.bookmarkedIds.contains(lw.id)
                )
            }

            LowonganScreen(
                lowonganList = items,
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
            val targetId = idArg.ifBlank { magangState.lowonganList.firstOrNull()?.id.orEmpty() }
            RenderDetailMagang(targetId = targetId)
        }

        // Fallback route lama tanpa query
        composable(Routes.DETAIL_MAGANG) {
            val targetId = magangState.selectedLowongan?.id
                ?: magangState.lowonganList.firstOrNull()?.id.orEmpty()
            RenderDetailMagang(targetId = targetId)
        }

        // Formulir Lamaran Magang (Step 1, 2, 3, 4)
        composable(Routes.FORMULIR_LAMARAN) {
            LaunchedEffect(Unit) {
                profilViewModel.loadData()
            }

            val lowongan = magangState.selectedLowongan
            val posisi = lowongan?.posisi ?: "Lowongan Magang"
            val perusahaan = lowongan?.namaPerusahaan ?: "Mitra Perusahaan"
            val targetLowonganId = lowongan?.id ?: magangState.lowonganList.firstOrNull()?.id.orEmpty()

            val session = SessionManager.getInstance(navController.context)
            val mhsProfile = profilState.profile ?: session.getProfile()

            FormulirLamaranScreen(
                posisiJudul = posisi,
                perusahaan = perusahaan,
                initialNama = mhsProfile?.namaLengkap.orEmpty(),
                initialNim = mhsProfile?.nim.orEmpty(),
                initialEmail = mhsProfile?.email.orEmpty(),
                initialProdi = mhsProfile?.programStudi.orEmpty(),
                initialNoWa = mhsProfile?.noTelepon.orEmpty(),
                profileDocs = profilState.dokumenList,
                isSubmitting = magangState.isSubmitting,
                onBackClick = { navController.popBackStack() },
                onSubmitLamaran = { formData, onError, onSuccess ->
                    val userId = mhsProfile?.id.orEmpty()
                    if (userId.isBlank() || targetLowonganId.isBlank()) {
                        onError("Data akun atau lowongan belum tersedia. Silakan muat ulang halaman.")
                    } else {
                        val payload = SubmitLamaranPayload(
                            mahasiswaId = userId,
                            lowonganId = targetLowonganId,
                            namaLengkap = formData.namaLengkap.ifBlank { mhsProfile?.namaLengkap.orEmpty() },
                            nim = formData.nim.ifBlank { mhsProfile?.nim.orEmpty() },
                            tempatTanggalLahir = formData.tempatTanggalLahir,
                            alamatKtp = formData.alamatKtp,
                            alamatDomisili = formData.alamatDomisili,
                            agama = formData.agama,
                            noTelepon = formData.noWa.ifBlank { mhsProfile?.noTelepon.orEmpty() },
                            email = formData.email.ifBlank { mhsProfile?.email.orEmpty() },
                            golonganDarah = formData.golonganDarah
                        )
                        magangViewModel.submitLamaran(
                            payload = payload,
                            dokumenFiles = formData.dokumenFiles,
                            profileDocPaths = formData.profileDocPaths,
                            onError = onError,
                            onSuccess = onSuccess
                        )
                    }
                },
                onCekStatusLamaran = {
                    val latestId = magangState.lamaranSayaList
                        .firstOrNull { it.lowonganId == targetLowonganId }?.id
                        ?: magangState.lamaranSayaList.firstOrNull()?.id.orEmpty()
                    val dest = if (latestId.isNotBlank()) Routes.statusLamaran(latestId) else Routes.LAMARAN_SAYA
                    navController.navigate(dest) {
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
            LaunchedEffect(Unit) {
                magangViewModel.loadMagangData()
            }

            LamaranSayaScreen(
                lamaranList = magangState.lamaranSayaList,
                lowonganList = magangState.lowonganList,
                onBackClick = { navController.popBackStack() },
                onStatusClick = { item ->
                    val target = magangState.lamaranSayaList.find {
                        it.id == item.lamaranUuid || it.nomorLamaran == item.id
                    }
                    if (target != null) {
                        magangViewModel.selectLamaran(target)
                        navController.navigate(Routes.statusLamaran(target.id))
                    } else {
                        navController.navigate(Routes.STATUS_LAMARAN)
                    }
                }
            )
        }

        // Status Lamaran dengan parameter ID
        composable(
            route = Routes.STATUS_LAMARAN_ROUTE,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getString("id").orEmpty()
            RenderStatusLamaran(idArg = idArg)
        }

        // Fallback Status Lamaran tanpa query
        composable(Routes.STATUS_LAMARAN) {
            RenderStatusLamaran(idArg = "")
        }

        // Status Lamaran Final
        composable(Routes.STATUS_LAMARAN_FINAL) {
            RenderStatusLamaran(idArg = "")
        }

        // Informasi Penerimaan Sementara
        composable(Routes.INFORMASI_PENERIMAAN) {
            val lamaran = magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()
            val lowongan = lamaran?.let { lam -> magangState.lowonganList.find { it.id == lam.lowonganId } }
            InformasiPenerimaanScreen(
                lamaran = lamaran,
                lowongan = lowongan,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Informasi Penerimaan Final
        composable(Routes.INFORMASI_PENERIMAAN_FINAL) {
            val lamaran = magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()
            val lowongan = lamaran?.let { lam -> magangState.lowonganList.find { it.id == lam.lowonganId } }
            InformasiPenerimaanScreen(
                lamaran = lamaran,
                lowongan = lowongan,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Perbaikan Dokumen (Perbaikan Dokumen.png)
        composable(Routes.PERBAIKAN_DOKUMEN) {
            val lamaran = magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()
            val lowongan = lamaran?.let { lam -> magangState.lowonganList.find { it.id == lam.lowonganId } }

            PerbaikanDokumenScreen(
                perusahaan = lowongan?.namaPerusahaan.orEmpty(),
                catatanRevisi = lamaran?.catatanAdmin.orEmpty(),
                isSubmitting = magangState.isSubmitting,
                onBackClick = { navController.popBackStack() },
                onSubmitPerbaikan = { jenis, fileName, fileBytes, onError, onSuccess ->
                    val lamId = lamaran?.id.orEmpty()
                    magangViewModel.uploadPerbaikanDokumen(
                        lamaranId = lamId,
                        jenis = jenis,
                        fileName = fileName,
                        fileBytes = fileBytes,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                },
                onNavigateAfterSuccess = { jenis, fileName ->
                    val waktu = ZonedDateTime.now(WITA_ZONE)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("id", "ID"))) + " WITA"
                    lastPerbaikanMeta = Triple(jenis, fileName, waktu)
                    navController.navigate(Routes.PERBAIKAN_TERKIRIM)
                }
            )
        }

        // Perbaikan Dokumen Terkirim (Perbaikan Terkirim.png)
        composable(Routes.PERBAIKAN_TERKIRIM) {
            val lamaran = magangState.selectedLamaran ?: magangState.lamaranSayaList.firstOrNull()
            val lowongan = lamaran?.let { lam -> magangState.lowonganList.find { it.id == lam.lowonganId } }

            PerbaikanTerkirimScreen(
                perusahaan = lowongan?.namaPerusahaan.orEmpty(),
                dokumenJenis = lastPerbaikanMeta.first,
                fileName = lastPerbaikanMeta.second,
                waktuKirim = lastPerbaikanMeta.third,
                onKembaliKeStatus = {
                    val lamId = lamaran?.id.orEmpty()
                    val dest = if (lamId.isNotBlank()) Routes.statusLamaran(lamId) else Routes.STATUS_LAMARAN
                    navController.navigate(dest) {
                        popUpTo(Routes.LAMARAN_SAYA) { inclusive = false }
                    }
                }
            )
        }

        // ====================================================================
        // Fitur Pengaturan & Profil Mahasiswa
        // ====================================================================

        // Halaman Utama Profil & Pengaturan (MahasiswaSettings.kt)
        composable(Routes.MAHASISWA_PROFIL) {
            LaunchedEffect(Unit) {
                profilViewModel.loadData()
            }
            val user = profilState.profile
            val nama = user?.namaLengkap?.ifBlank { "Mahasiswa" } ?: "Mahasiswa"
            val nim = user?.nim?.ifBlank { "-" } ?: "-"
            val kampusProdi = listOfNotNull(
                user?.perguruanTinggi?.takeIf { it.isNotBlank() },
                user?.programStudi?.takeIf { it.isNotBlank() }
            ).joinToString(" - ").ifBlank { "-" }
            val statusLabel = when (normalizeStatusAkun(user?.statusAkun.orEmpty())) {
                "aktif" -> "Mahasiswa aktif"
                "nonaktif" -> "Nonaktif"
                "lulus" -> "Lulus"
                else -> "Belum aktivasi"
            }

            MahasiswaSettings().mhsProfilScreen(
                namaLengkap = nama,
                nim = nim,
                kampusProdi = kampusProdi,
                statusAkun = statusLabel,
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
                onTabSelected = { tab -> navigateMahasiswaTab(tab, LenteraTab.PROFIL) }
            )
        }

        // Halaman Data Diri (DataDiriScreen.kt)
        composable(Routes.MAHASISWA_DATA_DIRI) {
            DataDiriScreen(
                userProfile = profilState.profile,
                onBackClick = { navController.popBackStack() },
                onSimpanDataDiri = { nama, telp, onSuccess, onError ->
                    profilViewModel.updateProfil(
                        namaLengkap = nama,
                        noTelepon = telp,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                },
                onTabSelected = { tab -> navigateMahasiswaTab(tab) }
            )
        }

        // Halaman Dokumen Saya (DokumenSayaScreen.kt)
        composable(Routes.MAHASISWA_DOKUMEN_SAYA) {
            LaunchedEffect(Unit) {
                profilViewModel.loadData()
            }
            val user = profilState.profile
            DokumenSayaScreen(
                dokumenList = profilState.dokumenList,
                ownerName = user?.namaLengkap?.ifBlank { "-" } ?: "-",
                ownerNim = user?.nim?.ifBlank { "-" } ?: "-",
                ownerProdi = user?.programStudi?.ifBlank { "-" } ?: "-",
                onUploadDokumen = { jenis, fileName, fileBytes, onSuccess, onError ->
                    profilViewModel.uploadDokumen(
                        jenis = jenis,
                        fileName = fileName,
                        fileBytes = fileBytes,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                },
                onDeleteDokumen = { docId, filePath, onSuccess, onError ->
                    profilViewModel.deleteDokumen(
                        docId = docId,
                        filePath = filePath,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                },
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateMahasiswaTab(tab) }
            )
        }

        // Halaman Notifikasi Mahasiswa (NotifikasiScreen.kt)
        composable(Routes.MAHASISWA_NOTIFIKASI) {
            LaunchedEffect(Unit) {
                profilViewModel.loadData()
            }
            MahasiswaNotifikasiScreen(
                notifikasiList = profilState.notifikasiList,
                onMarkRead = { notifId -> profilViewModel.markNotifikasiRead(notifId) },
                onMarkAllRead = { profilViewModel.markAllNotifikasiRead() },
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateMahasiswaTab(tab) }
            )
        }

        // Halaman Bantuan (BantuanScreen.kt)
        composable(Routes.MAHASISWA_BANTUAN) {
            BantuanScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateMahasiswaTab(tab) }
            )
        }

        // Halaman Tentang Aplikasi (TentangAplikasiScreen.kt)
        composable(Routes.MAHASISWA_TENTANG) {
            TentangAplikasiScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateMahasiswaTab(tab) }
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
