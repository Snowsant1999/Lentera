package com.PPBM.lentera.features.admin.dashboard
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme

@Composable
fun AdminDashboardScreen() {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        bottomBar = { AdminDashboardBottomBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(innerPadding)
        ) {
            // 1. Header Profil Biru Lengkung
            item { HeaderAdmin() }

            // Area Konten Utama
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Judul Dashboard
                    JudulDashboard()
                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Grid Statistik
                    GridStatistik()
                    Spacer(modifier = Modifier.height(20.dp))

                    // 4. Tombol Aksi Cepat
                    TombolAksiCepat()
                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Kartu Laporan Masuk
                    KartuLaporanMasuk()
                    Spacer(modifier = Modifier.height(16.dp))

                    // 6. Kartu Kelola Magang
                    KartuKelolaMagang()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderAdmin() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1E3A5F),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.iconamoon_profile_circle_fill),
                    contentDescription = "Profile",
                    tint = Color(0xFF1E3A5F),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Halo, Rina Wulandari",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Admin Kampus · LENTERA",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Icon(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.icomoon_free_notification),
                contentDescription = "Notification",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun JudulDashboard() {
    Column {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF1E3A5F))) {
                    append("KELOLA KAMPUS\nBERSAMA ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFEEB042))) {
                    append("LENTERA")
                }
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Ringkasan layanan · 17 Oktober 2026",
            fontSize = 13.sp,
            color = Color(0xFF7A8B9B)
        )
    }
}

@Composable
fun GridStatistik() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Baris 1: Mahasiswa & Lentera Siaga
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFE6F3F0),
                iconId = com.PPBM.lentera.R.drawable.student_accounts,
                count = "128",
                title = "MAHASISWA",
                subtitle = "124 aktif · 4 belum aktivasi"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFE3F0EC),
                iconId = com.PPBM.lentera.R.drawable.shield_alert,
                count = "24",
                title = "LENTERA SIAGA",
                subtitle = "3 baru · 5 proses · 16 selesai"
            )
        }

        // Baris 2: Tempat Magang & Lowongan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFFEF3E2),
                iconId = com.PPBM.lentera.R.drawable.company_logo,
                count = "12",
                title = "TEMPAT MAGANG",
                subtitle = "9 aktif · 2 draf · 1 arsip"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                bgColor = Color(0xFFFEF3E2),
                iconId = com.PPBM.lentera.R.drawable.folder_open,
                count = "8",
                title = "LOWONGAN",
                subtitle = "4 terbit · 3 draf · 1 ditutup"
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    iconId: Int,
    count: String,
    title: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = title,
                    tint = Color(0xFF1E3A5F),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = count,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E3A5F)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A5F)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF627D98)
            )
        }
    }
}

@Composable
fun TombolAksiCepat() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tombol 1: Tambah Mahasiswa
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.penciloutline),
                    contentDescription = "Tambah Mahasiswa",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tambah Mahasiswa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Tombol 2: Tambah Tempat Magang
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEB042)),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(
                text = "Tambah Tempat Magang",
                color = Color(0xFF1E3A5F),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun KartuLaporanMasuk() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Laporan Masuk",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A5F)
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E3A5F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pelecehan Verbal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A5F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LTR-2026-00123 · 15 Okt 2026",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8B9B)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFEF3E2), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Diperiksa",
                        color = Color(0xFFB57A00),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun KartuKelolaMagang() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kelola Magang",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A5F)
                )
                Text(
                    text = "Kelola",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E3A5F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PT. Cahaya Digital",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A5F)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Data tempat & dokumen dikelola admin",
                fontSize = 12.sp,
                color = Color(0xFF7A8B9B)
            )
        }
    }
}

@Composable
fun AdminDashboardBottomBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_beranda),
                    contentDescription = "Beranda",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E3A5F),
                indicatorColor = Color(0xFF1E3A5F)
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_mahasiswa),
                    contentDescription = "Mahasiswa",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Mahasiswa", fontSize = 11.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF7A8B9B),
                unselectedTextColor = Color(0xFF7A8B9B)
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_laporan),
                    contentDescription = "Laporan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Laporan", fontSize = 11.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF7A8B9B),
                unselectedTextColor = Color(0xFF7A8B9B)
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_magang),
                    contentDescription = "Magang",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Magang", fontSize = 11.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF7A8B9B),
                unselectedTextColor = Color(0xFF7A8B9B)
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_profil),
                    contentDescription = "Profil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Profil", fontSize = 11.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF7A8B9B),
                unselectedTextColor = Color(0xFF7A8B9B)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    LenteraTheme {
        AdminDashboardScreen()
    }
}