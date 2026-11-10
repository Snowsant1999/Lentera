package com.PPBM.lentera.features.mahasiswa.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import com.PPBM.lentera.backend.mahasiswa.model.LowonganTerbit
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun InformasiPenerimaanScreen(
    lamaran: LamaranSaya? = null,
    lowongan: LowonganTerbit? = null,
    onBackClick: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    val perusahaan = lowongan?.namaPerusahaan ?: "Mitra Magang"
    val posisi = lowongan?.posisi ?: "-"
    val periode = lowongan?.periodeMagang ?: "-"
    val sistemKerja = lowongan?.sistemKerja ?: "-"
    val lokasiGabungan = listOfNotNull(
        lowongan?.lokasi?.takeIf { it.isNotBlank() },
        lowongan?.kota?.takeIf { it.isNotBlank() },
        lowongan?.provinsi?.takeIf { it.isNotBlank() }
    ).distinct().joinToString(", ").ifBlank { "-" }
    val catatanAdmin = lamaran?.catatanAdmin?.takeIf { it.isNotBlank() }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MagangAmberPrimary)
                    ) {
                        Text(
                            text = "Kembali ke Status Lamaran",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_avatar_2),
                            contentDescription = perusahaan,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
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
                                text = "Konfirmasi Penerimaan Magang",
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
                        text = "Selamat, Saudara ${lamaran?.namaLengkap ?: "Mahasiswa"} (NIM ${lamaran?.nim ?: "-"}) dinyatakan diterima untuk mengikuti program magang industri sesuai rincian penempatan berikut.",
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Rincian Penempatan",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoPenerimaanRow("Nomor Lamaran", lamaran?.nomorLamaran ?: "-", fontFamily)
                    InfoPenerimaanRow("Posisi", posisi, fontFamily)
                    InfoPenerimaanRow("Bidang Usaha", lowongan?.bidangUsaha ?: "-", fontFamily)
                    InfoPenerimaanRow("Periode Magang", periode, fontFamily)
                    InfoPenerimaanRow("Sistem Kerja", sistemKerja, fontFamily)
                    InfoPenerimaanRow("Lokasi Penempatan", lokasiGabungan, fontFamily, isLast = catatanAdmin == null)
                    if (catatanAdmin != null) {
                        InfoPenerimaanRow("Catatan Admin / Mitra", catatanAdmin, fontFamily, isLast = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InputBackground),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(InputBorder))
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
                            text = "Langkah Selanjutnya",
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
                        text = "1. Hubungi koordinator magang program studi untuk penerbitan surat tugas resmi.\n" +
                            "2. Ikuti arahan jadwal onboarding dari admin kampus atau kontak mitra $perusahaan.",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 10.dp)
    ) {
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
