package com.PPBM.lentera.features.magang

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
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

enum class MagangNavTab(val label: String) {
    BERANDA("Beranda"),
    PENGADUAN("Pengaduan"),
    MAGANG_HUB("Magang Hub"),
    PROFIL("Profil")
}

data class LamaranItemData(
    val id: String,
    val posisi: String,
    val perusahaan: String,
    val status: String,
    val statusBg: Color,
    val statusText: Color
)

data class LowonganCardData(
    val id: String,
    val posisi: String,
    val perusahaan: String,
    val lokasi: String,
    val durasi: String,
    val tipe: String,
    val persentaseCocok: Int = 92,
    val batasHari: Int = 5,
    val logoDrawable: Int = R.drawable.ic_company_maju_mundur,
    val isBookmarked: Boolean = false
)

// ============================================================================
// Main Screen: MagangScreen
// ============================================================================

/**
 * Halaman Utama Magang Mahasiswa
 * Sesuai desain UI Magang.png
 */
@Composable
fun MagangScreen(
    userName: String = "Budi Santoso",
    userRole: String = "Mahasiswa",
    profilCompletion: Int = 70,
    totalLamaran: Int = 3,
    jumlahDiterima: Int = 1,
    jumlahDitolak: Int = 1,
    jumlahDireview: Int = 1,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onLihatLamaranClick: () -> Unit = {},
    onStatusClick: (String) -> Unit = {},
    onLamaranItemClick: (LamaranItemData) -> Unit = {},
    onViewAllLowonganClick: () -> Unit = {},
    onLowonganClick: (LowonganCardData) -> Unit = {},
    onBookmarkClick: (LowonganCardData) -> Unit = {},
    selectedNavTab: MagangNavTab = MagangNavTab.MAGANG_HUB,
    onNavTabClick: (MagangNavTab) -> Unit = {}
) {
    val plusJakarta = remember { FontFamily(Font(R.font.plus_jakarta_sans)) }

    val sampleLamaranList = remember {
        listOf(
            LamaranItemData(
                id = "1",
                posisi = "UI/UX Design",
                perusahaan = "PT. Maju Mundur",
                status = "Seleksi",
                statusBg = MagangPillGreenBg,
                statusText = MagangPillGreenText
            ),
            LamaranItemData(
                id = "2",
                posisi = "UI/UX Design",
                perusahaan = "PT. Maju Mundur",
                status = "Direview",
                statusBg = MagangPillOrangeBg,
                statusText = MagangPillOrangeText
            )
        )
    }

    val sampleLowonganList = remember {
        listOf(
            LowonganCardData(
                id = "1",
                posisi = "UI/UX DESIGN",
                perusahaan = "PT. Maju Mundur",
                lokasi = "Bontang",
                durasi = "3 Bulan",
                tipe = "Penuh Waktu",
                persentaseCocok = 92,
                batasHari = 5,
                logoDrawable = R.drawable.ic_company_maju_mundur
            ),
            LowonganCardData(
                id = "2",
                posisi = "WEB DEV",
                perusahaan = "PT. Maju Mundur",
                lokasi = "Bontang",
                durasi = "3 Bulan",
                tipe = "Penuh Waktu",
                persentaseCocok = 92,
                batasHari = 5,
                logoDrawable = R.drawable.studio_mark
            )
        )
    }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            MagangBottomNavigationBar(
                selectedTab = selectedNavTab,
                onTabSelected = onNavTabClick,
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
            // 1. Header Lengkung Golden Amber
            MagangHeader(
                userName = userName,
                userRole = userRole,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Judul Besar "MAGANG"
                Text(
                    text = "MAGANG",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Pill Bar "Lengkapi profil 70%"
                LengkapiProfilCard(
                    completionPercentage = profilCompletion,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Search Bar
                MagangSearchBar(
                    onSearchClick = onSearchClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Dua Kartu Ringkasan (Total Lamaran & Status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Kartu Total Lamaran
                    TotalLamaranCard(
                        total = totalLamaran,
                        onLihatLamaranClick = onLihatLamaranClick,
                        modifier = Modifier.weight(1f),
                        fontFamily = plusJakarta
                    )

                    // Kartu Status (Diterima, Ditolak, Direview)
                    StatusLamaranCard(
                        jumlahDiterima = jumlahDiterima,
                        jumlahDitolak = jumlahDitolak,
                        jumlahDireview = jumlahDireview,
                        onStatusClick = onStatusClick,
                        modifier = Modifier.weight(1.15f),
                        fontFamily = plusJakarta
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Section "Lamaran"
                LamaranSection(
                    lamaranList = sampleLamaranList,
                    onItemClick = onLamaranItemClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(22.dp))

                // 7. Section "Daftar Lowongan"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Lowongan",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Text(
                        text = "Lihat Semua",
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onViewAllLowonganClick)
                            .padding(4.dp),
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Row Kartu Lowongan
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(sampleLowonganList) { item ->
                        LowonganHorizontalCard(
                            data = item,
                            onClick = { onLowonganClick(item) },
                            onBookmarkClick = { onBookmarkClick(item) },
                            fontFamily = plusJakarta
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// Komponen Header Lengkung Golden Amber
// ============================================================================

@Composable
private fun MagangHeader(
    userName: String,
    userRole: String,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MagangAmberHeaderDark,
                        MagangAmberHeaderLight
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sisi Kiri: Avatar & Sapaan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onProfileClick)
            ) {
                // Avatar dengan border putih
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Avatar Pengguna",
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Halo, $userName",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userRole,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            // Sisi Kanan: Tombol Notifikasi (Putih Bulat)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 2.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = "Notifikasi",
                    tint = NavyDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ============================================================================
// Komponen Kartu "Lengkapi profil 70%"
// ============================================================================

@Composable
private fun LengkapiProfilCard(
    completionPercentage: Int,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, MagangAmberPrimary.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lengkapi profil",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Text(
                    text = "$completionPercentage%",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar Baris
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EEF5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (completionPercentage.coerceIn(0, 100) / 100f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(MagangAmberPrimary)
                )
            }
        }
    }
}

// ============================================================================
// Komponen Search Bar
// ============================================================================

@Composable
private fun MagangSearchBar(
    onSearchClick: () -> Unit,
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
            .clickable(onClick = onSearchClick)
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
                text = "Cari Berdasarkan Tempat Atau Posisi",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )
        }
    }
}

// ============================================================================
// Komponen Ringkasan 1: Total Lamaran
// ============================================================================

@Composable
private fun TotalLamaranCard(
    total: Int,
    onLihatLamaranClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily
) {
    Card(
        modifier = modifier.height(168.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TOTAL LAMARAN",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$total",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                )

                Text(
                    text = "Lamaran terkirim",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 11.5.sp,
                        color = SlateGray
                    )
                )
            }

            // Tombol "Lihat Lamaran >"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF5FA))
                    .clickable(onClick = onLihatLamaranClick)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat Lamaran",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_status_chevron),
                        contentDescription = null,
                        tint = NavyDark,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// Komponen Ringkasan 2: Status (Diterima, Ditolak, Direview)
// ============================================================================

@Composable
private fun StatusLamaranCard(
    jumlahDiterima: Int,
    jumlahDitolak: Int,
    jumlahDireview: Int,
    onStatusClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily
) {
    Card(
        modifier = modifier.height(168.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STATUS",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            // 1. Diterima
            StatusPillButton(
                title = "$jumlahDiterima Diterima",
                bgColor = MagangPillGreenBg,
                textColor = MagangPillGreenText,
                onClick = { onStatusClick("Diterima") },
                fontFamily = fontFamily
            )

            // 2. Ditolak
            StatusPillButton(
                title = "$jumlahDitolak Ditolak",
                bgColor = MagangPillRedBg,
                textColor = MagangPillRedText,
                onClick = { onStatusClick("Ditolak") },
                fontFamily = fontFamily
            )

            // 3. Direview
            StatusPillButton(
                title = "$jumlahDireview Direview",
                bgColor = MagangPillOrangeBg,
                textColor = MagangPillOrangeText,
                onClick = { onStatusClick("Direview") },
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
private fun StatusPillButton(
    title: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_status_chevron),
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(8.dp)
            )
        }
    }
}

// ============================================================================
// Komponen Kartu "Lamaran"
// ============================================================================

@Composable
private fun LamaranSection(
    lamaranList: List<LamaranItemData>,
    onItemClick: (LamaranItemData) -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Lamaran",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            lamaranList.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7FAFD))
                        .border(1.dp, Color(0xFFE4EDF5), RoundedCornerShape(12.dp))
                        .clickable { onItemClick(item) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.posisi,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.perusahaan,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                color = SlateGray
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge status
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(item.statusBg)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.status,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = item.statusText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Canvas(modifier = Modifier.size(10.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.1f)
                                lineTo(size.width * 0.8f, size.height * 0.5f)
                                lineTo(size.width * 0.2f, size.height * 0.9f)
                            }
                            drawPath(
                                path = path,
                                color = NavyDark,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }

                if (index < lamaranList.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

// ============================================================================
// Komponen Kartu Lowongan Horizontal
// ============================================================================

@Composable
private fun LowonganHorizontalCard(
    data: LowonganCardData,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Bagian Banner Atas Golden/Amber
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MagangAmberHeaderDark,
                                MagangAmberHeaderLight
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Logo perusahaan
                    Image(
                        painter = painterResource(id = data.logoDrawable),
                        contentDescription = "Logo Perusahaan",
                        modifier = Modifier.size(42.dp)
                    )

                    // Bookmark icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBookmarkClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookmark),
                            contentDescription = "Bookmark",
                            tint = NavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bagian Body Kartu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = data.posisi,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = data.perusahaan,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Detail Lokasi, Durasi, Tipe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = data.lokasi,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyDark
                        )
                    )
                    Text(
                        text = data.durasi,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyDark
                        )
                    )
                }

                Text(
                    text = data.tipe,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Available
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF287D4A))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Available",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF287D4A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${data.persentaseCocok}% cocok",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tutup ${data.batasHari} hari lagi",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 11.5.sp,
                        color = NavyDark
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
private fun MagangBottomNavigationBar(
    selectedTab: MagangNavTab,
    onTabSelected: (MagangNavTab) -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MagangNavBg)
            .navigationBarsPadding()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MagangNavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab

                if (isSelected) {
                    // Tab Aktif (Pill Container Golden/Amber)
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .width(88.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MagangAmberPrimary)
                            .clickable { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NavTabIconMagang(tab = tab, tint = NavyDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                        }
                    }
                } else {
                    // Tab Tidak Aktif
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .width(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NavTabIconMagang(tab = tab, tint = MagangNavUnselected)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MagangNavUnselected
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
private fun NavTabIconMagang(
    tab: MagangNavTab,
    tint: Color
) {
    when (tab) {
        MagangNavTab.BERANDA -> {
            Icon(
                painter = painterResource(id = R.drawable.house),
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        MagangNavTab.PENGADUAN -> {
            Icon(
                painter = painterResource(id = R.drawable.message_square_plus),
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        MagangNavTab.MAGANG_HUB -> {
            Icon(
                painter = painterResource(id = R.drawable.briefcase_business),
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        MagangNavTab.PROFIL -> {
            Icon(
                painter = painterResource(id = R.drawable.iconamoon_profile_circle_thin),
                contentDescription = tab.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}



// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MagangScreenPreview() {
    LenteraTheme {
        MagangScreen()
    }
}
