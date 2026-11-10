package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import java.util.Locale

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

/**
 * FRAME 7: Hasil & Penyelesaian
 * Menampilkan draf ringkasan penanganan untuk mahasiswa, dokumen penyelesaian,
 * pengaturan publikasi, dan tombol penutupan laporan.
 */
@Composable
fun HasilPenyelesaianScreen(
    laporan: AdminLaporan? = null,
    dokumenHasilList: List<DokumenHasilItem> = emptyList(),
    isUpdating: Boolean = false,
    onOpenDokumen: (DokumenHasilItem) -> Unit = {},
    onSimpanDanSelesaikan: (ringkasanPenanganan: String, isBagikanHasil: Boolean) -> Unit = { _, _ -> },
    onSimpanLengkap: ((ringkasanPenanganan: String, isBagikanHasil: Boolean, newAttachments: List<Triple<String, ByteArray, Boolean>>) -> Unit)? = null,
    onBackToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    val nomorLaporan = laporan?.nomorLaporan ?: "-"
    val judulLaporan = laporan?.judul ?: "Memuat laporan..."
    val isAnonim = laporan?.isAnonim ?: true

    var isBagikanHasil by remember(laporan?.id) { mutableStateOf(laporan?.hasilDipublikasi ?: true) }
    var ringkasanPenanganan by remember(laporan?.id) {
        mutableStateOf(laporan?.hasilPenanganan.orEmpty())
    }
    val pendingAttachments = remember(laporan?.id) {
        mutableStateListOf<Triple<String, ByteArray, Boolean>>()
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(laporan?.id) {
        if (laporan != null) {
            isBagikanHasil = laporan.hasilDipublikasi
            if (!laporan.hasilPenanganan.isNullOrBlank()) {
                ringkasanPenanganan = laporan.hasilPenanganan
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        validationError = null
        try {
            var fileName = "dokumen_hasil_${System.currentTimeMillis()}"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIdx >= 0) {
                    fileName = cursor.getString(nameIdx) ?: fileName
                }
            }
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            when {
                bytes == null || bytes.isEmpty() -> {
                    validationError = "Berkas '$fileName' kosong atau gagal dibaca."
                }
                bytes.size.toLong() > 20L * 1024L * 1024L -> {
                    validationError = "Ukuran berkas '$fileName' melebihi batas maksimal 20 MB."
                }
                else -> {
                    pendingAttachments.add(Triple(fileName, bytes, isBagikanHasil))
                }
            }
        } catch (e: Exception) {
            validationError = "Gagal membaca berkas: ${e.message ?: "kesalahan tidak diketahui"}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "HASIL & PENYELESAIAN",
            onBackClick = onBackToDetail
        )

        // 2. Konten Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ringkasan Laporan dengan Target Selesai
            LaporanSummaryHeaderCard(
                id = nomorLaporan,
                statusText = "Penanganan",
                statusBg = Color(0xFFFFE4E6),
                statusColor = Color(0xFFE11D48),
                title = "$judulLaporan · ${if (isAnonim) "Anonim" else "Terverifikasi"}",
                subtitle = ""
            )

            // Card Hasil Penanganan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hasil Penanganan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )

                        Text(
                            text = "Draf hasil",
                            modifier = Modifier
                                .background(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ringkasan penanganan untuk mahasiswa",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = ringkasanPenanganan,
                        onValueChange = {
                            ringkasanPenanganan = it
                            if (validationError != null) validationError = null
                        },
                        placeholder = {
                            Text(
                                text = "Tulis ringkasan resmi penanganan laporan...",
                                style = TextStyle(fontSize = 13.sp, fontFamily = PlusJakartaFont, color = SiagaTextMuted)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SiagaBorderColor,
                            focusedBorderColor = SiagaTealPrimary
                        ),
                        textStyle = TextStyle(fontSize = 13.sp, fontFamily = PlusJakartaFont, color = SiagaNavyDark)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (dokumenHasilList.isEmpty() && pendingAttachments.isEmpty()) {
                        Text(
                            text = "Belum ada dokumen penyelesaian yang dilampirkan.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                color = SiagaTextSecondary
                            )
                        )
                    }

                    dokumenHasilList.forEach { doc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, SiagaBorderColor, RoundedCornerShape(12.dp))
                                .clickable { onOpenDokumen(doc) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_file_text),
                                    contentDescription = doc.tipeFile,
                                    tint = SiagaNavyDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.namaFile,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            fontFamily = PlusJakartaFont,
                                            fontWeight = FontWeight.Bold,
                                            color = SiagaNavyDark
                                        )
                                    )
                                    Text(
                                        text = "${doc.tipeFile} · ${formatHasilSize(doc.ukuranBytes)} · ${if (doc.bolehDibagikan) "Boleh dibagikan" else "Internal"}",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = PlusJakartaFont,
                                            color = SiagaTextSecondary
                                        )
                                    )
                                }
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = "Buka",
                                tint = SiagaNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    pendingAttachments.forEachIndexed { idx, (fileName, bytes, shared) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$fileName (baru)",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = SiagaNavyDark
                                    )
                                )
                                Text(
                                    text = "${formatHasilSize(bytes.size.toLong())} · ${if (shared) "Boleh dibagikan ke pelapor" else "Khusus internal"} · Ketuk untuk ubah visibilitas",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontFamily = PlusJakartaFont,
                                        color = SiagaTealPrimary
                                    ),
                                    modifier = Modifier.clickable(enabled = !isUpdating) {
                                        pendingAttachments[idx] = Triple(fileName, bytes, !shared)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hapus",
                                style = TextStyle(
                                    fontSize = 11.5.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB91C1C)
                                ),
                                modifier = Modifier
                                    .clickable(enabled = !isUpdating) {
                                        pendingAttachments.removeAt(idx)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        enabled = !isUpdating && laporan != null,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(SiagaTealPrimary)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Tambah Dokumentasi",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaTealPrimary
                            )
                        )
                    }
                }
            }

            // Card Status Penyelesaian (Read-only info tercatat di server)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Status Penyelesaian",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Status setelah disimpan: Selesai",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!laporan?.tanggalPenyelesaian.isNullOrBlank()) {
                            "Tanggal penyelesaian tercatat: ${laporan?.tanggalPenyelesaian?.take(16)?.replace("T", " ")}"
                        } else {
                            "Tanggal penyelesaian dicatat otomatis oleh server saat disimpan."
                        },
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )
                }
            }

            // Card Publikasi Hasil
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Publikasi Hasil",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bagikan hasil ke mahasiswa",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )

                        Switch(
                            checked = isBagikanHasil,
                            onCheckedChange = { isBagikanHasil = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SiagaTealPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hanya ringkasan dan dokumen berlabel Boleh dibagikan yang tampil ke pelapor setelah status Selesai. Catatan internal tidak dibagikan.",
                        style = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    AdminInfoBanner(
                        text = "Pastikan ringkasan dan dokumen telah mendapat izin untuk dibagikan. Mahasiswa akan menerima notifikasi pembaruan."
                    )
                }
            }

            if (!validationError.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = validationError!!,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB91C1C)
                        )
                    )
                }
            }

            // Button: Simpan & Selesaikan Laporan
            Button(
                onClick = {
                    val trimmed = ringkasanPenanganan.trim()
                    if (trimmed.isBlank()) {
                        validationError = "Ringkasan penanganan wajib diisi sebelum menyelesaikan laporan."
                        return@Button
                    }
                    validationError = null
                    if (onSimpanLengkap != null) {
                        onSimpanLengkap(trimmed, isBagikanHasil, pendingAttachments.toList())
                    } else {
                        onSimpanDanSelesaikan(trimmed, isBagikanHasil)
                    }
                },
                enabled = !isUpdating && laporan != null,
                colors = ButtonDefaults.buttonColors(containerColor = SiagaTealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isUpdating) "Menyimpan..." else "Simpan & Selesaikan Laporan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Button: Kembali ke Detail Laporan
            OutlinedButton(
                onClick = onBackToDetail,
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(SiagaBorderColor)
                ),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Kembali ke Detail Laporan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }

        // 3. Bottom Bar
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

private fun formatHasilSize(bytes: Long): String {
    return when {
        bytes >= 1024L * 1024L -> String.format(Locale.US, "%.1f MB", bytes.toDouble() / (1024.0 * 1024.0))
        bytes >= 1024L -> "${bytes / 1024L} KB"
        else -> "$bytes B"
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HasilPenyelesaianScreenPreview() {
    HasilPenyelesaianScreen()
}

