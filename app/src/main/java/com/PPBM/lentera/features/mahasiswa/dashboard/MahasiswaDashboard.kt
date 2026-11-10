package com.PPBM.lentera.features.mahasiswa.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import android.net.Uri
import com.PPBM.lentera.core.designsystem.components.ProfileAvatarImage
import com.PPBM.lentera.core.designsystem.components.LenteraNotificationBell
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.mahasiswa.model.LaporanSaya
import com.PPBM.lentera.backend.mahasiswa.model.LowonganTerbit
import com.PPBM.lentera.core.designsystem.components.LenteraBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBlueDark
import com.PPBM.lentera.core.designsystem.theme.LenteraBlueLight
import com.PPBM.lentera.core.designsystem.theme.LenteraGreen
import com.PPBM.lentera.core.designsystem.theme.LenteraGreenBg
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTeal
import com.PPBM.lentera.core.designsystem.theme.LenteraTealBg
import com.PPBM.lentera.core.designsystem.theme.LenteraTextPrimary
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.LenteraYellow
import com.PPBM.lentera.core.designsystem.theme.LenteraYellowBg
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

class MahasiswaDashboard {

    @Composable
    fun mhsDashboard(
        modifier: Modifier = Modifier,
        photoUri: Uri? = null,
        photoUrl: String? = null,
        userName: String = "Mahasiswa Lentera",
        totalLaporan: Int = 0,
        laporanDiperiksa: Int = 0,
        laporanSelesai: Int = 0,
        totalLamaran: Int = 0,
        lamaranStatusText: String = "Tidak ada update",
        aktivitasList: List<LaporanSaya> = emptyList(),
        lowonganList: List<LowonganTerbit> = emptyList(),
        hasUnreadNotification: Boolean = true,
        onNotificationClick: () -> Unit = {},
        onTabSelected: (LenteraTab) -> Unit = {},
        onProfileClick: () -> Unit = { onTabSelected(LenteraTab.PROFIL) },
        onSiagaClick: () -> Unit = { onTabSelected(LenteraTab.PENGADUAN) },
        onMagangClick: () -> Unit = { onTabSelected(LenteraTab.MAGANG_HUB) },
        onAktivitasLihatSemuaClick: () -> Unit = { onTabSelected(LenteraTab.PENGADUAN) },
        onAktivitasItemClick: (String) -> Unit = { onTabSelected(LenteraTab.PENGADUAN) },
        onLowonganLihatSemuaClick: () -> Unit = { onTabSelected(LenteraTab.MAGANG_HUB) },
        onLowonganItemClick: (String) -> Unit = { onTabSelected(LenteraTab.MAGANG_HUB) }
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
                            ProfileAvatarImage(
                                photoUri = photoUri,
                                photoUrl = photoUrl,
                                modifier = Modifier.size(40.dp)
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
                    LenteraNotificationBell(
                        hasUnread = hasUnreadNotification,
                        onClick = onNotificationClick,
                        tint = Color.White
                    )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                    ) {
                        // Kartu 1: Lentera Siaga
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onSiagaClick() }
                                .background(
                                    color = LenteraTealBg,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shield),
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
                                text = "$totalLaporan LAPORAN",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = "$laporanDiperiksa Diperiksa\n$laporanSelesai Selesai",
                                minLines = 2,
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
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onMagangClick() }
                                .background(
                                    color = LenteraYellowBg,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_briefcase_business),
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
                                text = "$totalLamaran LAMARAN",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LenteraNavy
                                )
                            )
                            Text(
                                text = lamaranStatusText,
                                minLines = 2,
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
                                ),
                                modifier = Modifier.clickable { onAktivitasLihatSemuaClick() }
                            )
                        }

                        if (aktivitasList.isEmpty()) {
                            Text(
                                text = "Belum ada aktivitas laporan.",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaSans,
                                    color = LenteraTextSecondary
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            aktivitasList.forEach { item ->
                                val (statusBg, statusColor) = when (item.status) {
                                    "selesai" -> Pair(LenteraGreenBg, LenteraGreen)
                                    "ditolak" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
                                    else -> Pair(LenteraYellowBg, LenteraNavy)
                                }
                                AktivitasItem(
                                    title = item.judul,
                                    date = item.tanggalKejadian,
                                    status = item.status.replaceFirstChar { it.uppercase() },
                                    statusBg = statusBg,
                                    statusColor = statusColor,
                                    onClick = { onAktivitasItemClick(item.id) }
                                )
                            }
                        }
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
                                ),
                                modifier = Modifier.clickable { onLowonganLihatSemuaClick() }
                            )
                        }

                        // Horizontal list kartu rekomendasi magang
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            if (lowonganList.isEmpty()) {
                                MagangCard(
                                    title = "EXPLORE MAGANG",
                                    company = "Mitra Kampus",
                                    location = "Indonesia"
                                )
                            } else {
                                lowonganList.forEach { lowongan ->
                                    MagangCard(
                                        title = lowongan.posisi.uppercase(),
                                        company = lowongan.namaPerusahaan,
                                        location = lowongan.kota
                                    )
                                }
                            }
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
        statusColor: Color,
        onClick: () -> Unit = {}
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
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
        location: String,
        onClick: () -> Unit = {}
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .width(160.dp)
                .clickable { onClick() }
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
                    painter = painterResource(id = R.drawable.ic_group),
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