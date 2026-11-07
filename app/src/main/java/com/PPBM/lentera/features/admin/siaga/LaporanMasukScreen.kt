package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

/**
 * FRAME 1: Laporan Masuk
 * Menampilkan daftar laporan masuk dengan filter status, kolom pencarian,
 * banner kerahasiaan, dan bottom navigation bar.
 */
@Composable
fun LaporanMasukScreen(
    laporanList: List<AdminLaporanItem> = emptyList(),
    onNavigateToDetail: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val daftarLaporan = if (laporanList.isNotEmpty()) {
        laporanList
    } else {
        listOf(
            AdminLaporanItem("LTR-2026-00126", "Lampu Koridor Padam", "17 Okt 2026 · 08:15 WITA · Identitas terbatas", "Baru", Color(0xFFFEF3C7), Color(0xFFB45309)),
            AdminLaporanItem("LTR-2026-00123", "Pelecehan Verbal", "15 Okt 2026 · 15:00 WITA · Anonim", "Diperiksa", Color(0xFFFFEDD5), Color(0xFFC2410C)),
            AdminLaporanItem("LTR-2026-00122", "Keracunan Makanan", "12 Okt 2026 · 13:20 WITA · Identitas terbatas", "Penanganan", Color(0xFFFFE4E6), Color(0xFFE11D48)),
            AdminLaporanItem("LTR-2026-00120", "Pintu Mau Roboh", "10 Okt 2026 · 09:10 WITA · Identitas terbatas", "Selesai", Color(0xFFE8F4F1), Color(0xFF2F7078))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "LAPORAN MASUK",
            subtitle = "LENTERA · Admin Kampus"
        )

        // 2. Konten Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Banner Info Hijau
            AdminInfoBanner(
                text = "Laporan bersifat rahasia. Akses hanya untuk admin dan petugas berwenang; identitas anonim tetap disembunyikan."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Kotak Pencarian (Search Bar)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Cari",
                    tint = SiagaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Cari nomor atau judul laporan",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = SiagaTextMuted
                            )
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaNavyDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Deretan Filter Status
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("Semua 24", "Baru 3", "Proses 5", "Selesai 16")
                filters.forEach { filterText ->
                    val isSelected = selectedFilter == filterText
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) SiagaTealPrimary else Color.White,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable { selectedFilter = filterText }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filterText,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else SiagaTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subtitle Row: LENTERA Siaga & Terbaru
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LENTERA Siaga",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )
                Text(
                    text = "Terbaru",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Medium,
                        color = SiagaNavyPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val filteredLaporan = daftarLaporan.filter { laporan ->
                val matchesSearch = searchQuery.isBlank() ||
                        laporan.id.contains(searchQuery, ignoreCase = true) ||
                        laporan.judul.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when {
                    selectedFilter.startsWith("Semua") -> true
                    selectedFilter.startsWith("Baru") -> laporan.status.equals("Baru", ignoreCase = true)
                    selectedFilter.startsWith("Proses") -> laporan.status in listOf("Diperiksa", "Penanganan", "Proses")
                    selectedFilter.startsWith("Selesai") -> laporan.status.equals("Selesai", ignoreCase = true)
                    else -> true
                }
                matchesSearch && matchesFilter
            }

            // Kartu-kartu Laporan
            if (filteredLaporan.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Tidak ada laporan yang sesuai dengan pencarian.",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            filteredLaporan.forEach { laporan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable { onNavigateToDetail(laporan.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = laporan.id,
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = SiagaNavyDark
                                )
                            )

                            Text(
                                text = laporan.status,
                                modifier = Modifier
                                    .background(
                                        color = laporan.statusBgColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 3.dp),
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = laporan.statusTextColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = laporan.judul,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = SiagaNavyDark
                                )
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = "Lihat Detail",
                                tint = SiagaNavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = laporan.tanggal,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = SiagaTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Menampilkan 4 dari 24 laporan · Diperbarui 09:41",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Normal,
                    color = SiagaTextSecondary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.height(96.dp))

        }

        // 3. Bottom Bar
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LaporanMasukScreenPreview() {
    LaporanMasukScreen()
}

