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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * FRAME 4: Minta Informasi
 * Digunakan untuk meminta keterangan tambahan kepada pelapor
 * secara anonim melalui nomor laporan resmi.
 */
@Composable
fun MintaInformasiScreen(
    onKirimPermintaan: (String) -> Unit = {},
    onBackToDetail: () -> Unit = {}
) {
    var pesanText by remember { mutableStateOf("Tulis permintaan informasi tambahan untuk pelapor") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "MINTA INFORMASI",
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

            // Card Konteks Anonim
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Konteks anonim",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AdminInfoBanner(
                        text = "Identitas pelapor dirahasiakan. Komunikasi dilakukan melalui nomor laporan, tanpa membuka nama atau NIM."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Gunakan nomor laporan untuk menyampaikan permintaan klarifikasi tanpa membuka identitas pelapor.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )
                }
            }

            // Card Permintaan Informasi
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Permintaan informasi",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Pesan ke pelapor",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = pesanText,
                        onValueChange = { pesanText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SiagaBorderColor,
                            focusedBorderColor = SiagaTealPrimary
                        ),
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pesan akan dikirim melalui notifikasi dan nomor laporan.",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onKirimPermintaan(pesanText) },
                        colors = ButtonDefaults.buttonColors(containerColor = SiagaTealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Kirim Permintaan",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AdminInfoBanner(
                        text = "Setelah dikirim, status laporan akan berubah menjadi Menunggu Informasi Tambahan."
                    )
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
fun MintaInformasiScreenPreview() {
    MintaInformasiScreen()
}

