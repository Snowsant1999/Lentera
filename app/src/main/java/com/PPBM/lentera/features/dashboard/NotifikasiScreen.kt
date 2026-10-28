package com.PPBM.lentera.features.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraBlueDark
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraTextPrimary
import com.PPBM.lentera.ui.theme.LenteraTextSecondary
import com.PPBM.lentera.ui.theme.PlusJakartaSans

enum class NotifikasiFilter {
    SEMUA,
    LAPORAN,
    MAGANG
}

@Composable
fun NotifikasiScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(NotifikasiFilter.SEMUA) }
    var unreadCount by remember { mutableStateOf(2) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Tombol Kembali
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBackClick)
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.open_answer),
                    contentDescription = "Kembali",
                    tint = LenteraNavy,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(180f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Judul dan Subjudul
            Text(
                text = "Notifikasi",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = LenteraNavy
            )

            Text(
                text = "Kabar terbaru dari LENTERA",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Baris Sub-header: "2 belum dibaca" & "Tandai semua dibaca"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$unreadCount belum dibaca",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LenteraNavy
                )

                Text(
                    text = "Tandai semua dibaca",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = LenteraNavy,
                    modifier = Modifier.clickable {
                        unreadCount = 0
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips: Semua, Laporan, Magang
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    label = "Semua",
                    isSelected = selectedFilter == NotifikasiFilter.SEMUA,
                    onClick = { selectedFilter = NotifikasiFilter.SEMUA },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    label = "Laporan",
                    isSelected = selectedFilter == NotifikasiFilter.LAPORAN,
                    onClick = { selectedFilter = NotifikasiFilter.LAPORAN },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    label = "Magang",
                    isSelected = selectedFilter == NotifikasiFilter.MAGANG,
                    onClick = { selectedFilter = NotifikasiFilter.MAGANG },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Hari ini
            if (selectedFilter != NotifikasiFilter.MAGANG) {
                Text(
                    text = "Hari ini",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LenteraNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: Laporan sedang diperiksa
                NotifikasiCard(
                    iconRes = R.drawable.category_icon,
                    title = "Laporan sedang diperiksa",
                    description = "Laporan Keracunan Makanan sedang ditinjau oleh tim LENTERA Siaga.",
                    timeInfo = "LENTERA Siaga · 10 menit lalu",
                    isUnread = unreadCount > 0
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedFilter != NotifikasiFilter.LAPORAN) {
                if (selectedFilter == NotifikasiFilter.MAGANG) {
                    Text(
                        text = "Hari ini",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Item 2: Rekomendasi magang baru
                NotifikasiCard(
                    iconRes = R.drawable.category_icon_2,
                    title = "Rekomendasi magang baru",
                    description = "Posisi UI/UX Design di PT. Maju Mundur tersedia. Yuk, lihat detailnya!",
                    timeInfo = "Portal Magang · 1 jam lalu",
                    isUnread = unreadCount > 1
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 2: Sebelumnya
            if (selectedFilter != NotifikasiFilter.MAGANG) {
                Text(
                    text = "Sebelumnya",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LenteraNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 3: Laporan telah diselesaikan
                NotifikasiCard(
                    iconRes = R.drawable.category_icon_3,
                    title = "Laporan telah diselesaikan",
                    description = "Laporan Pintu Mau Roboh telah ditangani. Terima kasih atas kepedulianmu.",
                    timeInfo = "LENTERA Siaga · 12 Okt 2026",
                    isUnread = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 4: Tindak lanjut laporan selesai
                NotifikasiCard(
                    iconRes = R.drawable.category_icon_3,
                    title = "Tindak lanjut laporan selesai",
                    description = "Penanganan laporan Pelecehan Verbal selesai. Lihat tanggapan tim kami.",
                    timeInfo = "LENTERA Siaga · 10 Okt 2026",
                    isUnread = false
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Footer info
            Text(
                text = "Kamu sudah melihat semua notifikasi.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = LenteraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Navigation Bar
        LenteraBottomBar(
            currentTab = LenteraTab.BERANDA,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) LenteraBlueDark else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else LenteraNavy
        )
    }
}

@Composable
private fun NotifikasiCard(
    iconRes: Int,
    title: String,
    description: String,
    timeInfo: String,
    isUnread: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LenteraNavy,
                    modifier = Modifier.weight(1f)
                )

                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(LenteraNavy, CircleShape)
                    )
                }
            }

            Text(
                text = description,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = LenteraTextSecondary
            )

            Text(
                text = timeInfo,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewNotifikasiScreen() {
    NotifikasiScreen()
}
