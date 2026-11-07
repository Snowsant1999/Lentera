package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
 * FRAME 7: Hasil & Penyelesaian
 * Menampilkan draf ringkasan penanganan untuk mahasiswa, dokumen penyelesaian,
 * pengaturan tanggal penyelesaian, sakelar publikasi, dan tombol penutupan laporan.
 */
@Composable
fun HasilPenyelesaianScreen(
    onSimpanDanSelesaikan: () -> Unit = {},
    onBackToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var isBagikanHasil by remember { mutableStateOf(true) }
    var statusPenyelesaian by remember { mutableStateOf("Selesai") }
    var tanggalSelesai by remember { mutableStateOf("17 Oktober 2026, 15:10 WITA") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "HASIL & PENYELESAIAN",
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
            // Ringkasan Laporan dengan Target Selesai
            LaporanSummaryHeaderCard(
                id = "LTR-2026-00123",
                statusText = "Penanganan",
                statusBg = Color(0xFFFFEDD5),
                statusColor = Color(0xFFC2410C),
                title = "Pelecehan Verbal · Anonim",
                subtitle = "",
                extraRow = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_date),
                            contentDescription = "Target",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target selesai 17 Okt 2026, 15:10 WITA",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaNavyDark
                            )
                        )
                    }
                }
            )

            // Card Hasil Penanganan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hasil Penanganan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )

                        Text(
                            text = "Draf hasil",
                            modifier = Modifier
                                .background(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ringkasan untuk mahasiswa",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Tim Advokasi telah menindaklanjuti laporan dan melakukan koordinasi dengan pihak terkait. Pendampingan tersedia melalui kanal laporan ini.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val docs = listOf(
                        Pair("ringkasan_penanganan.pdf", "PDF · 480 KB · Boleh dibagikan"),
                        Pair("catatan_internal.pdf", "PDF · 320 KB · Internal, tidak dipublikasikan")
                    )

                    docs.forEach { (name, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_file_text),
                                    contentDescription = "PDF",
                                    tint = SiagaNavyDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = name,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.Bold,
                                            color = SiagaNavyDark
                                        )
                                    )
                                    Text(
                                        text = desc,
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaFont,
                                            color = SiagaTextSecondary
                                        )
                                    )
                                }
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = "Buka",
                                tint = SiagaNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { /* Tambah dokumentasi */ },
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(SiagaTealPrimary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Tambah Dokumentasi",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaTealPrimary
                            )
                        )
                    }
                }
            }

            // Card Status Penyelesaian
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Status Penyelesaian",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Status setelah disimpan",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusPenyelesaian,
                            style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Dropdown",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tanggal penyelesaian",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tanggalSelesai,
                        onValueChange = { tanggalSelesai = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SiagaBorderColor,
                            focusedBorderColor = SiagaTealPrimary
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                    )
                }
            }

            // Card Publikasi Hasil
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Publikasi Hasil",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bagikan hasil ke mahasiswa",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )

                        Switch(
                            checked = isBagikanHasil,
                            onCheckedChange = { isBagikanHasil = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SiagaTealPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hanya ringkasan dan dokumen berlabel Boleh dibagikan yang tampil ke pelapor setelah status Selesai. Catatan internal tidak dibagikan.",
                        style = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    AdminInfoBanner(
                        text = "Pastikan ringkasan dan dokumen telah mendapat izin untuk dibagikan. Mahasiswa akan menerima notifikasi pembaruan."
                    )
                }
            }

            // Button: Simpan & Selesaikan Laporan
            Button(
                onClick = onSimpanDanSelesaikan,
                colors = ButtonDefaults.buttonColors(containerColor = SiagaTealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Simpan & Selesaikan Laporan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Button: Kembali ke Detail Laporan
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
fun HasilPenyelesaianScreenPreview() {
    HasilPenyelesaianScreen()
}

