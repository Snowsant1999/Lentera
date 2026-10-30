package com.PPBM.lentera.features.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
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

data class DetailMagangData(
    val id: String = "1",
    val posisi: String = "UI/UX DESIGN",
    val perusahaan: String = "PT. Maju Mundur",
    val lokasi: String = "Bontang",
    val tipeWaktu: String = "Penuh Waktu",
    val durasi: String = "3 Bulan",
    val status: String = "Available",
    val persentaseCocok: Int = 92,
    val batasHari: Int = 5,
    val logoDrawable: Int = R.drawable.ic_company_maju_mundur,
    val isBookmarked: Boolean = false,
    val deskripsiPekerjaan: String = "Bertanggung jawab dalam merancang antarmuka pengguna (UI) dan alur pengalaman pengguna (UX) untuk produk digital, berkolaborasi dengan tim pengembang dan product manager.",
    val persyaratan: List<String> = listOf(
        "Mahasiswa aktif semester 5 ke atas",
        "IPK minimal 3.00",
        "Memiliki portofolio",
        "CV dan transkrip nilai"
    ),
    val periodePendaftaran: String = "10–31 Oktober 2026",
    val mulaiMagang: String = "November 2026"
)

// ============================================================================
// Main Screen: DetailMagangScreen
// ============================================================================

/**
 * Halaman Detail Magang (Scrollable)
 * Sesuai desain UI Detail Magang.png
 */
@Composable
fun DetailMagangScreen(
    data: DetailMagangData = DetailMagangData(),
    onBackClick: () -> Unit = {},
    onBookmarkToggle: () -> Unit = {},
    onLamarSekarangClick: () -> Unit = {}
) {
    val plusJakarta = remember { FontFamily(Font(R.font.plus_jakarta_sans)) }
    var selectedTab by remember { mutableStateOf(1) } // 0: Deskripsi, 1: Persyaratan

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol "Lamar Sekarang" di bagian bawah layar
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
                        .background(MagangAmberPrimary)
                        .clickable(onClick = onLamarSekarangClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lamar Sekarang",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
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
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Bar dengan tombol back bulat & Judul "DETAIL MAGANG"
            DetailMagangTopBar(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Kartu Header Utama Lowongan
                HeaderLowonganCard(
                    data = data,
                    onBookmarkToggle = onBookmarkToggle,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Tab Bar "Deskripsi" | "Persyaratan"
                MagangTabBar(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 1) {
                    // Konten Tab Persyaratan
                    Text(
                        text = "Siapkan langkah pertamamu",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daftar Persyaratan dengan Checkbox Amber
                    data.persyaratan.forEach { syarat ->
                        ChecklistItemRow(
                            text = syarat,
                            fontFamily = plusJakarta
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Kartu Periode Pendaftaran
                    PeriodePendaftaranCard(
                        periode = data.periodePendaftaran,
                        mulaiMagang = data.mulaiMagang,
                        fontFamily = plusJakarta
                    )
                } else {
                    // Konten Tab Deskripsi
                    Text(
                        text = "Tentang Pekerjaan",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = data.deskripsiPekerjaan,
                            modifier = Modifier.padding(16.dp),
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGray,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ============================================================================
// Komponen Top Bar
// ============================================================================

@Composable
private fun DetailMagangTopBar(
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
            text = "DETAIL MAGANG",
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
// Komponen Kartu Header Utama
// ============================================================================

@Composable
private fun HeaderLowonganCard(
    data: DetailMagangData,
    onBookmarkToggle: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Baris Atas: Logo Perusahaan & Tombol Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Logo dalam Rounded Box Biru Muda
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEFF5FA))
                        .border(1.dp, Color(0xFFE2ECF5), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = data.logoDrawable),
                        contentDescription = "Logo Perusahaan",
                        modifier = Modifier.size(44.dp)
                    )
                }

                // Bookmark icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBookmarkToggle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bookmark),
                        contentDescription = "Bookmark",
                        tint = NavyDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Posisi
            Text(
                text = data.posisi,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Perusahaan - Lokasi
            Text(
                text = "${data.perusahaan} - ${data.lokasi}",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tags Pill Row (Penuh Waktu, 3 Bulan, Available)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tag 1: Penuh Waktu
                TagPillAmber(
                    text = data.tipeWaktu,
                    fontFamily = fontFamily
                )

                // Tag 2: 3 Bulan
                TagPillAmber(
                    text = data.durasi,
                    fontFamily = fontFamily
                )

                // Tag 3: Available
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MagangPillGreenBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.status,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MagangPillGreenText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strip Bawah: 92% cocok & Tutup 5 hari lagi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF5FA))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${data.persentaseCocok}% cocok",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Text(
                        text = "Tutup ${data.batasHari} hari lagi",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TagPillAmber(
    text: String,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MagangAmberTagBg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )
        )
    }
}

// ============================================================================
// Komponen Tab Bar "Deskripsi" | "Persyaratan"
// ============================================================================

@Composable
private fun MagangTabBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Tab Deskripsi
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Deskripsi",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTabIndex == 0) NavyDark else SlateGray
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(if (selectedTabIndex == 0) MagangAmberPrimary else Color.Transparent)
            )
        }

        // Tab Persyaratan
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(1) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Persyaratan",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTabIndex == 1) NavyDark else SlateGray
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(if (selectedTabIndex == 1) MagangAmberPrimary else Color.Transparent)
            )
        }
    }
}

// ============================================================================
// Komponen Checklist Item dengan Kotak Amber
// ============================================================================

@Composable
private fun ChecklistItemRow(
    text: String,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kotak Ceklis Amber Lembut
        Image(painter = painterResource(id = R.drawable.ic_requirement_check), contentDescription = null, modifier = Modifier.size(28.dp))

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NavyDark
            )
        )
    }
}

// ============================================================================
// Komponen Kartu Periode Pendaftaran
// ============================================================================

@Composable
private fun PeriodePendaftaranCard(
    periode: String,
    mulaiMagang: String,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF9F2)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, MagangAmberTagBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "Periode pendaftaran",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = periode,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )

            Text(
                text = "Mulai magang: $mulaiMagang",
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
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailMagangScreenPreview() {
    LenteraTheme {
        DetailMagangScreen()
    }
}
