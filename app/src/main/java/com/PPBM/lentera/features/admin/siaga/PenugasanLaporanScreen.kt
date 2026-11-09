package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    laporan: AdminLaporan? = null,
    isUpdating: Boolean = false,
    onSimpanPenugasan: (unitPetugas: String, jalurTindakLanjut: String, catatanPenugasan: String) -> Unit = { _, _, _ -> },
    onBackToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val nomorLaporan = laporan?.nomorLaporan ?: "-"
    val judulLaporan = laporan?.judul ?: "Memuat laporan..."
    val isAnonim = laporan?.isAnonim ?: true

    val jalurOptions = remember {
        listOf(
            "Pemeriksaan dan Pendampingan Tim Advokasi",
            "Klarifikasi & Mediasi Internal",
            "Investigasi Satgas Kampus",
            "Rujukan Layanan Konseling / Psikologis"
        )
    }
    val unitOptions = remember {
        listOf(
            "Tim Advokasi Kampus",
            "Satgas PPKS Kampus",
            "Layanan Konseling Mahasiswa",
            "Bidang Kemahasiswaan"
        )
    }

    var jalurTindakLanjut by remember {
        mutableStateOf(laporan?.jalurTindakLanjut?.takeIf { it.isNotBlank() } ?: jalurOptions.first())
    }
    var unitPetugas by remember {
        mutableStateOf(laporan?.unitPenanganan?.takeIf { it.isNotBlank() } ?: unitOptions.first())
    }
    var catatanPenugasan by remember {
        mutableStateOf(laporan?.catatanPenugasan.orEmpty())
    }
    var showJalurMenu by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(laporan?.id) {
        if (laporan != null) {
            if (!laporan.jalurTindakLanjut.isNullOrBlank()) jalurTindakLanjut = laporan.jalurTindakLanjut
            if (!laporan.unitPenanganan.isNullOrBlank()) unitPetugas = laporan.unitPenanganan
            if (laporan.catatanPenugasan != null) catatanPenugasan = laporan.catatanPenugasan
        }
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
                id = nomorLaporan,
                statusText = "Penanganan",
                statusBg = Color(0xFFFFE4E6),
                statusColor = Color(0xFFE11D48),
                title = judulLaporan,
                subtitle = "Pelapor: ${if (isAnonim) "Anonim" else (laporan?.pelaporNama ?: "Mahasiswa")}"
            )

            // Banner Info Pelapor Anonim
            AdminInfoBanner(
                text = "Pelapor ${if (isAnonim) "anonim" else "terverifikasi"}. Seluruh komunikasi dilakukan melalui kanal laporan $nomorLaporan tanpa membuka identitas."
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                                .clickable { showJalurMenu = true }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = jalurTindakLanjut,
                                modifier = Modifier.weight(1f),
                                style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "Dropdown",
                                tint = SiagaNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showJalurMenu,
                            onDismissRequest = { showJalurMenu = false }
                        ) {
                            jalurOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontFamily = PlusJakartaFont, fontSize = 13.sp) },
                                    onClick = {
                                        jalurTindakLanjut = option
                                        validationError = null
                                        showJalurMenu = false
                                    }
                                )
                            }
                        }
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                                .clickable { showUnitMenu = true }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unitPetugas,
                                modifier = Modifier.weight(1f),
                                style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_down),
                                contentDescription = "Dropdown",
                                tint = SiagaNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showUnitMenu,
                            onDismissRequest = { showUnitMenu = false }
                        ) {
                            unitOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontFamily = PlusJakartaFont, fontSize = 13.sp) },
                                    onClick = {
                                        unitPetugas = option
                                        validationError = null
                                        showUnitMenu = false
                                    }
                                )
                            }
                        }
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
                        placeholder = {
                            Text(
                                text = "Tulis catatan atau arahan penugasan (opsional)...",
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
                        Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Alternatif jalur: koordinasi penanganan oleh admin sesuai prosedur kampus.",
                        style = TextStyle(fontSize = 11.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )
                }
            }

            // Button: Simpan Penugasan
            Button(
                onClick = {
                    val unitTrim = unitPetugas.trim()
                    val jalurTrim = jalurTindakLanjut.trim()
                    if (unitTrim.isBlank() || jalurTrim.isBlank()) {
                        validationError = "Jalur tindak lanjut dan unit petugas wajib dipilih."
                        return@Button
                    }
                    validationError = null
                    onSimpanPenugasan(unitTrim, jalurTrim, catatanPenugasan.trim())
                },
                enabled = !isUpdating && laporan != null,
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
                    text = if (isUpdating) "Menyimpan..." else "Simpan Penugasan",
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
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PenugasanLaporanScreenPreview() {
    PenugasanLaporanScreen()
}

