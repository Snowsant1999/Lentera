package com.PPBM.lentera.features.mahasiswa.siaga
import com.PPBM.lentera.core.designsystem.icons.*
import com.PPBM.lentera.core.designsystem.illustrations.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class DokumentasiTindakLanjutItem(
    val tanggal: String,
    val judul: String,
    val placeholderColor: Color = Color(0xFF334257),
    val filePath: String = "",
    val fileType: String = "",
    val fileSizeFormatted: String = ""
)

data class HasilPenangananData(
    val nomorLaporan: String = "",
    val judul: String = "",
    val status: String = "",
    val tanggalLaporan: String = "",
    val kategori: String = "",
    val ringkasanPenanganan: String = "",
    val tanggalPenyelesaian: String = "",
    val listDokumentasi: List<DokumentasiTindakLanjutItem> = emptyList()
)

// ============================================================================
// Main Screen: HasilPenangananScreen
// ============================================================================

/**
 * Halaman Hasil Penanganan Laporan Selesai
 * Sesuai desain UI Hasil Penanganan.png
 *
 * @param data Data hasil penanganan laporan
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onTinjauDokumentasiClick Aksi teks "Tinjau" atau thumbnail dokumentasi
 * @param onKembaliKeRiwayatClick Aksi tombol "Kembali ke Riwayat"
 */
@Composable
fun HasilPenangananScreen(
    data: HasilPenangananData = HasilPenangananData(),
    onBackClick: () -> Unit = {},
    onTinjauDokumentasiClick: () -> Unit = {},
    onItemClick: (DokumentasiTindakLanjutItem) -> Unit = {},
    onKembaliKeRiwayatClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol "Kembali ke Riwayat" di bagian bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenBackground)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SiagaTealPrimary)
                        .clickable(onClick = onKembaliKeRiwayatClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Riwayat",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Bar
            TopBarHasilPenanganan(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Kartu Informasi Utama Laporan
                HeaderHasilLaporanCard(
                    data = data,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Kartu Detail "Hasil Penanganan"
                DetailHasilPenangananCard(
                    data = data,
                    onTinjauClick = onTinjauDokumentasiClick,
                    onItemClick = onItemClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Alias composable
@Composable
fun HasilPenanganan(
    onBackClick: () -> Unit = {},
    onKembaliKeRiwayatClick: () -> Unit = {}
) {
    HasilPenangananScreen(
        onBackClick = onBackClick,
        onKembaliKeRiwayatClick = onKembaliKeRiwayatClick
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarHasilPenanganan(
    onBackClick: () -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            BackArrowIcon(
                modifier = Modifier.size(24.dp),
                tint = NavyDark
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "HASIL PENANGANAN",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ============================================================================
// Kartu Header Ringkasan Laporan
// ============================================================================

@Composable
private fun HeaderHasilLaporanCard(
    data: HasilPenangananData,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.nomorLaporan,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                )

                // Status Badge "Selesai"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SiagaBadgeSelesaiBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.status,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SiagaBadgeSelesaiText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = data.judul,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-box 1: Tanggal Laporan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarIcon(
                    modifier = Modifier.size(22.dp),
                    tint = NavyDark
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Tanggal Laporan",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = SlateGrayLight
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.tanggalLaporan,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-box 2: Kategori
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ListIcon(
                    modifier = Modifier.size(22.dp),
                    tint = NavyDark
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Kategori",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = SlateGrayLight
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.kategori,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Kartu Utama Hasil Penanganan
// ============================================================================

@Composable
private fun DetailHasilPenangananCard(
    data: HasilPenangananData,
    onTinjauClick: () -> Unit,
    onItemClick: (DokumentasiTindakLanjutItem) -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Hasil Penanganan",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-banner "Ringkasan Penanganan"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealBannerBg)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Ikon centang hijau bulat
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF28A745)),
                        contentAlignment = Alignment.Center
                    ) {
                        CheckIcon(
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ringkasan Penanganan",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.ringkasanPenanganan,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGray,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Header Bagian "Dokumentasi Tindak Lanjut"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dokumentasi Tindak Lanjut",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                if (data.listDokumentasi.isNotEmpty()) {
                    Text(
                        text = "Tinjau",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SiagaTealPrimary
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onTinjauClick)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (data.listDokumentasi.isEmpty()) {
                Text(
                    text = "Belum ada berkas dokumentasi tindak lanjut yang dibagikan.",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        color = SlateGrayLight
                    )
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    data.listDokumentasi.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    onItemClick(item)
                                    if (item.filePath.isBlank()) onTinjauClick()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(item.placeholderColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FileDocIcon(
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.judul,
                                        style = TextStyle(
                                            fontFamily = fontFamily,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDark
                                        ),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val meta = listOfNotNull(
                                        item.tanggal.takeIf { it.isNotBlank() },
                                        item.fileType.takeIf { it.isNotBlank() },
                                        item.fileSizeFormatted.takeIf { it.isNotBlank() }
                                    ).joinToString(" · ")
                                    Text(
                                        text = meta,
                                        style = TextStyle(
                                            fontFamily = fontFamily,
                                            fontSize = 11.5.sp,
                                            color = SlateGrayLight
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            ChevronRightIcon(
                                modifier = Modifier.size(16.dp),
                                tint = SlateGrayLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-box "Tanggal Penyelesaian"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarIcon(
                    modifier = Modifier.size(22.dp),
                    tint = NavyDark
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Tanggal Penyelesaian",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.tanggalPenyelesaian,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            color = SlateGray
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// ============================================================================


// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HasilPenangananScreenPreview() {
    LenteraTheme {
        HasilPenangananScreen(
            data = HasilPenangananData(
                nomorLaporan = "LTR-2026-00123",
                judul = "Pelecehan Verbal",
                status = "Selesai",
                tanggalLaporan = "14 Oktober 2026",
                kategori = "Pelecehan Verbal",
                ringkasanPenanganan = "Tim Advokasi telah menindaklanjuti laporan dan melakukan koordinasi dengan pihak terkait.",
                tanggalPenyelesaian = "15 Oktober 2026, 10:30 WITA",
                listDokumentasi = listOf(
                    DokumentasiTindakLanjutItem("13 Okt 2026", "Berita Acara Penanganan.pdf", Color(0xFF334257), fileType = "PDF", fileSizeFormatted = "420 KB")
                )
            )
        )
    }
}
