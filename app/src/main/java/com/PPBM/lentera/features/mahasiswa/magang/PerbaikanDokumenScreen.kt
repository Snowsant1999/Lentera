package com.PPBM.lentera.features.mahasiswa.magang

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.normalizeDokumenLamaranJenis
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.theme.*
import java.util.Locale

private const val MAX_PERBAIKAN_DOC_BYTES = 20L * 1024L * 1024L

@Composable
fun PerbaikanDokumenScreen(
    perusahaan: String = "",
    catatanRevisi: String = "",
    initialJenis: String = "Transkrip",
    isSubmitting: Boolean = false,
    onBackClick: () -> Unit = {},
    onSubmitPerbaikan: (
        jenis: String,
        fileName: String,
        fileBytes: ByteArray,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit = { _, _, _, _, _ -> },
    onNavigateAfterSuccess: (jenis: String, fileName: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val fontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
    val jenisOptions = listOf("CV", "Surat Pengantar", "Transkrip", "KTM", "Portofolio", "Sertifikat")

    var selectedJenis by remember(catatanRevisi, initialJenis) {
        mutableStateOf(
            normalizeDokumenLamaranJenis(
                catatanRevisi.ifBlank { initialJenis }
            )
        )
    }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFileSizeLabel by remember { mutableStateOf<String?>(null) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        var name = "dokumen_perbaikan.pdf"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && idx != -1) {
                name = cursor.getString(idx) ?: name
            }
        }
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        } catch (_: Exception) {
            ByteArray(0)
        }
        when {
            bytes.isEmpty() -> {
                errorMessage = "Berkas kosong atau gagal dibaca dari perangkat."
                showErrorDialog = true
            }
            bytes.size > MAX_PERBAIKAN_DOC_BYTES -> {
                errorMessage = "Ukuran maksimal berkas perbaikan adalah 20 MB."
                showErrorDialog = true
            }
            else -> {
                selectedFileName = name
                selectedFileBytes = bytes
                selectedFileSizeLabel = if (bytes.size >= 1024 * 1024) {
                    String.format(Locale.US, "%.1f MB", bytes.size / (1024.0 * 1024.0))
                } else {
                    "${(bytes.size / 1024).coerceAtLeast(1)} KB"
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        topBar = {
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
                        .clickable { if (!isSubmitting) onBackClick() }
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Perbaikan Dokumen",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        enabled = selectedFileBytes != null && !isSubmitting,
                        onClick = {
                            val name = selectedFileName ?: return@Button
                            val bytes = selectedFileBytes ?: return@Button
                            onSubmitPerbaikan(
                                selectedJenis,
                                name,
                                bytes,
                                { err ->
                                    errorMessage = err
                                    showErrorDialog = true
                                },
                                {
                                    onNavigateAfterSuccess(selectedJenis, name)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MagangAmberPrimary)
                    ) {
                        Text(
                            text = if (isSubmitting) "Mengunggah..." else "Kirim Perbaikan Dokumen",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFFED7AA)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_file_warning),
                            contentDescription = null,
                            tint = Color(0xFFC2410C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (perusahaan.isNotBlank()) "Catatan Revisi ($perusahaan)" else "Catatan Revisi Admin",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = catatanRevisi.ifBlank {
                            "Silakan unggah kembali dokumen perbaikan dengan kualitas jelas dan dapat terbaca."
                        },
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            lineHeight = 17.5.sp,
                            color = NavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Jenis Dokumen yang Diperbaiki",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jenisOptions.forEach { jenis ->
                    val isSelected = selectedJenis == jenis
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NavyDark else Color.White)
                            .border(1.dp, if (isSelected) NavyDark else CardBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedJenis = jenis }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = jenis,
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else NavyDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Unggah Dokumen Pengganti",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Format PDF, JPG, atau PNG dengan ukuran maksimal 20 MB.",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InputBackground)
                    .border(1.5.dp, MagangAmberPrimary, RoundedCornerShape(16.dp))
                    .clickable(enabled = !isSubmitting) { filePickerLauncher.launch("*/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload_symbol),
                        contentDescription = "Upload",
                        tint = MagangAmberPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedFileName == null) "Pilih file dokumen pengganti" else "Ganti file dokumen",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ketuk untuk membuka file manager",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 11.5.sp,
                            color = SlateGray
                        )
                    )
                }
            }

            if (selectedFileName != null && selectedFileBytes != null) {
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
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(InputBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_doc_transkrip),
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedFileName!!,
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                ),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${selectedFileSizeLabel ?: ""} · Siap diunggah ($selectedJenis)",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF287D4A)
                                )
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_check),
                            contentDescription = "Siap",
                            tint = Color(0xFF287D4A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    LenteraFeedbackDialog(
        show = showErrorDialog,
        isSuccess = false,
        title = "Unggah Perbaikan Gagal",
        message = errorMessage,
        onDismiss = { showErrorDialog = false }
    )
}

@Preview(showBackground = true)
@Composable
fun PerbaikanDokumenScreenPreview() {
    LenteraTheme {
        PerbaikanDokumenScreen()
    }
}
