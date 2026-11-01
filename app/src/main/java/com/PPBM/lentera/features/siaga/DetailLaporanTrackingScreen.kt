package com.PPBM.lentera.features.siaga

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

enum class TimelineStepState {
    COMPLETED,
    ACTIVE,
    PENDING
}

data class TimelineStepItem(
    val title: String,
    val description: String,
    val state: TimelineStepState
)

data class DetailLaporanTrackingData(
    val nomorReferensi: String = "LTR-2026-00123",
    val judul: String = "Pelecehan Verbal",
    val status: String = "Diperiksa",
    val tanggalPengiriman: String = "Dikirim 12 Oktober 2026 · Laporan anonim",
    val adminMessage: String = "Mohon jelaskan ciri pelaku dan apakah ada saksi saat kejadian.",
    val hasAdminRequest: Boolean = true,
    val timeline: List<TimelineStepItem> = listOf(
        TimelineStepItem(
            title = "Dikirim",
            description = "12 Okt 2026, 15.10 · Laporan diterima",
            state = TimelineStepState.COMPLETED
        ),
        TimelineStepItem(
            title = "Diperiksa",
            description = "13 Okt 2026 · Tim meninjau laporanmu",
            state = TimelineStepState.ACTIVE
        ),
        TimelineStepItem(
            title = "Ditindaklanjuti",
            description = "Menunggu hasil pemeriksaan",
            state = TimelineStepState.PENDING
        ),
        TimelineStepItem(
            title = "Selesai",
            description = "Ringkasan penanganan akan tersedia",
            state = TimelineStepState.PENDING
        )
    )
)

// ============================================================================
// Main Screen: DetailLaporanTrackingScreen
// ============================================================================

/**
 * Halaman Detail Laporan Tracking
 * Sesuai desain UI Detail Laporan Tracking.png
 *
 * @param reportData Data detail dan status penanganan laporan
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onLengkapiInformasiClick Aksi tombol "Lengkapi Informasi"
 */
@Composable
fun DetailLaporanTrackingScreen(
    reportData: DetailLaporanTrackingData = DetailLaporanTrackingData(),
    onBackClick: () -> Unit = {},
    onLengkapiInformasiClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var isHasilExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Bar
            TopBarDetailLaporan(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Kartu Header Laporan
                HeaderInfoCard(
                    data = reportData,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Kartu Timeline "Proses penanganan"
                TimelineProsesCard(
                    timeline = reportData.timeline,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Banner "Admin meminta informasi tambahan" (jika ada permintaan)
                if (reportData.hasAdminRequest) {
                    AdminRequestNoticeCard(
                        message = reportData.adminMessage,
                        onLengkapiClick = onLengkapiInformasiClick,
                        fontFamily = plusJakarta
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // 5. Kartu "Hasil Penanganan" (Expandable)
                HasilPenangananCard(
                    isExpanded = isHasilExpanded,
                    onToggleExpand = { isHasilExpanded = !isHasilExpanded },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}


// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarDetailLaporan(
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
// Kartu Header Ringkasan Laporan
// ============================================================================

@Composable
private fun HeaderInfoCard(
    data: DetailLaporanTrackingData,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.nomorReferensi,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SiagaBadgeDiperiksaBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.status,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SiagaBadgeDiperiksaText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = data.judul,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = data.tanggalPengiriman,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGrayLight
                )
            )
        }
    }
}

// ============================================================================
// Kartu Timeline "Proses penanganan"
// ============================================================================

@Composable
private fun TimelineProsesCard(
    timeline: List<TimelineStepItem>,
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
                text = "Proses penanganan",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            timeline.forEachIndexed { index, item ->
                val isLast = index == timeline.lastIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Kolom Ikon & Garis Vertikal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        when (item.state) {
                            TimelineStepState.COMPLETED -> {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SiagaTealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SiagaCheckIcon(
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                            TimelineStepState.ACTIVE -> {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SiagaAmberEmergency),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SiagaClockIcon(
                                        modifier = Modifier.size(18.dp),
                                        tint = SiagaAmberTextDark
                                    )
                                }
                            }
                            TimelineStepState.PENDING -> {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2EBF1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, SlateGray, CircleShape)
                                    )
                                }
                            }
                        }

                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(38.dp)
                                    .background(
                                        if (item.state == TimelineStepState.COMPLETED) SiagaTealPrimary else Color(0xFFE2EBF1)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Detail Teks Step
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (!isLast) 18.dp else 4.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.state == TimelineStepState.PENDING) SlateGray else NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGrayLight,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// Banner Permintaan Informasi dari Admin
// ============================================================================

@Composable
private fun AdminRequestNoticeCard(
    message: String,
    onLengkapiClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF4E7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF7E4CB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SiagaChatOutlineIcon(
                    modifier = Modifier.size(20.dp),
                    tint = NavyDark
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Admin meminta informasi tambahan",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    lineHeight = 19.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol "Lengkapi Informasi"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SiagaTealPrimary)
                    .clickable(onClick = onLengkapiClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lengkapi Informasi",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

// ============================================================================
// Kartu "Hasil Penanganan" (Expandable)
// ============================================================================

@Composable
private fun HasilPenangananCard(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onToggleExpand),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SiagaFileDocIcon(
                        modifier = Modifier.size(24.dp),
                        tint = SiagaTealPrimary
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Hasil Penanganan",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                }

                SiagaChevronDownIcon(
                    modifier = Modifier.size(20.dp),
                    tint = NavyDark,
                    isExpanded = isExpanded
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp, start = 38.dp)) {
                    Text(
                        text = "Hasil investigasi dan penanganan resmi akan dipublikasikan di sini setelah proses verifikasi oleh tim kampus selesai.",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.sp,
                            color = SlateGray,
                            lineHeight = 18.sp
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
fun SiagaClockIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaAmberTextDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth / 2f)
        val center = Offset(w / 2f, h / 2f)

        // Lingkaran jam
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Jarum pendek & jarum panjang
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x, center.y - (r * 0.55f)),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x + (r * 0.45f), center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaChatOutlineIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val path = Path().apply {
            val left = w * 0.14f
            val top = h * 0.16f
            val right = w * 0.86f
            val bottom = h * 0.72f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(w * 0.40f, bottom)
            lineTo(w * 0.22f, h * 0.88f)
            lineTo(w * 0.26f, bottom)
            lineTo(left + r, bottom)
            quadraticTo(left, bottom, left, bottom - r)
            lineTo(left, top + r)
            quadraticTo(left, top, left + r, top)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaChevronDownIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark,
    isExpanded: Boolean = false
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val startY = if (isExpanded) h * 0.65f else h * 0.35f
        val midY = if (isExpanded) h * 0.35f else h * 0.65f

        drawLine(
            color = tint,
            start = Offset(w * 0.25f, startY),
            end = Offset(w * 0.50f, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, midY),
            end = Offset(w * 0.75f, startY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

// Alias composable
@Composable
fun DetailLaporanTracking(
    reportData: DetailLaporanTrackingData = DetailLaporanTrackingData(),
    onBackClick: () -> Unit = {},
    onLengkapiInformasiClick: () -> Unit = {}
) {
    DetailLaporanTrackingScreen(
        reportData = reportData,
        onBackClick = onBackClick,
        onLengkapiInformasiClick = onLengkapiInformasiClick
    )
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailLaporanTrackingScreenPreview() {
    LenteraTheme {
        DetailLaporanTrackingScreen()
    }
}

