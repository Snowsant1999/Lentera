package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan

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
    laporan: AdminLaporan? = null,
    onValidasiPenugasan: () -> Unit = {},
    onMintaInformasi: () -> Unit = {},
    onTidakDapatDiproses: () -> Unit = {},
    onBackToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val nomorLaporan = laporan?.nomorLaporan ?: "LTR-..."
    val judulLaporan = laporan?.judul ?: "Laporan"
    val tanggalKejadian = laporan?.tanggalKejadian ?: "-"
    val isAnonim = laporan?.isAnonim ?: true
    val statusStr = (laporan?.status ?: "baru").replaceFirstChar { it.uppercase() }
    val (statusBg, statusColor) = when (laporan?.status?.lowercase()) {
        "baru" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "diproses", "penanganan", "investigasi" -> Pair(Color(0xFFFFE4E6), Color(0xFFE11D48))
        "selesai" -> Pair(Color(0xFFE8F4F1), Color(0xFF2F7078))
        "ditolak" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
    }

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
                id = nomorLaporan,
                statusText = statusStr,
                statusBg = statusBg,
                statusColor = statusColor,
                title = judulLaporan,
                subtitle = "Tanggal: $tanggalKejadian · ${if (isAnonim) "Anonim" else "Terverifikasi"}"
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
                        text = "Status: $statusStr · Perlu verifikasi",
                        modifier = Modifier
                            .background(color = statusBg, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
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
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun VerifikasiKelengkapanScreenPreview() {
    VerifikasiKelengkapanScreen()
}

