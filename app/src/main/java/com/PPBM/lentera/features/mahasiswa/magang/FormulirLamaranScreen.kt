package com.PPBM.lentera.features.mahasiswa.magang

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

data class LamaranDokumenItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val fileName: String? = null,
    val isUploaded: Boolean = false,
    val isRequired: Boolean = false
)

/**
 * Multi-step Formulir Lamaran Magang:
 * Step 1: Data Diri (Formulir Lamaran.png)
 * Step 2: Unggah Dokumen (Formulir Lamaran-1.png)
 * Step 3: Konfirmasi (Formulir Lamaran-2.png)
 * Step 4: Selesai / Konfirmasi Sukses (Konfirmasi Lamaran.png)
 */
@Composable
fun FormulirLamaranScreen(
    posisiJudul: String = "UI/UX Designer",
    perusahaan: String = "PT Teknologi Nusantara",
    onBackClick: () -> Unit = {},
    onCekStatusLamaran: () -> Unit = {},
    onKembaliKeBeranda: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    var currentStep by remember { mutableIntStateOf(1) }

    // State Step 1: Data Diri
    var namaLengkap by remember { mutableStateOf("Budi Santoso") }
    var nim by remember { mutableStateOf("2023110045") }
    var emailKampus by remember { mutableStateOf("budi.santoso@mhs.kampus.ac.id") }
    var prodi by remember { mutableStateOf("Teknik Informatika") }
    var semester by remember { mutableStateOf("Semester 5") }
    var noWa by remember { mutableStateOf("0812-3456-7890") }
    var ipk by remember { mutableStateOf("3.85") }

    // State Step 3: Pernyataan Checkbox
    var isAgreed by remember { mutableStateOf(true) }

    // State Step 2: Daftar Dokumen Persyaratan
    var dokumenList by remember {
        mutableStateOf(
            listOf(
                LamaranDokumenItem(
                    id = "cv",
                    title = "Curriculum Vitae (CV)",
                    iconRes = R.drawable.ic_doc_cv,
                    fileName = "cv_budi_santoso.pdf (1.2 MB)",
                    isUploaded = true,
                    isRequired = true
                ),
                LamaranDokumenItem(
                    id = "surat_pengantar",
                    title = "Surat Pengantar Kampus",
                    iconRes = R.drawable.ic_doc_surat_pengantar,
                    fileName = "surat_pengantar_fakultas.pdf (850 KB)",
                    isUploaded = true,
                    isRequired = true
                ),
                LamaranDokumenItem(
                    id = "transkrip",
                    title = "Transkrip Nilai Terakhir",
                    iconRes = R.drawable.ic_doc_transkrip,
                    fileName = "transkrip_nilai_sem4.pdf (1.8 MB)",
                    isUploaded = true,
                    isRequired = true
                ),
                LamaranDokumenItem(
                    id = "ktm",
                    title = "Foto Kartu Tanda Mahasiswa (KTM)",
                    iconRes = R.drawable.ic_doc_ktm,
                    fileName = "ktm_budi_santoso.jpg (450 KB)",
                    isUploaded = true,
                    isRequired = true
                ),
                LamaranDokumenItem(
                    id = "portofolio",
                    title = "Portofolio Desain",
                    iconRes = R.drawable.ic_doc_portfolio,
                    fileName = "portofolio_uiux_budi.pdf (4.5 MB)",
                    isUploaded = true,
                    isRequired = false
                ),
                LamaranDokumenItem(
                    id = "sertifikat",
                    title = "Sertifikat Pendukung",
                    iconRes = R.drawable.ic_doc_sertifikat,
                    fileName = null,
                    isUploaded = false,
                    isRequired = false
                )
            )
        )
    }

    var activeDocForUpload by remember { mutableStateOf<LamaranDokumenItem?>(null) }
    var showUploadModal by remember { mutableStateOf(false) }
    var showSelectFromProfileDialog by remember { mutableStateOf(false) }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackSuccess by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val (name, sizeStr) = getFileDetails(context, uri)
            activeDocForUpload?.let { doc ->
                dokumenList = dokumenList.map {
                    if (it.id == doc.id) {
                        it.copy(fileName = "$name ($sizeStr)", isUploaded = true)
                    } else it
                }
                feedbackTitle = "Berkas Berhasil Diunggah"
                feedbackMessage = "Berkas $name ($sizeStr) berhasil dilampirkan untuk ${doc.title}."
                feedbackSuccess = true
                showFeedback = true
            }
            showUploadModal = false
            activeDocForUpload = null
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        topBar = {
            if (currentStep < 4) {
                FormulirTopBar(
                    title = "Formulir Lamaran",
                    subtitle = "$perusahaan · $posisiJudul",
                    onBackClick = {
                        if (currentStep > 1) currentStep-- else onBackClick()
                    },
                    fontFamily = fontFamily
                )
            }
        },
        bottomBar = {
            if (currentStep < 4) {
                FormulirBottomBar(
                    currentStep = currentStep,
                    isNextEnabled = if (currentStep == 3) isAgreed else true,
                    onPrevClick = { if (currentStep > 1) currentStep-- },
                    onNextClick = {
                        if (currentStep < 4) currentStep++
                    },
                    fontFamily = fontFamily
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (currentStep < 4) {
                // Stepper Header (1, 2, 3, 4)
                StepperHeader(
                    currentStep = currentStep,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (currentStep) {
                1 -> Step1DataDiri(
                    nama = namaLengkap, onNamaChange = { namaLengkap = it },
                    nimVal = nim, onNimChange = { nim = it },
                    email = emailKampus, onEmailChange = { emailKampus = it },
                    prodiVal = prodi, onProdiChange = { prodi = it },
                    semVal = semester, onSemChange = { semester = it },
                    waVal = noWa, onWaChange = { noWa = it },
                    ipkVal = ipk, onIpkChange = { ipk = it },
                    fontFamily = fontFamily
                )
                2 -> Step2UnggahDokumen(
                    dokumenList = dokumenList,
                    onUploadClick = { item ->
                        activeDocForUpload = item
                        showUploadModal = true
                    },
                    fontFamily = fontFamily
                )
                3 -> Step3Konfirmasi(
                    nama = namaLengkap,
                    nim = nim,
                    email = emailKampus,
                    wa = noWa,
                    posisi = posisiJudul,
                    perusahaan = perusahaan,
                    dokumenList = dokumenList,
                    isAgreed = isAgreed,
                    onAgreedChange = { isAgreed = it },
                    onUbahDataDiri = { currentStep = 1 },
                    onUbahDokumen = { currentStep = 2 },
                    fontFamily = fontFamily
                )
                4 -> Step4Sukses(
                    posisi = posisiJudul,
                    perusahaan = perusahaan,
                    onCekStatus = onCekStatusLamaran,
                    onKembali = onKembaliKeBeranda,
                    fontFamily = fontFamily
                )
            }
        }
    }

    if (showUploadModal && activeDocForUpload != null) {
        UploadDokumenLamaranDialog(
            docTitle = activeDocForUpload!!.title,
            onDismiss = {
                showUploadModal = false
                activeDocForUpload = null
            },
            onChooseFromProfile = {
                showUploadModal = false
                showSelectFromProfileDialog = true
            },
            onChooseFromDevice = {
                filePickerLauncher.launch("*/*")
            },
            fontFamily = fontFamily
        )
    }

    if (showSelectFromProfileDialog && activeDocForUpload != null) {
        PilihDariProfilDialog(
            docTitle = activeDocForUpload!!.title,
            onDismiss = {
                showSelectFromProfileDialog = false
                activeDocForUpload = null
            },
            onDocSelected = { selectedDocName ->
                val currentDoc = activeDocForUpload!!
                dokumenList = dokumenList.map {
                    if (it.id == currentDoc.id) {
                        it.copy(fileName = selectedDocName, isUploaded = true)
                    } else it
                }
                showSelectFromProfileDialog = false
                activeDocForUpload = null
                feedbackTitle = "Berkas Dipilih dari Profil"
                feedbackMessage = "$selectedDocName berhasil dilampirkan untuk ${currentDoc.title}."
                feedbackSuccess = true
                showFeedback = true
            },
            fontFamily = fontFamily
        )
    }

    LenteraFeedbackDialog(
        show = showFeedback,
        isSuccess = feedbackSuccess,
        title = feedbackTitle,
        message = feedbackMessage,
        onDismiss = { showFeedback = false }
    )
}

// ============================================================================
// Top Bar
// ============================================================================

@Composable
private fun FormulirTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    fontFamily: FontFamily
) {
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
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    color = SlateGray
                ),
                maxLines = 1
            )
        }
    }
}

// ============================================================================
// Stepper Header Component
// ============================================================================

@Composable
private fun StepperHeader(
    currentStep: Int,
    fontFamily: FontFamily
) {
    val steps = listOf("Data Diri", "Dokumen", "Konfirmasi", "Selesai")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep
            val isActive = stepNumber == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone -> MagangPillGreenBg
                                isActive -> MagangAmberPrimary
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_check),
                            contentDescription = "Selesai",
                            tint = Color(0xFF287D4A),
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNumber",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else SlateGray
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 10.5.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) NavyDark else SlateGray
                    )
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp, vertical = 0.dp)
                        .background(if (stepNumber < currentStep) MagangAmberPrimary else Color(0xFFE2E8F0))
                )
            }
        }
    }
}

// ============================================================================
// Step 1: Data Diri (Formulir Lamaran.png)
// ============================================================================

@Composable
private fun Step1DataDiri(
    nama: String, onNamaChange: (String) -> Unit,
    nimVal: String, onNimChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    prodiVal: String, onProdiChange: (String) -> Unit,
    semVal: String, onSemChange: (String) -> Unit,
    waVal: String, onWaChange: (String) -> Unit,
    ipkVal: String, onIpkChange: (String) -> Unit,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Data Pribadi & Akademik",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )
        Text(
            text = "Pastikan data diri Anda sesuai dengan data resmi di kampus.",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.sp,
                color = SlateGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        FormFieldItem(label = "Nama Lengkap", value = nama, onValueChange = onNamaChange, fontFamily = fontFamily)
        FormFieldItem(label = "Nomor Induk Mahasiswa (NIM)", value = nimVal, onValueChange = onNimChange, fontFamily = fontFamily)
        FormFieldItem(label = "Email Kampus", value = email, onValueChange = onEmailChange, fontFamily = fontFamily)
        FormFieldItem(label = "Program Studi", value = prodiVal, onValueChange = onProdiChange, fontFamily = fontFamily)
        FormFieldItem(label = "Semester Saat Ini", value = semVal, onValueChange = onSemChange, fontFamily = fontFamily)
        FormFieldItem(label = "Nomor WhatsApp", value = waVal, onValueChange = onWaChange, fontFamily = fontFamily)
        FormFieldItem(label = "IPK Terakhir", value = ipkVal, onValueChange = onIpkChange, fontFamily = fontFamily)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormFieldItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: FontFamily
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyDark
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MagangAmberPrimary,
                unfocusedBorderColor = InputBorder
            ),
            textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = NavyDark
            ),
            singleLine = true
        )
    }
}

// ============================================================================
// Step 2: Unggah Dokumen (Formulir Lamaran-1.png)
// ============================================================================

@Composable
private fun Step2UnggahDokumen(
    dokumenList: List<LamaranDokumenItem>,
    onUploadClick: (LamaranDokumenItem) -> Unit,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Unggah Dokumen Persyaratan",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )
        Text(
            text = "Format PDF atau JPG/PNG, maksimal 5MB per dokumen.",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.sp,
                color = SlateGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        dokumenList.forEach { item ->
            DocUploadCard(
                item = item,
                onUploadClick = { onUploadClick(item) },
                fontFamily = fontFamily
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DocUploadCard(
    item: LamaranDokumenItem,
    onUploadClick: () -> Unit,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onUploadClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = NavyDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    if (item.isRequired) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "*Wajib",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ErrorRed
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.fileName ?: "Belum ada file diunggah",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 11.5.sp,
                        color = if (item.isUploaded) SlateGray else SlateGrayLight
                    )
                )
            }

            if (item.isUploaded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ubah",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagangAmberPillText
                        )
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_check),
                        contentDescription = "Terunggah",
                        tint = Color(0xFF287D4A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MagangAmberPillBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+ Unggah",
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

// ============================================================================
// Step 3: Konfirmasi (Formulir Lamaran-2.png)
// ============================================================================

@Composable
private fun Step3Konfirmasi(
    nama: String,
    nim: String,
    email: String,
    wa: String,
    posisi: String,
    perusahaan: String,
    dokumenList: List<LamaranDokumenItem> = emptyList(),
    isAgreed: Boolean,
    onAgreedChange: (Boolean) -> Unit,
    onUbahDataDiri: () -> Unit,
    onUbahDokumen: () -> Unit,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Ringkasan Lamaran",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
        )
        Text(
            text = "Periksa kembali kelengkapan sebelum mengirimkan berkas.",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.sp,
                color = SlateGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Kartu Posisi Magang
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_2),
                    contentDescription = perusahaan,
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = posisi,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Text(
                        text = perusahaan,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ringkasan Data Diri Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Data Pelamar",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Text(
                        text = "Ubah",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagangAmberPillText
                        ),
                        modifier = Modifier.clickable(onClick = onUbahDataDiri)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Nama: $nama", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "NIM: $nim", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "Email: $email", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "WhatsApp: $wa", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ringkasan Dokumen Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dokumen Persyaratan",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val uploadedCount = dokumenList.count { it.isUploaded }
                    val requiredUploaded = dokumenList.count { it.isRequired && it.isUploaded }
                    val totalRequired = dokumenList.count { it.isRequired }
                    Text(
                        text = if (requiredUploaded >= totalRequired) {
                            "$uploadedCount Dokumen telah terunggah lengkap"
                        } else {
                            "$requiredUploaded dari $totalRequired Dokumen Wajib terunggah"
                        },
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = if (requiredUploaded >= totalRequired) Color(0xFF287D4A) else ErrorRed
                        )
                    )
                }
                Text(
                    text = "Ubah",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MagangAmberPillText
                    ),
                    modifier = Modifier.clickable(onClick = onUbahDokumen)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Checkbox Pernyataan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAgreedChange(!isAgreed) },
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isAgreed,
                onCheckedChange = onAgreedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MagangAmberPrimary,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Saya menyatakan bahwa semua data dan dokumen yang saya berikan adalah benar, sah, dan dapat dipertanggungjawabkan.",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = SlateGray
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================================
// Step 4: Konfirmasi Lamaran Selesai (Konfirmasi Lamaran.png)
// ============================================================================

@Composable
private fun Step4Sukses(
    posisi: String,
    perusahaan: String,
    onCekStatus: () -> Unit,
    onKembali: () -> Unit,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_sent_success),
            contentDescription = "Sukses",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Lamaran Berhasil Dikirim!",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lamaran Anda untuk posisi $posisi di $perusahaan berhasil terkirim dan diteruskan ke tim rekrutmen.",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SlateGray,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailInfoRow(label = "Nomor Lamaran", value = "LNT-MG-2026-0891", fontFamily = fontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                DetailInfoRow(label = "Tanggal Kirim", value = "30 Okt 2026, 14:30 WIB", fontFamily = fontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                DetailInfoRow(label = "Estimasi Proses", value = "3 - 5 Hari Kerja", fontFamily = fontFamily)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCekStatus,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MagangAmberPrimary)
        ) {
            Text(
                text = "Cek Status Lamaran",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onKembali,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
        ) {
            Text(
                text = "Kembali ke Beranda Magang",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyDark
                )
            )
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = TextStyle(fontFamily = fontFamily, fontSize = 12.sp, color = SlateGray))
        Text(text = value, style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NavyDark))
    }
}

// ============================================================================
// Bottom Action Bar
// ============================================================================

@Composable
private fun FormulirBottomBar(
    currentStep: Int,
    isNextEnabled: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    fontFamily: FontFamily
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onPrevClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
                ) {
                    Text(
                        text = "Sebelumnya",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyDark
                        )
                    )
                }
            }

            Button(
                onClick = onNextClick,
                enabled = isNextEnabled,
                modifier = Modifier
                    .weight(if (currentStep > 1) 1.5f else 1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MagangAmberPrimary,
                    disabledContainerColor = SlateGrayLight
                )
            ) {
                Text(
                    text = if (currentStep == 3) "Kirim Lamaran" else "Selanjutnya",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormulirLamaranScreenPreview() {
    LenteraTheme {
        FormulirLamaranScreen()
    }
}



// ============================================================================
// Komponen Dialog Unggah & Pemilihan Dokumen
// ============================================================================

@Composable
private fun UploadDokumenLamaranDialog(
    docTitle: String,
    onDismiss: () -> Unit,
    onChooseFromProfile: () -> Unit,
    onChooseFromDevice: () -> Unit,
    fontFamily: FontFamily
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBD5E1))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Unggah Berkas",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pilih sumber berkas untuk $docTitle (PDF, JPG, atau PNG maks 5MB).",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.5.sp,
                        color = SlateGray
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Opsi 1: Ambil dari Dokumen Saya
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .clickable(onClick = onChooseFromProfile)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_briefcase_business),
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ambil dari Dokumen Saya",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pilih dari berkas profil yang sudah tersimpan",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 11.5.sp,
                                color = SlateGray
                            )
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_status_chevron),
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Opsi 2: Upload File Baru dari Perangkat
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .clickable(onClick = onChooseFromDevice)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MagangAmberPillBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload_symbol),
                            contentDescription = null,
                            tint = MagangAmberPillText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pilih Berkas Baru dari HP",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Buka File Manager / Penyimpanan Perangkat",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 11.5.sp,
                                color = SlateGray
                            )
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_status_chevron),
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "Batal",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PilihDariProfilDialog(
    docTitle: String,
    onDismiss: () -> Unit,
    onDocSelected: (String) -> Unit,
    fontFamily: FontFamily
) {
    val sampleProfileDocs = remember {
        listOf(
            "sertifikat_pelatihan_uiux.pdf (1.5 MB)",
            "sertifikat_web_development.pdf (950 KB)",
            "cv_budi_santoso_2026.pdf (1.2 MB)",
            "portofolio_uiux_design_budi.pdf (4.8 MB)"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = "Pilih dari Dokumen Saya",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pilih berkas profil untuk dilampirkan pada $docTitle",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            sampleProfileDocs.forEach { docName ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { onDocSelected(docName) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_check),
                        contentDescription = null,
                        tint = Color(0xFF287D4A),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = docName,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InputBackground)
            ) {
                Text(
                    text = "Tutup",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }
        }
    }
}

// Helper untuk membaca nama & ukuran file asli dari Content Resolver Android
private fun getFileDetails(context: android.content.Context, uri: Uri): Pair<String, String> {
    var name = "dokumen_terlampir.pdf"
    var sizeStr = "512 KB"
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex) ?: name
                }
                if (sizeIndex != -1) {
                    val bytes = cursor.getLong(sizeIndex)
                    sizeStr = if (bytes > 1024 * 1024) {
                        String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                    } else {
                        "${bytes / 1024} KB"
                    }
                }
            }
        }
    } catch (_: Exception) {}
    return Pair(name, sizeStr)
}
