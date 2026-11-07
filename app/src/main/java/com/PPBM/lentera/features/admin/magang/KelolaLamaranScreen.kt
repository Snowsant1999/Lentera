package com.PPBM.lentera.features.admin.magang

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.features.admin.magang.components.*

data class PelamarLamaranItem(
    val id: String = "LNT-MG-2026-0891",
    val namaMahasiswa: String = "Budi Santoso",
    val nim: String = "2023110045",
    val jurusan: String = "Teknologi Informasi",
    val prodi: String = "Teknik Informatika",
    val posisi: String = "UI/UX Designer Intern",
    val mitra: String = "PT Maju Mundur",
    val tanggalLamar: String = "24 Okt 2026",
    val status: String = "Perlu Perbaikan",
    val cvFileName: String = "cv_budi_santoso.pdf (1.2 MB)",
    val suratPengantarFileName: String = "surat_pengantar_fakultas.pdf (850 KB)",
    val transkripFileName: String = "transkrip_nilai_semester_4.pdf (1.8 MB)",
    val catatanRevisi: String = "Transkrip Nilai Terakhir buram atau tidak terbaca. Harap unggah scan dokumen asli beresolusi tinggi sebelum 15 Nov 2026."
)

@Composable
fun KelolaLamaranScreen(
    lamaranList: List<PelamarLamaranItem> = SampleDaftarLamaran,
    onNavigateToDetailValidasi: (PelamarLamaranItem) -> Unit = {},
    onNavigateToMitra: () -> Unit = {},
    onNavigateToLowongan: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterChips = listOf(
        "Semua",
        "Menunggu Verifikasi",
        "Perlu Perbaikan",
        "Dikirim ke Mitra",
        "Wawancara",
        "Diterima",
        "Ditolak"
    )

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "MAGANG HUB",
                subtitle = "Kelola Lamaran Masuk · LENTERA"
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang Hub", onNavClick = onNavClick)
        },
        containerColor = MagangBgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Sub-tab Pills (Mitra, Lowongan, Lamaran Masuk)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tab 1: Mitra Magang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillInactive)
                            .clickable { onNavigateToMitra() }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mitra Magang",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        )
                    }

                    // Tab 2: Lowongan
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillInactive)
                            .clickable { onNavigateToLowongan() }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kelola Lowongan",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        )
                    }

                    // Tab 3: Lamaran Masuk (Active)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MagangPillOrange)
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lamaran Masuk (${lamaranList.size})",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                    }
                }
            }

            // 2. Search Bar
            item {
                MagangSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Cari nama mahasiswa, NIM, atau posisi"
                )
            }

            // 3. Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterChips.forEachIndexed { index, label ->
                        val isSelected = selectedFilterIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) MagangPillOrange else Color.White)
                                .clickable { selectedFilterIndex = index }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MagangNavyDark else MagangTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Header Daftar Pelamar
            item {
                val filterName = filterChips[selectedFilterIndex]
                val filtered = lamaranList.filter { item ->
                    val matchQuery = searchQuery.isBlank() ||
                            item.namaMahasiswa.contains(searchQuery, ignoreCase = true) ||
                            item.nim.contains(searchQuery, ignoreCase = true) ||
                            item.posisi.contains(searchQuery, ignoreCase = true)
                    val matchFilter = when (filterName) {
                        "Semua" -> true
                        else -> item.status.equals(filterName, ignoreCase = true)
                    }
                    matchQuery && matchFilter
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Pelamar Masuk",
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                    Text(
                        text = "${filtered.size} berkas",
                        style = TextStyle(
                            fontSize = 12.5.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = MagangTextSecondary
                        )
                    )
                }
            }

            // List of Pelamar
            val filterName = filterChips[selectedFilterIndex]
            val filtered = lamaranList.filter { item ->
                val matchQuery = searchQuery.isBlank() ||
                        item.namaMahasiswa.contains(searchQuery, ignoreCase = true) ||
                        item.nim.contains(searchQuery, ignoreCase = true) ||
                        item.posisi.contains(searchQuery, ignoreCase = true)
                val matchFilter = when (filterName) {
                    "Semua" -> true
                    else -> item.status.equals(filterName, ignoreCase = true)
                }
                matchQuery && matchFilter
            }

            if (filtered.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Tidak ada berkas lamaran yang sesuai.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                color = MagangTextSecondary
                            ),
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(filtered) { pelamar ->
                    PelamarCardItem(
                        pelamar = pelamar,
                        onClick = { onNavigateToDetailValidasi(pelamar) }
                    )
                }
            }
        }
    }
}

@Composable
fun PelamarCardItem(
    pelamar: PelamarLamaranItem,
    onClick: () -> Unit = {}
) {
    val statusColors = when (pelamar.status) {
        "Menunggu Verifikasi" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "Perlu Perbaikan" -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
        "Dikirim ke Mitra" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "Wawancara" -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
        "Diterima" -> Pair(Color(0xFFD1FAE5), Color(0xFF065F46))
        "Ditolak" -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pelamar.id,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = MagangTextSecondary
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColors.first)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = pelamar.status,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = statusColors.second
                        )
                    )
                }
            }

            Text(
                text = pelamar.namaMahasiswa,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    color = MagangNavyDark
                )
            )

            Text(
                text = "${pelamar.nim} · ${pelamar.prodi}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaFont,
                    color = MagangTextSecondary
                )
            )

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = pelamar.posisi,
                        style = TextStyle(
                            fontSize = 13.5.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = MagangNavyDark
                        )
                    )
                    Text(
                        text = "${pelamar.mitra} · Dilamar ${pelamar.tanggalLamar}",
                        style = TextStyle(
                            fontSize = 11.5.sp,
                            fontFamily = PlusJakartaFont,
                            color = MagangTextSecondary
                        )
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = "Detail",
                    tint = MagangNavyDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

val SampleDaftarLamaran = listOf(
    PelamarLamaranItem(
        id = "LNT-MG-2026-0891",
        namaMahasiswa = "Budi Santoso",
        nim = "2023110045",
        jurusan = "Teknologi Informasi",
        prodi = "Teknik Informatika",
        posisi = "UI/UX Designer Intern",
        mitra = "PT Maju Mundur",
        tanggalLamar = "24 Okt 2026",
        status = "Perlu Perbaikan",
        catatanRevisi = "Transkrip Nilai Terakhir buram atau tidak terbaca. Harap unggah scan dokumen asli beresolusi tinggi sebelum 15 Nov 2026."
    ),
    PelamarLamaranItem(
        id = "LNT-MG-2026-0892",
        namaMahasiswa = "Siti Rahma",
        nim = "2023110022",
        jurusan = "Teknologi Informasi",
        prodi = "Teknik Informatika",
        posisi = "Web Developer",
        mitra = "PT Bismillah Berkah",
        tanggalLamar = "25 Okt 2026",
        status = "Menunggu Verifikasi"
    ),
    PelamarLamaranItem(
        id = "LNT-MG-2026-0888",
        namaMahasiswa = "Ahmad Faisal",
        nim = "2023110011",
        jurusan = "Teknologi Informasi",
        prodi = "Sistem Informasi",
        posisi = "UI/UX Designer Intern",
        mitra = "PT Maju Mundur",
        tanggalLamar = "22 Okt 2026",
        status = "Dikirim ke Mitra"
    ),
    PelamarLamaranItem(
        id = "LNT-MG-2026-0870",
        namaMahasiswa = "Rizky Ramadhan",
        nim = "2023110034",
        jurusan = "Teknologi Informasi",
        prodi = "Teknik Informatika",
        posisi = "Content Creator",
        mitra = "PT Cahaya Digital",
        tanggalLamar = "18 Okt 2026",
        status = "Wawancara"
    )
)

@Preview(showBackground = true)
@Composable
fun KelolaLamaranScreenPreview() {
    LenteraTheme {
        KelolaLamaranScreen()
    }
}

