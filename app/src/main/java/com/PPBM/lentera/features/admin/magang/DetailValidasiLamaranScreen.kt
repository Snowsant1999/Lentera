package com.PPBM.lentera.features.admin.magang

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.backend.mahasiswa.model.DokumenLamaranItem
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.features.admin.magang.components.*
import java.util.Locale

@Composable
fun DetailValidasiLamaranScreen(
    pelamar: PelamarLamaranItem = PelamarLamaranItem(),
    dokumenList: List<DokumenLamaranItem> = emptyList(),
    isUpdating: Boolean = false,
    onOpenDokumen: (DokumenLamaranItem) -> Unit = {},
    onBackClick: () -> Unit = {},
    onStatusUpdated: (
        newStatus: String,
        newCatatan: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit = { _, _, _, _ -> },
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var currentStatus by remember(pelamar.lamaranUuid, pelamar.status) {
        mutableStateOf(pelamar.status)
    }
    var currentCatatan by remember(pelamar.lamaranUuid, pelamar.catatanRevisi) {
        mutableStateOf(pelamar.catatanRevisi)
    }

    var showMintaRevisiDialog by remember { mutableStateOf(false) }
    var revisiInputText by remember(pelamar.lamaranUuid, pelamar.catatanRevisi) {
        mutableStateOf(pelamar.catatanRevisi)
    }
    var revisiError by remember { mutableStateOf<String?>(null) }

    var showKeputusanDialog by remember { mutableStateOf(false) }
    var selectedKeputusan by remember { mutableStateOf("Seleksi / Wawancara") }

    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    val statusColors = when (currentStatus) {
        "Menunggu Verifikasi" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "Pemeriksaan Berkas" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "Perlu Perbaikan" -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
        "Seleksi / Wawancara", "Wawancara", "Dikirim ke Mitra" -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
        "Diterima" -> Pair(Color(0xFFD1FAE5), Color(0xFF065F46))
        "Ditolak" -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    fun applyStatusChange(targetLabel: String, dbStatus: String, catatan: String, successMsg: String) {
        onStatusUpdated(
            dbStatus,
            catatan,
            { errMsg ->
                feedbackTitle = "Gagal Memperbarui Status"
                feedbackMessage = errMsg
                feedbackIsSuccess = false
                showFeedbackDialog = true
            },
            {
                currentStatus = targetLabel
                currentCatatan = catatan
                feedbackTitle = "Status Diperbarui"
                feedbackMessage = successMsg
                feedbackIsSuccess = true
                showFeedbackDialog = true
            }
        )
    }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "VALIDASI LAMARAN",
                subtitle = "Verifikasi Berkas Mahasiswa · ${pelamar.id}",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang", onNavClick = onNavClick)
        },
        containerColor = MagangBgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ringkasan Pelamar & Lowongan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pelamar.id,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangTextSecondary
                            )
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColors.first)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = currentStatus,
                                style = TextStyle(
                                    fontSize = 11.5.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColors.second
                                )
                            )
                        }
                    }

                    Text(
                        text = pelamar.namaMahasiswa,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.ExtraBold,
                            color = MagangNavyDark
                        )
                    )

                    val prodiInfo = listOf(pelamar.jurusan, pelamar.prodi)
                        .filter { it.isNotBlank() }
                        .joinToString(" - ")
                    Text(
                        text = if (prodiInfo.isNotBlank()) "NIM ${pelamar.nim} · $prodiInfo" else "NIM ${pelamar.nim}",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            color = MagangTextSecondary
                        )
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Posisi Dilamar",
                                style = TextStyle(fontSize = 11.sp, fontFamily = PlusJakartaFont, color = MagangTextSecondary)
                            )
                            Text(
                                text = pelamar.posisi,
                                style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.Bold, color = MagangNavyDark)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mitra Perusahaan",
                                style = TextStyle(fontSize = 11.sp, fontFamily = PlusJakartaFont, color = MagangTextSecondary)
                            )
                            Text(
                                text = pelamar.mitra,
                                style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.Bold, color = MagangNavyDark)
                            )
                        }
                    }

                    Text(
                        text = "Diajukan pada ${pelamar.tanggalLamar}",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            color = MagangTextSecondary
                        )
                    )
                }
            }

            if (currentCatatan.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFFED7AA)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_file_warning),
                                contentDescription = null,
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Catatan Admin untuk Mahasiswa",
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentCatatan,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                lineHeight = 17.sp,
                                color = MagangNavyDark
                            )
                        )
                    }
                }
            }

            // Pemeriksaan Berkas Dokumen Persyaratan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Dokumen Persyaratan Magang",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                    Text(
                        text = "Periksa keabsahan dan kelengkapan berkas yang diunggah mahasiswa.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            color = MagangTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (dokumenList.isEmpty()) {
                        Text(
                            text = "Belum ada berkas yang diunggah untuk lamaran ini.",
                            style = TextStyle(
                                fontSize = 12.5.sp,
                                fontFamily = PlusJakartaFont,
                                color = MagangTextSecondary
                            )
                        )
                    } else {
                        dokumenList.forEach { doc ->
                            val sizeLabel = if (doc.ukuranBytes >= 1024 * 1024) {
                                String.format(Locale.US, "%.1f MB", doc.ukuranBytes / (1024.0 * 1024.0))
                            } else {
                                "${(doc.ukuranBytes / 1024).coerceAtLeast(1)} KB"
                            }
                            val iconRes = when (doc.jenis.lowercase()) {
                                "surat pengantar" -> R.drawable.ic_doc_surat_pengantar
                                "transkrip" -> R.drawable.ic_doc_transkrip
                                else -> R.drawable.ic_doc_cv
                            }
                            BerkasItemRow(
                                title = doc.jenis,
                                fileName = "${doc.namaFile} · ${extractShortFileType(doc.namaFile)} ($sizeLabel)",
                                iconRes = iconRes,
                                onOpenClick = { onOpenDokumen(doc) }
                            )
                        }
                    }
                }
            }

            // Action Buttons Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Aksi Kontrol Lamaran",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )

                    Button(
                        enabled = !isUpdating,
                        onClick = {
                            applyStatusChange(
                                targetLabel = "Seleksi / Wawancara",
                                dbStatus = "seleksi",
                                catatan = "",
                                successMsg = "Berkas dinyatakan valid dan diteruskan ke tahap Seleksi / Wawancara mitra ${pelamar.mitra}."
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MagangNavyDark)
                    ) {
                        Text(
                            text = if (isUpdating) "Memproses..." else "Lolos Berkas · Lanjut Seleksi",
                            style = TextStyle(
                                fontSize = 13.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    OutlinedButton(
                        enabled = !isUpdating,
                        onClick = {
                            revisiError = null
                            showMintaRevisiDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFF7ED),
                            contentColor = Color(0xFFC2410C)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFDBA74))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_file_warning),
                                contentDescription = null,
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Minta Perbaikan Berkas",
                                style = TextStyle(
                                    fontSize = 13.5.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        enabled = !isUpdating,
                        onClick = { showKeputusanDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = MagangNavyDark
                        ),
                        border = BorderStroke(1.dp, Color(0xFFDCE6EE))
                    ) {
                        Text(
                            text = "Perbarui Tahap / Keputusan Akhir",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = MagangNavyDark
                            )
                        )
                    }
                }
            }
        }
    }

    if (showMintaRevisiDialog) {
        AlertDialog(
            onDismissRequest = { showMintaRevisiDialog = false },
            title = {
                Text(
                    text = "Minta Perbaikan Dokumen",
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MagangNavyDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tuliskan catatan perbaikan wajib untuk mahasiswa pelamar:",
                        fontFamily = PlusJakartaFont,
                        fontSize = 12.sp,
                        color = MagangTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = revisiInputText,
                        onValueChange = {
                            revisiInputText = it
                            revisiError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Contoh: Transkrip nilai buram, unggah ulang...") }
                    )
                    if (revisiError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = revisiError!!,
                            fontFamily = PlusJakartaFont,
                            fontSize = 11.5.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isUpdating,
                    onClick = {
                        val trimmed = revisiInputText.trim()
                        if (trimmed.isBlank()) {
                            revisiError = "Catatan perbaikan wajib diisi."
                            return@Button
                        }
                        showMintaRevisiDialog = false
                        applyStatusChange(
                            targetLabel = "Perlu Perbaikan",
                            dbStatus = "perlu_perbaikan",
                            catatan = trimmed,
                            successMsg = "Status lamaran diperbarui menjadi Perlu Perbaikan dan catatan dikirim ke mahasiswa."
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim Catatan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMintaRevisiDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showKeputusanDialog) {
        val options = listOf(
            "Pemeriksaan Berkas" to "pemeriksaan",
            "Seleksi / Wawancara" to "seleksi",
            "Diterima" to "diterima",
            "Ditolak" to "ditolak"
        )
        AlertDialog(
            onDismissRequest = { showKeputusanDialog = false },
            title = {
                Text(
                    text = "Perbarui Tahap Seleksi",
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MagangNavyDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { (label, _) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedKeputusan = label }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = selectedKeputusan == label,
                                onClick = { selectedKeputusan = label }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (label) {
                                    "Pemeriksaan Berkas" -> "Sedang Pemeriksaan Berkas"
                                    "Seleksi / Wawancara" -> "Tahap Seleksi / Wawancara Mitra"
                                    "Diterima" -> "Diterima Magang (Lolos Seleksi)"
                                    else -> "Tidak Lolos / Ditolak"
                                },
                                fontFamily = PlusJakartaFont,
                                fontSize = 13.sp,
                                color = MagangNavyDark
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isUpdating,
                    onClick = {
                        val dbStatus = options.firstOrNull { it.first == selectedKeputusan }?.second ?: "seleksi"
                        showKeputusanDialog = false
                        applyStatusChange(
                            targetLabel = selectedKeputusan,
                            dbStatus = dbStatus,
                            catatan = "",
                            successMsg = "Status lamaran berhasil diperbarui ke: $selectedKeputusan."
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagangNavyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKeputusanDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    LenteraFeedbackDialog(
        show = showFeedbackDialog,
        isSuccess = feedbackIsSuccess,
        title = feedbackTitle,
        message = feedbackMessage,
        onDismiss = { showFeedbackDialog = false }
    )
}

@Composable
fun BerkasItemRow(
    title: String,
    fileName: String,
    iconRes: Int,
    onOpenClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = MagangNavyDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = MagangNavyDark
                    )
                )
                Text(
                    text = fileName,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = PlusJakartaFont,
                        color = MagangTextSecondary
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE2E8F0))
                .clickable { onOpenClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Buka",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    color = MagangNavyDark
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailValidasiLamaranScreenPreview() {
    LenteraTheme {
        DetailValidasiLamaranScreen()
    }
}
