package com.PPBM.lentera.features.admin.magang

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.features.admin.magang.components.*

@Composable
fun DetailValidasiLamaranScreen(
    pelamar: PelamarLamaranItem = SampleDaftarLamaran.first(),
    onBackClick: () -> Unit = {},
    onStatusUpdated: (newStatus: String, newCatatan: String) -> Unit = { _, _ -> },
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var currentStatus by remember { mutableStateOf(pelamar.status) }
    var currentCatatan by remember { mutableStateOf(pelamar.catatanRevisi) }

    var showMintaRevisiDialog by remember { mutableStateOf(false) }
    var revisiInputText by remember { mutableStateOf(pelamar.catatanRevisi) }

    var showWawancaraDialog by remember { mutableStateOf(false) }
    var selectedKeputusan by remember { mutableStateOf("Wawancara") }

    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    val statusColors = when (currentStatus) {
        "Menunggu Verifikasi" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "Perlu Perbaikan" -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
        "Dikirim ke Mitra" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "Wawancara" -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
        "Diterima" -> Pair(Color(0xFFD1FAE5), Color(0xFF065F46))
        "Ditolak" -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
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
            AdminMagangBottomBar(currentRoute = "Magang Hub", onNavClick = onNavClick)
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
            // Card 1: Ringkasan Pelamar & Lowongan
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

                    Text(
                        text = "NIM ${pelamar.nim} · ${pelamar.jurusan} - ${pelamar.prodi}",
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
                        Column {
                            Text(
                                text = "Posisi Dilamar",
                                style = TextStyle(fontSize = 11.sp, fontFamily = PlusJakartaFont, color = MagangTextSecondary)
                            )
                            Text(
                                text = pelamar.posisi,
                                style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.Bold, color = MagangNavyDark)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
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

            // Warning Notice if "Perlu Perbaikan"
            if (currentStatus == "Perlu Perbaikan" && currentCatatan.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFED7AA)))
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
                                text = "Catatan Revisi untuk Mahasiswa",
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

            // Card 2: Pemeriksaan Berkas Dokumen Persyaratan
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

                    // Dokumen 1: CV
                    BerkasItemRow(
                        title = "Curriculum Vitae (CV)",
                        fileName = pelamar.cvFileName,
                        iconRes = R.drawable.ic_doc_cv
                    )

                    // Dokumen 2: Surat Pengantar
                    BerkasItemRow(
                        title = "Surat Pengantar Kampus",
                        fileName = pelamar.suratPengantarFileName,
                        iconRes = R.drawable.ic_doc_surat_pengantar
                    )

                    // Dokumen 3: Transkrip
                    BerkasItemRow(
                        title = "Transkrip Nilai Terakhir",
                        fileName = pelamar.transkripFileName,
                        iconRes = R.drawable.ic_doc_transkrip
                    )
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

                    // Tombol 1: Validasi & Kirim ke Mitra
                    Button(
                        onClick = {
                            currentStatus = "Dikirim ke Mitra"
                            onStatusUpdated("Dikirim ke Mitra", "")
                            feedbackTitle = "Lamaran Terverifikasi"
                            feedbackMessage = "Berkas lamaran mahasiswa dinyatakan valid dan telah diteruskan ke mitra ${pelamar.mitra}."
                            feedbackIsSuccess = true
                            showFeedbackDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MagangNavyDark)
                    ) {
                        Text(
                            text = "Validasi & Kirim ke Mitra",
                            style = TextStyle(
                                fontSize = 13.5.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    // Tombol 2: Minta Perbaikan Berkas
                    OutlinedButton(
                        onClick = { showMintaRevisiDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFF7ED),
                            contentColor = Color(0xFFC2410C)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74))
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

                    // Tombol 3: Atur Wawancara & Keputusan
                    OutlinedButton(
                        onClick = { showWawancaraDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = MagangNavyDark
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCE6EE))
                    ) {
                        Text(
                            text = "Atur Status Wawancara / Keputusan",
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

    // Dialog Minta Revisi Berkas
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
                        text = "Tuliskan catatan perbaikan untuk mahasiswa pelapor/pelamar:",
                        fontFamily = PlusJakartaFont,
                        fontSize = 12.sp,
                        color = MagangTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = revisiInputText,
                        onValueChange = { revisiInputText = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Contoh: Transkrip nilai buram...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMintaRevisiDialog = false
                        currentStatus = "Perlu Perbaikan"
                        currentCatatan = revisiInputText
                        onStatusUpdated("Perlu Perbaikan", revisiInputText)
                        feedbackTitle = "Permintaan Perbaikan Terkirim"
                        feedbackMessage = "Status lamaran telah diubah menjadi 'Perlu Perbaikan Dokumen'. Catatan revisi telah dikirimkan ke mahasiswa."
                        feedbackIsSuccess = true
                        showFeedbackDialog = true
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

    // Dialog Atur Wawancara / Keputusan
    if (showWawancaraDialog) {
        AlertDialog(
            onDismissRequest = { showWawancaraDialog = false },
            title = {
                Text(
                    text = "Perbarui Status Seleksi & Wawancara",
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MagangNavyDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Wawancara", "Diterima", "Ditolak").forEach { opsi ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedKeputusan = opsi }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = selectedKeputusan == opsi,
                                onClick = { selectedKeputusan = opsi }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (opsi) {
                                    "Wawancara" -> "Tahap Wawancara Mitra"
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
                    onClick = {
                        showWawancaraDialog = false
                        currentStatus = selectedKeputusan
                        onStatusUpdated(selectedKeputusan, "")
                        feedbackTitle = "Status Diperbarui"
                        feedbackMessage = "Status lamaran mahasiswa kini menjadi: $selectedKeputusan dan tersinkronisasi di linimasa mahasiswa."
                        feedbackIsSuccess = true
                        showFeedbackDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagangNavyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWawancaraDialog = false }) {
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
    iconRes: Int
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

