package com.PPBM.lentera.features.admin.siaga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

/**
 * FRAME 5: Penugasan Laporan
 * Menentukan jalur tindak lanjut dan menugaskan unit yang berwenang
 * seperti Tim Advokasi Kampus beserta catatan khusus penugasan.
 */
@Composable
fun PenugasanLaporanScreen(
    onSimpanPenugasan: () -> Unit = {},
    onBackToDetail: () -> Unit = {}
) {
    var jalurTindakLanjut by remember { mutableStateOf("Alihkan ke pihak berwenang") }
    var unitPetugas by remember { mutableStateOf("Tim Advokasi Kampus") }
    var catatanPenugasan by remember {
        mutableStateOf("Koordinasikan pendampingan dan pemeriksaan dengan menjaga kerahasiaan pelapor.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "PENUGASAN LAPORAN",
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
                statusText = "Penanganan",
                statusBg = Color(0xFFFFEDD5),
                statusColor = Color(0xFFC2410C),
                title = "Pelecehan Verbal",
                subtitle = "Dikirim 15 Oktober 2026, 15:00 WITA · Anonim"
            )

            // Banner Info Pelapor Anonim
            AdminInfoBanner(
                text = "Pelapor anonim. Seluruh komunikasi dilakukan melalui kanal laporan LTR-2026-00123 tanpa membuka identitas."
            )

            // Card Penugasan & Jalur Penanganan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Penugasan & Jalur Penanganan",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Jalur tindak lanjut",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Dropdown Box 1: Jalur
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = jalurTindakLanjut,
                            style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Dropdown",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Unit / petugas berwenang",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Dropdown Box 2: Unit
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = unitPetugas,
                            style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Dropdown",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Catatan penugasan",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = catatanPenugasan,
                        onValueChange = { catatanPenugasan = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SiagaBorderColor,
                            focusedBorderColor = SiagaTealPrimary
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Alternatif jalur: koordinasi penanganan oleh admin sesuai prosedur kampus.",
                        style = TextStyle(fontSize = 11.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )
                }
            }

            // Button: Simpan Penugasan
            Button(
                onClick = onSimpanPenugasan,
                colors = ButtonDefaults.buttonColors(containerColor = SiagaNavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_assign),
                    contentDescription = "Simpan",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simpan Penugasan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
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
fun PenugasanLaporanScreenPreview() {
    PenugasanLaporanScreen()
}

