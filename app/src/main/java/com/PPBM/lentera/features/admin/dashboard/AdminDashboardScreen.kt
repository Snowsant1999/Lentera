package com.PPBM.lentera.features.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.PPBM.lentera.core.designsystem.components.AdminBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigateToMahasiswa: () -> Unit = {},
    onNavigateToSiaga: () -> Unit = {},
    onNavigateToMagang: () -> Unit = {},
    onNavigateToLowongan: () -> Unit = onNavigateToMagang,
    onNavigateToProfil: () -> Unit = {},
    onNavigateToNotifikasi: () -> Unit = {},
    onTambahMahasiswa: () -> Unit = onNavigateToMahasiswa,
    onTambahTempatMagang: () -> Unit = onNavigateToMagang,
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AdminBottomBar(
                selected = LenteraNavItem.BERANDA,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. Header Profil Biru Lengkung Konsisten dengan Mahasiswa Dashboard
            item {
                HeaderAdmin(
                    onProfileClick = onNavigateToProfil,
                    onNotificationClick = onNavigateToNotifikasi
                )
            }

            // Area Konten Utama
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // 2. Judul Dashboard
                    JudulDashboard()
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Grid Statistik
                    GridStatistik(
                        onMahasiswaClick = onNavigateToMahasiswa,
                        onSiagaClick = onNavigateToSiaga,
                        onTempatMagangClick = onNavigateToMagang,
                        onLowonganClick = onNavigateToLowongan
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Tombol Aksi Cepat
                    TombolAksiCepat(
                        onTambahMahasiswa = onTambahMahasiswa,
                        onTambahTempatMagang = onTambahTempatMagang
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Kartu Laporan Masuk
                    KartuLaporanMasuk(onClick = onNavigateToSiaga)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 6. Kartu Kelola Magang
                    KartuKelolaMagang(onClick = onNavigateToMagang)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderAdmin(
    userName: String = "Rina Wulandari",
    userRole: String = "Admin Kampus · LENTERA",
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(LenteraBlueDark, LenteraBlueLight)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp)
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
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
                    .background(Color.White, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_2),
                    contentDescription = "Profile",
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
                    text = "Halo, $userName",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = userRole,
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
                painter = painterResource(id = R.drawable.ic_bell_outline),
                contentDescription = "Notifikasi",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun JudulDashboard() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = NavyDark)) {
                    append("KELOLA KAMPUS\nBERSAMA ")
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
        Text(
            text = "Ringkasan layanan · 17 Oktober 2026",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                color = SlateGray
            )
        )
    }
}

@Composable
fun GridStatistik(
    onMahasiswaClick: () -> Unit = {},
    onSiagaClick: () -> Unit = {},
    onTempatMagangClick: () -> Unit = {},
    onLowonganClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Baris 1: Mahasiswa & Lentera Siaga (Tinggi simetris seimbang)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraTealBg,
                iconId = R.drawable.ic_student_accounts,
                iconTint = LenteraTeal,
                count = "128",
                title = "MAHASISWA",
                subtitle = "124 aktif · 4 draf",
                onClick = onMahasiswaClick
            )
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraTealBg,
                iconId = R.drawable.ic_shield,
                iconTint = LenteraTeal,
                count = "24",
                title = "LENTERA SIAGA",
                subtitle = "3 baru · 16 selesai",
                onClick = onSiagaClick
            )
        }

        // Baris 2: Tempat Magang & Lowongan (Tinggi simetris seimbang)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraYellowBg,
                iconId = R.drawable.ic_briefcase_business,
                iconTint = LenteraYellow,
                count = "12",
                title = "TEMPAT MAGANG",
                subtitle = "9 aktif · 2 draf",
                onClick = onTempatMagangClick
            )
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraYellowBg,
                iconId = R.drawable.ic_folder_open,
                iconTint = LenteraYellow,
                count = "8",
                title = "LOWONGAN",
                subtitle = "4 terbit · 3 draf",
                onClick = onLowonganClick
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    iconId: Int,
    iconTint: Color = NavyDark,
    count: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(color = bgColor, shape = RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = count,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
            )
        }
        Text(
            text = title,
            style = TextStyle(
                fontSize = 13.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )
        )
        Text(
            text = subtitle,
            minLines = 2,
            style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                color = SlateGray
            )
        )
    }
}

@Composable
fun TombolAksiCepat(
    onTambahMahasiswa: () -> Unit = {},
    onTambahTempatMagang: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tombol 1: Tambah Mahasiswa
        Button(
            onClick = onTambahMahasiswa,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pencil_outline),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tambah Mhs",
                    style = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                )
            }
        }

        // Tombol 2: Tambah Tempat Magang
        Button(
            onClick = onTambahTempatMagang,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LenteraYellow),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            Text(
                text = "Tambah Magang",
                style = TextStyle(
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NavyDark
                )
            )
        }
    }
}

@Composable
fun KartuLaporanMasuk(onClick: () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Laporan Masuk",
                style = TextStyle(
                    fontSize = 17.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Text(
                text = "Kelola →",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateGray
                )
            )
        }
        Text(
            text = "LTR-2026-00126 · Lampu Koridor Padam",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )
        Text(
            text = "Perlu verifikasi kelengkapan dan penugasan",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                color = SlateGray
            )
        )
    }
}

@Composable
fun KartuKelolaMagang(onClick: () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kelola Tempat Magang",
                style = TextStyle(
                    fontSize = 17.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Text(
                text = "Kelola →",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateGray
                )
            )
        }
        Text(
            text = "PT. Cahaya Digital · 4 Lowongan Terbit",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )
        Text(
            text = "Data tempat & dokumen divalidasi admin",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                color = SlateGray
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
