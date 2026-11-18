package com.PPBM.lentera.features.admin.magang

import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.features.admin.magang.components.*

@Composable
fun KelolaStatusTempatScreen(
    tempat: TempatMagangEntity? = null,
    logoUrl: String? = null,
    isSubmitting: Boolean = false,
    onSimpanPerubahan: ((
        updatedEntity: TempatMagangEntity,
        removeLogo: Boolean,
        newLogoFileName: String?,
        newLogoBytes: ByteArray?,
        dokumenFileName: String?,
        dokumenBytes: ByteArray?,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit)? = null,
    onSimpanStatus: (newStatus: String, catatan: String) -> Unit = { _, _ -> },
    onBackToDetailTempat: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    val tempatKey = tempat?.id

    var selectedStatus by remember(tempatKey, tempat?.status) {
        mutableStateOf(
            when (tempat?.status?.lowercase()) {
                "aktif" -> "Aktif"
                "arsip" -> "Arsip"
                else -> "Draf"
            }
        )
    }
    var catatanAdmin by remember(tempatKey, tempat?.catatanInternal) {
        mutableStateOf(tempat?.catatanInternal.orEmpty())
    }
    var namaPerusahaan by remember(tempatKey, tempat?.namaPerusahaan) {
        mutableStateOf(tempat?.namaPerusahaan.orEmpty())
    }
    var bidangUsaha by remember(tempatKey, tempat?.bidangUsaha) {
        mutableStateOf(tempat?.bidangUsaha.orEmpty())
    }
    var namaCabang by remember(tempatKey, tempat?.namaCabang) {
        mutableStateOf(tempat?.namaCabang.orEmpty())
    }
    var deskripsiTempat by remember(tempatKey, tempat?.deskripsi) {
        mutableStateOf(tempat?.deskripsi.orEmpty())
    }
    var alamatLengkap by remember(tempatKey, tempat?.alamatLengkap) {
        mutableStateOf(tempat?.alamatLengkap.orEmpty())
    }
    var kota by remember(tempatKey, tempat?.kota) {
        mutableStateOf(tempat?.kota.orEmpty())
    }
    var provinsi by remember(tempatKey, tempat?.provinsi) {
        mutableStateOf(tempat?.provinsi.orEmpty())
    }
    var sistemKerja by remember(tempatKey, tempat?.sistemKerja) {
        mutableStateOf(tempat?.sistemKerja?.ifBlank { "On-site" } ?: "On-site")
    }
    var showSistemKerjaMenu by remember { mutableStateOf(false) }

    var namaKontak by remember(tempatKey, tempat?.namaKontak) {
        mutableStateOf(tempat?.namaKontak.orEmpty())
    }
    var emailKontak by remember(tempatKey, tempat?.emailKontak) {
        mutableStateOf(tempat?.emailKontak.orEmpty())
    }
    var teleponKontak by remember(tempatKey, tempat?.teleponKontak) {
        mutableStateOf(tempat?.teleponKontak.orEmpty())
    }
    var situsWeb by remember(tempatKey, tempat?.situsWeb) {
        mutableStateOf(tempat?.situsWeb.orEmpty())
    }

    var newLogoUri by remember(tempatKey) { mutableStateOf<Uri?>(null) }
    var newLogoFileName by remember(tempatKey) { mutableStateOf<String?>(null) }
    var newLogoBytes by remember(tempatKey) { mutableStateOf<ByteArray?>(null) }
    var removeExistingLogo by remember(tempatKey, tempat?.logoPath) { mutableStateOf(false) }
    var showDeleteLogoDialog by remember { mutableStateOf(false) }

    var dokumenFileName by remember(tempatKey) { mutableStateOf<String?>(null) }
    var dokumenSizeStr by remember(tempatKey) { mutableStateOf<String?>(null) }
    var dokumenBytes by remember(tempatKey) { mutableStateOf<ByteArray?>(null) }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackIsSuccess by remember { mutableStateOf(true) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }

    fun showErrorDialog(title: String, msg: String) {
        feedbackIsSuccess = false
        feedbackTitle = title
        feedbackMessage = msg
        showFeedback = true
    }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val picked = readPickedMitraFile(context, uri, "logo_perusahaan.png")
            if (picked == null) {
                showErrorDialog("Gagal Membaca Logo", "Berkas gambar tidak dapat dibaca.")
            } else if (picked.second.size.toLong() > MAX_MITRA_LOGO_BYTES) {
                showErrorDialog("Ukuran Logo Terlalu Besar", "Ukuran logo maksimal 10 MB.")
            } else {
                newLogoUri = uri
                newLogoFileName = picked.first
                newLogoBytes = picked.second
                removeExistingLogo = false
            }
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val picked = readPickedMitraFile(context, uri, "dokumen_mitra.pdf")
            if (picked == null) {
                showErrorDialog("Gagal Membaca Dokumen", "Berkas dokumen tidak dapat dibaca.")
            } else if (picked.second.size.toLong() > MAX_MITRA_DOC_BYTES) {
                showErrorDialog("Ukuran Dokumen Terlalu Besar", "Ukuran dokumen maksimal 20 MB.")
            } else {
                dokumenFileName = picked.first
                dokumenBytes = picked.second
                dokumenSizeStr = picked.third
            }
        }
    }

    val subInfo = "${kota.ifBlank { "-" }} · ${bidangUsaha.ifBlank { "-" }}"
    val currentStatusLabel = (tempat?.status ?: "draf").replaceFirstChar { it.uppercase() }
    val hasActiveLogoPreview = newLogoUri != null || (!removeExistingLogo && !tempat?.logoPath.isNullOrBlank())

    if (showDeleteLogoDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteLogoDialog = false },
            title = {
                Text(
                    text = "Hapus Foto / Logo Perusahaan?",
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Logo akan dihapus setelah perubahan disimpan dan tampilan akan kembali ke ikon default.",
                    fontFamily = PlusJakartaFont,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteLogoDialog = false
                        newLogoUri = null
                        newLogoFileName = null
                        newLogoBytes = null
                        removeExistingLogo = true
                    }
                ) {
                    Text(
                        text = "Hapus",
                        color = Color(0xFFD32F2F),
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteLogoDialog = false }) {
                    Text(
                        text = "Batal",
                        color = MagangNavyDark,
                        fontFamily = PlusJakartaFont
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "KELOLA & EDIT TEMPAT",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToDetailTempat
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
            // 1. Company Header & Logo Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable { logoPickerLauncher.launch("image/*") }
                            ) {
                                if (newLogoUri != null) {
                                    AsyncImage(
                                        model = newLogoUri,
                                        contentDescription = "Preview Logo Baru",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp))
                                            .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                                    )
                                } else {
                                    CompanyLogoBox(
                                        logoUrl = if (removeExistingLogo) null else logoUrl,
                                        contentDescription = namaPerusahaan.ifBlank { "Logo Perusahaan" },
                                        size = 64.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        fallbackDrawableRes = R.drawable.ic_building_2,
                                        fallbackTint = MagangNavyDark,
                                        fallbackIconSize = 30.dp
                                    )
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(MagangNavyDark, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Ganti Logo",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = namaPerusahaan.ifBlank { "Tempat Magang" },
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = subInfo,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = if (hasActiveLogoPreview) "Ganti Logo" else "Unggah Logo",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MagangNavyPrimary
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { logoPickerLauncher.launch("image/*") }
                                    )
                                    if (hasActiveLogoPreview) {
                                        Text(
                                            text = "Hapus Logo",
                                            style = TextStyle(
                                                fontSize = 12.sp,
                                                fontFamily = PlusJakartaFont,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFD32F2F)
                                            ),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable { showDeleteLogoDialog = true }
                                        )
                                    }
                                }
                                if (newLogoFileName != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Logo baru dipilih: $newLogoFileName",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaFont,
                                            color = MagangActiveGreenText
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (tempat?.status == "aktif") MagangActiveGreenBg else MagangCreamBox)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentStatusLabel,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (tempat?.status == "aktif") MagangActiveGreenText else MagangNavyDark
                                )
                            )
                        }
                    }
                }
            }

            // 2. Status Tempat & Catatan Internal Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Status tempat magang",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Aktif: dapat dipilih untuk lowongan. Draf: data belum lengkap. Arsip: tidak dipakai untuk lowongan baru.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = PlusJakartaFont,
                                color = MagangTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Aktif", "Draf", "Arsip").forEach { option ->
                                val isSelected = selectedStatus == option
                                val borderColor = when {
                                    isSelected -> Color.Transparent
                                    option == "Aktif" -> MagangNavyDark
                                    option == "Arsip" -> MagangRedBrown
                                    else -> Color(0xFFE2E8F0)
                                }
                                val textColor = if (option == "Arsip" && !isSelected) MagangRedBrown else MagangNavyDark
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) MagangGoldenOrange else Color.White)
                                        .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                        .clickable { selectedStatus = option }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(
                            label = "Catatan internal admin",
                            value = catatanAdmin,
                            onValueChange = { catatanAdmin = it },
                            isMultiLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Catatan hanya untuk admin, bukan pesan ke perusahaan.",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaFont,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            // 3. Edit Profil Perusahaan Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Profil Perusahaan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        FormInputField(
                            label = "Nama perusahaan *",
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
                            label = "Nama cabang / unit",
                            value = namaCabang,
                            onValueChange = { namaCabang = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormInputField(
                            label = "Deskripsi singkat",
                            value = deskripsiTempat,
                            onValueChange = { deskripsiTempat = it },
                            isMultiLine = true
                        )
                    }
                }
            }

            // 4. Edit Alamat & Sistem Kerja Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Alamat & Sistem Kerja",
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
                        FormInputField(
                            label = "Kota *",
                            value = kota,
                            onValueChange = { kota = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormInputField(
                            label = "Provinsi",
                            value = provinsi,
                            onValueChange = { provinsi = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box {
                            FormDropdownField(
                                label = "Sistem kerja",
                                value = sistemKerja,
                                onClick = { showSistemKerjaMenu = true }
                            )
                            DropdownMenu(
                                expanded = showSistemKerjaMenu,
                                onDismissRequest = { showSistemKerjaMenu = false }
                            ) {
                                listOf("On-site", "Hybrid", "Remote").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, fontFamily = PlusJakartaFont) },
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

            // 5. Kontak PIC & Dokumen Tambahan Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Kontak PIC & Dokumen Kemitraan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        FormInputField(
                            label = "Nama kontak PIC *",
                            value = namaKontak,
                            onValueChange = { namaKontak = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormInputField(
                            label = "Email PIC *",
                            value = emailKontak,
                            onValueChange = { emailKontak = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormInputField(
                            label = "No. telepon PIC *",
                            value = teleponKontak,
                            onValueChange = { teleponKontak = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormInputField(
                            label = "Situs web (opsional)",
                            value = situsWeb,
                            onValueChange = { situsWeb = it }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, MagangBorderColor, RoundedCornerShape(12.dp))
                                .clickable { documentPickerLauncher.launch("*/*") }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_file_text_alt),
                                contentDescription = "Unggah Dokumen",
                                tint = MagangNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dokumenFileName ?: "Tambah Dokumen Kemitraan (Opsional)",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MagangNavyDark
                                    )
                                )
                                Text(
                                    text = dokumenSizeStr ?: "PDF/Gambar · Maks. 20 MB (tidak menghapus dokumen lama)",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontFamily = PlusJakartaFont,
                                        color = MagangTextSecondary
                                    )
                                )
                            }
                            if (dokumenBytes != null) {
                                Text(
                                    text = "Batal",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD32F2F)
                                    ),
                                    modifier = Modifier.clickable {
                                        dokumenFileName = null
                                        dokumenBytes = null
                                        dokumenSizeStr = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 6. Action Buttons
            item {
                Button(
                    enabled = !isSubmitting && !tempat?.id.isNullOrBlank(),
                    onClick = {
                        if (namaPerusahaan.isBlank() || bidangUsaha.isBlank() ||
                            alamatLengkap.isBlank() || kota.isBlank() ||
                            namaKontak.isBlank() || emailKontak.isBlank() || teleponKontak.isBlank()
                        ) {
                            showErrorDialog(
                                "Data Belum Lengkap",
                                "Harap isi Nama Perusahaan, Bidang Usaha, Alamat, Kota, Nama PIC, Email, dan No. Telepon."
                            )
                            return@Button
                        }
                        if (!emailKontak.trim().contains("@") || !emailKontak.trim().contains(".")) {
                            showErrorDialog(
                                "Email Tidak Valid",
                                "Format email PIC perusahaan harus valid."
                            )
                            return@Button
                        }

                        if (onSimpanPerubahan != null && tempat != null) {
                            val updatedEntity = tempat.copy(
                                namaPerusahaan = namaPerusahaan.trim(),
                                bidangUsaha = bidangUsaha.trim(),
                                namaCabang = namaCabang.trim().ifBlank { "Pusat" },
                                deskripsi = deskripsiTempat.trim(),
                                alamatLengkap = alamatLengkap.trim(),
                                kota = kota.trim(),
                                provinsi = provinsi.trim(),
                                sistemKerja = sistemKerja,
                                namaKontak = namaKontak.trim(),
                                emailKontak = emailKontak.trim(),
                                teleponKontak = teleponKontak.trim(),
                                situsWeb = situsWeb.trim().ifBlank { null },
                                status = selectedStatus.lowercase(),
                                catatanInternal = catatanAdmin.trim().ifBlank { null }
                            )
                            onSimpanPerubahan(
                                updatedEntity,
                                removeExistingLogo,
                                newLogoFileName,
                                newLogoBytes,
                                dokumenFileName,
                                dokumenBytes,
                                { err -> showErrorDialog("Gagal Menyimpan Perubahan", err) },
                                {
                                    feedbackIsSuccess = true
                                    feedbackTitle = "Perubahan Tersimpan"
                                    feedbackMessage = "Data dan profil tempat magang berhasil diperbarui."
                                    showFeedback = true
                                }
                            )
                        } else {
                            onSimpanStatus(selectedStatus.lowercase(), catatanAdmin.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MagangNavyPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isSubmitting) "Menyimpan..." else "Simpan Perubahan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToDetailTempat() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Detail Tempat",
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
                    onBackToDetailTempat()
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun KelolaStatusTempatPreview() {
    KelolaStatusTempatScreen()
}
