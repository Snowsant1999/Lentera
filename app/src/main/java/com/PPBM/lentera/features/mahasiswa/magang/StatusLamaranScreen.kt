package com.PPBM.lentera.features.mahasiswa.magang

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
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.mahasiswa.model.DokumenLamaranItem
import com.PPBM.lentera.backend.mahasiswa.model.LamaranRiwayatItem
import com.PPBM.lentera.backend.mahasiswa.model.LamaranSaya
import com.PPBM.lentera.backend.mahasiswa.model.LowonganTerbit
import com.PPBM.lentera.core.designsystem.theme.*
import java.util.Locale

private fun statusTitleLabel(status: String): String = when (status.lowercase()) {
    "terkirim" -> "Lamaran Terkirim"
    "pemeriksaan" -> "Pemeriksaan Berkas"
    "perlu_perbaikan" -> "Perlu Perbaikan Dokumen"
    "seleksi" -> "Seleksi & Wawancara"
    "diterima" -> "Diterima Magang"
    "ditolak" -> "Ditolak"
    else -> status.replaceFirstChar { it.uppercase() }
}

@Composable
fun StatusLamaranScreen(
    lamaran: LamaranSaya? = null,
    lowongan: LowonganTerbit? = null,
    riwayatList: List<LamaranRiwayatItem> = emptyList(),
    dokumenList: List<DokumenLamaranItem> = emptyList(),
    onOpenDokumen: (DokumenLamaranItem) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPerbaikiDokumenClick: () -> Unit = {},
    onInformasiPenerimaanClick: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    val posisi = lowongan?.posisi ?: "Lowongan Magang"
    val perusahaan = lowongan?.namaPerusahaan ?: "Mitra Industri"
    val nomorLamaran = lamaran?.nomorLamaran ?: "-"
    val statusCode = lamaran?.status?.lowercase().orEmpty()
    val needsCorrection = statusCode == "perlu_perbaikan"
    val isFinalAccepted = statusCode == "diterima"
    val isRejected = statusCode == "ditolak"
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
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_2),
                        contentDescription = perusahaan,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
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
                            text = "No. $nomorLamaran · ${statusTitleLabel(statusCode)}",
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

            if (needsCorrection) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFFED7AA)))
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
                            text = catatanAdmin ?: "Admin meminta Anda mengunggah ulang dokumen perbaikan yang sesuai.",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
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

            if (isFinalAccepted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangPillGreenBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFB7E4C7)))
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
                            text = catatanAdmin
                                ?: "Anda dinyatakan diterima pada program magang $posisi di $perusahaan.",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Text(
                                text = "Lihat Informasi Penerimaan",
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

            if (isRejected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangPillRedBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFFECACA)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lamaran Tidak Lolos Seleksi",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MagangPillRedText
                            )
                        )
                        if (catatanAdmin != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = catatanAdmin,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = NavyDark
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
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
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

                    if (riwayatList.isNotEmpty()) {
                        riwayatList.forEachIndexed { index, entry ->
                            val ts = entry.createdAt.take(16).replace("T", " ")
                            val noteText = entry.catatan?.takeIf { it.isNotBlank() }?.let { "$it · $ts" } ?: ts
                            val tStatus = when (entry.status.lowercase()) {
                                "perlu_perbaikan", "ditolak" -> TimelineStatus.WARNING
                                else -> TimelineStatus.DONE
                            }
                            TimelineItemRow(
                                title = statusTitleLabel(entry.status),
                                subtitle = noteText,
                                status = tStatus,
                                isLast = index == riwayatList.lastIndex,
                                fontFamily = fontFamily
                            )
                        }
                    } else {
                        val submittedDate = lamaran?.createdAt?.take(10)?.takeIf { it.isNotBlank() } ?: "-"
                        TimelineItemRow(
                            title = "Lamaran Terkirim",
                            subtitle = "Diajukan pada $submittedDate",
                            status = TimelineStatus.DONE,
                            isLast = false,
                            fontFamily = fontFamily
                        )
                        val stage2Done = statusCode in setOf("pemeriksaan", "seleksi", "diterima")
                        TimelineItemRow(
                            title = "Pemeriksaan Dokumen",
                            subtitle = when {
                                needsCorrection -> catatanAdmin ?: "Menunggu dokumen revisi"
                                stage2Done -> "Berkas dalam/lulus pemeriksaan admin"
                                else -> "Menunggu giliran pemeriksaan admin"
                            },
                            status = when {
                                needsCorrection -> TimelineStatus.WARNING
                                stage2Done -> TimelineStatus.DONE
                                else -> TimelineStatus.PENDING
                            },
                            isLast = false,
                            fontFamily = fontFamily
                        )
                        val stage3Done = statusCode in setOf("seleksi", "diterima")
                        TimelineItemRow(
                            title = "Seleksi & Wawancara",
                            subtitle = if (stage3Done) "Memasuki tahap seleksi/wawancara" else "Menunggu hasil verifikasi berkas",
                            status = if (stage3Done) TimelineStatus.DONE else TimelineStatus.PENDING,
                            isLast = false,
                            fontFamily = fontFamily
                        )
                        TimelineItemRow(
                            title = "Keputusan Akhir",
                            subtitle = when {
                                isFinalAccepted -> "Diterima sebagai peserta magang"
                                isRejected -> "Tidak lolos seleksi"
                                else -> "Menunggu hasil seleksi"
                            },
                            status = when {
                                isFinalAccepted -> TimelineStatus.DONE
                                isRejected -> TimelineStatus.WARNING
                                else -> TimelineStatus.PENDING
                            },
                            isLast = true,
                            fontFamily = fontFamily
                        )
                    }
                }
            }

            if (dokumenList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Berkas Lamaran Terunggah",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        dokumenList.forEachIndexed { idx, doc ->
                            if (idx > 0) Spacer(modifier = Modifier.height(8.dp))
                            val sizeLabel = if (doc.ukuranBytes >= 1024 * 1024) {
                                String.format(Locale.US, "%.1f MB", doc.ukuranBytes / (1024.0 * 1024.0))
                            } else {
                                "${(doc.ukuranBytes / 1024).coerceAtLeast(1)} KB"
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .clickable { onOpenDokumen(doc) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.jenis,
                                        style = TextStyle(
                                            fontFamily = fontFamily,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDark
                                        )
                                    )
                                    Text(
                                        text = "${doc.namaFile} · ${extractShortFileType(doc.namaFile)} ($sizeLabel)",
                                        style = TextStyle(
                                            fontFamily = fontFamily,
                                            fontSize = 11.sp,
                                            color = SlateGray
                                        )
                                    )
                                }
                                Text(
                                    text = "Buka",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangAmberPillText
                                    )
                                )
                            }
                        }
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
