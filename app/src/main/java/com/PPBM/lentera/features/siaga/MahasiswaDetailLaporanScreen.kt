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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class LampiranBuktiDetail(
    val nama: String,
    val ukuran: String,
    val tipe: String // "PHOTO", "AUDIO", "DOC"
)

data class DetailLaporanFullData(
    val nomorLaporan: String = "LTR-2026-00123",
    val judul: String = "Pelecehan Verbal",
    val status: String = "Selesai",
    val waktuKirim: String = "Dikirim 15 Oktober 2026, jam 15:00 WITA",
    val pelaporNama: String = "Anonim",
    val pelaporDeskripsi: String = "Identitas pelapor dirahasiakan",
    val kategori: String = "Pelecehan Verbal",
    val tanggalKejadian: String = "15 Oktober 2026, 10:30 WITA",
    val lokasiKejadian: String = "Koridor Gedung Baru, Lantai 2",
    val ringkasanLaporan: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
    val listLampiran: List<LampiranBuktiDetail> = listOf(
        LampiranBuktiDetail("foto_lab.jpg", "2,4 MB", "PHOTO"),
        LampiranBuktiDetail("rekaman_suara.mp4", "2,4 MB", "AUDIO"),
        LampiranBuktiDetail("dokumen_pendukung.pdf", "2,4 MB", "DOC")
    ),
    val listTimeline: List<Pair<String, String>> = listOf(
        "Laporan Dikirim" to "12 Oktober 2026, 12:30 WITA",
        "Diverifikasi dan Divalidasi" to "12 Oktober 2026, 15:10 WITA",
        "Dalam Proses Penanganan" to "13 Oktober 2026, 12:30 WITA",
        "Selesai" to "15 Oktober 2026, 15:10 WITA"
    )
)

// ============================================================================
// Main Screen: DetailLaporanScreen
// ============================================================================

/**
 * Halaman Detail Laporan Lengkap (Scrollable)
 * Sesuai desain UI Detail Laporan.png
 *
 * @param data Data detail laporan
 * @param onBackClick Aksi saat tombol back di top bar ditekan
 * @param onAttachmentClick Aksi saat salah satu file lampiran ditekan
 * @param onLihatHasilPenangananClick Aksi tombol "Lihat Hasil Penanganan"
 */
@Composable
fun DetailLaporanScreen(
    data: DetailLaporanFullData = DetailLaporanFullData(),
    onBackClick: () -> Unit = {},
    onAttachmentClick: (LampiranBuktiDetail) -> Unit = {},
    onLihatHasilPenangananClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol "Lihat Hasil Penanganan" di bagian bawah
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
                        .clickable(onClick = onLihatHasilPenangananClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lihat Hasil Penanganan",
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
        // Kontainer Utama yang Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top App Bar
            TopBarDetailLaporanLengkap(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Kartu Header Tiket Laporan
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
                                    fontFamily = plusJakarta,
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
                                    .padding(horizontal = 14.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = data.status,
                                    style = TextStyle(
                                        fontFamily = plusJakarta,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SiagaBadgeSelesaiText
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = data.judul,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = data.waktuKirim,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGrayLight
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Kartu "Informasi Pelapor"
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
                            text = "Informasi Pelapor",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Box 1: Anonim
                        InfoDetailSubBox(
                            icon = {
                                SiagaAvatarIcon(
                                    modifier = Modifier.size(24.dp),
                                    tint = NavyDark
                                )
                            },
                            title = data.pelaporNama,
                            subtitle = data.pelaporDeskripsi,
                            fontFamily = plusJakarta
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Box 2: Kategori
                        InfoDetailSubBox(
                            icon = {
                                SiagaListIcon(
                                    modifier = Modifier.size(22.dp),
                                    tint = NavyDark
                                )
                            },
                            title = "Kategori",
                            subtitle = data.kategori,
                            fontFamily = plusJakarta
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Box 3: Tanggal Kejadian
                        InfoDetailSubBox(
                            icon = {
                                SiagaCalendarIcon(
                                    modifier = Modifier.size(22.dp),
                                    tint = NavyDark
                                )
                            },
                            title = "Tanggal Kejadian",
                            subtitle = data.tanggalKejadian,
                            fontFamily = plusJakarta
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Box 4: Lokasi Kejadian
                        InfoDetailSubBox(
                            icon = {
                                SiagaLocationPinIcon(
                                    modifier = Modifier.size(22.dp),
                                    tint = NavyDark
                                )
                            },
                            title = "Lokasi Kejadian",
                            subtitle = data.lokasiKejadian,
                            fontFamily = plusJakarta
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Kartu "Ringkasan Laporan"
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
                            text = "Ringkasan Laporan",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = data.ringkasanLaporan,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGray,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Kartu "Bukti yang Dilampirkan"
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
                            text = "Bukti yang Dilampirkan",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        data.listLampiran.forEach { lampiran ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .clickable { onAttachmentClick(lampiran) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Kotak Ikon Gelap
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NavyDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when (lampiran.tipe) {
                                            "PHOTO" -> SiagaImagePlaceholderIcon(
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.White
                                            )
                                            "AUDIO" -> SiagaMusicNoteIcon(
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.White
                                            )
                                            else -> SiagaFileDocIcon(
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = lampiran.nama,
                                            style = TextStyle(
                                                fontFamily = plusJakarta,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyDark
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = lampiran.ukuran,
                                            style = TextStyle(
                                                fontFamily = plusJakarta,
                                                fontSize = 12.sp,
                                                color = SlateGrayLight
                                            )
                                        )
                                    }
                                }

                                SiagaChevronRightIcon(
                                    modifier = Modifier.size(16.dp),
                                    tint = SlateGrayLight
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 6. Kartu "Timeline Status"
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
                            text = "Timeline Status",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        data.listTimeline.forEachIndexed { index, pair ->
                            val isLast = index == data.listTimeline.lastIndex

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Kolom Ring & Garis
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(28.dp)
                                ) {
                                    // Ring Lingkaran Navy
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(CardWhite)
                                            .border(3.dp, NavyDark, CircleShape)
                                    )

                                    if (!isLast) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.5.dp)
                                                .height(34.dp)
                                                .background(NavyDark)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = if (!isLast) 16.dp else 4.dp)
                                ) {
                                    Text(
                                        text = pair.first,
                                        style = TextStyle(
                                            fontFamily = plusJakarta,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDark
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = pair.second,
                                        style = TextStyle(
                                            fontFamily = plusJakarta,
                                            fontSize = 12.sp,
                                            color = SlateGrayLight
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Alias composables untuk kompatibilitas
@Composable
fun DetailLaporanLengkapScreen(
    data: DetailLaporanFullData = DetailLaporanFullData(),
    onBackClick: () -> Unit = {},
    onAttachmentClick: (LampiranBuktiDetail) -> Unit = {},
    onLihatHasilPenangananClick: () -> Unit = {}
) {
    DetailLaporanScreen(
        data = data,
        onBackClick = onBackClick,
        onAttachmentClick = onAttachmentClick,
        onLihatHasilPenangananClick = onLihatHasilPenangananClick
    )
}

@Composable
fun DetailLaporanView(
    onBackClick: () -> Unit = {},
    onLihatHasilPenangananClick: () -> Unit = {}
) {
    DetailLaporanScreen(
        onBackClick = onBackClick,
        onLihatHasilPenangananClick = onLihatHasilPenangananClick
    )
}

// ============================================================================
// Komponen Sub-Box Informasi Detail
// ============================================================================

@Composable
private fun InfoDetailSubBox(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )
        }
    }
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarDetailLaporanLengkap(
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
            text = "DETAIL LAPORAN",
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
// Ikon & Aset Visual
// ============================================================================

@Composable
fun SiagaMusicNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Batang not musik
        drawLine(
            color = tint,
            start = Offset(w * 0.65f, h * 0.20f),
            end = Offset(w * 0.65f, h * 0.68f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Kepala not musik
        drawCircle(
            color = tint,
            radius = w * 0.16f,
            center = Offset(w * 0.52f, h * 0.68f)
        )

        // Bendera not musik atas
        val flag = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.34f)
            close()
        }
        drawPath(path = flag, color = tint)
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailLaporanScreenPreview() {
    LenteraTheme {
        DetailLaporanScreen()
    }
}
