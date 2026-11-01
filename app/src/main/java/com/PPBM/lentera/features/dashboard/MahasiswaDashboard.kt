package com.PPBM.lentera.features.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraBlueDark
import com.PPBM.lentera.ui.theme.LenteraBlueLight
import com.PPBM.lentera.ui.theme.LenteraGreen
import com.PPBM.lentera.ui.theme.LenteraGreenBg
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraTeal
import com.PPBM.lentera.ui.theme.LenteraTealBg
import com.PPBM.lentera.ui.theme.LenteraTextPrimary
import com.PPBM.lentera.ui.theme.LenteraTextSecondary
import com.PPBM.lentera.ui.theme.LenteraYellow
import com.PPBM.lentera.ui.theme.LenteraYellowBg
import com.PPBM.lentera.ui.theme.PlusJakartaSans

class MahasiswaDashboard {

    @Composable
    fun mhsDashboard(
        modifier: Modifier = Modifier,
        onNotificationClick: () -> Unit = {},
        onTabSelected: (LenteraTab) -> Unit = {}
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(LenteraBg)
        ) {
            // Konten halaman yang dapat di-scroll
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // --- BAGIAN 1: STUDENT HEADER (Linear Gradient) ---
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 0.dp, topEnd =0.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .height(76.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(LenteraBlueDark, LenteraBlueLight)
                            )
                        )
                        .padding(horizontal = 24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Bagian Avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_2),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        }

                        // 2. Bagian Teks Sapaan
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Halo, Budi Santoso",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Mahasiswa",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFFE6F1E9)
                                )
                            )
                        }
                    }

                    // 3. Bagian Lonceng Notifikasi
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onNotificationClick)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "Notifikasi",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // --- BAGIAN 2: KONTEN UTAMA (BODY) ---
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Teks Selamat Datang (Kata LENTERA Berwarna Kuning #E8B86D)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = LenteraNavy)) {
                                append("SELAMAT DATANG\nDI ")
                            }
                            withStyle(style = SpanStyle(color = LenteraYellow)) {
                                append("LENTERA")
                            }
                        },
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 35.sp,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    // SERVICE VIEW (Menampung 2 Kartu: Lentera Siaga & Portal Magang)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Kartu 1: Lentera Siaga
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = LenteraTealBg,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.shield),
                                contentDescription = "Lentera Siaga",
                                tint = LenteraTeal,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "LENTERA SIAGA",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 17.5.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraTeal
                                )
                            )
                            Text(
                                text = "3 LAPORAN",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = "1 Diperiksa\n2 Selesai",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Normal,
                                    color = LenteraTextSecondary
                                )
                            )
                        }

                        // Kartu 2: Portal Magang
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = LenteraYellowBg,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.briefcase_business),
                                contentDescription = "Portal Magang",
                                tint = LenteraYellow,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "PORTAL MAGANG",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 17.5.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraYellow
                                )
                            )
                            Text(
                                text = "0 LAPORAN",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = "Tidak ada Update!",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Normal,
                                    color = LenteraTextSecondary
                                )
                            )
                        }
                    }

                    // --- KARTU 3: AKTIVITAS TERBARU ---
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 16.dp,
                                spotColor = Color(0x09173B57),
                                ambientColor = Color(0x09173B57),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // Header Card
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Aktivitas Terbaru",
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = "Lihat Semua",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LenteraTextSecondary
                                )
                            )
                        }

                        // Item 1: Keracunan Makanan (Diperiksa)
                        AktivitasItem(
                            title = "Keracunan Makanan",
                            date = "12 Okt 2026",
                            status = "Diperiksa",
                            statusBg = LenteraYellowBg,
                            statusColor = LenteraNavy
                        )

                        // Item 2: Pintu Mau Roboh (Selesai)
                        AktivitasItem(
                            title = "Pintu Mau Roboh",
                            date = "10 Okt 2026",
                            status = "Selesai",
                            statusBg = LenteraGreenBg,
                            statusColor = LenteraGreen
                        )

                        // Item 3: Pelecehan Verbal (Selesai)
                        AktivitasItem(
                            title = "Pelecehan Verbal",
                            date = "08 Okt 2026",
                            status = "Selesai",
                            statusBg = LenteraGreenBg,
                            statusColor = LenteraGreen
                        )
                    }

                    // --- KARTU 4: REKOMENDASI MAGANG ---
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Rekomendasi Magang",
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = "Lihat Semua",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LenteraTextSecondary
                                )
                            )
                        }

                        // Horizontal list kartu rekomendasi magang
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            MagangCard(
                                title = "UI/UX DESIGN",
                                company = "PT. Maju Mundur",
                                location = "Bontang"
                            )
                            MagangCard(
                                title = "WEB DEV",
                                company = "PT. Bismillah Berkah",
                                location = "Bontang"
                            )
                            MagangCard(
                                title = "UI/UX DESIGN",
                                company = "PT. Maju Mundur",
                                location = "Bontang"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Bottom Navigation Bar
            LenteraBottomBar(
                currentTab = LenteraTab.BERANDA,
                onTabSelected = onTabSelected
            )
        }
    }

    @Composable
    private fun AktivitasItem(
        title: String,
        date: String,
        status: String,
        statusBg: Color,
        statusColor: Color
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        color = LenteraNavy
                    )
                )
                Text(
                    text = date,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        color = LenteraTextSecondary
                    )
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = statusBg, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = status,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                )
            }
        }
    }

    @Composable
    private fun MagangCard(
        title: String,
        company: String,
        location: String
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .width(160.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = Color(0x09173B57),
                    ambientColor = Color(0x09173B57),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            // Icon Group Company
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEFF5FD), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.group),
                    contentDescription = null,
                    tint = LenteraNavy,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    color = LenteraNavy
                )
            )

            Text(
                text = company,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    color = LenteraTextSecondary
                )
            )

            Text(
                text = location,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    color = LenteraTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Status Available dengan Indikator Hijau
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Available",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        color = LenteraNavy
                    )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF22C55E), CircleShape)
                )
            }
        }
    }

    @Preview(showBackground = true, widthDp = 412, heightDp = 917)
    @Composable
    fun PreviewMhsDashboard() {
        mhsDashboard()
    }
}