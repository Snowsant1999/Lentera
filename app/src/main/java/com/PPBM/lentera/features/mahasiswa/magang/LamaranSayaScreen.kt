package com.PPBM.lentera.features.mahasiswa.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import com.PPBM.lentera.backend.mahasiswa.model.LowonganTerbit

data class RiwayatLamaranModel(
    val id: String,
    val lamaranUuid: String = id,
    val posisi: String,
    val perusahaan: String,
    val tanggal: String,
    val status: String,
    val rawStatus: String = "",
    val statusBg: Color,
    val statusText: Color,
    val logoRes: Int = R.drawable.ic_avatar_2
)

/**
 * Halaman Riwayat Lamaran Saya
 */
@Composable
fun LamaranSayaScreen(
    lamaranList: List<LamaranSaya> = emptyList(),
    lowonganList: List<LowonganTerbit> = emptyList(),
    initialFilter: String = "Semua",
    onBackClick: () -> Unit = {},
    onStatusClick: (RiwayatLamaranModel) -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember(initialFilter) { mutableStateOf(initialFilter) }

    val daftarLamaran = remember(lamaranList, lowonganList) {
        lamaranList.map { lamaran ->
            val lowongan = lowonganList.find { it.id == lamaran.lowonganId }
            val posisi = lowongan?.posisi ?: "Lowongan Magang"
            val perusahaan = lowongan?.namaPerusahaan ?: "Mitra Industri"
            val rawStatus = lamaran.status.lowercase()
            val (statusLabel, statusBg, statusText) = when (rawStatus) {
                "diterima" -> Triple("Diterima", MagangPillGreenBg, MagangPillGreenText)
                "ditolak" -> Triple("Ditolak", MagangPillRedBg, MagangPillRedText)
                "perlu_perbaikan" -> Triple("Perlu Perbaikan", MagangAmberPillBg, MagangAmberPillText)
                "seleksi" -> Triple("Seleksi", MagangAmberPillBg, MagangAmberPillText)
                "pemeriksaan" -> Triple("Pemeriksaan", MagangAmberPillBg, MagangAmberPillText)
                else -> Triple("Direview", MagangAmberPillBg, MagangAmberPillText)
            }
            val tanggalStr = if (lamaran.createdAt.length >= 10) {
                "Diajukan ${lamaran.createdAt.substring(0, 10)}"
            } else {
                "Diajukan baru saja"
            }
            RiwayatLamaranModel(
                id = lamaran.nomorLamaran,
                lamaranUuid = lamaran.id,
                posisi = posisi,
                perusahaan = perusahaan,
                tanggal = tanggalStr,
                status = statusLabel,
                rawStatus = rawStatus,
                statusBg = statusBg,
                statusText = statusText,
                logoRes = R.drawable.ic_avatar_2
            )
        }
    }

    val inReviewStatuses = setOf("terkirim", "pemeriksaan", "perlu_perbaikan", "seleksi")
    val filterOptions = listOf(
        "Semua" to daftarLamaran.size,
        "Direview" to daftarLamaran.count { it.rawStatus in inReviewStatuses },
        "Perlu Perbaikan" to daftarLamaran.count { it.rawStatus == "perlu_perbaikan" },
        "Diterima" to daftarLamaran.count { it.rawStatus == "diterima" },
        "Ditolak" to daftarLamaran.count { it.rawStatus == "ditolak" }
    )

    val filteredList = remember(daftarLamaran, selectedFilter, searchQuery) {
        daftarLamaran.filter { item ->
            val matchesFilter = when (selectedFilter) {
                "Semua" -> true
                "Direview" -> item.rawStatus in inReviewStatuses
                "Perlu Perbaikan" -> item.rawStatus == "perlu_perbaikan"
                else -> item.status.equals(selectedFilter, ignoreCase = true)
            }
            val matchesSearch = item.posisi.contains(searchQuery, ignoreCase = true) ||
                item.perusahaan.contains(searchQuery, ignoreCase = true) ||
                item.id.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_button),
                    contentDescription = "Kembali",
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(onClick = onBackClick)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Lamaran Saya",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Banner Pantau Status
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangAmberPillBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF6D8A8)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = null,
                                tint = MagangAmberPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Pantau Status Lamaran",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cek progres tahapan seleksi secara berkala dan perhatikan notifikasi perbaikan berkas.",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.5.sp,
                                    color = SlateGray
                                )
                            )
                        }
                    }
                }
            }

            item {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Cari lowongan atau perusahaan...",
                            style = TextStyle(fontFamily = fontFamily, fontSize = 13.sp, color = SlateGrayLight)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search_alt),
                            contentDescription = "Search",
                            tint = SlateGray,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MagangAmberPrimary,
                        unfocusedBorderColor = CardBorder
                    ),
                    singleLine = true
                )
            }

            item {
                // Filter Tabs (Semua, Diterima, Ditolak, Direview)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { (label, count) ->
                        val isSelected = selectedFilter == label
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NavyDark else Color.White)
                                .border(1.dp, if (isSelected) NavyDark else CardBorder, RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = label }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$label ($count)",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else NavyDark
                                )
                            )
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Belum Ada Lamaran",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Anda belum mengajukan lamaran magang atau tidak ada data yang cocok dengan pencarian/filter.",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    color = SlateGray
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(filteredList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStatusClick(item) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = item.logoRes),
                                contentDescription = item.perusahaan,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.posisi,
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.perusahaan,
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp,
                                        color = SlateGray
                                    )
                                )
                            }
                            // Status Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.statusBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.statusText
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.tanggal,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.5.sp,
                                    color = SlateGray
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onStatusClick(item) }
                            ) {
                                Text(
                                    text = "Lihat Status",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_status_chevron),
                                    contentDescription = null,
                                    tint = NavyDark,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LamaranSayaScreenPreview() {
    LenteraTheme {
        LamaranSayaScreen()
    }
}

