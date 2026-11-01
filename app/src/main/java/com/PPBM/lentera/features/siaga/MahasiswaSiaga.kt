package com.PPBM.lentera.features.siaga

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model & Data
// ============================================================================

enum class LaporanStatus(
    val label: String,
    val textColor: Color,
    val backgroundColor: Color
) {
    DIPERIKSA("Diperiksa", SiagaBadgeDiperiksaText, SiagaBadgeDiperiksaBg),
    SELESAI("Selesai", SiagaBadgeSelesaiText, SiagaBadgeSelesaiBg),
    DIPROSES("Diproses", InfoBlue, InfoBlueLight),
    DITOLAK("Ditolak", ErrorRed, ErrorRedLight)
}

data class LaporanItem(
    val id: String,
    val title: String,
    val date: String,
    val status: LaporanStatus
)

enum class SiagaNavTab(val label: String) {
    BERANDA("Beranda"),
    PENGADUAN("Pengaduan"),
    MAGANG_HUB("Magang Hub"),
    PROFIL("Profil")
}

// Sample data default sesuai UI mockup
val sampleLaporanList = listOf(
    LaporanItem(
        id = "LTR-2026-00123",
        title = "Pelecehan Verbal",
        date = "12 Okt 2026",
        status = LaporanStatus.DIPERIKSA
    ),
    LaporanItem(
        id = "LTR-2026-00108",
        title = "Perundungan di kelas",
        date = "08 Okt 2026",
        status = LaporanStatus.SELESAI
    )
)

// ============================================================================
// Main Screen: MahasiswaSiagaScreen
// ============================================================================

/**
 * Halaman Lentera Siaga Mahasiswa
 * Sesuai desain UI Siaga.png
 *
 * @param userName Nama mahasiswa untuk header
 * @param userRole Peran mahasiswa
 * @param laporanList Daftar laporan yang ditampilkan
 * @param onNotificationClick Aksi saat tombol notifikasi ditekan
 * @param onProfileClick Aksi saat avatar profil ditekan
 * @param onEmergencyHelpClick Aksi tombol "Butuh Bantuan Segera"
 * @param onCreateReportClick Aksi tombol "Buat Laporan"
 * @param onViewAllReportsClick Aksi link "Lihat Semua" laporan
 * @param onReportClick Aksi saat kartu laporan diklik
 * @param onNavTabClick Aksi perpindahan tab pada Bottom Bar
 */
@Composable
fun MahasiswaSiagaScreen(
    userName: String = "Budi Santoso",
    userRole: String = "Mahasiswa",
    laporanList: List<LaporanItem> = sampleLaporanList,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEmergencyHelpClick: () -> Unit = {},
    onCreateReportClick: () -> Unit = {},
    onViewAllReportsClick: () -> Unit = {},
    onReportClick: (LaporanItem) -> Unit = {},
    onNavTabClick: (SiagaNavTab) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var currentTab by remember { mutableStateOf(SiagaNavTab.PENGADUAN) }
    var showPilihKategoriSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            SiagaBottomNavigationBar(
                selectedTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                    onNavTabClick(tab)
                },
                fontFamily = plusJakarta
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Bagian Atas dengan Gradien Lengkung
            SiagaHeader(
                userName = userName,
                userRole = userRole,
                onProfileClick = onProfileClick,
                onNotificationClick = onNotificationClick,
                fontFamily = plusJakarta
            )

            // 2. Konten Utama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Judul Halaman
                Text(
                    text = "LENTERA SIAGA",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(plusJakarta),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kartu Informasi Kerahasiaan
                KerahasiaanBanner(fontFamily = plusJakarta)

                Spacer(modifier = Modifier.height(18.dp))

                // Tombol Bantuan Darurat
                EmergencyHelpButton(
                    onClick = onEmergencyHelpClick,
                    fontFamily = plusJakarta
                )

                // Subtitle Keterangan Bantuan
                Text(
                    text = "Kontak darurat & konselor kampus tersedia untuk bantuan cepat.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(plusJakarta),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(top = 8.dp, start = 2.dp, end = 2.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Tombol Buat Laporan
                CreateReportButton(
                    onClick = { showPilihKategoriSheet = true },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Header Bagian "Laporan Saya"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laporan Saya",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = fontFamilyFallback(plusJakarta),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Text(
                        text = "Lihat Semua",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = fontFamilyFallback(plusJakarta),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateGray
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onViewAllReportsClick)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Daftar Kartu Laporan
                laporanList.forEach { laporan ->
                    LaporanCard(
                        item = laporan,
                        onClick = { onReportClick(laporan) },
                        fontFamily = plusJakarta
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal Bottom Sheet Pilih Kategori Laporan (Langkah 1 dari 4)
    PilihKategoriBottomSheet(
        isVisible = showPilihKategoriSheet,
        onDismiss = { showPilihKategoriSheet = false },
        onLanjutKeDetail = { kategori ->
            showPilihKategoriSheet = false
            onCreateReportClick()
        }
    )
}

// Alias composable agar fleksibel digunakan
@Composable
fun SiagaScreen(
    onEmergencyHelpClick: () -> Unit = {},
    onCreateReportClick: () -> Unit = {}
) {
    MahasiswaSiagaScreen(
        onEmergencyHelpClick = onEmergencyHelpClick,
        onCreateReportClick = onCreateReportClick
    )
}

// ============================================================================
// Komponen Header
// ============================================================================

@Composable
private fun SiagaHeader(
    userName: String,
    userRole: String,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(SiagaHeaderTealDark, SiagaHeaderTealLight)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Profil (Dapat diganti dengan foto profil asli)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                SiagaAvatarIcon(
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Nama & Peran Pengguna
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Halo, $userName",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userRole,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                )
            }

            // Tombol Notifikasi Lonceng
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNotificationClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                SiagaBellIcon(
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}

// ============================================================================
// Komponen Banner Informasi Kerahasiaan
// ============================================================================

@Composable
private fun KerahasiaanBanner(
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SiagaTealBannerBg)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Perisai Centang
            SiagaShieldCheckIcon(
                modifier = Modifier.size(28.dp),
                tint = SiagaTealBannerText
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Kamu tidak sendirian. Laporanmu dijaga kerahasiaannya.",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = fontFamilyFallback(fontFamily),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SiagaTealBannerText,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

// ============================================================================
// Komponen Tombol Bantuan Darurat & Buat Laporan
// ============================================================================

@Composable
private fun EmergencyHelpButton(
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(SiagaAmberEmergency)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SiagaPhoneIcon(
                modifier = Modifier.size(20.dp),
                tint = SiagaAmberTextDark
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Butuh Bantuan Segera",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = fontFamilyFallback(fontFamily),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SiagaAmberTextDark
                )
            )
        }
    }
}

@Composable
private fun CreateReportButton(
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(SiagaTealPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SiagaPlusIcon(
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Buat Laporan",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = fontFamilyFallback(fontFamily),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

// ============================================================================
// Komponen Kartu Laporan
// ============================================================================

@Composable
private fun LaporanCard(
    item: LaporanItem,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Kode ID Laporan
                Text(
                    text = item.id,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGrayLight
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Judul Laporan
                Text(
                    text = item.title,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tanggal
                Text(
                    text = item.date,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGrayLight
                    )
                )
            }

            // Status Badge (Capsule)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(item.status.backgroundColor)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.status.label,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFamilyFallback(fontFamily),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = item.status.textColor
                    )
                )
            }
        }
    }
}

// ============================================================================
// Komponen Bottom Navigation Bar
// ============================================================================

@Composable
private fun SiagaBottomNavigationBar(
    selectedTab: SiagaNavTab,
    onTabSelected: (SiagaNavTab) -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SiagaNavBg)
            .navigationBarsPadding()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SiagaNavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab

                if (isSelected) {
                    // Tab Aktif (Pill Container Hijau-Teal)
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(82.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(SiagaTealPrimary)
                            .clickable { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NavTabIcon(tab = tab, tint = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = fontFamilyFallback(fontFamily),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                } else {
                    // Tab Tidak Aktif
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NavTabIcon(tab = tab, tint = SiagaNavUnselected)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = fontFamilyFallback(fontFamily),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SiagaNavUnselected
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavTabIcon(
    tab: SiagaNavTab,
    tint: Color
) {
    val iconModifier = Modifier.size(22.dp)
    when (tab) {
        SiagaNavTab.BERANDA -> SiagaHomeIcon(modifier = iconModifier, tint = tint)
        SiagaNavTab.PENGADUAN -> SiagaChatPlusIcon(modifier = iconModifier, tint = tint)
        SiagaNavTab.MAGANG_HUB -> SiagaBriefcaseIcon(modifier = iconModifier, tint = tint)
        SiagaNavTab.PROFIL -> SiagaProfileNavIcon(modifier = iconModifier, tint = tint)
    }
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// Catatan: Ikon di bawah dapat Anda ganti dengan painterResource / Icon bawaan
// jika aset SVG / PNG resmi sudah siap.
// ============================================================================

@Composable
fun SiagaAvatarIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Kepala
        drawCircle(
            color = tint,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.36f),
            style = Stroke(width = strokeWidth)
        )

        // Bahu / Torso
        drawArc(
            color = tint,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.16f, h * 0.54f),
            size = Size(w * 0.68f, h * 0.44f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SiagaBellIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Badan lonceng
        val bellPath = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            cubicTo(w * 0.38f, h * 0.22f, w * 0.26f, h * 0.42f, w * 0.24f, h * 0.64f)
            lineTo(w * 0.18f, h * 0.72f)
            cubicTo(w * 0.18f, h * 0.76f, w * 0.22f, h * 0.78f, w * 0.26f, h * 0.78f)
            lineTo(w * 0.74f, h * 0.78f)
            cubicTo(w * 0.78f, h * 0.78f, w * 0.82f, h * 0.76f, w * 0.82f, h * 0.72f)
            lineTo(w * 0.76f, h * 0.64f)
            cubicTo(w * 0.74f, h * 0.42f, w * 0.62f, h * 0.22f, w * 0.5f, h * 0.18f)
        }
        drawPath(
            path = bellPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Cincin atas lonceng
        drawCircle(
            color = tint,
            radius = w * 0.06f,
            center = Offset(w * 0.5f, h * 0.14f),
            style = Stroke(width = strokeWidth)
        )

        // Bandul bawah lonceng
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.42f, h * 0.78f),
            size = Size(w * 0.16f, h * 0.12f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SiagaShieldCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealBannerText
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        // Kontur perisai
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.14f)
            lineTo(w * 0.82f, h * 0.24f)
            cubicTo(w * 0.82f, h * 0.56f, w * 0.72f, h * 0.78f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.28f, h * 0.78f, w * 0.18f, h * 0.56f, w * 0.18f, h * 0.24f)
            close()
        }
        drawPath(
            path = shieldPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Centang di dalam perisai
        val checkPath = Path().apply {
            moveTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaPhoneIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaAmberTextDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.24f)
            cubicTo(w * 0.36f, h * 0.18f, w * 0.44f, h * 0.20f, w * 0.48f, h * 0.26f)
            lineTo(w * 0.52f, h * 0.38f)
            cubicTo(w * 0.54f, h * 0.44f, w * 0.50f, h * 0.48f, w * 0.44f, h * 0.52f)
            cubicTo(w * 0.48f, h * 0.60f, w * 0.54f, h * 0.66f, w * 0.62f, h * 0.70f)
            cubicTo(w * 0.66f, h * 0.64f, w * 0.70f, h * 0.60f, w * 0.76f, h * 0.62f)
            lineTo(w * 0.86f, h * 0.66f)
            cubicTo(w * 0.92f, h * 0.70f, w * 0.94f, h * 0.78f, w * 0.88f, h * 0.84f)
            cubicTo(w * 0.78f, h * 0.94f, w * 0.62f, h * 0.90f, w * 0.48f, h * 0.76f)
            cubicTo(w * 0.32f, h * 0.62f, w * 0.20f, h * 0.44f, w * 0.24f, h * 0.32f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaPlusIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val w = size.width
        val h = size.height

        // Garis horizontal
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.5f),
            end = Offset(w * 0.80f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Garis vertikal
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.20f),
            end = Offset(w * 0.5f, h * 0.80f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaHomeIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaNavUnselected
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.16f)
            lineTo(w * 0.84f, h * 0.46f)
            lineTo(w * 0.84f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.82f)
            lineTo(w * 0.16f, h * 0.82f)
            lineTo(w * 0.16f, h * 0.46f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaChatPlusIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Balon pesan
        val path = Path().apply {
            val left = w * 0.16f
            val top = h * 0.18f
            val right = w * 0.84f
            val bottom = h * 0.72f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(w * 0.42f, bottom)
            lineTo(w * 0.24f, h * 0.86f)
            lineTo(w * 0.28f, bottom)
            lineTo(left + r, bottom)
            quadraticTo(left, bottom, left, bottom - r)
            lineTo(left, top + r)
            quadraticTo(left, top, left + r, top)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // '+' di dalam balon pesan
        val midX = w * 0.50f
        val midY = h * 0.45f
        val plusLen = w * 0.12f

        drawLine(
            color = tint,
            start = Offset(midX - plusLen, midY),
            end = Offset(midX + plusLen, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(midX, midY - plusLen),
            end = Offset(midX, midY + plusLen),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaBriefcaseIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaNavUnselected
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Badan tas
        val bodyRect = Path().apply {
            val left = w * 0.16f
            val top = h * 0.32f
            val right = w * 0.84f
            val bottom = h * 0.82f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(left + r, bottom)
            quadraticTo(left, bottom, left, bottom - r)
            lineTo(left, top + r)
            quadraticTo(left, top, left + r, top)
            close()
        }
        drawPath(
            path = bodyRect,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Gagang pegangan
        val handlePath = Path().apply {
            moveTo(w * 0.36f, h * 0.32f)
            lineTo(w * 0.36f, h * 0.20f)
            quadraticTo(w * 0.36f, h * 0.16f, w * 0.42f, h * 0.16f)
            lineTo(w * 0.58f, h * 0.16f)
            quadraticTo(w * 0.64f, h * 0.16f, w * 0.64f, h * 0.20f)
            lineTo(w * 0.64f, h * 0.32f)
        }
        drawPath(
            path = handlePath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis tengah tas
        drawLine(
            color = tint,
            start = Offset(w * 0.16f, h * 0.52f),
            end = Offset(w * 0.84f, h * 0.52f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Square
        )
    }
}

@Composable
fun SiagaProfileNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaNavUnselected
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.8.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth / 2f)
        val center = Offset(size.width / 2f, size.height / 2f)

        // Lingkaran luar
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Kepala
        drawCircle(
            color = tint,
            radius = r * 0.32f,
            center = Offset(center.x, center.y - r * 0.18f),
            style = Stroke(width = strokeWidth)
        )

        // Garis bahu
        drawArc(
            color = tint,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - r * 0.65f, center.y + r * 0.15f),
            size = Size(r * 1.30f, r * 1.10f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

/**
 * Fallback aman jika font kustom sedang loading / tidak terpasang
 */
private fun fontFamilyFallback(custom: FontFamily): FontFamily = custom

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MahasiswaSiagaScreenPreview() {
    LenteraTheme {
        MahasiswaSiagaScreen()
    }
}
