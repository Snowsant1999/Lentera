package com.PPBM.lentera.features.magang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Status Lamaran Screen
 * Mendukung 2 mode sesuai referensi:
 * 1. Status Lamaran.png (Perlu perbaikan dokumen & info sementara)
 * 2. Status Lamaran Final.png (Final diterima)
 */
@Composable
fun StatusLamaranScreen(
    posisi: String = "UI/UX Designer Intern",
    perusahaan: String = "PT Maju Mundur",
    nomorLamaran: String = "LNT-MG-2026-0891",
    needsCorrection: Boolean = true,
    isFinalAccepted: Boolean = false,
    onBackClick: () -> Unit = {},
    onPerbaikiDokumenClick: () -> Unit = {},
    onInformasiPenerimaanClick: () -> Unit = {}
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
                    text = "Status Lamaran",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
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
            // Header Info Lamaran Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isFinalAccepted) R.drawable.ic_company_bismillah else R.drawable.ic_company_maju_mundur
                        ),
                        contentDescription = perusahaan,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = posisi,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Text(
                            text = perusahaan,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.5.sp,
                                color = SlateGray
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No. $nomorLamaran",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MagangAmberPillText
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Box Perhatian: Perlu Perbaikan Dokumen (Jika needsCorrection)
            if (needsCorrection && !isFinalAccepted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFED7AA)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_file_warning),
                                contentDescription = null,
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Perlu Perbaikan Dokumen",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Transkrip Nilai Terakhir buram atau tidak terbaca. Harap unggah scan dokumen asli beresolusi tinggi sebelum 15 Nov 2026.",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onPerbaikiDokumenClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_document_correction),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Perbaiki Dokumen Sekarang",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Box Final Accepted: Selamat Anda Diterima (Jika isFinalAccepted)
            if (isFinalAccepted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangPillGreenBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFB7E4C7)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_check),
                                contentDescription = null,
                                tint = Color(0xFF287D4A),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Selamat! Anda Lolos Seleksi",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF287D4A)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selamat, Anda dinyatakan diterima sebagai peserta program magang di $perusahaan. Silakan baca informasi penerimaan lengkap di bawah ini.",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onInformasiPenerimaanClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF287D4A)),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = "Lihat Informasi Penerimaan Lengkap",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Timeline Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Tahapan Seleksi",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Step 1: Lamaran Terkirim
                    TimelineItemRow(
                        title = "Lamaran Terkirim",
                        subtitle = "Berkas lamaran diterima sistem · 24 Okt 2026",
                        status = TimelineStatus.DONE,
                        isLast = false,
                        fontFamily = fontFamily
                    )

                    // Step 2: Pemeriksaan Dokumen
                    TimelineItemRow(
                        title = "Pemeriksaan Dokumen",
                        subtitle = if (needsCorrection && !isFinalAccepted)
                            "Memerlukan revisi transkrip nilai"
                        else
                            "Dokumen lengkap dan terverifikasi",
                        status = if (needsCorrection && !isFinalAccepted) TimelineStatus.WARNING else TimelineStatus.DONE,
                        isLast = false,
                        fontFamily = fontFamily
                    )

                    // Step 3: Seleksi & Wawancara
                    TimelineItemRow(
                        title = "Seleksi & Wawancara",
                        subtitle = if (isFinalAccepted)
                            "Wawancara user selesai · Direkomendasikan"
                        else if (needsCorrection)
                            "Menunggu verifikasi revisi berkas"
                        else
                            "Jadwal wawancara akan dikirim melalui email",
                        status = if (isFinalAccepted) TimelineStatus.DONE else TimelineStatus.PENDING,
                        isLast = false,
                        fontFamily = fontFamily
                    )

                    // Step 4: Keputusan Akhir
                    TimelineItemRow(
                        title = "Keputusan Akhir",
                        subtitle = if (isFinalAccepted)
                            "Diterima magang · Surat penerimaan terbit"
                        else
                            "Pengumuman hasil akhir penerimaan",
                        status = if (isFinalAccepted) TimelineStatus.DONE else TimelineStatus.PENDING,
                        isLast = true,
                        fontFamily = fontFamily
                    )
                }
            }

            // Tautan Informasi Penerimaan Sementara (jika provisional)
            if (!isFinalAccepted) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onInformasiPenerimaanClick),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Informasi Penerimaan Sementara",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_status_chevron),
                            contentDescription = null,
                            tint = NavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

enum class TimelineStatus {
    DONE, WARNING, PENDING
}

@Composable
private fun TimelineItemRow(
    title: String,
    subtitle: String,
    status: TimelineStatus,
    isLast: Boolean,
    fontFamily: FontFamily
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        when (status) {
                            TimelineStatus.DONE -> MagangPillGreenBg
                            TimelineStatus.WARNING -> Color(0xFFFFF7ED)
                            TimelineStatus.PENDING -> Color(0xFFF1F5F9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    TimelineStatus.DONE -> Icon(
                        painter = painterResource(id = R.drawable.ic_circle_check),
                        contentDescription = "Selesai",
                        tint = Color(0xFF287D4A),
                        modifier = Modifier.size(16.dp)
                    )
                    TimelineStatus.WARNING -> Icon(
                        painter = painterResource(id = R.drawable.ic_file_warning),
                        contentDescription = "Perhatian",
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(14.dp)
                    )
                    TimelineStatus.PENDING -> Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SlateGrayLight)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(
                            if (status == TimelineStatus.DONE) Color(0xFF287D4A) else Color(0xFFE2E8F0)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 20.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 11.5.sp,
                    color = SlateGray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusLamaranScreenPreview() {
    LenteraTheme {
        StatusLamaranScreen()
    }
}

