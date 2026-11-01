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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class BuatLaporanFormData(
    val kategori: String = "Pelecehan Verbal",
    val judul: String = "Pelecehan Verbal",
    val tanggalWaktu: String = "12 Oktober 2026, 14.30",
    val lokasi: String = "Koridor Gedung B, lantai 2",
    val deskripsi: String = "Saat menunggu kelas, saya menerima ucapan yang merendahkan dari seorang mahasiswa. Kejadian ini berulang dan membuat saya merasa tidak aman.",
    val isAnonim: Boolean = true
)

// ============================================================================
// Main Screen: BuatLaporanScreen
// ============================================================================

/**
 * Halaman Buat Laporan — Langkah 2 dari 4 (Detail Kejadian)
 * Sesuai desain UI Buat Laporan.png
 *
 * @param initialData Data awal formulir
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onUbahKategoriClick Aksi tombol "Ubah" pada kartu kategori terpilih
 * @param onKembaliKeKategoriClick Aksi tombol "Kembali ke Kategori"
 * @param onLanjutKeBuktiClick Aksi tombol "Lanjut ke Bukti" dengan membawa data formulir
 * @param onDatePickerClick Aksi saat field tanggal diklik
 * @param onLocationPickerClick Aksi saat field lokasi diklik
 */
@Composable
fun BuatLaporanScreen(
    initialData: BuatLaporanFormData = BuatLaporanFormData(),
    onBackClick: () -> Unit = {},
    onUbahKategoriClick: () -> Unit = {},
    onKembaliKeKategoriClick: () -> Unit = {},
    onLanjutKeBuktiClick: (BuatLaporanFormData) -> Unit = {},
    onDatePickerClick: () -> Unit = {},
    onLocationPickerClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    val focusManager = LocalFocusManager.current

    var judul by remember { mutableStateOf(initialData.judul) }
    var tanggalWaktu by remember { mutableStateOf(initialData.tanggalWaktu) }
    var lokasi by remember { mutableStateOf(initialData.lokasi) }
    var deskripsi by remember { mutableStateOf(initialData.deskripsi) }
    var isAnonim by remember { mutableStateOf(initialData.isAnonim) }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol Aksi di Bagian Bawah
            BottomActionBar(
                onKembali = onKembaliKeKategoriClick,
                onLanjut = {
                    onLanjutKeBuktiClick(
                        BuatLaporanFormData(
                            kategori = initialData.kategori,
                            judul = judul,
                            tanggalWaktu = tanggalWaktu,
                            lokasi = lokasi,
                            deskripsi = deskripsi,
                            isAnonim = isAnonim
                        )
                    )
                },
                fontFamily = plusJakarta
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top App Bar
            TopBarBuatLaporan(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            // 2. Stepper / Progress Bar (Langkah 2 dari 4)
            StepperProgressSection(
                currentStep = 2,
                totalSteps = 4,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Konten Formulir Utama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Kategori Terpilih Card
                KategoriTerpilihCard(
                    kategori = initialData.kategori,
                    onUbahClick = onUbahKategoriClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Judul Bagian Formulir
                Text(
                    text = "Ceritakan yang terjadi",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        letterSpacing = (-0.3).sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Field 1: Judul Laporan
                FormFieldLabel(label = "Judul Laporan", fontFamily = plusJakarta)
                CustomInputField(
                    value = judul,
                    onValueChange = { judul = it },
                    placeholder = "Masukkan judul laporan",
                    fontFamily = plusJakarta,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 2: Tanggal & Waktu Kejadian
                FormFieldLabel(label = "Tanggal & Waktu Kejadian", fontFamily = plusJakarta)
                CustomInputField(
                    value = tanggalWaktu,
                    onValueChange = { tanggalWaktu = it },
                    placeholder = "Pilih tanggal & waktu kejadian",
                    fontFamily = plusJakarta,
                    trailingIcon = {
                        SiagaCalendarIcon(
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDatePickerClick
                                ),
                            tint = SiagaTealPrimary
                        )
                    },
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 3: Lokasi Kejadian
                FormFieldLabel(label = "Lokasi Kejadian", fontFamily = plusJakarta)
                CustomInputField(
                    value = lokasi,
                    onValueChange = { lokasi = it },
                    placeholder = "Masukkan lokasi kejadian",
                    fontFamily = plusJakarta,
                    trailingIcon = {
                        SiagaLocationPinIcon(
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onLocationPickerClick
                                ),
                            tint = SiagaTealPrimary
                        )
                    },
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field 4: Deskripsi Kejadian
                FormFieldLabel(label = "Deskripsi Kejadian", fontFamily = plusJakarta)
                CustomTextArea(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    placeholder = "Ceritakan kejadian secara detail...",
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Switch Toggle: Laporkan Sebagai Anonim
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laporkan sebagai anonim",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )

                    Switch(
                        checked = isAnonim,
                        onCheckedChange = { isAnonim = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SiagaTealPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = SlateGrayLight,
                            uncheckedBorderColor = Color.Transparent,
                            checkedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarBuatLaporan(
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
            text = "BUAT LAPORAN",
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
// Stepper / Progress Bar
// ============================================================================

@Composable
private fun StepperProgressSection(
    currentStep: Int = 2,
    totalSteps: Int = 4,
    fontFamily: FontFamily
) {
    val stepTitles = listOf("Kategori", "Detail", "Bukti", "Tinjau")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Teks Langkah
        Text(
            text = "Langkah $currentStep dari $totalSteps",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = SiagaTealPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row Lingkaran & Label Langkah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            stepTitles.forEachIndexed { index, title ->
                val stepNum = index + 1
                val isCompleted = stepNum < currentStep
                val isActive = stepNum == currentStep

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lingkaran Step
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> SiagaTealPrimary
                                    isActive -> SiagaAmberEmergency
                                    else -> Color(0xFFE2EBF1)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            SiagaCheckIcon(
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        } else {
                            Text(
                                text = "$stepNum",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) Color.White else SlateGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Label Step
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isActive -> NavyDark
                                isCompleted -> SlateGray
                                else -> SlateGrayLight
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Garis Indikator di Bawah Tiap Step
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isCompleted -> SiagaTealPrimary
                                    isActive -> SiagaAmberEmergency
                                    else -> Color(0xFFE2EBF1)
                                }
                            )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Kartu Kategori Terpilih
// ============================================================================

@Composable
private fun KategoriTerpilihCard(
    kategori: String,
    onUbahClick: () -> Unit,
    fontFamily: FontFamily
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Kategori terpilih",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ikon Kotak Centang
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SiagaTealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    SiagaCheckIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Detail Kategori
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kategori,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Dipilih dari daftar kategori",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = SlateGrayLight
                        )
                    )
                }

                // Tombol "Ubah"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFEBF5F7))
                        .clickable(onClick = onUbahClick)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ubah",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SiagaTealPrimary
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Komponen Form Input Fields
// ============================================================================

@Composable
private fun FormFieldLabel(
    label: String,
    fontFamily: FontFamily
) {
    Text(
        text = label,
        style = TextStyle(
            fontFamily = fontFamily,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyDark
        ),
        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
    )
}

@Composable
private fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    fontFamily: FontFamily,
    trailingIcon: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Default
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.5.sp,
                            color = SlateGrayLight
                        )
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = NavyDark
                    ),
                    cursorBrush = SolidColor(SiagaTealPrimary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = imeAction),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                trailingIcon()
            }
        }
    }
}

@Composable
private fun CustomTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    color = SlateGrayLight,
                    lineHeight = 20.sp
                )
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = NavyDark,
                lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(SiagaTealPrimary)
        )
    }
}

// ============================================================================
// Bottom Action Buttons
// ============================================================================

@Composable
private fun BottomActionBar(
    onKembali: () -> Unit,
    onLanjut: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tombol "Kembali ke Kategori" (Outlined)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
                    .border(1.5.dp, SiagaTealPrimary, RoundedCornerShape(16.dp))
                    .clickable(onClick = onKembali),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kembali ke Kategori",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SiagaTealPrimary
                    )
                )
            }

            // Tombol "Lanjut ke Bukti" (Filled)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealPrimary)
                    .clickable(onClick = onLanjut),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lanjut ke Bukti",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// Catatan: Ikon di bawah dapat Anda ganti dengan painterResource / Icon bawaan
// jika aset SVG / PNG resmi sudah siap.
// ============================================================================

@Composable
fun SiagaBackArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.2.dp.toPx()
        val midY = h / 2f
        val startX = w * 0.25f
        val endX = w * 0.75f
        val arrowSize = w * 0.20f

        // Garis panah horizontal
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(endX, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Sayap panah atas
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(startX + arrowSize, midY - arrowSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Sayap panah bawah
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(startX + arrowSize, midY + arrowSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val checkPath = Path().apply {
            moveTo(w * 0.22f, h * 0.50f)
            lineTo(w * 0.42f, h * 0.70f)
            lineTo(w * 0.78f, h * 0.32f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaCalendarIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val top = h * 0.22f
        val bottom = h * 0.88f
        val left = w * 0.14f
        val right = w * 0.86f
        val r = 3.dp.toPx()

        // Kotak kalender
        drawRoundRect(
            color = tint,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = strokeWidth)
        )

        // Cincin penjepit atas (kiri & kanan)
        drawLine(
            color = tint,
            start = Offset(w * 0.34f, h * 0.10f),
            end = Offset(w * 0.34f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.66f, h * 0.10f),
            end = Offset(w * 0.66f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Garis pemisah header kalender
        drawLine(
            color = tint,
            start = Offset(left, h * 0.42f),
            end = Offset(right, h * 0.42f),
            strokeWidth = strokeWidth * 0.85f
        )

        // Titik-titik tanggal di dalam kalender
        val dotR = strokeWidth * 0.65f
        val cols = listOf(0.32f, 0.50f, 0.68f)
        val rows = listOf(0.58f, 0.74f)
        for (col in cols) {
            for (row in rows) {
                drawCircle(
                    color = tint,
                    radius = dotR,
                    center = Offset(w * col, h * row)
                )
            }
        }
    }
}

@Composable
fun SiagaLocationPinIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Kontur pin lokasi
        val pinPath = Path().apply {
            moveTo(w * 0.5f, h * 0.88f)
            cubicTo(w * 0.36f, h * 0.64f, w * 0.20f, h * 0.48f, w * 0.20f, h * 0.36f)
            cubicTo(w * 0.20f, h * 0.18f, w * 0.34f, h * 0.12f, w * 0.5f, h * 0.12f)
            cubicTo(w * 0.66f, h * 0.12f, w * 0.80f, h * 0.18f, w * 0.80f, h * 0.36f)
            cubicTo(w * 0.80f, h * 0.48f, w * 0.64f, h * 0.64f, w * 0.5f, h * 0.88f)
            close()
        }
        drawPath(
            path = pinPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Titik lingkaran di tengah pin
        drawCircle(
            color = tint,
            radius = w * 0.10f,
            center = Offset(w * 0.5f, h * 0.36f),
            style = Stroke(width = strokeWidth)
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BuatLaporanScreenPreview() {
    LenteraTheme {
        BuatLaporanScreen()
    }
}
