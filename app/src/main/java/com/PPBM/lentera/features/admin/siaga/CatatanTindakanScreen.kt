package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
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
 * FRAME 6: Catatan Tindakan
 * Menampilkan ringkasan klarifikasi, timeline log tindakan penanganan,
 * dan input catatan tindakan baru secara berkala.
 */
@Composable
fun CatatanTindakanScreen(
    onHasilPenyelesaian: () -> Unit = {},
    onSimpanCatatan: (String) -> Unit = {},
    onBackToDetail: () -> Unit = {}
) {
    var catatanTindakan by remember {
        mutableStateOf("Pendampingan telah dilakukan. Tim berwenang telah menyampaikan tindak lanjut sesuai prosedur.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "CATATAN TINDAKAN",
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
                title = "Pelecehan Verbal · Anonim",
                subtitle = "Ditugaskan ke Tim Advokasi Kampus · 17 Okt, 10:30"
            )

            // Card Ringkasan Permintaan Informasi
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Info",
                            tint = SiagaTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ringkasan permintaan informasi",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Informasi sudah dilengkapi",
                        modifier = Modifier
                            .background(color = SiagaTealLight, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = SiagaTealPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Admin Rina Wulandari meminta klarifikasi frekuensi dan saksi (17 Okt, 09:30). Pelapor menanggapi: terjadi dua kali, ada saksi bersedia (17 Okt, 10:05).",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )
                }
            }

            // Card Log Tindakan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "Log",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Tindakan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val logs = listOf(
                        Triple("Diverifikasi dan divalidasi", "17 Okt, 10:15 · Data dinyatakan lengkap", true),
                        Triple("Dialihkan ke Tim Advokasi", "17 Okt, 10:30 · Pendampingan dijadwalkan", true),
                        Triple("Koordinasi penanganan", "17 Okt, 13:00 · Pemeriksaan dan tindak lanjut", false)
                    )

                    logs.forEach { (judul, ket, hasNext) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                painter = painterResource(
                                    id = if (hasNext) R.drawable.ic_timeline_marker else R.drawable.ic_timeline_marker_dot
                                ),
                                contentDescription = "Status: $judul",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(16.dp, if (hasNext) 48.dp else 22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = judul,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = SiagaNavyDark
                                    )
                                )
                                Text(
                                    text = ket,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        color = SiagaTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tambah catatan tindakan",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = catatanTindakan,
                        onValueChange = { catatanTindakan = it },
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onSimpanCatatan(catatanTindakan) },
                        colors = ButtonDefaults.buttonColors(containerColor = SiagaNavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Simpan Catatan",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Tombol: Hasil & Penyelesaian
            OutlinedButton(
                onClick = onHasilPenyelesaian,
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
                    text = "Hasil & Penyelesaian",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )
            }

            // Tombol: Kembali ke Detail Laporan
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
fun CatatanTindakanScreenPreview() {
    CatatanTindakanScreen()
}

