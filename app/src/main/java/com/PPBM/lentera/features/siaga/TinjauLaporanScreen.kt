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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

data class TinjauLaporanData(
    val kategori: String = "Pelecehan Verbal",
    val judul: String = "Pelecehan Verbal",
    val tanggalWaktu: String = "12 Oktober 2026, 14.30",
    val lokasi: String = "Koridor Gedung B, lantai 2",
    val deskripsi: String = "Saat menunggu kelas, saya menerima ucapan yang merendahkan dari seorang mahasiswa. Kejadian berulang dan membuat saya merasa tidak aman.",
    val buktiSummary: String = "2 file · foto dan kronologi",
    val isAnonim: Boolean = true
)

// ============================================================================
// Main Screen: TinjauLaporanScreen
// ============================================================================

/**
 * Halaman Tinjau Laporan — Langkah 4 dari 4
 * Sesuai desain UI Tinjau Laporan.png
 *
 * @param reportData Data ringkasan laporan
 * @param onBackClick Aksi saat tombol back ditekan
 * @param onUbahKategori Aksi ubah kategori
 * @param onUbahJudul Aksi ubah judul
 * @param onUbahTanggal Aksi ubah tanggal & waktu
 * @param onUbahLokasi Aksi ubah lokasi kejadian
 * @param onUbahDeskripsi Aksi ubah deskripsi kejadian
 * @param onUbahBukti Aksi ubah lampiran bukti
 * @param onKirimLaporan Aksi saat tombol "Kirim Laporan" ditekan
 */
@Composable
fun TinjauLaporanScreen(
    reportData: TinjauLaporanData = TinjauLaporanData(),
    onBackClick: () -> Unit = {},
    onUbahKategori: () -> Unit = {},
    onUbahJudul: () -> Unit = {},
    onUbahTanggal: () -> Unit = {},
    onUbahLokasi: () -> Unit = {},
    onUbahDeskripsi: () -> Unit = {},
    onUbahBukti: () -> Unit = {},
    onKirimLaporan: (TinjauLaporanData) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var isConfirmed by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol Kirim Laporan di bagian bawah
            BottomSendActionBar(
                isEnabled = isConfirmed,
                onKirimLaporan = { onKirimLaporan(reportData) },
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
            // 1. Top Bar
            TopBarTinjauLaporan(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            // 2. Stepper / Progress Bar (Langkah 4 dari 4)
            StepperProgressSectionTinjau(
                currentStep = 4,
                totalSteps = 4,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Konten Utama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Kartu Ringkasan Laporan
                RingkasanLaporanCard(
                    data = reportData,
                    onUbahKategori = onUbahKategori,
                    onUbahJudul = onUbahJudul,
                    onUbahTanggal = onUbahTanggal,
                    onUbahLokasi = onUbahLokasi,
                    onUbahDeskripsi = onUbahDeskripsi,
                    onUbahBukti = onUbahBukti,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox Konfirmasi "Data yang saya isi sudah benar"
                ConfirmationCheckboxRow(
                    isChecked = isConfirmed,
                    onCheckedChange = { isConfirmed = it },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Teks Penjelasan / Disclaimer Pengiriman
                Text(
                    text = "Laporan akan dikirim ke tim LENTERA Siaga. Kamu dapat memantau prosesnya dengan nomor referensi.",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray,
                        lineHeight = 19.sp
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Alias composable
@Composable
fun TinjauLaporan(
    onBackClick: () -> Unit = {},
    onKirimLaporan: (TinjauLaporanData) -> Unit = {}
) {
    TinjauLaporanScreen(
        onBackClick = onBackClick,
        onKirimLaporan = onKirimLaporan
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarTinjauLaporan(
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
            text = "Tinjau Laporan",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark,
                letterSpacing = 0.3.sp
            )
        )
    }
}

// ============================================================================
// Stepper / Progress Bar (Langkah 4 dari 4)
// ============================================================================

@Composable
private fun StepperProgressSectionTinjau(
    currentStep: Int = 4,
    totalSteps: Int = 4,
    fontFamily: FontFamily
) {
    val stepTitles = listOf("Kategori", "Detail", "Bukti", "Tinjau")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
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
// Kartu Ringkasan Laporan
// ============================================================================

@Composable
private fun RingkasanLaporanCard(
    data: TinjauLaporanData,
    onUbahKategori: () -> Unit,
    onUbahJudul: () -> Unit,
    onUbahTanggal: () -> Unit,
    onUbahLokasi: () -> Unit,
    onUbahDeskripsi: () -> Unit,
    onUbahBukti: () -> Unit,
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
            // Header Kartu: "Ringkasan laporan" & Badge "Anonim"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ringkasan laporan",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                if (data.isAnonim) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SiagaAmberEmergency)
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Anonim",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SiagaAmberTextDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Kategori
            TinjauItemRow(
                label = "Kategori",
                value = data.kategori,
                onUbah = onUbahKategori,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Judul
            TinjauItemRow(
                label = "Judul",
                value = data.judul,
                isBoldValue = true,
                onUbah = onUbahJudul,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Tanggal
            TinjauItemRow(
                label = "Tanggal",
                value = data.tanggalWaktu,
                onUbah = onUbahTanggal,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Lokasi
            TinjauItemRow(
                label = "Lokasi",
                value = data.lokasi,
                onUbah = onUbahLokasi,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Deskripsi
            TinjauItemRow(
                label = "Deskripsi",
                value = data.deskripsi,
                onUbah = onUbahDeskripsi,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pemisah Halus Sebelum Bukti
            HorizontalDivider(
                color = CardBorder,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Bukti
            TinjauItemRow(
                label = "Bukti",
                value = data.buktiSummary,
                onUbah = onUbahBukti,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
private fun TinjauItemRow(
    label: String,
    value: String,
    isBoldValue: Boolean = false,
    onUbah: () -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGrayLight
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Medium,
                    color = NavyDark,
                    lineHeight = 20.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Tombol Ubah
        Text(
            text = "Ubah",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = SiagaTealPrimary
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onUbah)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// ============================================================================
// Checkbox Konfirmasi "Data yang saya isi sudah benar"
// ============================================================================

@Composable
private fun ConfirmationCheckboxRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kotak Checkbox Kustom Teal
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) Color.Transparent else CardWhite)
                .border(
                    width = 2.dp,
                    color = if (isChecked) SiagaTealPrimary else SlateGrayLight,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                SiagaCheckIcon(
                    modifier = Modifier.size(15.dp),
                    tint = SiagaTealPrimary
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Data yang saya isi sudah benar",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )
        )
    }
}

// ============================================================================
// Tombol Kirim Laporan di Bagian Bawah
// ============================================================================

@Composable
private fun BottomSendActionBar(
    isEnabled: Boolean,
    onKirimLaporan: () -> Unit,
    fontFamily: FontFamily
) {
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
                .shadow(if (isEnabled) 2.dp else 0.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isEnabled) SiagaTealPrimary else SlateGrayLight
                )
                .clickable(enabled = isEnabled, onClick = onKirimLaporan),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SiagaSendIcon(
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Kirim Laporan",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
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
fun SiagaSendIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.16f, h * 0.50f)
            lineTo(w * 0.86f, h * 0.14f)
            lineTo(w * 0.50f, h * 0.86f)
            lineTo(w * 0.42f, h * 0.56f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis lipatan pesawat kertas
        drawLine(
            color = tint,
            start = Offset(w * 0.42f, h * 0.56f),
            end = Offset(w * 0.86f, h * 0.14f),
            strokeWidth = strokeWidth * 0.9f,
            cap = StrokeCap.Round
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TinjauLaporanScreenPreview() {
    LenteraTheme {
        TinjauLaporanScreen()
    }
}
