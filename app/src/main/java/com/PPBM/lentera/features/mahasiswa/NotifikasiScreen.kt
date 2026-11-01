package com.PPBM.lentera.features.mahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.ui.theme.*

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
    val imageVector: ImageVector,
    val iconContainer: Color,
    val iconTint: Color,
    val belumDibaca: Boolean
)

private val SampleHariIni = listOf(
    NotifikasiItem(
        judul = "Laporan masuk perlu verifikasi",
        isi = "1 laporan baru memerlukan tinjauan admin sebelum diproses oleh LENTERA Siaga.",
        meta = "Laporan Masuk · 10 menit lalu",
        kategori = KategoriNotifikasi.LAPORAN,
        imageVector = Icons.Default.Notifications,
        iconContainer = LenteraColors.TealSoft,
        iconTint = LenteraColors.Teal,
        belumDibaca = true
    ),
    NotifikasiItem(
        judul = "Draf lowongan perlu ditinjau",
        isi = "3 draf lowongan belum diterbitkan. Tinjau detail tempat magang dan dokumen pendukung.",
        meta = "Portal Magang · 1 jam lalu",
        kategori = KategoriNotifikasi.MAGANG,
        imageVector = Icons.Default.DateRange,
        iconContainer = LenteraColors.GoldSoft,
        iconTint = LenteraColors.Gold,
        belumDibaca = true
    )
)

private val SampleSebelumnya = listOf(
    NotifikasiItem(
        judul = "Pembaruan penanganan laporan",
        isi = "2 laporan telah diperbarui statusnya oleh tim LENTERA Siaga.",
        meta = "Laporan Masuk · 12 Okt 2026",
        kategori = KategoriNotifikasi.LAPORAN,
        imageVector = Icons.Default.CheckCircle,
        iconContainer = LenteraColors.GreenSoft,
        iconTint = LenteraColors.Green,
        belumDibaca = false
    ),
    NotifikasiItem(
        judul = "Tempat magang aktif diperbarui",
        isi = "Data tempat magang PT. Cahaya Digital telah diperbarui oleh admin.",
        meta = "Portal Magang · 10 Okt 2026",
        kategori = KategoriNotifikasi.MAGANG,
        imageVector = Icons.Default.CheckCircle,
        iconContainer = LenteraColors.GreenSoft,
        iconTint = LenteraColors.Green,
        belumDibaca = false
    )
)

@Composable
fun NotifikasiScreen(
    hariIni: List<NotifikasiItem> = SampleHariIni,
    sebelumnya: List<NotifikasiItem> = SampleSebelumnya,
    onBackClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onTandaiSemuaDibacaClick: () -> Unit = {},
    onNotifikasiClick: (NotifikasiItem) -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var filter by remember { mutableStateOf(KategoriNotifikasi.SEMUA) }

    fun cocok(item: NotifikasiItem) =
        filter == KategoriNotifikasi.SEMUA || item.kategori == filter

    val tampilHariIni = hariIni.filter(::cocok)
    val tampilSebelumnya = sebelumnya.filter(::cocok)
    val jumlahBelumDibaca = (hariIni + sebelumnya).count { it.belumDibaca }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                NotifikasiHeader(
                    onBackClick = onBackClick,
                    onAvatarClick = onAvatarClick
                )
            }
            item {
                BarisStatus(
                    jumlahBelumDibaca = jumlahBelumDibaca,
                    onTandaiSemuaClick = onTandaiSemuaDibacaClick
                )
            }
            item { FilterNotifikasi(selected = filter, onSelect = { filter = it }) }

            if (tampilHariIni.isNotEmpty()) {
                item { JudulKelompok(text = "Hari ini") }
                items(tampilHariIni) { item ->
                    NotifikasiCard(item = item, onClick = { onNotifikasiClick(item) })
                }
            }
            if (tampilSebelumnya.isNotEmpty()) {
                item { JudulKelompok(text = "Sebelumnya") }
                items(tampilSebelumnya) { item ->
                    NotifikasiCard(item = item, onClick = { onNotifikasiClick(item) })
                }
            }
            item {
                Text(
                    text = "Kamu sudah melihat semua notifikasi.",
                    color = LenteraColors.SlateText,
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun NotifikasiHeader(
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LenteraBackIcon(onClick = onBackClick)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Notifikasi",
                color = LenteraColors.NavyDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans
            )
            Text(
                text = "Kabar operasional kampus · LENTERA",
                color = LenteraColors.NavyDark,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans
            )
        }
        ProfileAvatar(
            size = 48.dp,
            modifier = Modifier.clickable(onClick = onAvatarClick)
        )
    }
}

@Composable
private fun BarisStatus(
    jumlahBelumDibaca: Int,
    onTandaiSemuaClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$jumlahBelumDibaca belum dibaca",
            color = LenteraColors.NavyDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans
        )
        Text(
            text = "Tandai semua dibaca",
            color = LenteraColors.NavyPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans,
            modifier = Modifier.clickable(onClick = onTandaiSemuaClick)
        )
    }
}

@Composable
private fun FilterNotifikasi(
    selected: KategoriNotifikasi,
    onSelect: (KategoriNotifikasi) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        KategoriNotifikasi.entries.forEach { item ->
            val aktif = item == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (aktif) LenteraColors.NavyPrimary else LenteraColors.Surface)
                    .clickable { onSelect(item) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.label,
                    color = if (aktif) Color.White else LenteraColors.NavyPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans
                )
            }
        }
    }
}

@Composable
private fun JudulKelompok(text: String) {
    Text(
        text = text,
        color = LenteraColors.NavyDark,
        fontSize = 15.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = PlusJakartaSans,
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
private fun NotifikasiCard(
    item: NotifikasiItem,
    onClick: () -> Unit
) {
    LenteraCard(withShadow = true, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.imageVector,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = item.judul,
                        color = LenteraColors.NavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        modifier = Modifier.weight(1f)
                    )
                    if (item.belumDibaca) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LenteraColors.NavyPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.isi,
                    color = LenteraColors.SlateText,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.meta,
                    color = LenteraColors.SlateText,
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NotifikasiScreenPreview() {
    LenteraTheme {
        NotifikasiScreen()
    }
}