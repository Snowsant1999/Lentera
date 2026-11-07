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
                LanternShieldIllustration(
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
                                ClockIcon(
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
            BackArrowIcon(
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
