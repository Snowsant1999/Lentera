package com.PPBM.lentera.features.admin.siaga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * FRAME 3: Verifikasi Kelengkapan
 * Berfungsi memverifikasi bukti yang ada sebelum memvalidasi penugasan,
 * meminta klarifikasi informasi, atau menolak/menutup laporan.
 */
@Composable
fun VerifikasiKelengkapanScreen(
    onValidasiPenugasan: () -> Unit = {},
    onMintaInformasi: () -> Unit = {},
    onTidakDapatDiproses: () -> Unit = {},
    onBackToDetail: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "VERIFIKASI KELENGKAPAN",
            onBackClick = onBackToDetail
        )

        // 2. Konten Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ringkasan Laporan
            LaporanSummaryHeaderCard(
                id = "LTR-2026-00123",
                statusText = "Diperiksa",
                statusBg = Color(0xFFFFEDD5),
                statusColor = Color(0xFFC2410C),
                title = "Pelecehan Verbal",
                subtitle = "Dikirim 15 Oktober 2026, 15:00 WITA · Anonim"
            )

            // Card Ringkasan Verifikasi
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan verifikasi",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "3 bukti tersedia · Perlu pemeriksaan",
                        modifier = Modifier
                            .background(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB45309)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Periksa kronologi, kategori, waktu, lokasi dan bukti sebelum memvalidasi laporan.",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button 1: Validasi & Atur Penugasan
                    Button(
                        onClick = onValidasiPenugasan,
                        colors = ButtonDefaults.buttonColors(containerColor = SiagaTealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Validasi & Atur Penugasan",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button 2: Minta Informasi
                    OutlinedButton(
                        onClick = onMintaInformasi,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(SiagaTealPrimary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Minta Informasi",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaTealPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button 3: Tidak Dapat Diproses
                    OutlinedButton(
                        onClick = onTidakDapatDiproses,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE11D48))
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Tidak Dapat Diproses",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE11D48)
                            )
                        )
                    }
                }
            }

            // Tombol Kembali
            OutlinedButton(
                onClick = onBackToDetail,
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(SiagaBorderColor)
                ),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Kembali ke Detail Laporan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )
            }
            Spacer(modifier = Modifier.height(96.dp))
        }

        // 3. Bottom Bar
        AdminSiagaBottomBar(currentRoute = "Laporan")
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun VerifikasiKelengkapanScreenPreview() {
    VerifikasiKelengkapanScreen()
}

