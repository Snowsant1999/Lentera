package com.PPBM.lentera.features.mahasiswa.siaga
import com.PPBM.lentera.core.designsystem.icons.*
import com.PPBM.lentera.core.designsystem.illustrations.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

// ============================================================================
// Model & Data
// ============================================================================

enum class FilterStatusTab(val label: String) {
    SEMUA("Semua"),
    AKTIF("Aktif"),
    SELESAI("Selesai")
}

data class RiwayatLaporanItem(
    val id: String,
    val nomorLaporan: String,
    val judul: String,
    val kategori: String,
    val waktu: String,
    val statusBadge: String,
    val badgeBgColor: Color,
    val badgeTextColor: Color,
    val isSelesai: Boolean
)

val sampleRiwayatLaporan = listOf(
    RiwayatLaporanItem(
        id = "1",
        nomorLaporan = "LTR-2026-00123",
        judul = "Pelecehan Verbal",
        kategori = "Pelecehan Verbal",
        waktu = "15 Okt 2026, 15:00",
        statusBadge = "Selesai",
        badgeBgColor = Color(0xFFE3F5EB),
        badgeTextColor = Color(0xFF287D4A),
        isSelesai = true
    ),
    RiwayatLaporanItem(
        id = "2",
        nomorLaporan = "LTR-2026-00123",
        judul = "Pelecehan Verbal",
        kategori = "Pelecehan Verbal",
        waktu = "15 Okt 2026, 15:00",
        statusBadge = "Selesai",
        badgeBgColor = Color(0xFFE3F5EB),
        badgeTextColor = Color(0xFF287D4A),
        isSelesai = true
    ),
    RiwayatLaporanItem(
        id = "3",
        nomorLaporan = "LTR-2026-00123",
        judul = "Pelecehan Verbal",
        kategori = "Pelecehan Verbal",
        waktu = "15 Okt 2026, 15:00",
        statusBadge = "Perlu Informasi Tambahan",
        badgeBgColor = Color(0xFFE6B363),
        badgeTextColor = Color.White,
        isSelesai = false
    )
)

// ============================================================================
// Main Screen: RiwayatLaporanScreen
// ============================================================================

/**
 * Halaman Riwayat Laporan Mahasiswa (Laporan Saya)
 * Sesuai desain UI Riwayat Laporan.png
 *
 * @param reportList Daftar riwayat laporan
 * @param onBackClick Aksi tombol kembali
 * @param onReportClick Aksi saat salah satu kartu laporan diklik
 * @param onKembaliKeDashboard Aksi tombol "Kembali ke Dashboard"
 */
@Composable
fun RiwayatLaporanScreen(
    reportList: List<RiwayatLaporanItem> = sampleRiwayatLaporan,
    onBackClick: () -> Unit = {},
    onReportClick: (RiwayatLaporanItem) -> Unit = {},
    onKembaliKeDashboard: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterStatusTab.SEMUA) }

    val filteredList = remember(searchQuery, selectedFilter, reportList) {
        reportList.filter { item ->
            val matchQuery = searchQuery.isEmpty() ||
                    item.nomorLaporan.contains(searchQuery, ignoreCase = true) ||
                    item.judul.contains(searchQuery, ignoreCase = true)

            val matchFilter = when (selectedFilter) {
                FilterStatusTab.SEMUA -> true
                FilterStatusTab.AKTIF -> !item.isSelesai
                FilterStatusTab.SELESAI -> item.isSelesai
            }

            matchQuery && matchFilter
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol "Kembali ke Dashboard" di bagian bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenBackground)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SiagaTealPrimary)
                        .clickable(onClick = onKembaliKeDashboard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Dashboard",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            // 1. Top Bar
            TopBarRiwayatLaporan(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Search Bar
                SearchBarRiwayat(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Filter Chips (Semua, Aktif, Selesai)
                FilterTabsRow(
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            // 4. Daftar Kartu Laporan (LazyColumn)
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum Ada Riwayat Laporan",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Laporan yang Anda buat akan muncul di sini.",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.sp,
                                color = SlateGray
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        RiwayatLaporanCard(
                            item = item,
                            onClick = { onReportClick(item) },
                            fontFamily = plusJakarta
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

// Alias composable
@Composable
fun RiwayatLaporan(
    onBackClick: () -> Unit = {},
    onKembaliKeDashboard: () -> Unit = {}
) {
    RiwayatLaporanScreen(
        onBackClick = onBackClick,
        onKembaliKeDashboard = onKembaliKeDashboard
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarRiwayatLaporan(
    onBackClick: () -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            BackArrowIcon(
                modifier = Modifier.size(24.dp),
                tint = NavyDark
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "LAPORAN SAYA",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ============================================================================
// Search Bar
// ============================================================================

@Composable
private fun SearchBarRiwayat(
    query: String,
    onQueryChange: (String) -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchIcon(
                modifier = Modifier.size(20.dp),
                tint = SlateGray
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Cari nomor dan judul laporan",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            color = SlateGrayLight
                        )
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        color = NavyDark,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(SiagaTealPrimary),
                    singleLine = true
                )
            }
        }
    }
}

// ============================================================================
// Filter Chips Row
// ============================================================================

@Composable
private fun FilterTabsRow(
    selectedFilter: FilterStatusTab,
    onFilterSelect: (FilterStatusTab) -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterStatusTab.entries.forEach { tab ->
            val isSelected = tab == selectedFilter

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) SiagaTealPrimary else Color(0xFFE2F1ED)
                    )
                    .clickable { onFilterSelect(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = when {
                            isSelected -> Color.White
                            tab == FilterStatusTab.SELESAI -> Color(0xFF287D4A)
                            else -> SiagaTealPrimary
                        }
                    )
                )
            }
        }
    }
}

// ============================================================================
// Kartu Item Riwayat Laporan
// ============================================================================

@Composable
private fun RiwayatLaporanCard(
    item: RiwayatLaporanItem,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Laporan di sebelah kiri
            ReportFileIcon(
                modifier = Modifier.size(36.dp),
                tint = SiagaTealPrimary
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Rincian Teks
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nomorLaporan,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.judul,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Kategori ${item.kategori}\n${item.waktu}",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGrayLight,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Kolom Kanan: Status Badge & Chevron Right
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(item.badgeBgColor)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.statusBadge,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.badgeTextColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Chevron Right
                ChevronRightIcon(
                    modifier = Modifier.size(16.dp),
                    tint = SiagaTealPrimary
                )
            }
        }
    }
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// ============================================================================



// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RiwayatLaporanScreenPreview() {
    LenteraTheme {
        RiwayatLaporanScreen()
    }
}
