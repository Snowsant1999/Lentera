package com.PPBM.lentera.features.mahasiswa.magang

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.WITA_ZONE
import com.PPBM.lentera.backend.core.normalizeDokumenLamaranJenis
import com.PPBM.lentera.backend.mahasiswa.model.DokumenMahasiswaItem
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.theme.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val MAX_FORM_LAMARAN_BYTES = 20L * 1024L * 1024L

data class LamaranDokumenItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val fileName: String? = null,
    val isUploaded: Boolean = false,
    val isRequired: Boolean = false
)

data class FormLamaranData(
    val namaLengkap: String,
    val nim: String,
    val tempatTanggalLahir: String,
    val alamatKtp: String,
    val alamatDomisili: String,
    val agama: String,
    val noWa: String,
    val email: String,
    val golonganDarah: String,
    val prodi: String = "",
    val semester: String = "",
    val ipk: String = "",
    val dokumenFiles: List<Triple<String, String, ByteArray>> = emptyList(),
    val profileDocPaths: List<Triple<String, String, String>> = emptyList()
)

@Composable
fun FormulirLamaranScreen(
    posisiJudul: String = "",
    perusahaan: String = "",
    initialNama: String = "",
    initialNim: String = "",
    initialEmail: String = "",
    initialProdi: String = "",
    initialNoWa: String = "",
    profileDocs: List<DokumenMahasiswaItem> = emptyList(),
    isSubmitting: Boolean = false,
    onBackClick: () -> Unit = {},
    onSubmitLamaran: ((
        formData: FormLamaranData,
        onError: (String) -> Unit,
        onSuccess: (nomorLamaran: String) -> Unit
    ) -> Unit)? = null,
    onCekStatusLamaran: () -> Unit = {},
    onKembaliKeBeranda: () -> Unit = {}
) {
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    var currentStep by remember { mutableIntStateOf(1) }

    // State Step 1: Data Diri lengkap sesuai skema `lamaran`
    var namaLengkap by remember(initialNama) { mutableStateOf(initialNama) }
    var nim by remember(initialNim) { mutableStateOf(initialNim) }
    var tempatTanggalLahir by remember { mutableStateOf("") }
    var alamatKtp by remember { mutableStateOf("") }
    var alamatDomisili by remember { mutableStateOf("") }
    var agama by remember { mutableStateOf("") }
    var noWa by remember(initialNoWa) { mutableStateOf(initialNoWa) }
    var emailKampus by remember(initialEmail) { mutableStateOf(initialEmail) }
    var golonganDarah by remember { mutableStateOf("") }
    var prodi by remember(initialProdi) { mutableStateOf(initialProdi) }
    var semester by remember { mutableStateOf("") }
    var ipk by remember { mutableStateOf("") }

    var isAgreed by remember { mutableStateOf(false) }

    var submittedNomorLamaran by remember { mutableStateOf("") }
    var submittedTimestampText by remember { mutableStateOf("") }

    var dokumenList by remember {
        mutableStateOf(
            listOf(
                LamaranDokumenItem("cv", "Curriculum Vitae (CV)", R.drawable.ic_doc_cv, isRequired = true),
                LamaranDokumenItem("surat_pengantar", "Surat Pengantar Kampus", R.drawable.ic_doc_surat_pengantar, isRequired = true),
                LamaranDokumenItem("transkrip", "Transkrip Nilai Terakhir", R.drawable.ic_doc_transkrip, isRequired = true),
                LamaranDokumenItem("ktm", "Foto Kartu Tanda Mahasiswa (KTM)", R.drawable.ic_doc_ktm, isRequired = true),
                LamaranDokumenItem("portofolio", "Portofolio Desain", R.drawable.ic_doc_portfolio, isRequired = false),
                LamaranDokumenItem("sertifikat", "Sertifikat Pendukung", R.drawable.ic_doc_sertifikat, isRequired = false)
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
    var docFilesMap by remember { mutableStateOf<Map<String, Triple<String, String, ByteArray>>>(emptyMap()) }
    var profileDocsMap by remember { mutableStateOf<Map<String, Triple<String, String, String>>>(emptyMap()) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val (name, sizeStr) = getFileDetails(context, uri)
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        } catch (_: Exception) {
            ByteArray(0)
        }
        val doc = activeDocForUpload
        if (bytes.isEmpty()) {
            feedbackTitle = "Gagal Membaca Berkas"
            feedbackMessage = "Berkas kosong atau tidak dapat diakses dari perangkat."
            feedbackSuccess = false
            showFeedback = true
        } else if (bytes.size > MAX_FORM_LAMARAN_BYTES) {
            feedbackTitle = "Ukuran Berkas Melebihi Batas"
            feedbackMessage = "Ukuran maksimal setiap dokumen lamaran adalah 20 MB."
            feedbackSuccess = false
            showFeedback = true
        } else if (doc != null) {
            val jenisNormalized = normalizeDokumenLamaranJenis(doc.title)
            dokumenList = dokumenList.map {
                if (it.id == doc.id) it.copy(fileName = "$name ($sizeStr)", isUploaded = true) else it
            }
            docFilesMap = docFilesMap + (doc.id to Triple(jenisNormalized, name, bytes))
            profileDocsMap = profileDocsMap - doc.id
            feedbackTitle = "Berkas Berhasil Dipilih"
            feedbackMessage = "Berkas $name ($sizeStr) siap dikirim untuk ${doc.title}."
            feedbackSuccess = true
            showFeedback = true
        }
        showUploadModal = false
        activeDocForUpload = null
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color.White,
            surface = Color.White,
            surfaceVariant = Color.White,
            onBackground = NavyDark,
            onSurface = NavyDark
        )
    ) {
        Scaffold(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        topBar = {
            if (currentStep < 4) {
                FormulirTopBar(
                    title = "Formulir Lamaran",
                    subtitle = listOf(perusahaan, posisiJudul).filter { it.isNotBlank() }.joinToString(" · "),
                    onBackClick = {
                        if (!isSubmitting) {
                            if (currentStep > 1) currentStep-- else onBackClick()
                        }
                    },
                    fontFamily = fontFamily
                )
            }
        },
        bottomBar = {
            if (currentStep < 4) {
                val requiredComplete = dokumenList.filter { it.isRequired }.all { it.isUploaded }
                FormulirBottomBar(
                    currentStep = currentStep,
                    isNextEnabled = when (currentStep) {
                        3 -> isAgreed && !isSubmitting
                        else -> !isSubmitting
                    },
                    nextLabel = when {
                        currentStep == 3 && isSubmitting -> "Mengirim Lamaran..."
                        currentStep == 3 -> "Kirim Lamaran"
                        else -> "Selanjutnya"
                    },
                    onPrevClick = { if (!isSubmitting && currentStep > 1) currentStep-- },
                    onNextClick = {
                        when (currentStep) {
                            1 -> {
                                val missing = buildList {
                                    if (namaLengkap.isBlank()) add("Nama lengkap")
                                    if (nim.isBlank()) add("NIM")
                                    if (tempatTanggalLahir.isBlank()) add("Tempat, tanggal lahir")
                                    if (alamatKtp.isBlank()) add("Alamat KTP")
                                    if (alamatDomisili.isBlank()) add("Alamat domisili")
                                    if (agama.isBlank()) add("Agama")
                                    if (noWa.isBlank()) add("Nomor WhatsApp/Telepon")
                                    if (emailKampus.isBlank() || !emailKampus.contains("@")) add("Email aktif")
                                    if (golonganDarah.isBlank()) add("Golongan darah")
                                }
                                if (missing.isNotEmpty()) {
                                    feedbackTitle = "Data Diri Belum Lengkap"
                                    feedbackMessage = "Mohon lengkapi kolom wajib: ${missing.joinToString(", ")}."
                                    feedbackSuccess = false
                                    showFeedback = true
                                } else {
                                    currentStep = 2
                                }
                            }
                            2 -> {
                                if (!requiredComplete) {
                                    val missingDocs = dokumenList.filter { it.isRequired && !it.isUploaded }.map { it.title }
                                    feedbackTitle = "Dokumen Wajib Belum Lengkap"
                                    feedbackMessage = "Silakan unggah dokumen wajib berikut: ${missingDocs.joinToString(", ")}."
                                    feedbackSuccess = false
                                    showFeedback = true
                                } else {
                                    currentStep = 3
                                }
                            }
                            3 -> {
                                if (!requiredComplete) {
                                    feedbackTitle = "Dokumen Wajib Belum Lengkap"
                                    feedbackMessage = "Kembali ke langkah Dokumen untuk melengkapi berkas wajib."
                                    feedbackSuccess = false
                                    showFeedback = true
                                    return@FormulirBottomBar
                                }
                                val formData = FormLamaranData(
                                    namaLengkap = namaLengkap.trim(),
                                    nim = nim.trim(),
                                    tempatTanggalLahir = tempatTanggalLahir.trim(),
                                    alamatKtp = alamatKtp.trim(),
                                    alamatDomisili = alamatDomisili.trim(),
                                    agama = agama.trim(),
                                    noWa = noWa.trim(),
                                    email = emailKampus.trim(),
                                    golonganDarah = golonganDarah.trim(),
                                    prodi = prodi.trim(),
                                    semester = semester.trim(),
                                    ipk = ipk.trim(),
                                    dokumenFiles = docFilesMap.values.toList(),
                                    profileDocPaths = profileDocsMap.values.toList()
                                )
                                onSubmitLamaran?.invoke(
                                    formData,
                                    { errMsg ->
                                        feedbackTitle = "Gagal Mengirim Lamaran"
                                        feedbackMessage = errMsg
                                        feedbackSuccess = false
                                        showFeedback = true
                                    },
                                    { nomorLamaran ->
                                        submittedNomorLamaran = nomorLamaran
                                        submittedTimestampText = ZonedDateTime.now(WITA_ZONE)
                                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("id", "ID"))) + " WITA"
                                        currentStep = 4
                                    }
                                )
                            }
                        }
                    },
                    fontFamily = fontFamily
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (currentStep < 4) {
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
                    ttlVal = tempatTanggalLahir, onTtlChange = { tempatTanggalLahir = it },
                    alamatKtpVal = alamatKtp, onAlamatKtpChange = { alamatKtp = it },
                    alamatDomisiliVal = alamatDomisili, onAlamatDomisiliChange = { alamatDomisili = it },
                    agamaVal = agama, onAgamaChange = { agama = it },
                    golDarahVal = golonganDarah, onGolDarahChange = { golonganDarah = it },
                    email = emailKampus, onEmailChange = { emailKampus = it },
                    waVal = noWa, onWaChange = { noWa = it },
                    prodiVal = prodi, onProdiChange = { prodi = it },
                    semVal = semester, onSemChange = { semester = it },
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
                    ttl = tempatTanggalLahir,
                    alamatDomisili = alamatDomisili,
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
                    nomorLamaran = submittedNomorLamaran,
                    tanggalKirim = submittedTimestampText,
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
            profileDocs = profileDocs,
            onDismiss = {
                showSelectFromProfileDialog = false
                activeDocForUpload = null
            },
            onDocSelected = { selectedDoc ->
                val currentDoc = activeDocForUpload!!
                val jenisNormalized = normalizeDokumenLamaranJenis(currentDoc.title)
                dokumenList = dokumenList.map {
                    if (it.id == currentDoc.id) {
                        it.copy(fileName = "${selectedDoc.namaFile} (Dari Profil)", isUploaded = true)
                    } else it
                }
                profileDocsMap = profileDocsMap + (
                    currentDoc.id to Triple(jenisNormalized, selectedDoc.namaFile, selectedDoc.filePath)
                )
                docFilesMap = docFilesMap - currentDoc.id
                showSelectFromProfileDialog = false
                activeDocForUpload = null
                feedbackTitle = "Berkas Dipilih dari Profil"
                feedbackMessage = "${selectedDoc.namaFile} akan dilampirkan untuk ${currentDoc.title}."
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
}

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
            .background(Color.White)
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
            if (subtitle.isNotBlank()) {
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
}

@Composable
private fun StepperHeader(
    currentStep: Int,
    fontFamily: FontFamily
) {
    val steps = listOf("Data Diri", "Dokumen", "Konfirmasi", "Selesai")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep
            val isActive = stepNumber == currentStep

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        .padding(horizontal = 4.dp)
                        .background(if (stepNumber < currentStep) MagangAmberPrimary else Color(0xFFE2E8F0))
                )
            }
        }
    }
}

@Composable
private fun Step1DataDiri(
    nama: String, onNamaChange: (String) -> Unit,
    nimVal: String, onNimChange: (String) -> Unit,
    ttlVal: String, onTtlChange: (String) -> Unit,
    alamatKtpVal: String, onAlamatKtpChange: (String) -> Unit,
    alamatDomisiliVal: String, onAlamatDomisiliChange: (String) -> Unit,
    agamaVal: String, onAgamaChange: (String) -> Unit,
    golDarahVal: String, onGolDarahChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    waVal: String, onWaChange: (String) -> Unit,
    prodiVal: String, onProdiChange: (String) -> Unit,
    semVal: String, onSemChange: (String) -> Unit,
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
            text = "Semua kolom bertanda * wajib diisi sesuai identitas resmi Anda.",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 12.sp,
                color = SlateGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        FormFieldItem(label = "Nama Lengkap *", value = nama, onValueChange = onNamaChange, fontFamily = fontFamily)
        FormFieldItem(label = "Nomor Induk Mahasiswa (NIM) *", value = nimVal, onValueChange = onNimChange, fontFamily = fontFamily)
        FormFieldItem(
            label = "Tempat, Tanggal Lahir *",
            value = ttlVal,
            onValueChange = onTtlChange,
            placeholder = "Contoh: Balikpapan, 12 Mei 2004",
            fontFamily = fontFamily
        )
        FormFieldItem(
            label = "Alamat Sesuai KTP *",
            value = alamatKtpVal,
            onValueChange = onAlamatKtpChange,
            singleLine = false,
            fontFamily = fontFamily
        )
        FormFieldItem(
            label = "Alamat Domisili Saat Ini *",
            value = alamatDomisiliVal,
            onValueChange = onAlamatDomisiliChange,
            singleLine = false,
            fontFamily = fontFamily
        )
        DropdownSelectFieldItem(
            label = "Agama *",
            value = agamaVal,
            placeholder = "Pilih agama",
            options = listOf("Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu", "Lainnya"),
            onOptionSelected = onAgamaChange,
            fontFamily = fontFamily
        )
        DropdownSelectFieldItem(
            label = "Golongan Darah *",
            value = golDarahVal,
            placeholder = "Pilih golongan darah",
            options = listOf("A", "B", "AB", "O", "Tidak Tahu"),
            onOptionSelected = onGolDarahChange,
            fontFamily = fontFamily
        )
        FormFieldItem(label = "Nomor WhatsApp / Telepon *", value = waVal, onValueChange = onWaChange, fontFamily = fontFamily)
        FormFieldItem(label = "Email Kampus *", value = email, onValueChange = onEmailChange, fontFamily = fontFamily)
        FormFieldItem(label = "Program Studi (Opsional)", value = prodiVal, onValueChange = onProdiChange, fontFamily = fontFamily)
        FormFieldItem(label = "Semester Saat Ini (Opsional)", value = semVal, onValueChange = onSemChange, fontFamily = fontFamily)
        FormFieldItem(label = "IPK Terakhir (Opsional)", value = ipkVal, onValueChange = onIpkChange, fontFamily = fontFamily)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormFieldItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
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
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, fontFamily = fontFamily, fontSize = 13.sp, color = SlateGrayLight) }
            } else null,
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
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3
        )
    }
}

@Composable
private fun DropdownSelectFieldItem(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    fontFamily: FontFamily
) {
    var expanded by remember { mutableStateOf(false) }
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
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifBlank { placeholder },
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = if (value.isBlank()) SlateGrayLight else NavyDark
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down_alt),
                    contentDescription = "Pilih",
                    tint = NavyDark,
                    modifier = Modifier.size(16.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontFamily = fontFamily) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

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
            text = "Format PDF atau JPG/PNG, maksimal 20 MB per dokumen.",
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
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
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

@Composable
private fun Step3Konfirmasi(
    nama: String,
    nim: String,
    ttl: String,
    alamatDomisili: String,
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
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
                Text(text = "TTL: $ttl", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "Domisili: $alamatDomisili", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "Email: $email", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
                Text(text = "WhatsApp: $wa", style = TextStyle(fontFamily = fontFamily, fontSize = 12.5.sp, color = NavyDark))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
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

@Composable
private fun Step4Sukses(
    posisi: String,
    perusahaan: String,
    nomorLamaran: String,
    tanggalKirim: String,
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
            text = "Lamaran Anda untuk posisi $posisi di $perusahaan berhasil terkirim dan diteruskan untuk verifikasi berkas.",
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
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailInfoRow(label = "Nomor Lamaran", value = nomorLamaran.ifBlank { "-" }, fontFamily = fontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                DetailInfoRow(label = "Waktu Kirim", value = tanggalKirim.ifBlank { "-" }, fontFamily = fontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                DetailInfoRow(label = "Status Awal", value = "Menunggu Verifikasi", fontFamily = fontFamily)
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
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(CardBorder))
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

@Composable
private fun FormulirBottomBar(
    currentStep: Int,
    isNextEnabled: Boolean,
    nextLabel: String,
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
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(CardBorder))
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
                    text = nextLabel,
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
                    text = "Pilih sumber berkas untuk $docTitle (PDF, JPG, atau PNG maks. 20 MB).",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.5.sp,
                        color = SlateGray
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    border = BorderStroke(1.dp, CardBorder)
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
    profileDocs: List<DokumenMahasiswaItem> = emptyList(),
    onDismiss: () -> Unit,
    onDocSelected: (DokumenMahasiswaItem) -> Unit,
    fontFamily: FontFamily
) {
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

            if (profileDocs.isEmpty()) {
                Text(
                    text = "Belum ada berkas tersimpan di Dokumen Saya. Gunakan opsi 'Pilih Berkas Baru dari HP'.",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.5.sp,
                        color = SlateGray
                    ),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                profileDocs.forEach { doc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .clickable { onDocSelected(doc) }
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.namaFile,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NavyDark
                                )
                            )
                            Text(
                                text = doc.jenis,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.sp,
                                    color = SlateGray
                                )
                            )
                        }
                    }
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

private fun getFileDetails(context: android.content.Context, uri: Uri): Pair<String, String> {
    var name = "dokumen_terlampir.pdf"
    var sizeStr = "0 KB"
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
                    sizeStr = if (bytes >= 1024 * 1024) {
                        String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                    } else {
                        "${(bytes / 1024).coerceAtLeast(1)} KB"
                    }
                }
            }
        }
    } catch (_: Exception) {}
    return Pair(name, sizeStr)
}

@Preview(showBackground = true)
@Composable
fun FormulirLamaranScreenPreview() {
    LenteraTheme {
        FormulirLamaranScreen(
            posisiJudul = "UI/UX Designer",
            perusahaan = "PT Teknologi Nusantara"
        )
    }
}
