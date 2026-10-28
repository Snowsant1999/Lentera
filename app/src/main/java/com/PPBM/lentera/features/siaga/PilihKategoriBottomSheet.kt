package com.PPBM.lentera.features.siaga

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model Kategori
// ============================================================================

enum class KategoriLaporanType(val displayName: String) {
    PERUNDUNGAN("Perundungan"),
    PELECEHAN_VERBAL("Pelecehan Verbal"),
    PELECEHAN_FISIK("Pelecehan Fisik"),
    KEKERASAN("Kekerasan"),
    DISKRIMINASI("Diskriminasi"),
    LAINNYA("Lainnya")
}

// ============================================================================
// Composable: PilihKategoriBottomSheet
// ============================================================================

/**
 * Modal Bottom Sheet — Pilih Kategori Laporan (Langkah 1 dari 4)
 * Sesuai desain UI Siaga · Pilih kategori.png
 *
 * @param isVisible Status tampil bottom sheet
 * @param initialSelected Kategori yang terpilih awal
 * @param onDismiss Aksi saat modal ditutup (close button atau klik luar)
 * @param onLanjutKeDetail Aksi tombol "Lanjut ke Detail ->" dengan membawa nama kategori
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihKategoriBottomSheet(
    isVisible: Boolean,
    initialSelected: KategoriLaporanType = KategoriLaporanType.PELECEHAN_VERBAL,
    onDismiss: () -> Unit = {},
    onLanjutKeDetail: (KategoriLaporanType) -> Unit = {}
) {
    if (!isVisible) return

    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var selectedCategory by remember { mutableStateOf(initialSelected) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = CardWhite,
        dragHandle = {
            // Drag Handle Bar
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDFE7ED))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // 1. Header & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih kategori laporan",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SiagaCloseIcon(
                        modifier = Modifier.size(16.dp),
                        tint = NavyDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Pilih satu kategori yang paling sesuai dengan kejadian yang kamu alami.",
                style = TextStyle(
                    fontFamily = plusJakarta,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    lineHeight = 19.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Stepper Indikator (Langkah 1 dari 4)
            StepperProgressSectionKategori(
                currentStep = 1,
                totalSteps = 4,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Grid 2 Kolom Pilihan Kategori
            val categories = KategoriLaporanType.entries
            for (rowIndex in 0 until (categories.size + 1) / 2) {
                val first = categories[rowIndex * 2]
                val second = if (rowIndex * 2 + 1 < categories.size) categories[rowIndex * 2 + 1] else null

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryItemCard(
                        modifier = Modifier.weight(1f),
                        type = first,
                        isSelected = selectedCategory == first,
                        onClick = { selectedCategory = first },
                        fontFamily = plusJakarta
                    )

                    if (second != null) {
                        CategoryItemCard(
                            modifier = Modifier.weight(1f),
                            type = second,
                            isSelected = selectedCategory == second,
                            onClick = { selectedCategory = second },
                            fontFamily = plusJakarta
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Keterangan Kerahasiaan di Bawah
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SiagaShieldCheckIcon(
                    modifier = Modifier.size(18.dp),
                    tint = SiagaTealPrimary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Laporanmu dijaga kerahasiaannya. Kamu bisa melapor secara anonim.",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray,
                        lineHeight = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 5. Tombol "Lanjut ke Detail ->"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealPrimary)
                    .clickable { onLanjutKeDetail(selectedCategory) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lanjut ke Detail",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    SiagaArrowForwardIcon(
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// ============================================================================
// Komponen Kartu Pilihan Kategori
// ============================================================================

@Composable
private fun CategoryItemCard(
    modifier: Modifier = Modifier,
    type: KategoriLaporanType,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SiagaTealBannerBg else CardWhite)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) SiagaTealPrimary else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Kategori
            KategoriIcon(type = type, tint = SiagaTealPrimary)

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = type.displayName,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                ),
                modifier = Modifier.weight(1f)
            )

            // Indikator Centang Kotak jika terpilih
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(SiagaTealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    SiagaCheckIcon(
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun KategoriIcon(
    type: KategoriLaporanType,
    tint: Color
) {
    val mod = Modifier.size(22.dp)
    when (type) {
        KategoriLaporanType.PERUNDUNGAN -> SiagaPeopleIcon(modifier = mod, tint = tint)
        KategoriLaporanType.PELECEHAN_VERBAL -> SiagaChatOutlineIcon(modifier = mod, tint = tint)
        KategoriLaporanType.PELECEHAN_FISIK -> SiagaHandStopIcon(modifier = mod, tint = tint)
        KategoriLaporanType.KEKERASAN -> SiagaShieldAlertIcon(modifier = mod, tint = tint)
        KategoriLaporanType.DISKRIMINASI -> SiagaScaleIcon(modifier = mod, tint = tint)
        KategoriLaporanType.LAINNYA -> SiagaMoreDotsIcon(modifier = mod, tint = tint)
    }
}

// ============================================================================
// Stepper Progress Section (Langkah 1 dari 4)
// ============================================================================

@Composable
private fun StepperProgressSectionKategori(
    currentStep: Int = 1,
    totalSteps: Int = 4,
    fontFamily: FontFamily
) {
    val stepTitles = listOf("Kategori", "Detail", "Bukti", "Tinjau")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Langkah $currentStep dari $totalSteps",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SiagaTealPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            stepTitles.forEachIndexed { index, title ->
                val stepNum = index + 1
                val isActive = stepNum == currentStep

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lingkaran Step
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) SiagaAmberEmergency else Color(0xFFE2EBF1)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNum",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isActive) Color.White else SlateGray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) NavyDark else SlateGrayLight
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isActive) SiagaAmberEmergency else Color(0xFFE2EBF1)
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
fun SiagaCloseIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.20f),
            end = Offset(w * 0.80f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.80f, h * 0.20f),
            end = Offset(w * 0.20f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaArrowForwardIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.15f, h * 0.50f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.55f, h * 0.20f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.55f, h * 0.80f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaPeopleIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Figur depan
        drawCircle(
            color = tint,
            radius = w * 0.15f,
            center = Offset(w * 0.50f, h * 0.32f),
            style = Stroke(width = strokeW)
        )
        drawArc(
            color = tint,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.20f, h * 0.50f),
            size = Size(w * 0.60f, h * 0.40f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SiagaHandStopIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Telapak tangan
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.80f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.32f)
            cubicTo(w * 0.30f, h * 0.26f, w * 0.38f, h * 0.26f, w * 0.38f, h * 0.32f)
            lineTo(w * 0.38f, h * 0.48f)

            // Jari tengah
            moveTo(w * 0.38f, h * 0.30f)
            lineTo(w * 0.38f, h * 0.22f)
            cubicTo(w * 0.38f, h * 0.16f, w * 0.46f, h * 0.16f, w * 0.46f, h * 0.22f)
            lineTo(w * 0.46f, h * 0.48f)

            // Jari manis
            moveTo(w * 0.46f, h * 0.32f)
            lineTo(w * 0.46f, h * 0.24f)
            cubicTo(w * 0.46f, h * 0.18f, w * 0.54f, h * 0.18f, w * 0.54f, h * 0.24f)
            lineTo(w * 0.54f, h * 0.50f)

            // Jari kelingking
            moveTo(w * 0.54f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.32f)
            cubicTo(w * 0.54f, h * 0.26f, w * 0.62f, h * 0.26f, w * 0.62f, h * 0.32f)
            lineTo(w * 0.62f, h * 0.60f)

            // Telapak bawah & jempol
            cubicTo(w * 0.62f, h * 0.74f, w * 0.54f, h * 0.84f, w * 0.44f, h * 0.84f)
            lineTo(w * 0.30f, h * 0.84f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaShieldAlertIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        val shield = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            lineTo(w * 0.82f, h * 0.24f)
            cubicTo(w * 0.82f, h * 0.56f, w * 0.72f, h * 0.78f, w * 0.50f, h * 0.88f)
            cubicTo(w * 0.28f, h * 0.78f, w * 0.18f, h * 0.56f, w * 0.18f, h * 0.24f)
            close()
        }
        drawPath(
            path = shield,
            color = tint,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Tanda seru
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.34f),
            end = Offset(w * 0.50f, h * 0.54f),
            strokeWidth = strokeW * 1.1f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = tint,
            radius = strokeW * 0.8f,
            center = Offset(w * 0.50f, h * 0.66f)
        )
    }
}

@Composable
fun SiagaScaleIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Tiang tengah
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.18f),
            end = Offset(w * 0.50f, h * 0.82f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        // Alas bawah
        drawLine(
            color = tint,
            start = Offset(w * 0.32f, h * 0.82f),
            end = Offset(w * 0.68f, h * 0.82f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        // Palang timbangan atas
        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.32f),
            end = Offset(w * 0.82f, h * 0.32f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Mangkuk timbangan kiri
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.32f), end = Offset(w * 0.12f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.32f), end = Offset(w * 0.24f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawArc(color = tint, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.12f, h * 0.46f), size = Size(w * 0.12f, h * 0.16f), style = Stroke(width = strokeW * 0.8f))

        // Mangkuk timbangan kanan
        drawLine(color = tint, start = Offset(w * 0.82f, h * 0.32f), end = Offset(w * 0.76f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawLine(color = tint, start = Offset(w * 0.82f, h * 0.32f), end = Offset(w * 0.88f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawArc(color = tint, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.76f, h * 0.46f), size = Size(w * 0.12f, h * 0.16f), style = Stroke(width = strokeW * 0.8f))
    }
}

@Composable
fun SiagaMoreDotsIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = 2.2.dp.toPx()

        drawCircle(color = tint, radius = r, center = Offset(w * 0.25f, h * 0.50f))
        drawCircle(color = tint, radius = r, center = Offset(w * 0.50f, h * 0.50f))
        drawCircle(color = tint, radius = r, center = Offset(w * 0.75f, h * 0.50f))
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true)
@Composable
fun PilihKategoriBottomSheetPreview() {
    LenteraTheme {
        PilihKategoriBottomSheet(isVisible = true)
    }
}
