package com.PPBM.lentera.features.admin.profil
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun DataAkunAdminScreen(
    nama: String = "",
    peran: String = "Admin Kampus",
    email: String? = null,
    nomorTelepon: String? = null,
    unitKerja: String = "LENTERA",
    aksesPengelolaan: String = "Mahasiswa, laporan, dan magang",
    photoUrl: String? = null,
    onUploadFoto: (ByteArray) -> Unit = {},
    onHapusFoto: () -> Unit = {},
    isSaving: Boolean = false,
    onBackClick: () -> Unit = {},
    onUbahClick: () -> Unit = {},
    onDataUpdated: (nama: String, email: String, telepon: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, onSuccess, _ -> onSuccess() },
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var rawUriToCrop by remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    var showConfirmHapusFotoDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            rawUriToCrop = uri
        }
    }

    if (rawUriToCrop != null) {
        ProfilePhotoCropDialog(
            sourceUri = rawUriToCrop!!,
            onDismiss = { rawUriToCrop = null },
            onCropSuccess = { croppedUri, _ ->
                imageUri = croppedUri
                rawUriToCrop = null
                runCatching {
                    context.contentResolver.openInputStream(croppedUri)?.use { stream ->
                        val bytes = stream.readBytes()
                        if (bytes.isNotEmpty()) {
                            onUploadFoto(bytes)
                        }
                    }
                }
            }
        )
    }

    if (showPhotoOptionsDialog) {
        OpsiFotoProfilDialog(
            onDismiss = { showPhotoOptionsDialog = false },
            onGantiFoto = {
                showPhotoOptionsDialog = false
                imagePickerLauncher.launch("image/*")
            },
            onHapusFoto = {
                showPhotoOptionsDialog = false
                showConfirmHapusFotoDialog = true
            }
        )
    }

    if (showConfirmHapusFotoDialog) {
        KonfirmasiHapusFotoDialog(
            onDismiss = { showConfirmHapusFotoDialog = false },
            onConfirm = {
                showConfirmHapusFotoDialog = false
                imageUri = null
                onHapusFoto()
            }
        )
    }

    val belumDitambahkan = "Belum ditambahkan"
    var currentNama by remember(nama) { mutableStateOf(nama.ifBlank { belumDitambahkan }) }
    var currentEmail by remember(email) { mutableStateOf(email.orEmpty().ifBlank { belumDitambahkan }) }
    var currentTelepon by remember(nomorTelepon) { mutableStateOf(nomorTelepon.orEmpty().ifBlank { belumDitambahkan }) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackIsSuccess by remember { mutableStateOf(true) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LenteraCompactHeader(
                backLabel = "Profil",
                title = "DATA AKUN ADMIN",
                description = "Informasi akun, peran, dan akses pengelolaan.",
                onBackClick = onBackClick
            )
            RingkasanAdminCard(
                nama = currentNama,
                peran = peran,
                photoUrl = photoUrl,
                imageUri = imageUri,
                onAvatarClick = {
                    if (!photoUrl.isNullOrBlank() || imageUri != null) {
                        showPhotoOptionsDialog = true
                    } else {
                        imagePickerLauncher.launch("image/*")
                    }
                }
            )
            InformasiPribadiCard(
                nama = currentNama,
                email = if (currentEmail.isBlank()) belumDitambahkan else currentEmail,
                nomorTelepon = if (currentTelepon.isBlank()) belumDitambahkan else currentTelepon,
                onUbahClick = {
                    showEditDialog = true
                }
            )
            InformasiPeranCard(
                peran = peran,
                unitKerja = unitKerja,
                aksesPengelolaan = aksesPengelolaan
            )
            LenteraInfoBanner(
                text = "Data akun admin dikelola oleh tim internal. " +
                        "Hubungi tim IT jika ada data yang perlu diperbarui."
            )
        }
    }

    if (showEditDialog) {
        UbahDataAkunDialog(
            currentNama = if (currentNama == belumDitambahkan) "" else currentNama,
            currentEmail = currentEmail,
            currentTelepon = currentTelepon,
            onDismiss = { showEditDialog = false },
            onSave = { newNama, newEmail, newTelepon ->
                onDataUpdated(
                    newNama,
                    newEmail,
                    newTelepon,
                    {
                        currentNama = newNama
                        currentEmail = newEmail.ifBlank { belumDitambahkan }
                        currentTelepon = newTelepon.ifBlank { belumDitambahkan }
                        showEditDialog = false
                        feedbackIsSuccess = true
                        feedbackTitle = "Data Berhasil Disimpan"
                        feedbackMessage = "Informasi pribadi akun admin berhasil diperbarui."
                        showFeedbackDialog = true
                    },
                    { err ->
                        showEditDialog = false
                        feedbackIsSuccess = false
                        feedbackTitle = "Gagal Menyimpan Data"
                        feedbackMessage = err
                        showFeedbackDialog = true
                    }
                )
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
private fun UbahDataAkunDialog(
    currentNama: String,
    currentEmail: String,
    currentTelepon: String,
    onDismiss: () -> Unit,
    onSave: (nama: String, email: String, telepon: String) -> Unit
) {
    var tempNama by remember { mutableStateOf(currentNama) }
    var tempEmail by remember { mutableStateOf(if (currentEmail == "Belum ditambahkan") "" else currentEmail) }
    var tempTelepon by remember { mutableStateOf(if (currentTelepon == "Belum ditambahkan") "" else currentTelepon) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Ubah Informasi Pribadi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Nama Lengkap",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans,
                    color = LenteraColors.SlateText
                )
                OutlinedTextField(
                    value = tempNama,
                    onValueChange = { tempNama = it; errorMessage = null },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans,
                    color = LenteraColors.SlateText
                )
                OutlinedTextField(
                    value = tempEmail,
                    onValueChange = { tempEmail = it; errorMessage = null },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Nomor Telepon",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans,
                    color = LenteraColors.SlateText
                )
                OutlinedTextField(
                    value = tempTelepon,
                    onValueChange = { tempTelepon = it; errorMessage = null },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = LenteraColors.Rose,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraColors.Background,
                        contentColor = LenteraColors.NavyDark
                    )
                ) {
                    Text(
                        text = "Batal",
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans
                    )
                }

                Button(
                    onClick = {
                        val cleanedNama = tempNama.trim()
                        val cleanedEmail = tempEmail.trim()
                        val cleanedTelepon = tempTelepon.trim()

                        if (cleanedNama.isEmpty()) {
                            errorMessage = "Nama lengkap tidak boleh kosong."
                            return@Button
                        }
                        if (cleanedEmail.isNotEmpty() && (!cleanedEmail.contains("@") || !cleanedEmail.contains("."))) {
                            errorMessage = "Format email tidak valid."
                            return@Button
                        }
                        onSave(cleanedNama, cleanedEmail, cleanedTelepon)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraColors.NavyPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Simpan",
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans
                    )
                }
            }
        }
    }
}

@Composable
private fun RingkasanAdminCard(
    nama: String,
    peran: String,
    photoUrl: String? = null,
    imageUri: Uri? = null,
    onAvatarClick: () -> Unit = {}
) {
    LenteraCard(containerColor = LenteraColors.BlueGray) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                ProfileAvatarImage(
                    photoUri = imageUri,
                    photoUrl = photoUrl,
                    modifier = Modifier
                        .size(58.dp)
                        .align(Alignment.Center)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .background(LenteraNavy, CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah Foto",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nama,
                    color = LenteraColors.NavyDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(6.dp))
                LenteraBadge(text = peran)
            }
        }
    }
}

@Composable
private fun InformasiPribadiCard(
    nama: String,
    email: String,
    nomorTelepon: String,
    onUbahClick: () -> Unit
) {
    LenteraCard(withShadow = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LenteraCardTitle(text = "Informasi pribadi", modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.clickable(onClick = onUbahClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = LenteraColors.NavyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ubah",
                    color = LenteraColors.NavyPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LenteraDivider()
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Nama lengkap", nilai = nama)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Email", nilai = email)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Nomor telepon", nilai = nomorTelepon)
    }
}

@Composable
private fun InformasiPeranCard(
    peran: String,
    unitKerja: String,
    aksesPengelolaan: String
) {
    LenteraCard(withShadow = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_circle),
                contentDescription = null,
                tint = LenteraColors.NavyDark,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Informasi peran & unit kerja",
                color = LenteraColors.NavyDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LenteraDivider()
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Peran", nilai = peran)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Unit kerja", nilai = unitKerja)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Akses pengelolaan", nilai = aksesPengelolaan)
    }
}

@Composable
private fun DataBaris(label: String, nilai: String) {
    Column {
        Text(
            text = label,
            color = LenteraColors.SlateText,
            fontSize = 12.sp,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = nilai,
            color = LenteraColors.NavyDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DataAkunAdminScreenPreview() {
    LenteraTheme {
        DataAkunAdminScreen(
            nama = "Admin Lentera",
            email = "admin@lentera.ac.id",
            nomorTelepon = "081234567890"
        )
    }
}