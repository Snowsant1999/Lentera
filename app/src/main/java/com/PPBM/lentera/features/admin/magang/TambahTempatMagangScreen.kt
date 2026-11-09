package com.PPBM.lentera.features.admin.magang

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.extractShortFileType
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.features.admin.magang.components.*
import java.util.Locale

private const val MAX_MITRA_LOGO_BYTES = 10L * 1024L * 1024L
private const val MAX_MITRA_DOC_BYTES = 20L * 1024L * 1024L

private fun readPickedFile(context: Context, uri: Uri, fallbackName: String): Triple<String, ByteArray, String>? {
    var name = fallbackName
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex != -1) {
            name = cursor.getString(nameIndex) ?: fallbackName
        }
    }
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    val sizeStr = if (bytes.size >= 1024 * 1024) {
        String.format(Locale.US, "%.1f MB", bytes.size / (1024.0 * 1024.0))
    } else {
        "${(bytes.size / 1024).coerceAtLeast(1)} KB"
    }
    return Triple(name, bytes, sizeStr)
}

@Composable
fun TambahTempatMagangScreen(
    isSubmitting: Boolean = false,
    onSimpanSuccess: () -> Unit = {},
    onSimpanTempatMagang: ((
        namaPerusahaan: String,
        bidangUsaha: String,
        namaTempat: String,
        deskripsi: String,
        alamat: String,
        kota: String,
        provinsi: String,
        sistemKerja: String,
        namaKontak: String,
        email: String,
        telepon: String,
        situsWeb: String?,
        logoFileName: String?,
        logoBytes: ByteArray?,
        dokumenFileName: String?,
        dokumenBytes: ByteArray?,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit)? = null,
    onBackToTempatMagang: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    var namaPerusahaan by remember { mutableStateOf("") }
    var bidangUsaha by remember { mutableStateOf("") }
    var namaTempat by remember { mutableStateOf("") }
    var deskripsiTempat by remember { mutableStateOf("") }
    var alamatLengkap by remember { mutableStateOf("") }
    var kota by remember { mutableStateOf("") }
    var provinsi by remember { mutableStateOf("") }
    var sistemKerja by remember { mutableStateOf("On-site") }
    var showSistemKerjaMenu by remember { mutableStateOf(false) }
    var namaKontak by remember { mutableStateOf("") }
    var emailPerusahaan by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }
    var situsWeb by remember { mutableStateOf("") }

    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var logoFileName by remember { mutableStateOf<String?>(null) }
    var logoBytes by remember { mutableStateOf<ByteArray?>(null) }

    var uploadedDocName by remember { mutableStateOf<String?>(null) }
    var uploadedDocMeta by remember { mutableStateOf<String?>(null) }
    var uploadedDocBytes by remember { mutableStateOf<ByteArray?>(null) }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val picked = readPickedFile(context, uri, "logo_mitra.png")
        if (picked == null || picked.second.isEmpty()) {
            feedbackTitle = "Gagal Membaca Logo"
            feedbackMessage = "File gambar tidak dapat dibaca atau kosong."
            feedbackIsSuccess = false
            showFeedback = true
        } else if (picked.second.size > MAX_MITRA_LOGO_BYTES) {
            feedbackTitle = "Ukuran Logo Terlalu Besar"
            feedbackMessage = "Maksimal ukuran logo perusahaan adalah 10 MB."
            feedbackIsSuccess = false
            showFeedback = true
        } else {
            logoUri = uri
            logoFileName = picked.first
            logoBytes = picked.second
        }
    }

    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val picked = readPickedFile(context, uri, "dokumen_mitra.pdf")
        if (picked == null || picked.second.isEmpty()) {
            feedbackTitle = "Gagal Membaca Dokumen"
            feedbackMessage = "Berkas dokumen tidak dapat dibaca atau kosong."
            feedbackIsSuccess = false
            showFeedback = true
        } else if (picked.second.size > MAX_MITRA_DOC_BYTES) {
            feedbackTitle = "Ukuran Dokumen Terlalu Besar"
            feedbackMessage = "Maksimal ukuran dokumen mitra adalah 20 MB."
            feedbackIsSuccess = false
            showFeedback = true
        } else {
            uploadedDocName = picked.first
            uploadedDocBytes = picked.second
            uploadedDocMeta = "${extractShortFileType(picked.first)} · ${picked.third} · Siap diunggah"
        }
    }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "TAMBAH TEMPAT MAGANG",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToTempatMagang
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang", onNavClick = onNavClick)
        },
        containerColor = MagangBgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Daftarkan tempat magang baru untuk mahasiswa.",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            // Card 1: Profil perusahaan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Profil perusahaan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { logoPickerLauncher.launch("image/*") }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEAF2F8))
                                        .border(1.dp, MagangBorderColor, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (logoUri != null) {
                                        AsyncImage(
                                            model = logoUri,
                                            contentDescription = "Logo Perusahaan",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_building_2),
                                            contentDescription = "Placeholder Logo Perusahaan",
                                            tint = MagangNavyDark,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(MagangNavyDark, CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable { logoPickerLauncher.launch("image/*") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Ubah Logo Perusahaan",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = logoFileName?.let { "Logo: $it" } ?: "Logo Perusahaan (maks. 10 MB)",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Medium,
                                    color = MagangTextSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(
                            label = "Nama perusahaan / tempat *",
                            value = namaPerusahaan,
                            onValueChange = { namaPerusahaan = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Bidang usaha *",
                            value = bidangUsaha,
                            onValueChange = { bidangUsaha = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Nama tempat / cabang *",
                            value = namaTempat,
                            onValueChange = { namaTempat = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Deskripsi tempat magang *",
                            value = deskripsiTempat,
                            onValueChange = { deskripsiTempat = it },
                            isMultiLine = true
                        )
                    }
                }
            }

            // Card 2: Lokasi magang
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Lokasi magang",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FormInputField(
                            label = "Alamat lengkap *",
                            value = alamatLengkap,
                            onValueChange = { alamatLengkap = it },
                            isMultiLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                FormInputField(
                                    label = "Kota *",
                                    value = kota,
                                    onValueChange = { kota = it }
                                )
                            }
                            Box(modifier = Modifier.weight(1.2f)) {
                                FormInputField(
                                    label = "Provinsi *",
                                    value = provinsi,
                                    onValueChange = { provinsi = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box {
                            FormDropdownField(
                                label = "Sistem kerja *",
                                value = sistemKerja,
                                onClick = { showSistemKerjaMenu = true }
                            )
                            DropdownMenu(
                                expanded = showSistemKerjaMenu,
                                onDismissRequest = { showSistemKerjaMenu = false }
                            ) {
                                listOf("On-site", "Remote", "Hybrid").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            sistemKerja = option
                                            showSistemKerjaMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 3: Kontak penanggung jawab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Kontak penanggung jawab",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FormInputField(
                            label = "Nama kontak *",
                            value = namaKontak,
                            onValueChange = { namaKontak = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Email perusahaan *",
                            value = emailPerusahaan,
                            onValueChange = { emailPerusahaan = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Nomor telepon *",
                            value = nomorTelepon,
                            onValueChange = { nomorTelepon = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Situs web (opsional)",
                            value = situsWeb,
                            onValueChange = { situsWeb = it }
                        )
                    }
                }
            }

            // Card 4: Dokumen pendukung
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Dokumen pendukung",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (uploadedDocName != null) {
                            DocumentItemBox(
                                name = uploadedDocName!!,
                                meta = uploadedDocMeta ?: "Siap diunggah"
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, MagangNavyDark, RoundedCornerShape(12.dp))
                                .clickable { docPickerLauncher.launch("*/*") }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uploadedDocName != null) "Ganti Dokumen" else "Unggah Dokumen",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = MagangNavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Dokumen disimpan admin sebagai referensi (MOU/Legalitas). PDF, JPG atau PNG, maks. 20 MB.",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            // Card 5: Status tempat
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Status tempat",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MagangCreamBox)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Draf setelah disimpan",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MagangNavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Admin melengkapi data lalu mengaktifkan tempat. Lowongan dibuat dan diterbitkan secara terpisah.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            item {
                MagangInfoBox(
                    text = "Semua kolom bertanda * wajib diisi sebelum menyimpan tempat magang."
                )
            }

            // Button 1: Simpan Tempat Magang
            item {
                Button(
                    enabled = !isSubmitting,
                    onClick = {
                        val missingFields = buildList {
                            if (namaPerusahaan.isBlank()) add("Nama perusahaan")
                            if (bidangUsaha.isBlank()) add("Bidang usaha")
                            if (namaTempat.isBlank()) add("Nama cabang/tempat")
                            if (deskripsiTempat.isBlank()) add("Deskripsi")
                            if (alamatLengkap.isBlank()) add("Alamat lengkap")
                            if (kota.isBlank()) add("Kota")
                            if (provinsi.isBlank()) add("Provinsi")
                            if (namaKontak.isBlank()) add("Nama kontak")
                            if (emailPerusahaan.isBlank() || !emailPerusahaan.contains("@")) add("Email perusahaan yang valid")
                            if (nomorTelepon.isBlank()) add("Nomor telepon")
                        }
                        if (missingFields.isNotEmpty()) {
                            feedbackTitle = "Data Belum Lengkap"
                            feedbackMessage = "Mohon lengkapi: ${missingFields.joinToString(", ")}."
                            feedbackIsSuccess = false
                            showFeedback = true
                        } else if (onSimpanTempatMagang != null) {
                            onSimpanTempatMagang(
                                namaPerusahaan.trim(),
                                bidangUsaha.trim(),
                                namaTempat.trim(),
                                deskripsiTempat.trim(),
                                alamatLengkap.trim(),
                                kota.trim(),
                                provinsi.trim(),
                                sistemKerja,
                                namaKontak.trim(),
                                emailPerusahaan.trim(),
                                nomorTelepon.trim(),
                                situsWeb.trim().ifBlank { null },
                                logoFileName,
                                logoBytes,
                                uploadedDocName,
                                uploadedDocBytes,
                                { errMsg ->
                                    feedbackTitle = "Gagal Menyimpan Tempat Magang"
                                    feedbackMessage = errMsg
                                    feedbackIsSuccess = false
                                    showFeedback = true
                                },
                                {
                                    feedbackTitle = "Tempat Magang Berhasil Disimpan"
                                    feedbackMessage = "Data mitra ${namaPerusahaan.trim()} berhasil disimpan ke Supabase."
                                    feedbackIsSuccess = true
                                    showFeedback = true
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MagangGoldenOrange,
                        contentColor = MagangNavyDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isSubmitting) "Menyimpan..." else "Simpan Tempat Magang",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // Button 2: Kembali ke Tempat Magang
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToTempatMagang() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Tempat Magang",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        LenteraFeedbackDialog(
            show = showFeedback,
            isSuccess = feedbackIsSuccess,
            title = feedbackTitle,
            message = feedbackMessage,
            onDismiss = {
                showFeedback = false
                if (feedbackIsSuccess) {
                    onSimpanSuccess()
                }
            }
        )
    }
}

@Composable
fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isMultiLine: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = PlusJakartaFont,
                fontWeight = FontWeight.Bold,
                color = MagangNavyDark
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = if (isMultiLine) 12.dp else 10.dp)
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Normal,
                    color = MagangNavyDark
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = !isMultiLine,
                maxLines = if (isMultiLine) 4 else 1
            )
        }
    }
}

@Composable
fun FormDropdownField(
    label: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = PlusJakartaFont,
                fontWeight = FontWeight.Bold,
                color = MagangNavyDark
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Normal,
                    color = MagangNavyDark
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_down_alt),
                contentDescription = "Dropdown",
                tint = MagangNavyDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun TambahTempatMagangPreview() {
    TambahTempatMagangScreen()
}
