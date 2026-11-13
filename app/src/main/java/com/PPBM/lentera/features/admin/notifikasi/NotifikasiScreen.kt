package com.PPBM.lentera.features.admin.notifikasi

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import com.PPBM.lentera.core.designsystem.components.AdminBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon
import com.PPBM.lentera.core.designsystem.theme.*

enum class KategoriNotifikasi(val label: String) {
    SEMUA("Semua"),
    LAPORAN("Laporan"),
    MAGANG("Magang")
}

data class NotifikasiItem(
    val judul: String,
    val isi: String,
    val meta: String,
    val kategori: KategoriNotifikasi,
    @param:DrawableRes val iconRes: Int,
    val belumDibaca: Boolean
)

private val SampleHariIni = listOf(
    NotifikasiItem(
        judul = "Laporan masuk perlu verifikasi",
        isi = "1 laporan baru memerlukan tinjauan admin sebelum diproses oleh LENTERA Siaga.",
        meta = "LENTERA Siaga · 10 menit lalu",
        kategori = KategoriNotifikasi.LAPORAN,
        iconRes = R.drawable.ic_category,
        belumDibaca = true
    ),
    NotifikasiItem(
        judul = "Draf lowongan perlu ditinjau",
        isi = "3 draf lowongan belum diterbitkan. Tinjau detail tempat magang dan dokumen pendukung.",
        meta = "Magang Hub · 1 jam lalu",
        kategori = KategoriNotifikasi.MAGANG,
        iconRes = R.drawable.ic_category_2,
        belumDibaca = true
    )
)

private val SampleSebelumnya = listOf(
    NotifikasiItem(
        judul = "Pembaruan penanganan laporan",
        isi = "2 laporan telah diperbarui statusnya oleh tim LENTERA Siaga.",
        meta = "LENTERA Siaga · 12 Okt 2026",
        kategori = KategoriNotifikasi.LAPORAN,
        iconRes = R.drawable.ic_category_3,
        belumDibaca = false
    ),
    NotifikasiItem(
        judul = "Tempat magang aktif diperbarui",
        isi = "Data tempat magang PT. Cahaya Digital telah diperbarui oleh admin.",
        meta = "Magang Hub · 10 Okt 2026",
        kategori = KategoriNotifikasi.MAGANG,
        iconRes = R.drawable.ic_category_2,
        belumDibaca = false
    )
)

@Composable
fun AdminNotifikasiScreen(
    hariIni: List<NotifikasiItem> = SampleHariIni,
    sebelumnya: List<NotifikasiItem> = SampleSebelumnya,
    onBackClick: () -> Unit = {},
    onPengaturanClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTandaiSemuaDibacaClick: () -> Unit = {},
    onNotifikasiClick: (NotifikasiItem) -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var filter by remember { mutableStateOf(KategoriNotifikasi.SEMUA) }
    var markAllRead by remember { mutableStateOf(false) }

    fun cocok(item: NotifikasiItem) =
        filter == KategoriNotifikasi.SEMUA || item.kategori == filter

    val tampilHariIni = hariIni.filter(::cocok)
    val tampilSebelumnya = sebelumnya.filter(::cocok)
    val jumlahBelumDibaca = if (markAllRead) 0 else (hariIni + sebelumnya).count { it.belumDibaca }

    Scaffold(
        containerColor = LenteraBg,
        bottomBar = {
            AdminBottomBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Tombol Kembali & Pengaturan Notifikasi
            item {
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
                            .padding(vertical = 4.dp)
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
            }

            // 2. Judul & Subjudul (28.sp ExtraBold)
            item {
                Column {
                    Text(
                        text = "Notifikasi",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = "Kabar operasional kampus · LENTERA",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = LenteraTextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }
            }

            // 3. Sub-header: "$jumlah belum dibaca" & "Tandai semua dibaca"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$jumlahBelumDibaca belum dibaca",
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
                            markAllRead = true
                            onTandaiSemuaDibacaClick()
                        }
                    )
                }
            }

            // 4. Filter Chips (Semua, Laporan, Magang - Bobot Seimbang)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KategoriNotifikasi.entries.forEach { kategori ->
                        FilterChipItem(
                            label = kategori.label,
                            isSelected = filter == kategori,
                            onClick = { filter = kategori },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Section: Hari Ini
            if (tampilHariIni.isNotEmpty()) {
                item {
                    Text(
                        text = "Hari ini",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(tampilHariIni) { notif ->
                    NotifikasiCardAdmin(
                        item = notif,
                        isUnread = !markAllRead && notif.belumDibaca,
                        onClick = { onNotifikasiClick(notif) }
                    )
                }
            }

            // 6. Section: Sebelumnya
            if (tampilSebelumnya.isNotEmpty()) {
                item {
                    Text(
                        text = "Sebelumnya",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(tampilSebelumnya) { notif ->
                    NotifikasiCardAdmin(
                        item = notif,
                        isUnread = !markAllRead && notif.belumDibaca,
                        onClick = { onNotifikasiClick(notif) }
                    )
                }
            }

            // 7. Footer Notifikasi
            item {
                Text(
                    text = "Kamu sudah melihat semua notifikasi.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
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
private fun NotifikasiCardAdmin(
    item: NotifikasiItem,
    isUnread: Boolean,
    onClick: () -> Unit
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
            painter = painterResource(id = item.iconRes),
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
                    text = item.judul,
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
                text = item.isi,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = LenteraTextSecondary
            )

            Text(
                text = item.meta,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AdminNotifikasiScreenPreview() {
    LenteraTheme {
        AdminNotifikasiScreen()
    }
}
