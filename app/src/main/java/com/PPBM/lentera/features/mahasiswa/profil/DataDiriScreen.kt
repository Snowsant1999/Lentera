package com.PPBM.lentera.features.mahasiswa.profil

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.LenteraBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import com.PPBM.lentera.core.designsystem.components.ProfileAvatarImage
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon
import com.PPBM.lentera.core.designsystem.theme.LenteraBadgeBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBorder
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTeal
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

import com.PPBM.lentera.backend.auth.model.UserProfile

@Composable
fun DataDiriScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {},
    userProfile: UserProfile? = null,
    initialPhotoUri: Uri? = null,
    onPhotoUpdated: (Uri?) -> Unit = {},
    initialNama: String = "",
    onNamaUpdated: (String) -> Unit = {},
    onSimpanDataDiri: (nama: String, noTelepon: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, onSuccess, _ -> onSuccess() }
) {
    val scrollState = rememberScrollState()

    var photoUri by remember { mutableStateOf(initialPhotoUri) }
    var namaLengkap by remember(userProfile, initialNama) {
        mutableStateOf(userProfile?.namaLengkap?.ifBlank { null } ?: initialNama.ifBlank { "-" })
    }
    val email = userProfile?.email?.ifBlank { null } ?: "Belum ditambahkan"
    var nomorTelepon by remember(userProfile) {
        mutableStateOf(userProfile?.noTelepon?.ifBlank { null } ?: "")
    }
    val nim = userProfile?.nim?.ifBlank { null } ?: "-"
    val perguruanTinggi = userProfile?.perguruanTinggi?.ifBlank { null } ?: "-"
    val programStudi = userProfile?.programStudi?.ifBlank { null } ?: "-"
    val statusBadgeText = when (userProfile?.statusAkun?.lowercase()) {
        "aktif" -> "Mahasiswa aktif"
        "belum_aktivasi" -> "Belum aktivasi"
        "lulus" -> "Lulus"
        "nonaktif" -> "Nonaktif"
        else -> "Mahasiswa"
    }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackIsSuccess by remember { mutableStateOf(true) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Tombol Kembali: "â† Profil"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBackClick)
                    .padding(vertical = 8.dp)
            ) {
                BackArrowIcon(
                    modifier = Modifier.size(20.dp),
                    tint = LenteraNavy
                )
                Text(
                    text = "Profil",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LenteraNavy
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Judul dan Subjudul
            Text(
                text = "DATA DIRI",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = LenteraNavy
            )

            Text(
                text = "Informasi akun dan akademik kamu.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Card Ringkasan Profil
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2EBF4))
                    .padding(16.dp)
            ) {
                ProfileAvatarImage(
                    photoUri = photoUri,
                    modifier = Modifier.size(60.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = namaLengkap,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = LenteraNavy
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(LenteraBadgeBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusBadgeText,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = LenteraTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Informasi Pribadi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        spotColor = Color(0x09173B57),
                        ambientColor = Color(0x09173B57),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Informasi pribadi",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showEditDialog = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_open_answer),
                            contentDescription = "Ubah",
                            tint = LenteraNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Ubah",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LenteraNavy
                        )
                    }
                }

                HorizontalDivider(
                    color = LenteraBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                FieldItem(label = "Nama lengkap", value = namaLengkap, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                FieldItem(label = "Email", value = if (email.isBlank()) "Belum ditambahkan" else email, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                FieldItem(label = "Nomor telepon", value = if (nomorTelepon.isBlank()) "Belum ditambahkan" else nomorTelepon, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Informasi Akademik
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        spotColor = Color(0x09173B57),
                        ambientColor = Color(0x09173B57),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_academic_calendar),
                        contentDescription = null,
                        tint = LenteraNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Informasi akademik",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy
                    )
                }

                HorizontalDivider(
                    color = LenteraBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                FieldItem(label = "Nomor Induk Mahasiswa (NIM)", value = nim, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                FieldItem(label = "Perguruan tinggi", value = perguruanTinggi, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                FieldItem(label = "Program studi", value = programStudi, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notice / Alert Info
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE2EBF4))
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_information_icon),
                    contentDescription = null,
                    tint = LenteraNavy,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Data akademik dikelola oleh kampus. Hubungi admin jika ada data yang perlu diperbarui.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = LenteraNavy
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }


    }

    if (showEditDialog) {
        UbahDataDiriDialog(
            currentNama = if (namaLengkap == "-") "" else namaLengkap,
            currentEmail = email,
            currentTelepon = nomorTelepon,
            currentPhotoUri = photoUri,
            onDismiss = { showEditDialog = false },
            onSave = { newNama, _, newTelepon, newPhotoUri ->
                val cleanedPhone = if (newTelepon == "Belum ditambahkan") "" else newTelepon
                onSimpanDataDiri(
                    newNama,
                    cleanedPhone,
                    {
                        namaLengkap = newNama
                        onNamaUpdated(newNama)
                        nomorTelepon = cleanedPhone
                        photoUri = newPhotoUri
                        onPhotoUpdated(newPhotoUri)
                        showEditDialog = false

                        feedbackIsSuccess = true
                        feedbackTitle = "Data Berhasil Disimpan"
                        feedbackMessage = "Informasi data diri berhasil diperbarui."
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
private fun UbahDataDiriDialog(
    currentNama: String,
    currentEmail: String,
    currentTelepon: String,
    currentPhotoUri: Uri?,
    onDismiss: () -> Unit,
    onSave: (nama: String, email: String, telepon: String, photoUri: Uri?) -> Unit
) {
    var tempNama by remember { mutableStateOf(currentNama) }
    var tempEmail by remember { mutableStateOf(if (currentEmail == "Belum ditambahkan") "" else currentEmail) }
    var tempTelepon by remember { mutableStateOf(if (currentTelepon == "Belum ditambahkan") "" else currentTelepon) }
    var tempPhotoUri by remember { mutableStateOf(currentPhotoUri) }

    var showValidationError by remember { mutableStateOf(false) }
    var validationErrorTitle by remember { mutableStateOf("") }
    var validationErrorMessage by remember { mutableStateOf("") }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            tempPhotoUri = uri
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ubah Informasi Pribadi",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LenteraNavy
            )

            // Bagian Foto Profil (Preview & Ikon Pensil)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Preview avatar/foto bulat
                    ProfileAvatarImage(
                        photoUri = tempPhotoUri,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .clickable { photoLauncher.launch("image/*") }
                    )

                    // Overlay badge pensil di sudut kanan bawah lingkaran foto
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LenteraNavy)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ganti Foto Profil",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ketuk untuk ganti foto",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = LenteraNavy,
                    modifier = Modifier.clickable { photoLauncher.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Kotak Catatan Keterangan Almamater
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEBF3FA))
                        .border(1.dp, Color(0xFFD0E2F2), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ℹ️",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Wajib menggunakan foto formal mengenakan jas almamater kampus.",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = LenteraNavy
                    )
                }
            }

            // Input Nama Lengkap
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nama Lengkap",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary
                )
                BasicTextField(
                    value = tempNama,
                    onValueChange = { tempNama = it },
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LenteraNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LenteraBg)
                        .border(1.dp, LenteraBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }

            // Informasi Email (Read-only, dikelola admin kampus)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Email Kampus (Dikelola Admin)",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary
                )
                Text(
                    text = if (tempEmail.isBlank()) "Belum ditambahkan" else tempEmail,
                    fontFamily = PlusJakartaSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LenteraTextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LenteraBg)
                        .border(1.dp, LenteraBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }

            // Input Nomor Telepon
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nomor Telepon",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary
                )
                BasicTextField(
                    value = tempTelepon,
                    onValueChange = { tempTelepon = it },
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LenteraNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LenteraBg)
                        .border(1.dp, LenteraBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons: Batal & Simpan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraBg,
                        contentColor = LenteraNavy
                    )
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
                        val cleanedNama = tempNama.trim()
                        val cleanedEmail = tempEmail.trim()
                        val cleanedPhone = tempTelepon.replace(" ", "").replace("-", "")

                        if (cleanedNama.isEmpty()) {
                            validationErrorTitle = "Nama Tidak Boleh Kosong"
                            validationErrorMessage = "Silakan masukkan nama lengkap Anda sebelum menyimpan perubahan."
                            showValidationError = true
                            return@Button
                        }

                        if (cleanedEmail.isNotEmpty() && (!cleanedEmail.contains("@") || !cleanedEmail.contains("."))) {
                            validationErrorTitle = "Format Email Tidak Valid"
                            validationErrorMessage = "Alamat email harus memiliki format yang benar (contoh: budi@lentera.ac.id)."
                            showValidationError = true
                            return@Button
                        }

                        if (cleanedPhone.isNotEmpty() && (!cleanedPhone.all { it.isDigit() || it == '+' } || cleanedPhone.length < 9)) {
                            validationErrorTitle = "Nomor Telepon Tidak Valid"
                            validationErrorMessage = "Nomor telepon harus berupa angka valid dengan panjang minimal 9 digit."
                            showValidationError = true
                            return@Button
                        }

                        onSave(
                            cleanedNama,
                            if (cleanedEmail.isEmpty()) "Belum ditambahkan" else cleanedEmail,
                            if (cleanedPhone.isEmpty()) "Belum ditambahkan" else tempTelepon.trim(),
                            tempPhotoUri
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Simpan",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    LenteraFeedbackDialog(
        show = showValidationError,
        isSuccess = false,
        title = validationErrorTitle,
        message = validationErrorMessage,
        onDismiss = { showValidationError = false },
        buttonText = "Periksa Kembali"
    )
}

@Composable
private fun FieldItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = LenteraTextSecondary
        )
        Text(
            text = value,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = LenteraNavy
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewDataDiriScreen() {
    LenteraTheme {
        DataDiriScreen()
    }
}
