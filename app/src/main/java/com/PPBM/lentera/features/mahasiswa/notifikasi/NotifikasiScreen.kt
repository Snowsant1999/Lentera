package com.PPBM.lentera.features.mahasiswa.notifikasi

import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.mahasiswa.model.NotifikasiItem
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBlueDark
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTextPrimary
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

enum class NotifikasiFilter {
    SEMUA,
    LAPORAN,
    MAGANG
}

@Composable
fun MahasiswaNotifikasiScreen(
    notifikasiList: List<NotifikasiItem> = emptyList(),
    onMarkRead: (String) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onPengaturanClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(NotifikasiFilter.SEMUA) }

    val filteredList = when (selectedFilter) {
        NotifikasiFilter.SEMUA -> notifikasiList
        NotifikasiFilter.LAPORAN -> notifikasiList.filter {
            it.kategori.equals("laporan", ignoreCase = true) || it.kategori.equals("siaga", ignoreCase = true)
        }
        NotifikasiFilter.MAGANG -> notifikasiList.filter {
            it.kategori.equals("magang", ignoreCase = true)
        }
    }

    val unreadCount = filteredList.count { !it.isDibaca }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Tombol Kembali & Pengaturan Notifikasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBackClick)
                        .padding(vertical = 8.dp)
                ) {
                    BackArrowIcon(
                        modifier = Modifier.size(20.dp),
                        tint = LenteraNavy
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onPengaturanClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan Notifikasi",
                        tint = LenteraNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pengaturan",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LenteraNavy,
                            fontFamily = PlusJakartaSans
                        )
                    )
                }
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

            // Baris Sub-header
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

                if (unreadCount > 0) {
                    Text(
                        text = "Tandai semua dibaca",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = LenteraNavy,
                        modifier = Modifier.clickable { onMarkAllRead() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
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

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x09173B57),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum Ada Notifikasi",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LenteraNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pembaruan terkait laporan siaga dan lamaran magang akan muncul di sini.",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            color = LenteraTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredList.forEach { notif ->
                        val iconRes = if (notif.kategori.equals("magang", ignoreCase = true)) {
                            R.drawable.ic_category_2
                        } else {
                            R.drawable.ic_category
                        }
                        val formattedTime = if (notif.createdAt.length >= 10) {
                            notif.createdAt.substring(0, 10)
                        } else {
                            notif.createdAt
                        }

                        NotifikasiCard(
                            iconRes = iconRes,
                            title = notif.judul,
                            description = notif.isi,
                            timeInfo = "LENTERA · $formattedTime",
                            isUnread = !notif.isDibaca,
                            onClick = { onMarkRead(notif.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
    isUnread: Boolean,
    onClick: () -> Unit = {}
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
            .clickable(onClick = onClick)
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
fun MahasiswaNotifikasiScreenPreview() {
    MahasiswaNotifikasiScreen()
}
