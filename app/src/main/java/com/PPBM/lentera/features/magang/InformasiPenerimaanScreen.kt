package com.PPBM.lentera.features.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

/**
 * Informasi Penerimaan Screen
 * Sesuai referensi Informasi Penerimaan.png & Informasi Penerimaan Final.png
 */
@Composable
fun InformasiPenerimaanScreen(
    posisi: String = "Product Design Intern",
    perusahaan: String = "PT Bismillah Berkah",
    isFinal: Boolean = true,
    onBackClick: () -> Unit = {},
    onDownloadPdf: () -> Unit = {},
    onKonfirmasiKehadiran: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_button),
                    contentDescription = "Kembali",
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(onClick = onBackClick)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Informasi Penerimaan",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = onKonfirmasiKehadiran,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MagangAmberPrimary)
                    ) {
                        Text(
                            text = "Konfirmasi Kehadiran",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDownloadPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                    ) {
                        Text(
                            text = "Unduh Surat Penerimaan (PDF)",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyDark
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Letterhead Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_company_bismillah),
                            contentDescription = perusahaan,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = perusahaan,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                            Text(
                                text = "Surat Keterangan Penerimaan Magang",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Dengan bangga kami menginformasikan bahwa Saudara dinyatakan lolos seleksi dan diterima untuk mengikuti program magang industri.",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = NavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rincian Penempatan Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Rincian Penempatan & Jadwal",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoPenerimaanRow("Posisi", posisi, fontFamily)
                    InfoPenerimaanRow("Divisi / Departemen", "Product & Technology", fontFamily)
                    InfoPenerimaanRow("Periode Magang", "1 Des 2026 - 31 Mei 2027 (6 Bulan)", fontFamily)
                    InfoPenerimaanRow("Tipe Kerja", "Hybrid (3 Hari WFO, 2 Hari WFH)", fontFamily)
                    InfoPenerimaanRow("Lokasi Kantor", "Gedung Equity Tower Lt. 22, SCBD, Jakarta Selatan", fontFamily)
                    InfoPenerimaanRow("Mentor Lapangan", "Hendra Gunawan, S.Kom", fontFamily)
                    InfoPenerimaanRow("Uang Saku & Benefit", "Tersedia sesuai ketentuan mitra", fontFamily, isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Petunjuk Onboarding Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InputBackground),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(InputBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                            tint = NavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Petunjuk Persiapan Magang",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. Lengkapi Surat Tugas Resmi Magang dari fakultas sebelum tanggal mulai.\n" +
                                "2. Konfirmasi kehadiran melalui tombol di bawah paling lambat 25 November 2026.\n" +
                                "3. Hadir pada sesi induksi pertama tanggal 1 Desember 2026 pukul 09:00 WIB di kantor mitra.",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = NavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun InfoPenerimaanRow(
    label: String,
    value: String,
    fontFamily: FontFamily,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = if (isLast) 0.dp else 10.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 11.5.sp,
                color = SlateGray
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )
        )
        if (!isLast) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InformasiPenerimaanScreenPreview() {
    LenteraTheme {
        InformasiPenerimaanScreen()
    }
}

