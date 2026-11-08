package com.PPBM.lentera.features.mahasiswa.siaga
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.foundation.Image

import com.PPBM.lentera.core.designsystem.components.MahasiswaBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab

import com.PPBM.lentera.core.designsystem.icons.*
import com.PPBM.lentera.core.designsystem.illustrations.*

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

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
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Konten Utama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
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
                if (laporanList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardWhite)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada laporan pengaduan aktif.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = plusJakarta,
                                color = SlateGray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    laporanList.forEach { laporan ->
                        LaporanCard(
                            item = laporan,
                            onClick = { onReportClick(laporan) },
                            fontFamily = plusJakarta
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
            ShieldCheckIcon(
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
            PhoneIcon(
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
            PlusIcon(
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
    val currentTab = when (selectedTab) {
        SiagaNavTab.BERANDA -> LenteraTab.BERANDA
        SiagaNavTab.PENGADUAN -> LenteraTab.PENGADUAN
        SiagaNavTab.MAGANG_HUB -> LenteraTab.MAGANG_HUB
        SiagaNavTab.PROFIL -> LenteraTab.PROFIL
    }
    MahasiswaBottomBar(
        currentTab = currentTab,
        onTabSelected = { tab ->
            onTabSelected(
                when (tab) {
                    LenteraTab.BERANDA -> SiagaNavTab.BERANDA
                    LenteraTab.PENGADUAN -> SiagaNavTab.PENGADUAN
                    LenteraTab.MAGANG_HUB -> SiagaNavTab.MAGANG_HUB
                    LenteraTab.PROFIL -> SiagaNavTab.PROFIL
                }
            )
        }
    )
}


// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// Catatan: Ikon di bawah dapat Anda ganti dengan painterResource / Icon bawaan
// jika aset SVG / PNG resmi sudah siap.
// ============================================================================










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
