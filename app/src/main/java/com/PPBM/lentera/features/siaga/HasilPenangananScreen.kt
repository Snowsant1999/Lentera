package com.PPBM.lentera.features.siaga

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
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class DokumentasiTindakLanjutItem(
    val tanggal: String,
    val judul: String,
    val placeholderColor: Color
)

data class HasilPenangananData(
    val nomorLaporan: String = "LTR-2026-00123",
    val judul: String = "Pelecehan Verbal",
    val status: String = "Selesai",
    val tanggalLaporan: String = "14 Oktober 2026",
    val kategori: String = "Pelecehan Verbal",
    val ringkasanPenanganan: String = "Tim Advokasi telah menindaklanjuti laporan dan melakukan koordinasi dengan pihak terkait. Terima kasih telah membantu menciptakan lingkungan kampus yang aman.",
    val tanggalPenyelesaian: String = "15 Oktober 2026, 10:30 WITA",
    val listDokumentasi: List<DokumentasiTindakLanjutItem> = listOf(
        DokumentasiTindakLanjutItem("13 Okt 2026", "Rapat Koordinasi", Color(0xFF334257)),
        DokumentasiTindakLanjutItem("13 Okt 2026", "Surat Teguran", Color(0xFF476072)),
        DokumentasiTindakLanjutItem("16 Okt 2026", "Pendampingan", Color(0xFF548CA8))
    )
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
            SiagaBackArrowIcon(
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
                SiagaCalendarIcon(
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
                SiagaListIcon(
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
                        SiagaCheckIcon(
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

                Text(
                    text = "Tinjau",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateGray
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onTinjauClick)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thumbnail List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                data.listDokumentasi.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onTinjauClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(76.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.placeholderColor),
                            contentAlignment = Alignment.Center
                        ) {
                            SiagaImagePlaceholderIcon(
                                modifier = Modifier.size(24.dp),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.tanggal,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Text(
                            text = item.judul,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 10.5.sp,
                                color = SlateGrayLight
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Kotak "+2"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onTinjauClick)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+2",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyDark
                            )
                        )
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
                SiagaCalendarIcon(
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

@Composable
fun SiagaListIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        val rows = listOf(0.28f, 0.50f, 0.72f)
        for (yRel in rows) {
            val y = h * yRel
            // Titik / bullet
            drawCircle(
                color = tint,
                radius = strokeW * 0.9f,
                center = Offset(w * 0.22f, y)
            )
            // Baris garis teks
            drawLine(
                color = tint,
                start = Offset(w * 0.38f, y),
                end = Offset(w * 0.84f, y),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HasilPenangananScreenPreview() {
    LenteraTheme {
        HasilPenangananScreen()
    }
}
