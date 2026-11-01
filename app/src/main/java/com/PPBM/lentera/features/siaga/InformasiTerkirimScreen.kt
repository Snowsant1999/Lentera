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
// Main Screen: InformasiTerkirimScreen
// ============================================================================

/**
 * Halaman Konfirmasi Informasi Tambahan Berhasil Dikirim
 * Sesuai desain UI Informasi Berhasil Dikirim.png
 *
 * @param nomorLaporan Nomor tiket laporan terkait
 * @param onBackClick Aksi tombol kembali di top bar
 * @param onLihatStatusClick Aksi tombol "Lihat Status Laporan"
 * @param onKembaliKeLaporanClick Aksi tombol "Kembali ke Laporan Saya"
 */
@Composable
fun InformasiTerkirimScreen(
    nomorLaporan: String = "LTR-2026-00123",
    onBackClick: () -> Unit = {},
    onLihatStatusClick: () -> Unit = {},
    onKembaliKeLaporanClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

    Scaffold(
        containerColor = ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Top Bar
                TopBarInformasiTambahan(
                    onBackClick = onBackClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Ilustrasi Lentera & Perisai
                SiagaLanternShieldIllustration(
                    modifier = Modifier
                        .size(230.dp)
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Judul "Informasi Berhasil Dikirim"
                Text(
                    text = "Informasi Berhasil\nDikirim",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Deskripsi Keterangan
                Text(
                    text = "Informasi tambahanmu sudah dikirim. Tim akan meninjaunya untuk melanjutkan penanganan laporan.",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Kartu Status "Nomor laporan & Menunggu peninjauan"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nomor laporan",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGrayLight
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = nomorLaporan,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SiagaTealPrimary,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Badge "Menunggu peninjauan"
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFDF4E7))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SiagaClockIcon(
                                    modifier = Modifier.size(15.dp),
                                    tint = NavyDark
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "Menunggu peninjauan",
                                    style = TextStyle(
                                        fontFamily = plusJakarta,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NavyDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. Tombol Aksi Bawah
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Tombol "Lihat Status Laporan" (Filled)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SiagaTealPrimary)
                        .clickable(onClick = onLihatStatusClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lihat Status Laporan",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol "Kembali ke Laporan Saya" (Outlined)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Transparent)
                        .border(1.5.dp, SiagaTealPrimary, RoundedCornerShape(16.dp))
                        .clickable(onClick = onKembaliKeLaporanClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Laporan Saya",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SiagaTealPrimary
                        )
                    )
                }
            }
        }
    }
}

// Alias composable
@Composable
fun InformasiBerhasilDikirim(
    nomorLaporan: String = "LTR-2026-00123",
    onBackClick: () -> Unit = {},
    onLihatStatusClick: () -> Unit = {},
    onKembaliKeLaporanClick: () -> Unit = {}
) {
    InformasiTerkirimScreen(
        nomorLaporan = nomorLaporan,
        onBackClick = onBackClick,
        onLihatStatusClick = onLihatStatusClick,
        onKembaliKeLaporanClick = onKembaliKeLaporanClick
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarInformasiTambahan(
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
            text = "INFORMASI TAMBAHAN",
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
// Ilustrasi Lentera & Perisai Pelindung
// ============================================================================

@Composable
fun SiagaLanternShieldIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Cahaya keemasan lentera di latar kiri
        drawCircle(
            color = Color(0xFFFFECC8),
            radius = w * 0.22f,
            center = Offset(w * 0.32f, h * 0.38f),
            style = Fill
        )

        // Perisai pelindung di sebelah kanan
        val shieldPath = Path().apply {
            moveTo(w * 0.70f, h * 0.22f)
            cubicTo(w * 0.88f, h * 0.22f, w * 0.94f, h * 0.28f, w * 0.94f, h * 0.38f)
            lineTo(w * 0.94f, h * 0.58f)
            cubicTo(w * 0.94f, h * 0.76f, w * 0.82f, h * 0.86f, w * 0.70f, h * 0.90f)
            cubicTo(w * 0.58f, h * 0.86f, w * 0.46f, h * 0.76f, w * 0.46f, h * 0.58f)
            lineTo(w * 0.46f, h * 0.38f)
            cubicTo(w * 0.46f, h * 0.28f, w * 0.52f, h * 0.22f, w * 0.70f, h * 0.22f)
            close()
        }
        drawPath(
            path = shieldPath,
            color = Color(0xFFF1F7FA),
            style = Fill
        )
        drawPath(
            path = shieldPath,
            color = Color(0xFF173B57),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Centang hijau teal di dalam perisai
        val check = Path().apply {
            moveTo(w * 0.60f, h * 0.54f)
            lineTo(w * 0.67f, h * 0.64f)
            lineTo(w * 0.82f, h * 0.42f)
        }
        drawPath(
            path = check,
            color = Color(0xFF267375),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Figur Pelapor / Mahasiswa di tengah-kiri
        // Kepala
        drawCircle(
            color = Color(0xFFFFDAB9),
            radius = w * 0.08f,
            center = Offset(w * 0.44f, h * 0.24f),
            style = Fill
        )
        // Rambut hitam
        drawArc(
            color = Color(0xFF1A2634),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.36f, h * 0.16f),
            size = Size(w * 0.16f, h * 0.14f),
            style = Fill
        )

        // Blazer teal
        val body = Path().apply {
            moveTo(w * 0.36f, h * 0.32f)
            lineTo(w * 0.52f, h * 0.32f)
            lineTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.34f, h * 0.60f)
            close()
        }
        drawPath(path = body, color = Color(0xFF267375), style = Fill)
        drawPath(path = body, color = Color(0xFF173B57), style = Stroke(width = 1.8.dp.toPx()))

        // Lentera di tangan kiri
        val lantern = Path().apply {
            moveTo(w * 0.28f, h * 0.34f)
            lineTo(w * 0.36f, h * 0.34f)
            lineTo(w * 0.38f, h * 0.52f)
            lineTo(w * 0.26f, h * 0.52f)
            close()
        }
        drawPath(path = lantern, color = Color(0xFFFFB74D), style = Fill)
        drawPath(path = lantern, color = Color(0xFF795548), style = Stroke(width = 1.8.dp.toPx()))

        // Gagang lentera
        drawArc(
            color = Color(0xFF795548),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.29f, h * 0.26f),
            size = Size(w * 0.06f, h * 0.08f),
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InformasiTerkirimScreenPreview() {
    LenteraTheme {
        InformasiTerkirimScreen()
    }
}
