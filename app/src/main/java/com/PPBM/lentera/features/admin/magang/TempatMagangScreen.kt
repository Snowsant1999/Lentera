package com.PPBM.lentera.features.admin.magang
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
import com.PPBM.lentera.R

@Composable
fun TempatMagangScreen(
    onNavigateToDetailTempat: () -> Unit = {},
    onNavigateToTambahTempat: () -> Unit = {},
    onNavigateToLowongan: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterChips = listOf("Semua 12", "Aktif 9", "Draf 2", "Arsip 1")

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "TEMPAT MAGANG",
                subtitle = "LENTERA · Admin Kampus"
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
            // 1. Sub-tab Pills (Tempat Magang / Kelola Lowongan)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Active Tab: Tempat Magang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillOrange)
                            .padding(horizontal = 18.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tempat Magang",
                            style = TextStyle(
                                fontSize = 13.sp,
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
                            .padding(horizontal = 18.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kelola Lowongan →",
                            style = TextStyle(
                                fontSize = 13.sp,
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
                        text = "12 tempat",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyPrimary
                        )
                    )
                }
            }

            val places = listOf(
                Triple("PT. Cahaya Digital", "Bontang · Teknologi", "Draf") to ("2 dokumen · 16 Okt" to R.drawable.ic_building_2),
                Triple("PT. Maju Mundur", "Bontang · Desain & Bisnis", "Aktif") to ("2 lowongan terbit" to R.drawable.ic_avatar_2),
                Triple("PT. Bismillah Berkah", "Bontang · Teknologi", "Aktif") to ("1 lowongan terbit" to R.drawable.ic_avatar_2)
            )

            val filteredPlaces = places.filter { (data, _) ->
                val (name, cat, status) = data
                val matchesSearch = searchQuery.isBlank() ||
                        name.contains(searchQuery, ignoreCase = true) ||
                        cat.contains(searchQuery, ignoreCase = true)
                val matchesChip = when (selectedFilterIndex) {
                    0 -> true // Semua 12
                    1 -> status.equals("Aktif", ignoreCase = true) // Aktif 9
                    2 -> status.equals("Draf", ignoreCase = true) // Draf 2
                    3 -> status.equals("Arsip", ignoreCase = true) // Arsip 1
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
                            text = "Tidak ada tempat magang yang cocok dengan pencarian atau filter.",
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
                    val (data, extra) = filteredPlaces[index]
                    val (name, category, badgeText) = data
                    val (infoText, iconRes) = extra
                    TempatMagangItemCard(
                        iconRes = iconRes,
                        name = name,
                        category = category,
                        badgeText = badgeText,
                        badgeBg = if (badgeText == "Aktif") MagangActiveGreenBg else MagangCreamBox,
                        badgeColor = if (badgeText == "Aktif") MagangActiveGreenText else MagangNavyDark,
                        infoText = infoText,
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
