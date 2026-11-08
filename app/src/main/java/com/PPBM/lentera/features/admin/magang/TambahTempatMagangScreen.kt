package com.PPBM.lentera.features.admin.magang
import com.PPBM.lentera.features.admin.magang.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

@Composable
fun TambahTempatMagangScreen(
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
        onSuccess: () -> Unit
    ) -> Unit)? = null,
    onBackToTempatMagang: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var namaPerusahaan by remember { mutableStateOf("") }
    var bidangUsaha by remember { mutableStateOf("") }
    var namaTempat by remember { mutableStateOf("") }
    var deskripsiTempat by remember { mutableStateOf("") }
    var alamatLengkap by remember { mutableStateOf("") }
    var kota by remember { mutableStateOf("") }
    var provinsi by remember { mutableStateOf("") }
    var sistemKerja by remember { mutableStateOf("Di kantor (On-site)") }
    var namaKontak by remember { mutableStateOf("") }
    var emailPerusahaan by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }
    var situsWeb by remember { mutableStateOf("") }

    var uploadedDocName by remember { mutableStateOf<String?>(null) }
    var uploadedDocMeta by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var name = "dokumen_referensi.pdf"
            var sizeStr = "512 KB"
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) name = cursor.getString(nameIndex) ?: name
                        if (sizeIndex != -1) {
                            val bytes = cursor.getLong(sizeIndex)
                            sizeStr = if (bytes > 1024 * 1024) String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0)) else "${bytes / 1024} KB"
                        }
                    }
                }
            } catch (_: Exception) {}
            uploadedDocName = name
            uploadedDocMeta = "PDF/Gambar · $sizeStr · Terunggah"
            feedbackTitle = "Dokumen Berhasil Dipilih"
            feedbackMessage = "Berkas $name ($sizeStr) siap disimpan sebagai dokumen referensi tempat magang."
            feedbackIsSuccess = true
            showFeedback = true
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
            // Subtitle intro
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

                        // Nama perusahaan / tempat *
                        FormInputField(
                            label = "Nama perusahaan / tempat *",
                            value = namaPerusahaan,
                            onValueChange = { namaPerusahaan = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bidang usaha *
                        FormDropdownField(
                            label = "Bidang usaha *",
                            value = bidangUsaha
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nama tempat / cabang *
                        FormInputField(
                            label = "Nama tempat / cabang *",
                            value = namaTempat,
                            onValueChange = { namaTempat = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deskripsi tempat magang *
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

                        // Alamat lengkap *
                        FormInputField(
                            label = "Alamat lengkap *",
                            value = alamatLengkap,
                            onValueChange = { alamatLengkap = it },
                            isMultiLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Row: Kota & Provinsi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                FormDropdownField(
                                    label = "Kota *",
                                    value = kota
                                )
                            }
                            Box(modifier = Modifier.weight(1.2f)) {
                                FormDropdownField(
                                    label = "Provinsi *",
                                    value = provinsi
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sistem kerja
                        FormDropdownField(
                            label = "Sistem kerja",
                            value = sistemKerja
                        )
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

                        // File 1
                        DocumentItemBox(
                            name = "profil_sinar_karya.pdf",
                            meta = "PDF · 1,2 MB · Terunggah"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // File 2
                        DocumentItemBox(
                            name = "legalitas_perusahaan.pdf",
                            meta = "PDF · 860 KB · Terunggah"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (uploadedDocName != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DocumentItemBox(
                                name = uploadedDocName!!,
                                meta = uploadedDocMeta ?: "Terunggah"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Unggah Dokumen Button
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
                            text = "Dokumen disimpan admin sebagai referensi. PDF, JPG atau PNG, maks. 5 MB per berkas.",
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

            // Info Box
            item {
                MagangInfoBox(
                    text = "Kolom * wajib diisi. Menyimpan tempat tidak otomatis menerbitkan lowongan."
                )
            }

            // Button 1: Simpan Tempat Magang
            item {
                Button(
                    onClick = {
                        if (namaPerusahaan.isBlank() || namaTempat.isBlank()) {
                            feedbackTitle = "Data Belum Lengkap"
                            feedbackMessage = "Mohon lengkapi Nama Perusahaan dan Nama Tempat sebelum menyimpan."
                            feedbackIsSuccess = false
                            showFeedback = true
                        } else if (onSimpanTempatMagang != null) {
                            onSimpanTempatMagang(
                                namaPerusahaan,
                                bidangUsaha,
                                namaTempat,
                                deskripsiTempat,
                                alamatLengkap,
                                kota,
                                provinsi,
                                sistemKerja,
                                namaKontak,
                                emailPerusahaan,
                                nomorTelepon,
                                situsWeb.ifBlank { null }
                            ) {
                                feedbackTitle = "Tempat Magang Berhasil Disimpan"
                                feedbackMessage = "Data mitra tempat magang $namaPerusahaan berhasil disimpan ke Supabase."
                                feedbackIsSuccess = true
                                showFeedback = true
                            }
                        } else {
                            feedbackTitle = "Tempat Magang Berhasil Disimpan"
                            feedbackMessage = "Data mitra tempat magang $namaPerusahaan berhasil disimpan ke sistem."
                            feedbackIsSuccess = true
                            showFeedback = true
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
                        text = "Simpan Tempat Magang",
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
