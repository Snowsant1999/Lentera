package com.PPBM.lentera.features.admin.magang

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
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
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.admin.model.LowonganEntity
import com.PPBM.lentera.backend.admin.model.TempatMagangEntity
import com.PPBM.lentera.backend.core.WITA_ZONE
import com.PPBM.lentera.backend.core.normalizeSistemKerja
import com.PPBM.lentera.backend.core.parseLocalDateFlexible
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.features.admin.magang.components.*
import java.time.LocalDate
import java.util.Locale

@Composable
fun FormLowonganScreen(
    tempatMagangList: List<TempatMagangEntity> = emptyList(),
    existingLowongan: LowonganEntity? = null,
    isSubmitting: Boolean = false,
    onSimpanLowongan: () -> Unit = {},
    onSubmitLowongan: ((
        tempatMagangId: String,
        posisi: String,
        kategori: String?,
        lokasi: String,
        sistemKerja: String,
        deskripsi: String,
        persyaratan: String,
        periode: String,
        batas: String,
        kuota: Int,
        status: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit)? = null,
    onBackToDaftarLowongan: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    val isEditMode = existingLowongan != null
    val defaultDeadline = remember {
        LocalDate.now(WITA_ZONE).plusDays(30).toString()
    }

    var selectedTempatId by remember(existingLowongan?.id, tempatMagangList) {
        mutableStateOf(
            existingLowongan?.tempatMagangId
                ?: tempatMagangList.firstOrNull { it.status.equals("aktif", ignoreCase = true) }?.id
                ?: tempatMagangList.firstOrNull()?.id
                ?: ""
        )
    }
    var showTempatMenu by remember { mutableStateOf(false) }

    var posisi by remember(existingLowongan?.id) { mutableStateOf(existingLowongan?.posisi.orEmpty()) }
    var kategori by remember(existingLowongan?.id) { mutableStateOf(existingLowongan?.kategori.orEmpty()) }
    val kategoriOptions = remember {
        listOf("Teknologi", "Desain", "Bisnis", "Pemasaran", "Keuangan", "Pendidikan", "Kesehatan", "Lainnya")
    }
    var showKategoriMenu by remember { mutableStateOf(false) }
    var lokasi by remember(existingLowongan?.id) { mutableStateOf(existingLowongan?.lokasi.orEmpty()) }
    var sistemKerja by remember(existingLowongan?.id) {
        mutableStateOf(normalizeSistemKerja(existingLowongan?.sistemKerja ?: "On-site"))
    }
    var showSistemKerjaMenu by remember { mutableStateOf(false) }
    var deskripsiPekerjaan by remember(existingLowongan?.id) {
        mutableStateOf(existingLowongan?.deskripsiPekerjaan.orEmpty())
    }
    var persyaratan by remember(existingLowongan?.id) {
        mutableStateOf(existingLowongan?.persyaratan.orEmpty())
    }
    var periodeMagang by remember(existingLowongan?.id) {
        mutableStateOf(existingLowongan?.periodeMagang.orEmpty())
    }
    var batasPendaftaran by remember(existingLowongan?.id) {
        mutableStateOf(existingLowongan?.batasPendaftaran?.takeIf { it.isNotBlank() } ?: defaultDeadline)
    }
    var kuota by remember(existingLowongan?.id) {
        mutableStateOf(existingLowongan?.kuota?.toString() ?: "")
    }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    val selectedTempat = tempatMagangList.find { it.id == selectedTempatId }

    fun submitForm(targetStatus: String, successNote: String) {
        val missingFields = buildList {
            if (selectedTempatId.isBlank()) add("Tempat magang")
            if (posisi.isBlank()) add("Posisi")
            if (lokasi.isBlank()) add("Lokasi")
            if (deskripsiPekerjaan.isBlank()) add("Deskripsi pekerjaan")
            if (persyaratan.isBlank()) add("Persyaratan")
            if (periodeMagang.isBlank()) add("Periode magang")
            if (parseLocalDateFlexible(batasPendaftaran) == null) add("Batas pendaftaran")
        }
        val kuotaInt = kuota.filter { it.isDigit() }.toIntOrNull()
        if (missingFields.isNotEmpty()) {
            feedbackTitle = "Data Belum Lengkap"
            feedbackMessage = "Mohon lengkapi: ${missingFields.joinToString(", ")}."
            feedbackIsSuccess = false
            showFeedback = true
            return
        }
        if (kuotaInt == null || kuotaInt <= 0) {
            feedbackTitle = "Kuota Tidak Valid"
            feedbackMessage = "Masukkan angka kuota mahasiswa minimal 1."
            feedbackIsSuccess = false
            showFeedback = true
            return
        }
        if (targetStatus == "terbit" && selectedTempat != null && !selectedTempat.status.equals("aktif", ignoreCase = true)) {
            feedbackTitle = "Mitra Belum Aktif"
            feedbackMessage = "Aktifkan tempat magang ${selectedTempat.namaPerusahaan} terlebih dahulu agar lowongan tampil di portal mahasiswa."
            feedbackIsSuccess = false
            showFeedback = true
            return
        }

        val normalizedDeadline = parseLocalDateFlexible(batasPendaftaran)?.toString() ?: batasPendaftaran
        onSubmitLowongan?.invoke(
            selectedTempatId,
            posisi.trim(),
            kategori.trim().ifBlank { null },
            lokasi.trim(),
            normalizeSistemKerja(sistemKerja),
            deskripsiPekerjaan.trim(),
            persyaratan.trim(),
            periodeMagang.trim(),
            normalizedDeadline,
            kuotaInt,
            targetStatus,
            { err ->
                feedbackTitle = "Gagal Menyimpan Lowongan"
                feedbackMessage = err
                feedbackIsSuccess = false
                showFeedback = true
            },
            {
                feedbackTitle = if (isEditMode) "Lowongan Diperbarui" else "Lowongan Disimpan"
                feedbackMessage = successNote
                feedbackIsSuccess = true
                showFeedback = true
            }
        )
    }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = if (isEditMode) "UBAH LOWONGAN" else "FORM LOWONGAN",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToDaftarLowongan
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
                    text = if (isEditMode) {
                        "Perbarui detail lowongan magang yang dipilih."
                    } else {
                        "Buat lowongan baru dan pilih mitra perusahaan."
                    },
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            // Card 1: Tempat magang
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Tempat magang *",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable(enabled = tempatMagangList.isNotEmpty()) {
                                        showTempatMenu = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_avatar_2),
                                    contentDescription = "Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedTempat?.namaPerusahaan
                                            ?: "Belum ada mitra tersedia",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.Bold,
                                            color = MagangNavyDark
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (selectedTempat != null) {
                                            "${selectedTempat.namaCabang} · ${selectedTempat.kota}, ${selectedTempat.provinsi} (${selectedTempat.status})"
                                        } else {
                                            "Tambahkan mitra di menu Mitra Magang terlebih dahulu."
                                        },
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.Normal,
                                            color = MagangTextSecondary
                                        )
                                    )
                                }

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_down_alt),
                                    contentDescription = "Pilih Mitra",
                                    tint = MagangNavyDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showTempatMenu,
                                onDismissRequest = { showTempatMenu = false }
                            ) {
                                tempatMagangList.forEach { mitra ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = mitra.namaPerusahaan,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "${mitra.kota} · Status: ${mitra.status}",
                                                    fontSize = 11.sp,
                                                    color = MagangTextSecondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedTempatId = mitra.id ?: ""
                                            if (lokasi.isBlank()) {
                                                lokasi = mitra.kota
                                            }
                                            showTempatMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Detail lowongan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Detail lowongan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FormInputField(
                            label = "Posisi *",
                            value = posisi,
                            onValueChange = { posisi = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            FormDropdownField(
                                label = "Kategori (opsional)",
                                value = if (kategori.isBlank()) "Pilih Kategori" else kategori,
                                onClick = { showKategoriMenu = true }
                            )
                            DropdownMenu(
                                expanded = showKategoriMenu,
                                onDismissRequest = { showKategoriMenu = false }
                            ) {
                                kategoriOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            kategori = opt
                                            showKategoriMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                FormInputField(
                                    label = "Lokasi *",
                                    value = lokasi,
                                    onValueChange = { lokasi = it }
                                )
                            }
                            Box(modifier = Modifier.weight(1.2f)) {
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

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Deskripsi pekerjaan *",
                            value = deskripsiPekerjaan,
                            onValueChange = { deskripsiPekerjaan = it },
                            isMultiLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FormInputField(
                            label = "Persyaratan *",
                            value = persyaratan,
                            onValueChange = { persyaratan = it },
                            isMultiLine = true
                        )
                    }
                }
            }

            // Card 3: Jadwal & kuota
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Jadwal & kuota",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FormInputField(
                            label = "Periode magang *",
                            value = periodeMagang,
                            onValueChange = { periodeMagang = it }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable {
                                    val initial = parseLocalDateFlexible(batasPendaftaran)
                                        ?: LocalDate.now(WITA_ZONE).plusDays(30)
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            batasPendaftaran = String.format(
                                                Locale.US,
                                                "%04d-%02d-%02d",
                                                year,
                                                month + 1,
                                                dayOfMonth
                                            )
                                        },
                                        initial.year,
                                        initial.monthValue - 1,
                                        initial.dayOfMonth
                                    ).show()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_date_alt),
                                contentDescription = "Pilih Tanggal",
                                tint = MagangNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Batas pendaftaran * (ketuk untuk ubah)",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = batasPendaftaran,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        FormInputField(
                            label = "Kuota *",
                            value = kuota,
                            onValueChange = { kuota = it.filter { ch -> ch.isDigit() } }
                        )
                    }
                }
            }

            item {
                MagangInfoBox(
                    text = if (isEditMode) {
                        "Menyimpan perubahan mempertahankan status saat ini (${existingLowongan?.status}). Gunakan Terbitkan untuk menampilkan ke mahasiswa."
                    } else {
                        "Pilih Simpan sebagai Draf untuk ditinjau nanti, atau Simpan & Terbitkan agar langsung tampil di portal mahasiswa."
                    }
                )
            }

            // Button 1: Simpan (Draf atau pertahankan status saat edit)
            item {
                val keepStatus = existingLowongan?.status ?: "draf"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangNavyDark, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isSubmitting) {
                            submitForm(
                                targetStatus = keepStatus,
                                successNote = "Lowongan ${posisi.trim()} berhasil disimpan dengan status $keepStatus."
                            )
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isSubmitting -> "Menyimpan..."
                            isEditMode -> "Simpan Perubahan"
                            else -> "Simpan sebagai Draf"
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // Button 2: Simpan & Terbitkan
            item {
                Button(
                    enabled = !isSubmitting,
                    onClick = {
                        submitForm(
                            targetStatus = "terbit",
                            successNote = "Lowongan ${posisi.trim()} berhasil disimpan dan diterbitkan ke portal mahasiswa."
                        )
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
                        text = if (isSubmitting) "Memproses..." else "Simpan & Terbitkan",
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
                Text(
                    text = "Publikasi dilakukan oleh admin. Lowongan yang terbit pada mitra aktif langsung tampil kepada mahasiswa.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToDaftarLowongan() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Daftar Lowongan",
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
                    onSimpanLowongan()
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun FormLowonganPreview() {
    FormLowonganScreen()
}
