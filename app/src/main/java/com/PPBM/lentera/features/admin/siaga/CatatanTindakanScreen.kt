package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan

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
    laporan: AdminLaporan? = null,
    logTindakanList: List<com.PPBM.lentera.backend.admin.model.LogTindakanItem> = emptyList(),
    permintaanList: List<com.PPBM.lentera.backend.mahasiswa.model.PermintaanInformasiItem> = emptyList(),
    isUpdating: Boolean = false,
    onHasilPenyelesaian: () -> Unit = {},
    onSimpanCatatan: (String) -> Unit = {},
    onBackToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val nomorLaporan = laporan?.nomorLaporan ?: "-"
    val judulLaporan = laporan?.judul ?: "Memuat laporan..."
    val isAnonim = laporan?.isAnonim ?: true
    val unitPenanganan = laporan?.unitPenanganan?.takeIf { it.isNotBlank() } ?: "Tim Kampus"

    var catatanTindakan by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

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
                id = nomorLaporan,
                statusText = "Penanganan",
                statusBg = Color(0xFFFFE4E6),
                statusColor = Color(0xFFE11D48),
                title = judulLaporan,
                subtitle = "Ditugaskan ke: $unitPenanganan · ${if (isAnonim) "Anonim" else "Terverifikasi"}"
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

                    val latestPermintaan = permintaanList.lastOrNull()
                    val (infoBadgeText, infoBadgeBg, infoBadgeColor) = when {
                        permintaanList.isEmpty() ->
                            Triple("Belum ada permintaan informasi", Color(0xFFF1F5F9), SiagaTextSecondary)
                        permintaanList.any { it.balasanPelapor.isNullOrBlank() } ->
                            Triple("Menunggu balasan pelapor", Color(0xFFFEF3C7), Color(0xFFB45309))
                        else ->
                            Triple("Balasan pelapor diterima", SiagaTealLight, SiagaTealPrimary)
                    }

                    Text(
                        text = infoBadgeText,
                        modifier = Modifier
                            .background(color = infoBadgeBg, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = infoBadgeColor
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (latestPermintaan != null) {
                        Text(
                            text = "Pesan admin: ${latestPermintaan.pesanKePelapor}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaNavyDark
                            )
                        )
                        if (!latestPermintaan.balasanPelapor.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Balasan pelapor: ${latestPermintaan.balasanPelapor}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Medium,
                                    color = SiagaTealPrimary
                                )
                            )
                        }
                    } else {
                        Text(
                            text = laporan?.catatanPenugasan?.ifBlank { "Tidak ada catatan instruksi khusus dari admin penerima." }
                                ?: "Tidak ada catatan instruksi khusus dari admin penerima.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaTextSecondary
                            )
                        )
                    }
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

                    if (logTindakanList.isEmpty()) {
                        Text(
                            text = "Belum ada log tindakan yang dicatat untuk laporan ini.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaTextSecondary
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        logTindakanList.forEachIndexed { index, log ->
                            val hasNext = index < logTindakanList.size - 1
                            val dateInfo = log.createdAt?.take(10) ?: "Baru saja"
                            val ket = "$dateInfo · ${log.deskripsi}"
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    painter = painterResource(
                                        id = if (hasNext) R.drawable.ic_timeline_marker else R.drawable.ic_timeline_marker_dot
                                    ),
                                    contentDescription = "Status: ${log.judulTindakan}",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(16.dp, if (hasNext) 48.dp else 22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = log.judulTindakan,
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
                        onValueChange = {
                            catatanTindakan = it
                            if (validationError != null) validationError = null
                        },
                        placeholder = {
                            Text(
                                text = "Tuliskan catatan tindakan penanganan baru...",
                                style = TextStyle(fontSize = 13.sp, fontFamily = PlusJakartaFont, color = SiagaTextMuted)
                            )
                        },
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

                    if (!validationError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = validationError!!,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFB91C1C)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val trimmed = catatanTindakan.trim()
                            if (trimmed.isBlank()) {
                                validationError = "Catatan tindakan tidak boleh kosong."
                                return@Button
                            }
                            validationError = null
                            catatanTindakan = ""
                            onSimpanCatatan(trimmed)
                        },
                        enabled = !isUpdating && laporan != null,
                        colors = ButtonDefaults.buttonColors(containerColor = SiagaNavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isUpdating) "Menyimpan..." else "Simpan Catatan",
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
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun CatatanTindakanScreenPreview() {
    CatatanTindakanScreen()
}

