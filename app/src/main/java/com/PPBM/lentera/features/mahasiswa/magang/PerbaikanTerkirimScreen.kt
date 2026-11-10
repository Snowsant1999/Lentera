package com.PPBM.lentera.features.mahasiswa.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
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

@Composable
fun PerbaikanTerkirimScreen(
    perusahaan: String = "",
    dokumenJenis: String = "",
    fileName: String = "",
    waktuKirim: String = "",
    onKembaliKeStatus: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    val dokumenLabel = listOf(dokumenJenis, fileName)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
        .ifBlank { "Berkas Perbaikan" }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_sent_success),
                contentDescription = "Berhasil",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Perbaikan Dokumen Terkirim!",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (perusahaan.isNotBlank()) {
                    "Dokumen revisi Anda untuk lamaran di $perusahaan telah tersimpan dan menunggu pemeriksaan ulang oleh admin kampus."
                } else {
                    "Dokumen revisi Anda telah tersimpan dan menunggu pemeriksaan ulang oleh admin kampus."
                },
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dokumen",
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.sp, color = SlateGray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = dokumenLabel,
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NavyDark),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu Pengiriman",
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.sp, color = SlateGray)
                        )
                        Text(
                            text = waktuKirim.ifBlank { "-" },
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Status Verifikasi",
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.sp, color = SlateGray)
                        )
                        Text(
                            text = "Menunggu Verifikasi Admin",
                            style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = MagangAmberPillText)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onKembaliKeStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
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

@Preview(showBackground = true)
@Composable
fun PerbaikanTerkirimScreenPreview() {
    LenteraTheme {
        PerbaikanTerkirimScreen()
    }
}
