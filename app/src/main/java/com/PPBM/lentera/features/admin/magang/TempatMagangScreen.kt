package com.PPBM.lentera.features.admin.magang
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.PPBM.lentera.features.admin.magang.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.R

@Composable
fun TempatMagangScreen(
    mitraList: List<TempatMagangEntity> = emptyList(),
    onNavigateToDetailTempat: () -> Unit = {},
    onNavigateToTambahTempat: () -> Unit = {},
    onNavigateToLowongan: () -> Unit = {},
    onNavigateToLamaran: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val totalCount = mitraList.size
    val aktifCount = mitraList.count { it.status.equals("aktif", ignoreCase = true) }
    val drafCount = mitraList.count { it.status.equals("draf", ignoreCase = true) }
    val arsipCount = mitraList.count { it.status.equals("arsip", ignoreCase = true) || it.status.equals("nonaktif", ignoreCase = true) }
    val filterChips = listOf("Semua $totalCount", "Aktif $aktifCount", "Draf $drafCount", "Arsip $arsipCount")

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "MAGANG HUB",
                subtitle = "Mitra & Lowongan · LENTERA"
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang", onNavClick = onNavClick)
        },
        containerColor = MagangBgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Sub-tab Pills (Mitra Magang / Kelola Lowongan / Lamaran Masuk)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Active Tab: Mitra Magang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillOrange)
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mitra Magang",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                    }

                    // Inactive Tab: Kelola Lowongan
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillInactive)
                            .clickable { onNavigateToLowongan() }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kelola Lowongan",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        )
                    }

                    // Inactive Tab: Lamaran Masuk
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillInactive)
                            .clickable { onNavigateToLamaran() }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lamaran Masuk",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        )
                    }
                }
            }

            // 2. Search Bar
            item {
                MagangSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Cari perusahaan atau lokasi"
                )
            }

            // 3. Filter Chips
            item {
                MagangFilterChips(
                    chips = filterChips,
                    selectedIndex = selectedFilterIndex,
                    onChipSelected = { selectedFilterIndex = it }
                )
            }

            // 4. Primary Button: Tambah Tempat Magang
            item {
                Button(
                    onClick = onNavigateToTambahTempat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MagangGoldenOrange,
                        contentColor = MagangNavyDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_create),
                        contentDescription = "Tambah",
                        tint = MagangNavyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Tempat Magang",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // 5. Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Tempat Magang",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.ExtraBold,
                            color = MagangNavyDark
                        )
                    )
                    Text(
                        text = "${mitraList.size} tempat",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyPrimary
                        )
                    )
                }
            }

            val filteredPlaces = mitraList.filter { item ->
                val matchesSearch = searchQuery.isBlank() ||
                        item.namaPerusahaan.contains(searchQuery, ignoreCase = true) ||
                        item.bidangUsaha.contains(searchQuery, ignoreCase = true) ||
                        item.kota.contains(searchQuery, ignoreCase = true)
                val matchesChip = when (selectedFilterIndex) {
                    0 -> true
                    1 -> item.status.equals("aktif", ignoreCase = true)
                    2 -> item.status.equals("draf", ignoreCase = true)
                    3 -> item.status.equals("arsip", ignoreCase = true) || item.status.equals("nonaktif", ignoreCase = true)
                    else -> true
                }
                matchesSearch && matchesChip
            }

            if (filteredPlaces.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = if (mitraList.isEmpty()) "Belum ada data tempat magang. Silakan tekan tombol 'Tambah Tempat Magang'." else "Tidak ada tempat magang yang cocok dengan pencarian atau filter.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                color = MagangTextSecondary
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(filteredPlaces.size) { index ->
                    val item = filteredPlaces[index]
                    val isAktif = item.status.equals("aktif", ignoreCase = true)
                    TempatMagangItemCard(
                        iconRes = R.drawable.ic_building_2,
                        name = item.namaPerusahaan,
                        category = "${item.kota} · ${item.bidangUsaha}",
                        badgeText = item.status.replaceFirstChar { it.uppercase() },
                        badgeBg = if (isAktif) MagangActiveGreenBg else MagangCreamBox,
                        badgeColor = if (isAktif) MagangActiveGreenText else MagangNavyDark,
                        infoText = "${item.sistemKerja.uppercase()} · ${item.namaCabang}",
                        onClick = onNavigateToDetailTempat
                    )
                }
            }

            // 9. Aktivitas Portal Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3DE)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aktivitas Portal",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = MagangNavyDark
                                )
                            )
                            Text(
                                text = "Kelola →",
                                modifier = Modifier.clickable { onNavigateToLowongan() },
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = MagangNavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "28 lamaran pada 4 lowongan terbit",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Medium,
                                color = MagangNavyPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "3 draf lowongan · 1 ditutup",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TempatMagangItemCard(
    iconRes: Int,
    name: String,
    category: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    infoText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MagangCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon Box (Avatar Perusahaan Bulat)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF2F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_2),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Normal,
                            color = MagangTextSecondary
                        )
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_vector_chevron),
                    contentDescription = "Detail",
                    tint = MagangNavyDark,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeColor
                        )
                    )
                }

                Text(
                    text = infoText,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun TempatMagangPreview() {
    TempatMagangScreen()
}
