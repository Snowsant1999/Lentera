package com.PPBM.lentera.features.magang

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class LowonganItem(
    val id: String,
    val posisi: String,
    val perusahaan: String,
    val lokasi: String,
    val durasi: String,
    val kategori: String,
    val status: String = "Available",
    val persentaseCocok: Int = 92,
    val batasHari: Int = 5,
    val logoDrawable: Int = R.drawable.ic_company_maju_mundur,
    val isBookmarked: Boolean = false
)

// ============================================================================
// Main Screen: LowonganScreen
// ============================================================================

/**
 * Halaman Daftar Lowongan Magang
 * Sesuai desain UI Lowongan.png
 */
@Composable
fun LowonganScreen(
    onBackClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onLowonganClick: (LowonganItem) -> Unit = {},
    onBookmarkToggle: (LowonganItem) -> Unit = {}
) {
    val plusJakarta = remember { FontFamily(Font(R.font.plus_jakarta_sans)) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = remember {
        listOf("Semua", "Teknologi", "Desain", "Bisnis", "Remote")
    }

    val lowonganList = remember {
        listOf(
            LowonganItem(
                id = "1",
                posisi = "UI/UX Design",
                perusahaan = "PT. Maju Mundur",
                lokasi = "Bontang",
                durasi = "3 Bulan",
                kategori = "Desain",
                logoDrawable = R.drawable.ic_company_maju_mundur
            ),
            LowonganItem(
                id = "2",
                posisi = "Web Developer",
                perusahaan = "PT. Bismillah Berkah",
                lokasi = "Bontang",
                durasi = "3 Bulan",
                kategori = "Teknologi",
                logoDrawable = R.drawable.studio_mark
            ),
            LowonganItem(
                id = "3",
                posisi = "Business Development",
                perusahaan = "PT. Maju Mundur",
                lokasi = "Samarinda",
                durasi = "3 Bulan",
                kategori = "Bisnis",
                logoDrawable = R.drawable.ic_company_maju_mundur
            ),
            LowonganItem(
                id = "4",
                posisi = "Product Designer",
                perusahaan = "Studio Ruang Reka",
                lokasi = "Remote",
                durasi = "3 Bulan",
                kategori = "Desain",
                logoDrawable = R.drawable.studio_mark
            )
        )
    }

    val filteredList = remember(selectedCategory, searchQuery) {
        lowonganList.filter { item ->
            val matchCategory = selectedCategory == "Semua" || item.kategori.equals(selectedCategory, ignoreCase = true) ||
                    (selectedCategory == "Remote" && item.lokasi.equals("Remote", ignoreCase = true))
            val matchQuery = searchQuery.isEmpty() ||
                    item.posisi.contains(searchQuery, ignoreCase = true) ||
                    item.perusahaan.contains(searchQuery, ignoreCase = true) ||
                    item.lokasi.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    Scaffold(
        containerColor = ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. Top Bar dengan tombol back bulat & Judul "LOWONGAN"
            LowonganTopBar(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Search Bar
                LowonganSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearchQueryChange(it)
                    },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Filter Chips Horizontal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        FilterChipButton(
                            text = category,
                            isSelected = isSelected,
                            onClick = { selectedCategory = category },
                            fontFamily = plusJakarta
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Counter teks peluang
                Text(
                    text = "${filteredList.size} peluang untuk langkah pertamamu",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. Daftar Kartu Lowongan Vertikal
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList) { item ->
                    LowonganItemCard(
                        item = item,
                        onClick = { onLowonganClick(item) },
                        onBookmarkClick = { onBookmarkToggle(item) },
                        fontFamily = plusJakarta
                    )
                }
            }
        }
    }
}

// ============================================================================
// Komponen Top Bar
// ============================================================================

@Composable
private fun LowonganTopBar(
    onBackClick: () -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.ic_back_button), contentDescription = "Kembali", modifier = Modifier.size(44.dp).clickable(onClick = onBackClick))

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "LOWONGAN",
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
// Komponen Search Bar
// ============================================================================

@Composable
private fun LowonganSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(0.5.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search",
                tint = SlateGray,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (query.isEmpty()) "Cari tempat atau posisi magang" else query,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (query.isEmpty()) SlateGray else NavyDark
                )
            )
        }
    }
}

// ============================================================================
// Komponen Filter Chip
// ============================================================================

@Composable
private fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(CircleShape)
            .background(if (isSelected) MagangAmberPrimary else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) MagangAmberPrimary else CardBorder,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NavyDark else NavyMedium
            )
        )
    }
}

// ============================================================================
// Komponen Kartu Lowongan Vertikal
// ============================================================================

@Composable
private fun LowonganItemCard(
    item: LowonganItem,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Sisi Kiri: Logo Perusahaan & Info Posisi
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    // Logo Perusahaan Bulat
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F7FA))
                            .border(1.dp, Color(0xFFE4EDF5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = item.logoDrawable),
                            contentDescription = "Logo Perusahaan",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.posisi,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.perusahaan,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.5.sp,
                                color = SlateGray
                            )
                        )
                    }
                }

                // Sisi Kanan: Bookmark Button Bulat
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable(onClick = onBookmarkClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bookmark),
                        contentDescription = "Bookmark",
                        tint = NavyDark,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Baris Lokasi & Status Available
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lokasi dengan pin icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_map_pin), contentDescription = null, tint = SlateGrayLight, modifier = Modifier.size(13.dp))

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "${item.lokasi} · ${item.durasi}",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    )
                }

                // Status Available
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF287D4A))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = item.status,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF287D4A)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strip Bawah: Persentase cocok & Countdown Tutup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF5FA))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.persentaseCocok}% cocok",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Text(
                        text = "Tutup ${item.batasHari} hari lagi",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LowonganScreenPreview() {
    LenteraTheme {
        LowonganScreen()
    }
}
