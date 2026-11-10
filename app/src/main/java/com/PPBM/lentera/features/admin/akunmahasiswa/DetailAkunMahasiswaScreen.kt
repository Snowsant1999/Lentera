package com.PPBM.lentera.features.admin.akunmahasiswa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.PPBM.lentera.core.designsystem.components.*
import com.PPBM.lentera.core.designsystem.icons.CloseIcon
import com.PPBM.lentera.core.designsystem.icons.TrashIcon
import com.PPBM.lentera.core.designsystem.theme.*

private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

private fun cleanEditableValue(value: String): String =
    value.trim().takeUnless { it == "-" } ?: ""

@Composable
fun DetailAkunMahasiswaScreen(
    mahasiswa: Mahasiswa,
    perguruanTinggi: String = "",
    programStudi: String = mahasiswa.prodi,
    angkatan: String = "",
    emailKampus: String = "",
    metodeAkses: String = "Kirim tautan aktivasi ke email",
    isProcessing: Boolean = false,
    onBackClick: () -> Unit = {},
    onKirimUlangTautanClick: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { onSuccess, _ -> onSuccess() },
    onEditSave: (
        nama: String,
        nim: String,
        email: String,
        perguruanTinggi: String,
        programStudi: String,
        angkatan: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit = { _, _, _, _, _, _, onSuccess, _ -> onSuccess() },
    onToggleStatusClick: (newStatus: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, onSuccess, _ -> onSuccess() },
    onDeleteClick: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { onSuccess, _ -> onSuccess() },
    onAfterDelete: () -> Unit = onBackClick,
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackIsSuccess by remember { mutableStateOf(true) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var navigateBackOnFeedbackDismiss by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editError by remember { mutableStateOf<String?>(null) }
    var isSavingEdit by remember { mutableStateOf(false) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    var editNama by remember(mahasiswa) { mutableStateOf(cleanEditableValue(mahasiswa.nama)) }
    var editNim by remember(mahasiswa) { mutableStateOf(cleanEditableValue(mahasiswa.nim)) }
    var editEmail by remember(emailKampus) { mutableStateOf(cleanEditableValue(emailKampus)) }
    var editPt by remember(perguruanTinggi) { mutableStateOf(cleanEditableValue(perguruanTinggi)) }
    var editProdi by remember(programStudi) { mutableStateOf(cleanEditableValue(programStudi)) }
    var editAngkatan by remember(angkatan) { mutableStateOf(cleanEditableValue(angkatan)) }

    val isNonaktif = mahasiswa.status == StatusAkun.NONAKTIF

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            AdminBottomBar(
                selected = LenteraNavItem.MAHASISWA,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Layar
            LenteraBackHeader(
                title = "DETAIL AKUN MAHASISWA",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackClick
            )

            // 1. Teks Pengantar
            Text(
                text = "Tinjau identitas, akademik, dan akses akun mahasiswa.",
                color = LenteraColors.SlateText,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 2. Card 1: Identitas Mahasiswa
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Identitas mahasiswa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileAvatar(
                            size = 46.dp,
                            iconTint = LenteraColors.NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = mahasiswa.nama,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                color = LenteraColors.NavyDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${mahasiswa.nim} · ${mahasiswa.prodi}",
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaSans,
                                color = LenteraColors.SlateText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val (badgeBg, badgeText) = when (mahasiswa.status) {
                        StatusAkun.BELUM_AKTIVASI -> Pair(Color(0xFFFEF3EB), Color(0xFFD97706))
                        StatusAkun.AKTIF -> Pair(LenteraColors.GreenActiveBg, LenteraColors.GreenActive)
                        StatusAkun.LULUS -> Pair(LenteraColors.Background, LenteraColors.SlateText)
                        StatusAkun.NONAKTIF -> Pair(LenteraColors.RedInactiveBg, LenteraColors.RedInactive)
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (mahasiswa.status == StatusAkun.BELUM_AKTIVASI) "Belum aktivasi" else mahasiswa.status.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans,
                            color = badgeText
                        )
                    }
                }
            }

            // 3. Card 2: Informasi Akademik
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Informasi akademik",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReadOnlyField(
                        label = "Perguruan tinggi",
                        value = perguruanTinggi.ifBlank { "-" }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReadOnlyField(
                            label = "Program studi",
                            value = programStudi.ifBlank { "-" },
                            modifier = Modifier.weight(1.35f)
                        )
                        ReadOnlyField(
                            label = "Angkatan",
                            value = angkatan.ifBlank { "-" },
                            modifier = Modifier.weight(0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ReadOnlyField(
                        label = "Email kampus",
                        value = emailKampus.ifBlank { "-" }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tautan aktivasi dikirim ke email kampus yang terdaftar.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.SlateText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            editNama = cleanEditableValue(mahasiswa.nama)
                            editNim = cleanEditableValue(mahasiswa.nim)
                            editEmail = cleanEditableValue(emailKampus)
                            editPt = cleanEditableValue(perguruanTinggi)
                            editProdi = cleanEditableValue(programStudi)
                            editAngkatan = cleanEditableValue(angkatan)
                            editError = null
                            isSavingEdit = false
                            showEditDialog = true
                        },
                        enabled = !isProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = LenteraColors.NavyDark
                        ),
                        border = BorderStroke(1.2.dp, LenteraColors.NavyDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit data",
                            tint = LenteraColors.NavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit data",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans,
                            color = LenteraColors.NavyDark
                        )
                    }
                }
            }

            // 4. Card 3: Akses Akun
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Akses akun",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReadOnlyField(
                        label = "Metode pemberian akses",
                        value = metodeAkses
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isProcessing) return@Button
                            onKirimUlangTautanClick(
                                {
                                    feedbackIsSuccess = true
                                    feedbackTitle = "Tautan Aktivasi Terkirim"
                                    feedbackMessage = "Tautan pemulihan/aktivasi telah dikirimkan ke $emailKampus."
                                    navigateBackOnFeedbackDismiss = false
                                    showFeedbackDialog = true
                                },
                                { err ->
                                    feedbackIsSuccess = false
                                    feedbackTitle = "Gagal Mengirim Tautan"
                                    feedbackMessage = err
                                    navigateBackOnFeedbackDismiss = false
                                    showFeedbackDialog = true
                                }
                            )
                        },
                        enabled = !isProcessing && emailKampus.isNotBlank() && emailKampus != "-",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LenteraColors.NavyPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isProcessing) "Memproses..." else "Kirim Ulang Tautan Aktivasi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tautan aktivasi berlaku 24 jam. Setelah dikirim ulang, mahasiswa dapat membuat password sendiri dari email kampus yang terdaftar.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.SlateText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F4F8), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = LenteraColors.NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Jangan bagikan password melalui pesan biasa. Akun tetap Belum aktivasi sampai mahasiswa membuat password sendiri.",
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            color = LenteraColors.NavyDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. Tombol Aksi Bawah (Nonaktifkan/Aktifkan & Hapus Akun)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val statusButtonColor = if (isNonaktif) LenteraColors.GreenActive else LenteraColors.NavyDark
                val statusButtonLabel = if (isNonaktif) "Aktifkan Mahasiswa" else "Nonaktifkan Mahasiswa"
                val statusButtonIcon = if (isNonaktif) Icons.Default.Check else Icons.Default.Edit

                OutlinedButton(
                    onClick = {
                        if (isProcessing) return@OutlinedButton
                        val nextStatus = if (isNonaktif) "aktif" else "nonaktif"
                        onToggleStatusClick(
                            nextStatus,
                            {
                                feedbackIsSuccess = true
                                feedbackTitle = "Status Akun Diperbarui"
                                feedbackMessage = if (nextStatus == "nonaktif") {
                                    "Akun mahasiswa ${mahasiswa.nama} berhasil dinonaktifkan."
                                } else {
                                    "Akun mahasiswa ${mahasiswa.nama} berhasil diaktifkan."
                                }
                                navigateBackOnFeedbackDismiss = false
                                showFeedbackDialog = true
                            },
                            { err ->
                                feedbackIsSuccess = false
                                feedbackTitle = "Gagal Mengubah Status"
                                feedbackMessage = err
                                navigateBackOnFeedbackDismiss = false
                                showFeedbackDialog = true
                            }
                        )
                    },
                    enabled = !isProcessing && mahasiswa.status != StatusAkun.LULUS,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = statusButtonColor
                    ),
                    border = BorderStroke(1.2.dp, statusButtonColor)
                ) {
                    Icon(
                        imageVector = statusButtonIcon,
                        contentDescription = statusButtonLabel,
                        tint = statusButtonColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusButtonLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = statusButtonColor
                    )
                }

                val deleteRed = Color(0xFFE53935)
                OutlinedButton(
                    onClick = {
                        if (isProcessing) return@OutlinedButton
                        deleteError = null
                        isDeleting = false
                        showDeleteConfirmDialog = true
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = deleteRed
                    ),
                    border = BorderStroke(1.2.dp, deleteRed)
                ) {
                    TrashIcon(
                        modifier = Modifier.size(18.dp),
                        tint = deleteRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hapus Akun",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = deleteRed
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        Dialog(
            onDismissRequest = {
                if (!isSavingEdit) {
                    editError = null
                    showEditDialog = false
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .widthIn(max = 460.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 720.dp)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit data mahasiswa",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = LenteraColors.NavyDark
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable(enabled = !isSavingEdit) {
                                    editError = null
                                    showEditDialog = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            CloseIcon(
                                modifier = Modifier.size(16.dp),
                                tint = LenteraColors.SlateText
                            )
                        }
                    }

                    Text(
                        text = "Perbarui identitas dan informasi akademik mahasiswa.",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = LenteraColors.SlateText
                    )

                    EditDialogInputField(
                        label = "Nama",
                        value = editNama,
                        onValueChange = { editNama = it },
                        enabled = !isSavingEdit
                    )

                    EditDialogInputField(
                        label = "NIM",
                        value = editNim,
                        onValueChange = { editNim = it },
                        enabled = !isSavingEdit
                    )

                    EditDialogInputField(
                        label = "Email",
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        enabled = !isSavingEdit,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Informasi akademik",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraColors.NavyDark
                    )

                    EditDialogInputField(
                        label = "Perguruan tinggi",
                        value = editPt,
                        onValueChange = { editPt = it },
                        enabled = !isSavingEdit
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EditDialogInputField(
                            label = "Program studi",
                            value = editProdi,
                            onValueChange = { editProdi = it },
                            enabled = !isSavingEdit,
                            modifier = Modifier.weight(1.45f)
                        )
                        EditDialogInputField(
                            label = "Angkatan",
                            value = editAngkatan,
                            onValueChange = { editAngkatan = it },
                            enabled = !isSavingEdit,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.85f)
                        )
                    }

                    if (!editError.isNullOrBlank()) {
                        Text(
                            text = editError!!,
                            color = Color(0xFFE53935),
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            lineHeight = 17.sp
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                editError = null
                                showEditDialog = false
                            },
                            enabled = !isSavingEdit,
                            modifier = Modifier
                                .weight(0.36f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = LenteraColors.NavyDark
                            ),
                            border = BorderStroke(1.2.dp, LenteraColors.NavyDark)
                        ) {
                            Text(
                                text = "Batal",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LenteraColors.NavyDark
                            )
                        }

                        Button(
                            onClick = {
                                if (isSavingEdit) return@Button
                                val cleanNama = editNama.trim()
                                val cleanNim = editNim.trim()
                                val cleanEmail = editEmail.trim().lowercase()
                                val cleanPt = editPt.trim()
                                val cleanProdi = editProdi.trim()
                                val cleanAngkatan = editAngkatan.trim()

                                when {
                                    cleanNama.isBlank() -> {
                                        editError = "Nama mahasiswa tidak boleh kosong."
                                        return@Button
                                    }
                                    cleanNim.isBlank() -> {
                                        editError = "NIM mahasiswa tidak boleh kosong."
                                        return@Button
                                    }
                                    !EMAIL_REGEX.matches(cleanEmail) -> {
                                        editError = "Masukkan alamat email yang valid."
                                        return@Button
                                    }
                                    cleanAngkatan.isNotEmpty() &&
                                        (cleanAngkatan.length != 4 || !cleanAngkatan.all { it.isDigit() }) -> {
                                        editError = "Angkatan harus berupa 4 digit angka."
                                        return@Button
                                    }
                                }

                                editError = null
                                isSavingEdit = true
                                onEditSave(
                                    cleanNama,
                                    cleanNim,
                                    cleanEmail,
                                    cleanPt,
                                    cleanProdi,
                                    cleanAngkatan,
                                    {
                                        isSavingEdit = false
                                        showEditDialog = false
                                        feedbackIsSuccess = true
                                        feedbackTitle = "Data Mahasiswa Diperbarui"
                                        feedbackMessage = "Perubahan data identitas dan akademik mahasiswa berhasil disimpan."
                                        navigateBackOnFeedbackDismiss = false
                                        showFeedbackDialog = true
                                    },
                                    { err ->
                                        isSavingEdit = false
                                        editError = err
                                    }
                                )
                            },
                            enabled = !isSavingEdit,
                            modifier = Modifier
                                .weight(0.64f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LenteraColors.NavyDark,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isSavingEdit) "Menyimpan..." else "Simpan perubahan",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        val deleteRed = Color(0xFFE53935)
        Dialog(
            onDismissRequest = {
                if (!isDeleting) {
                    deleteError = null
                    showDeleteConfirmDialog = false
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Hapus Akun Mahasiswa?",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = LenteraColors.NavyDark
                    )
                    Text(
                        text = "Akun ${mahasiswa.nama} (${mahasiswa.nim}) akan dihapus secara permanen. Jika mahasiswa memiliki riwayat laporan, lamaran, atau surat, gunakan Nonaktifkan Mahasiswa.",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = LenteraColors.SlateText
                    )
                    if (!deleteError.isNullOrBlank()) {
                        Text(
                            text = deleteError!!,
                            color = deleteRed,
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            lineHeight = 17.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                deleteError = null
                                showDeleteConfirmDialog = false
                            },
                            enabled = !isDeleting,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = LenteraColors.NavyDark
                            ),
                            border = BorderStroke(1.2.dp, LenteraColors.LightGrayBorder)
                        ) {
                            Text(
                                text = "Batal",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = {
                                if (isDeleting) return@Button
                                deleteError = null
                                isDeleting = true
                                onDeleteClick(
                                    {
                                        isDeleting = false
                                        showDeleteConfirmDialog = false
                                        feedbackIsSuccess = true
                                        feedbackTitle = "Akun Mahasiswa Dihapus"
                                        feedbackMessage = "Akun mahasiswa ${mahasiswa.nama} berhasil dihapus."
                                        navigateBackOnFeedbackDismiss = true
                                        showFeedbackDialog = true
                                    },
                                    { err ->
                                        isDeleting = false
                                        deleteError = err
                                    }
                                )
                            },
                            enabled = !isDeleting,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = deleteRed,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isDeleting) "Menghapus..." else "Hapus Akun",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Aksi Admin Mahasiswa
    LenteraFeedbackDialog(
        show = showFeedbackDialog,
        isSuccess = feedbackIsSuccess,
        title = feedbackTitle,
        message = feedbackMessage,
        onDismiss = {
            val shouldNavigateBack = navigateBackOnFeedbackDismiss
            navigateBackOnFeedbackDismiss = false
            showFeedbackDialog = false
            if (shouldNavigateBack) {
                onAfterDelete()
            }
        }
    )
}

@Composable
private fun EditDialogInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.NavyDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(
                fontFamily = PlusJakartaSans,
                fontSize = 14.sp,
                color = LenteraColors.NavyDark
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LenteraColors.NavyDark,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                disabledBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF9FAFB),
                focusedTextColor = LenteraColors.NavyDark,
                unfocusedTextColor = LenteraColors.NavyDark
            )
        )
    }
}

/**
 * Komponen reusable field read-only bergaya form input bergaris
 */
@Composable
private fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.SlateText
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyDark
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetailAkunMahasiswaScreenPreview() {
    LenteraTheme {
        DetailAkunMahasiswaScreen(
            mahasiswa = Mahasiswa(
                nama = "Nadia Putri",
                nim = "2026110008",
                prodi = "Teknik Informatika",
                status = StatusAkun.BELUM_AKTIVASI
            ),
            perguruanTinggi = "Universitas Contoh",
            programStudi = "Teknik Informatika",
            angkatan = "2026",
            emailKampus = "email kampus tersimpan"
        )
    }
}
