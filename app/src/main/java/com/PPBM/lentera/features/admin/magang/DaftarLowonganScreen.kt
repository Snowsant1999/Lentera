package com.PPBM.lentera.features.admin.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.features.admin.magang.components.*

@Composable
fun DaftarLowonganScreen(
    lowonganList: List<LowonganEntity> = emptyList(),
    companyNamesById: Map<String, String> = emptyMap(),
    isUpdating: Boolean = false,
    onNavigateToDetailLowongan: (LowonganEntity) -> Unit = {},
    onNavigateToTambahLowongan: () -> Unit = {},
    onNavigateToUbahLowongan: (LowonganEntity) -> Unit = {},
    onTogglePublish: (LowonganEntity, String) -> Unit = { _, _ -> },
    onTutupLowongan: (LowonganEntity) -> Unit = {},
    onNavigateToLamaran: () -> Unit = {},
    onBackToTempatMagang: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val totalCount = lowonganList.size
    val drafCount = lowonganList.count { it.status.equals("draf", ignoreCase = true) }
    val terbitCount = lowonganList.count { it.status.equals("terbit", ignoreCase = true) }
    val ditutupCount = lowonganList.count { it.status.equals("ditutup", ignoreCase = true) }
    val filterChips = listOf("Semua $totalCount", "Draf $drafCount", "Terbit $terbitCount", "Ditutup $ditutupCount")

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "MAGANG HUB",
                subtitle = "Kelola Lowongan · LENTERA",
                onBackClick = onBackToTempatMagang
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
            // Sub-tab Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillInactive)
                            .clickable { onBackToTempatMagang() }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mitra Magang",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillOrange)
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kelola Lowongan",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                    }

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

            // Primary Action Button: + Tambah Lowongan
            item {
                Button(
                    onClick = onNavigateToTambahLowongan,
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
                    Text(
                        text = "+ Tambah Lowongan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // Search Bar
            item {
                MagangSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Cari posisi, lokasi, atau perusahaan"
                )
            }

            // Filter Chips
            item {
                MagangFilterChips(
                    chips = filterChips,
                    selectedIndex = selectedFilterIndex,
                    onChipSelected = { selectedFilterIndex = it }
                )
            }

            // Summary Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7EB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "${lowonganList.size} lowongan terdaftar",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$terbitCount lowongan terbit · $drafCount draf · $ditutupCount ditutup",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            val filteredLowongan = lowonganList.filter { item ->
                val companyName = companyNamesById[item.tempatMagangId].orEmpty()
                val matchesSearch = searchQuery.isBlank() ||
                    item.posisi.contains(searchQuery, ignoreCase = true) ||
                    item.lokasi.contains(searchQuery, ignoreCase = true) ||
                    companyName.contains(searchQuery, ignoreCase = true)
                val matchesChip = when (selectedFilterIndex) {
                    0 -> true
                    1 -> item.status.equals("draf", ignoreCase = true)
                    2 -> item.status.equals("terbit", ignoreCase = true)
                    3 -> item.status.equals("ditutup", ignoreCase = true)
                    else -> true
                }
                matchesSearch && matchesChip
            }

            if (filteredLowongan.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = if (lowonganList.isEmpty()) {
                                "Belum ada lowongan terdaftar. Silakan buat lowongan baru."
                            } else {
                                "Tidak ada lowongan yang sesuai dengan filter pencarian."
                            },
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
                items(filteredLowongan.size) { index ->
                    val item = filteredLowongan[index]
                    val isTerbit = item.status.equals("terbit", ignoreCase = true)
                    val isDraf = item.status.equals("draf", ignoreCase = true)
                    val companyLabel = companyNamesById[item.tempatMagangId]
                        ?.takeIf { it.isNotBlank() }
                        ?.let { "$it · ${item.lokasi}" }
                        ?: item.lokasi
                    LowonganItemCard(
                        title = item.posisi,
                        company = companyLabel,
                        metaText = "Periode: ${item.periodeMagang} · Kuota: ${item.kuota}",
                        badgeText = item.status.replaceFirstChar { it.uppercase() },
                        badgeBg = if (isTerbit) MagangActiveGreenBg else MagangCreamBox,
                        badgeColor = if (isTerbit) MagangActiveGreenText else MagangNavyDark,
                        isUpdating = isUpdating,
                        showCloseButton = isDraf,
                        onCardClick = { onNavigateToDetailLowongan(item) },
                        onUbahClick = { onNavigateToUbahLowongan(item) },
                        actionPrimaryText = if (isTerbit) "Tutup" else "Terbitkan",
                        onActionPrimaryClick = {
                            onTogglePublish(item, if (isTerbit) "ditutup" else "terbit")
                        },
                        onTutupClick = { onTutupLowongan(item) }
                    )
                }
            }

            if (lowonganList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Ringkasan Batas Pendaftaran",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = MagangNavyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            lowonganList.take(3).forEachIndexed { idx, lowongan ->
                                if (idx > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                val mitraName = companyNamesById[lowongan.tempatMagangId] ?: "Mitra"
                                Text(
                                    text = "${lowongan.posisi} · $mitraName",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Status: ${lowongan.status.replaceFirstChar { it.uppercase() }} · Batas ${lowongan.batasPendaftaran} · Kuota ${lowongan.kuota}",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        color = MagangTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Pilih kartu untuk melihat detail lowongan atau tombol Ubah untuk menyunting.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(16.dp))
                        .clickable { onBackToTempatMagang() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Tempat Magang",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LowonganItemCard(
    title: String,
    company: String,
    metaText: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    isUpdating: Boolean = false,
    showCloseButton: Boolean = true,
    onCardClick: () -> Unit,
    onUbahClick: () -> Unit,
    actionPrimaryText: String,
    onActionPrimaryClick: () -> Unit,
    onTutupClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MagangCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF2F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_2),
                        contentDescription = company,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = company,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Normal,
                            color = MagangTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metaText,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    )
                )

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
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, MagangNavyDark, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isUpdating) { onUbahClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ubah",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MagangGoldenOrange)
                        .clickable(enabled = !isUpdating) { onActionPrimaryClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actionPrimaryText,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }

                if (showCloseButton) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, MagangRedBrown, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isUpdating) { onTutupClick() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tutup",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangRedBrown
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DaftarLowonganPreview() {
    DaftarLowonganScreen()
}
