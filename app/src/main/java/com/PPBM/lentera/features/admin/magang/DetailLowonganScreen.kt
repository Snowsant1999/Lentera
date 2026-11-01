package com.PPBM.lentera.features.admin.magang
import com.PPBM.lentera.features.admin.magang.components.*

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun DetailLowonganScreen(
    onNavigateToPublikasiLowongan: () -> Unit = {},
    onNavigateToFormLowongan: () -> Unit = {},
    onBackToDaftarLowongan: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "DETAIL LOWONGAN",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToDaftarLowongan
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang")
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
            // 1. Header & Detail Content Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Company Mark & Title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAF2F8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logo_company_mark),
                                    contentDescription = "Logo",
                                    tint = MagangNavyDark,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "UI/UX Design",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "PT. Maju Mundur",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Badge Tempat Aktif
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MagangActiveGreenBg)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tempat aktif",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MagangActiveGreenText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lokasi & Durasi
                        Text(
                            text = "Lokasi & durasi",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Bontang · Di kantor · 3 Bulan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Periode & Kuota
                        Text(
                            text = "Periode & kuota",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Januari–Maret 2027 · 2 mahasiswa",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Batas Pendaftaran (Date icon)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_date_alt),
                                contentDescription = "Date",
                                tint = MagangNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Batas pendaftaran",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "30 November 2026",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Lamaran lowongan ini
                        Text(
                            text = "Lamaran lowongan ini",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "0 · Belum dipublikasikan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Deskripsi Pekerjaan
                        Text(
                            text = "Deskripsi pekerjaan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mendukung riset pengguna, membuat wireframe dan prototipe, serta berkolaborasi dengan tim pengembang untuk menyempurnakan pengalaman produk digital.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangNavyPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Persyaratan
                        Text(
                            text = "Persyaratan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val bullets = listOf(
                            "Mahasiswa aktif minimal semester 5",
                            "Menguasai Figma dan dasar desain UI/UX",
                            "Melampirkan CV dan portofolio",
                            "Bersedia mengikuti magang selama 3 bulan"
                        )
                        bullets.forEach { bullet ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Text(
                                    text = bullet,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 19.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangNavyPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 2. Timestamp Info
            item {
                Text(
                    text = "Dibuat admin 16 Okt 2026, 15:30 WITA. Draf periode baru, terpisah dari lowongan yang sudah terbit.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // 3. Button: Tinjau & Terbitkan
            item {
                Button(
                    onClick = onNavigateToPublikasiLowongan,
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
                        text = "Tinjau & Terbitkan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // 4. Button: Ubah Lowongan
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToFormLowongan() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ubah Lowongan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // 5. Button: Kembali ke Daftar Lowongan
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToDaftarLowongan() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Daftar Lowongan",
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetailLowonganPreview() {
    DetailLowonganScreen()
}
