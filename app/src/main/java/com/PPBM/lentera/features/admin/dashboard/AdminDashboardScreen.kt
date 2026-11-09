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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.AdminBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*

data class DashboardLaporanItem(
    val id: String,
    val judul: String,
    val waktu: String,
    val status: String,
    val statusBg: Color,
    val statusColor: Color
)

data class DashboardLowonganItem(
    val posisi: String,
    val mitra: String,
    val totalPelamar: Int,
    val status: String,
    val statusBg: Color,
    val statusColor: Color
)

@Composable
fun AdminDashboardScreen(
    userName: String = "Admin Lentera",
    totalMahasiswa: Int = 0,
    totalLaporan: Int = 0,
    totalMitra: Int = 0,
    totalLowongan: Int = 0,
    laporanList: List<DashboardLaporanItem> = emptyList(),
    lowonganList: List<DashboardLowonganItem> = emptyList(),
    onNavigateToMahasiswa: () -> Unit = {},
    onNavigateToSiaga: () -> Unit = {},
    onNavigateToMagang: () -> Unit = {},
    onNavigateToLowongan: () -> Unit = onNavigateToMagang,
    onNavigateToProfil: () -> Unit = {},
    onNavigateToNotifikasi: () -> Unit = {},
    onLaporanClick: (DashboardLaporanItem) -> Unit = { onNavigateToSiaga() },
    onLowonganItemClick: (DashboardLowonganItem) -> Unit = { onNavigateToLowongan() },
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val laporanTerbaru = laporanList
    val lowonganTerbaru = lowonganList

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
                    userName = userName,
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
                        totalMahasiswa = totalMahasiswa,
                        totalLaporan = totalLaporan,
                        totalMitra = totalMitra,
                        totalLowongan = totalLowongan,
                        onMahasiswaClick = onNavigateToMahasiswa,
                        onSiagaClick = onNavigateToSiaga,
                        onTempatMagangClick = onNavigateToMagang,
                        onLowonganClick = onNavigateToLowongan
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Kartu Laporan Masuk Terakhir
                    KartuLaporanMasukTerakhir(
                        laporanList = laporanTerbaru,
                        onLihatSemuaClick = onNavigateToSiaga,
                        onItemClick = onLaporanClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Kartu Lowongan Magang Terbaru (Bukan kelola tempat magang)
                    KartuLowonganMagangTerbaru(
                        lowonganList = lowonganTerbaru,
                        onLihatSemuaClick = onNavigateToLowongan,
                        onItemClick = onLowonganItemClick
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderAdmin(
    userName: String = "Admin Lentera",
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
            // Bagian Avatar
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

            // Bagian Teks Sapaan
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Halo, $userName",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = userRole,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD4E6FA)
                    )
                )
            }
        }

        // Bagian Lonceng Notifikasi (Disamakan dengan Mahasiswa Dashboard)
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
            text = "DASHBOARD ADMIN",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark
            )
        )
        Text(
            text = "Pusat monitoring dan pengelolaan operasional kampus",
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
    totalMahasiswa: Int = 0,
    totalLaporan: Int = 0,
    totalMitra: Int = 0,
    totalLowongan: Int = 0,
    onMahasiswaClick: () -> Unit = {},
    onSiagaClick: () -> Unit = {},
    onTempatMagangClick: () -> Unit = {},
    onLowonganClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Baris 1: Mahasiswa & Lentera Siaga
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
                count = if (totalMahasiswa > 0) totalMahasiswa.toString() else "0",
                title = "MAHASISWA",
                subtitle = if (totalMahasiswa > 0) "$totalMahasiswa terdaftar" else "Belum ada data",
                onClick = onMahasiswaClick
            )
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraTealBg,
                iconId = R.drawable.ic_shield,
                iconTint = LenteraTeal,
                count = if (totalLaporan > 0) totalLaporan.toString() else "0",
                title = "LENTERA SIAGA",
                subtitle = if (totalLaporan > 0) "$totalLaporan laporan masuk" else "Belum ada laporan",
                onClick = onSiagaClick
            )
        }

        // Baris 2: Mitra Magang & Lowongan
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
                count = if (totalMitra > 0) totalMitra.toString() else "0",
                title = "MITRA MAGANG",
                subtitle = if (totalMitra > 0) "$totalMitra mitra terdaftar" else "Belum ada mitra",
                onClick = onTempatMagangClick
            )
            StatCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                bgColor = LenteraYellowBg,
                iconId = R.drawable.ic_folder_open,
                iconTint = LenteraYellow,
                count = if (totalLowongan > 0) totalLowongan.toString() else "0",
                title = "LOWONGAN",
                subtitle = if (totalLowongan > 0) "$totalLowongan lowongan aktif" else "Belum ada lowongan",
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
fun KartuLaporanMasukTerakhir(
    laporanList: List<DashboardLaporanItem>,
    onLihatSemuaClick: () -> Unit = {},
    onItemClick: (DashboardLaporanItem) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Laporan Masuk Terakhir",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Text(
                text = "Lihat Semua →",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    color = LenteraTeal
                ),
                modifier = Modifier.clickable { onLihatSemuaClick() }
            )
        }

        if (laporanList.isEmpty()) {
            Text(
                text = "Belum ada laporan masuk.",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = SlateGray
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                laporanList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onItemClick(item) }
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.judul,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.id} · ${item.waktu}",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSans,
                                    color = SlateGray
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(item.statusBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.status,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = item.statusColor
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
fun KartuLowonganMagangTerbaru(
    lowonganList: List<DashboardLowonganItem>,
    onLihatSemuaClick: () -> Unit = {},
    onItemClick: (DashboardLowonganItem) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lowongan Magang Terbaru",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Text(
                text = "Lihat Semua →",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    color = LenteraYellow
                ),
                modifier = Modifier.clickable { onLihatSemuaClick() }
            )
        }

        if (lowonganList.isEmpty()) {
            Text(
                text = "Belum ada lowongan terdaftar.",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = SlateGray
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                lowonganList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onItemClick(item) }
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.posisi,
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.mitra} · ${item.totalPelamar} pelamar",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSans,
                                    color = SlateGray
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(item.statusBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.status,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    color = item.statusColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    LenteraTheme {
        AdminDashboardScreen()
    }
}
