package com.PPBM.lentera.features.admin.profil
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.*

data class KategoriBantuan(
    val judul: String,
    val deskripsi: String,
    val imageVector: ImageVector
)

data class FaqItem(
    val pertanyaan: String,
    val jawaban: String
)

private val SampleKategori = listOf(
    KategoriBantuan(
        "Kelola Akun Mahasiswa",
        "Aktivasi, peran, dan data profil mahasiswa",
        Icons.Default.AccountBox
    ),
    KategoriBantuan(
        "Verifikasi Laporan",
        "Tinjau, tugaskan, dan catat status laporan",
        Icons.Default.Notifications
    )
)

private val SampleFaq = listOf(
    FaqItem(
        "Bagaimana cara menambah akun mahasiswa baru?",
        "Gunakan tombol Tambah Mahasiswa di Beranda Admin. Pastikan data profil, NIM, " +
                "dan program studi sudah sesuai sebelum aktivasi akun."
    ),
    FaqItem(
        "Bagaimana cara menugaskan laporan ke petugas?",
        "Buka detail laporan di menu Laporan, pilih petugas yang menangani, lalu simpan penugasan."
    ),
    FaqItem(
        "Bagaimana cara menerbitkan tempat magang dan lowongan?",
        "Buka menu Magang, lengkapi data tempat magang dan dokumen pendukung, lalu terbitkan dari draf."
    )
)

@Composable
fun BantuanAdminScreen(
    kategori: List<KategoriBantuan> = SampleKategori,
    faq: List<FaqItem> = SampleFaq,
    jamLayanan: String = "Senin–Jumat · 08.00–16.00 WIB",
    onBackClick: () -> Unit = {},
    onKategoriClick: (KategoriBantuan) -> Unit = {},
    onHubungiDukunganClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var terbuka by remember { mutableStateOf<Int?>(0) }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LenteraCompactHeader(
                backLabel = "Profil",
                title = "BANTUAN ADMIN",
                description = "Panduan operasional untuk kelola akun mahasiswa, laporan, " +
                        "dan publikasi magang.",
                onBackClick = onBackClick
            )
            LenteraSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Cari topik bantuan...",
                height = 48.dp
            )
            KategoriBantuanRow(kategori = kategori, onClick = onKategoriClick)
            Text(
                text = "Pertanyaan umum",
                color = LenteraColors.NavyDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.padding(top = 6.dp)
            )
            FaqCard(
                faq = faq.filter { query.isBlank() || it.pertanyaan.contains(query, ignoreCase = true) },
                terbuka = terbuka,
                onToggle = { index -> terbuka = if (terbuka == index) null else index }
            )
            DukunganCard(
                jamLayanan = jamLayanan,
                onHubungiClick = onHubungiDukunganClick
            )
        }
    }
}

@Composable
private fun KategoriBantuanRow(
    kategori: List<KategoriBantuan>,
    onClick: (KategoriBantuan) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        kategori.forEach { item ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LenteraColors.BlueGray)
                    .clickable { onClick(item) }
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = item.imageVector,
                    contentDescription = null,
                    tint = LenteraColors.NavyDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = item.judul,
                    color = LenteraColors.NavyDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.deskripsi,
                    color = LenteraColors.SlateText,
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FaqCard(
    faq: List<FaqItem>,
    terbuka: Int?,
    onToggle: (Int) -> Unit
) {
    LenteraCard(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        withShadow = true
    ) {
        faq.forEachIndexed { index, item ->
            val expanded = terbuka == index
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(index) }
                    .padding(vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = item.pertanyaan,
                        color = LenteraColors.NavyDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (expanded) LenteraColors.NavyDark else LenteraColors.SlateText,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(18.dp)
                    )
                }
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.jawaban,
                        color = LenteraColors.SlateText,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        lineHeight = 18.sp
                    )
                }
            }
            if (index < faq.lastIndex) LenteraDivider()
        }
    }
}

@Composable
private fun DukunganCard(
    jamLayanan: String,
    onHubungiClick: () -> Unit
) {
    LenteraCard(withShadow = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LenteraColors.Background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LenteraColors.NavyDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Masih butuh bantuan?",
                    color = LenteraColors.NavyDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans
                )
                Text(
                    text = "Tim LENTERA siap membantu operasional admin kampus.",
                    color = LenteraColors.SlateText,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        LenteraButton(
            text = "Hubungi Dukungan",
            onClick = onHubungiClick,
            leadingIcon = Icons.Default.Email,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = jamLayanan,
            color = LenteraColors.SlateText,
            fontSize = 11.sp,
            fontFamily = PlusJakartaSans,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BantuanAdminScreenPreview() {
    LenteraTheme {
        BantuanAdminScreen()
    }
}