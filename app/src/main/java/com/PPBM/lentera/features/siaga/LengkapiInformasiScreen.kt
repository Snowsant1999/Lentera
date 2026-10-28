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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
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
// Main Screen: LengkapiInformasiScreen
// ============================================================================

/**
 * Halaman Lengkapi Informasi Tambahan
 * Sesuai desain UI Lengkapi Informasi.png
 *
 * @param kodeLaporan Nomor kode tiket laporan
 * @param pesanAdmin Pesan dari admin penanganan
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onUploadLampiranClick Aksi saat kotak upload lampiran ditekan
 * @param onKembaliClick Aksi tombol "Kembali"
 * @param onKirimInformasiClick Aksi tombol "Kirim Informasi" dengan isi data teks
 */
@Composable
fun LengkapiInformasiScreen(
    kodeLaporan: String = "LTR-2026-00123",
    pesanAdmin: String = "Mohon jelaskan kronologi lebih rinci dan apakah ada saksi saat kejadian.",
    onBackClick: () -> Unit = {},
    onUploadLampiranClick: () -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onKirimInformasiClick: (String) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var informasiTambahan by remember { mutableStateOf("") }
    val maxChar = 1000

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            // Tombol Aksi di Bawah (Kembali & Kirim Informasi)
            BottomActionBarLengkapi(
                onKembali = onKembaliClick,
                onKirim = { onKirimInformasiClick(informasiTambahan) },
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
            TopBarLengkapiInformasi(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Banner Pemberitahuan Permintaan Informasi
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SiagaExclamationCircleIcon(
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Admin meminta informasi tambahan",
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Mohon lengkapi informasi berikut agar laporan dapat ditindaklanjuti.",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGray,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Kartu Kode Laporan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kode Laporan",
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = SlateGrayLight
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = kodeLaporan,
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark,
                                    letterSpacing = 0.3.sp
                                )
                            )
                        }

                        SiagaCalendarIcon(
                            modifier = Modifier.size(24.dp),
                            tint = SiagaTealPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Kartu Pesan dari Admin
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Avatar Admin Lingkaran Navy
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(NavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            SiagaAvatarIcon(
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pesan dari Admin",
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = pesanAdmin,
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = SlateGray,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Formulir Input Informasi Tambahan & Lampiran
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
                        // Label Informasi Tambahan
                        Text(
                            text = "Informasi Tambahan",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Text Area Input
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            if (informasiTambahan.isEmpty()) {
                                Text(
                                    text = "Tulis informasi tambahan...",
                                    style = TextStyle(
                                        fontFamily = plusJakarta,
                                        fontSize = 14.sp,
                                        color = SlateGrayLight
                                    )
                                )
                            }
                            BasicTextField(
                                value = informasiTambahan,
                                onValueChange = {
                                    if (it.length <= maxChar) informasiTambahan = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 14.sp,
                                    color = NavyDark,
                                    lineHeight = 20.sp
                                ),
                                cursorBrush = SolidColor(SiagaTealPrimary)
                            )
                        }

                        // Karakter Counter
                        Text(
                            text = "${informasiTambahan.length}/$maxChar",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 12.sp,
                                color = SlateGrayLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Label Lampiran
                        Text(
                            text = "Lampiran (Opsional)",
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dropzone Box Lampiran
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF3F7FA))
                                .clickable(onClick = onUploadLampiranClick)
                                .padding(horizontal = 20.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Ikon Awan Upload Hitam Outline
                                SiagaCloudUploadIcon(
                                    modifier = Modifier.size(42.dp),
                                    tint = Color.Black
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Tap untuk mengunggah foto, video atau dokumen\n(JPG, PNG, PDF, DOC)",
                                    style = TextStyle(
                                        fontFamily = plusJakarta,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = SlateGray,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Alias composable
@Composable
fun LengkapiInformasi(
    onBackClick: () -> Unit = {},
    onKirimInformasiClick: (String) -> Unit = {}
) {
    LengkapiInformasiScreen(
        onBackClick = onBackClick,
        onKirimInformasiClick = onKirimInformasiClick
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarLengkapiInformasi(
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
            text = "LENGKAPI INFORMASI",
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
// Tombol Aksi Bawah
// ============================================================================

@Composable
private fun BottomActionBarLengkapi(
    onKembali: () -> Unit,
    onKirim: () -> Unit,
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
            // Tombol "Kembali" (Outlined)
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
                    text = "Kembali",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SiagaTealPrimary
                    )
                )
            }

            // Tombol "Kirim Informasi" (Filled)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealPrimary)
                    .clickable(onClick = onKirim),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kirim Informasi",
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
// Ikon & Aset Visual
// ============================================================================

/**
 * Ikon tanda seru di dalam lingkaran hitam
 */
@Composable
fun SiagaExclamationCircleIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val r = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Lingkaran hitam fill
        drawCircle(
            color = Color.Black,
            radius = r,
            center = center,
            style = Fill
        )

        // Batang tanda seru putih
        val strokeW = 2.2.dp.toPx()
        drawLine(
            color = Color.White,
            start = Offset(center.x, center.y - (r * 0.45f)),
            end = Offset(center.x, center.y + (r * 0.12f)),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Titik tanda seru putih
        drawCircle(
            color = Color.White,
            radius = strokeW * 0.75f,
            center = Offset(center.x, center.y + (r * 0.45f))
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LengkapiInformasiScreenPreview() {
    LenteraTheme {
        LengkapiInformasiScreen()
    }
}
