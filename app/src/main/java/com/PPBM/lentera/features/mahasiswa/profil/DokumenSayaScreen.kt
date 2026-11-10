package com.PPBM.lentera.features.mahasiswa.profil

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.LenteraBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon
import com.PPBM.lentera.core.designsystem.theme.LenteraBadgeBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBorder
import com.PPBM.lentera.core.designsystem.theme.LenteraGreen
import com.PPBM.lentera.core.designsystem.theme.LenteraGreenBg
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraRedMuted
import com.PPBM.lentera.core.designsystem.theme.LenteraTeal
import com.PPBM.lentera.core.designsystem.theme.LenteraTextPrimary
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

data class DokumenModel(
    val id: String,
    val iconRes: Int,
    val title: String,
    val fileName: String,
    val fileInfo: String,
    val filePath: String = "",
    val localUri: Uri? = null
)

// 3 Kategori Dokumen Arsip Profil sesuai CHECK constraint `dokumen_mahasiswa.jenis`
val KATEGORI_DOKUMEN_BAKU = listOf(
    "CV",
    "Transkrip",
    "Portofolio"
)

@Composable
fun DokumenSayaScreen(
    dokumenList: List<com.PPBM.lentera.backend.mahasiswa.model.DokumenMahasiswaItem> = emptyList(),
    ownerName: String = "-",
    ownerNim: String = "-",
    ownerProdi: String = "-",
    onUploadDokumen: (jenis: String, fileName: String, fileBytes: ByteArray, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, onSuccess, _ -> onSuccess() },
    onDeleteDokumen: (docId: String, filePath: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, onSuccess, _ -> onSuccess() },
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val displayDocs = dokumenList.map { item ->
        val icon = when {
            item.jenis.contains("CV", ignoreCase = true) -> R.drawable.ic_document_symbol
            item.jenis.contains("Transkrip", ignoreCase = true) -> R.drawable.ic_document_symbol_2
            item.jenis.contains("Portofolio", ignoreCase = true) -> R.drawable.ic_document_symbol_3
            item.jenis.contains("KTM", ignoreCase = true) -> R.drawable.ic_document_symbol
            else -> R.drawable.ic_document_symbol_2
        }
        val sizeKb = item.ukuranBytes / 1024
        val sizeStr = if (sizeKb > 1024) String.format(java.util.Locale.US, "%.1f MB", sizeKb / 1024.0) else "$sizeKb KB"
        val dateStr = item.createdAt?.take(10) ?: "Tersimpan"
        DokumenModel(
            id = item.id ?: "",
            iconRes = icon,
            title = item.jenis,
            fileName = item.namaFile,
            fileInfo = "PDF · $sizeStr · $dateStr",
            filePath = item.filePath
        )
    }

    var showUploadDialog by remember { mutableStateOf(false) }
    var uploadTargetCategory by remember { mutableStateOf<String?>(null) }
    var selectedDocForPreview by remember { mutableStateOf<DokumenModel?>(null) }
    var docPendingDelete by remember { mutableStateOf<DokumenModel?>(null) }
    val terakhirDiperbarui = remember(dokumenList) {
        dokumenList.maxOfOrNull { it.createdAt?.take(10).orEmpty() }
            ?.takeIf { it.isNotBlank() } ?: "Belum ada unggahan"
    }

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
            // Tombol Kembali
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
                text = "DOKUMEN SAYA",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = LenteraNavy
            )

            Text(
                text = "CV, transkrip, dan portofolio dalam satu tempat.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Card Ringkasan Dokumen Tersimpan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2EBF4))
                    .padding(18.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_folder_open),
                    contentDescription = null,
                    tint = LenteraNavy,
                    modifier = Modifier.size(32.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${displayDocs.size} dokumen tersimpan",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = "Siap digunakan untuk melamar magang secara otomatis.",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar Dokumen
            if (displayDocs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, spotColor = Color(0x09173B57), shape = RoundedCornerShape(16.dp))
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum Ada Dokumen",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LenteraNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Unggah CV, Transkrip Nilai, atau Portofolio Anda untuk kebutuhan pendaftaran magang.",
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            color = LenteraTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            } else {
                displayDocs.forEach { doc ->
                    DokumenCard(
                        iconRes = doc.iconRes,
                        title = doc.title,
                        fileName = doc.fileName,
                        fileInfo = doc.fileInfo,
                        onClick = { selectedDocForPreview = doc }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tombol Unggah Dokumen Baru
            Button(
                onClick = {
                    uploadTargetCategory = null
                    showUploadDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LenteraNavy,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_open_answer),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(-90f)
                    )
                    Text(
                        text = "Unggah Dokumen",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Format PDF · Maksimal 5 MB per dokumen",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = LenteraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Riwayat Terakhir Diperbarui
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history_alt),
                    contentDescription = null,
                    tint = LenteraTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Terakhir diperbarui $terakhirDiperbarui",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alert Info
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
                    text = "Dokumen yang Anda simpan di sini akan otomatis terhubung saat Anda mengajukan lamaran magang.",
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

    // Modal In-App Preview Dokumen Langsung di Dalam Aplikasi
    selectedDocForPreview?.let { doc ->
        InAppDocumentPreviewDialog(
            doc = doc,
            ownerName = ownerName,
            ownerNim = ownerNim,
            ownerProdi = ownerProdi,
            onDismiss = { selectedDocForPreview = null },
            onGanti = {
                uploadTargetCategory = doc.title
                selectedDocForPreview = null
                showUploadDialog = true
            },
            onDelete = {
                selectedDocForPreview = null
                docPendingDelete = doc
            }
        )
    }

    docPendingDelete?.let { doc ->
        AlertDialog(
            onDismissRequest = { docPendingDelete = null },
            title = {
                Text(
                    text = "Hapus Dokumen?",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = LenteraNavy
                )
            },
            text = {
                Text(
                    text = "Berkas ${doc.title} (${doc.fileName}) akan dihapus dari arsip profil Anda.",
                    fontFamily = PlusJakartaSans,
                    color = LenteraTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        docPendingDelete = null
                        onDeleteDokumen(
                            doc.id,
                            doc.filePath,
                            {
                                feedbackIsSuccess = true
                                feedbackTitle = "Dokumen Dihapus"
                                feedbackMessage = "Berkas ${doc.title} (${doc.fileName}) berhasil dihapus dari akun Anda."
                                showFeedbackDialog = true
                            },
                            { err ->
                                feedbackIsSuccess = false
                                feedbackTitle = "Gagal Menghapus"
                                feedbackMessage = err
                                showFeedbackDialog = true
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LenteraRedMuted)
                ) {
                    Text("Hapus", color = Color.White, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { docPendingDelete = null }) {
                    Text("Batal", fontFamily = PlusJakartaSans, color = LenteraNavy)
                }
            }
        )
    }

    // Modal Unggah Dokumen dengan Pilihan Kategori Baku & File Picker HP
    if (showUploadDialog) {
        UploadDokumenDialog(
            initialCategory = uploadTargetCategory ?: KATEGORI_DOKUMEN_BAKU[0],
            onDismiss = { showUploadDialog = false },
            onUploadSuccess = { kategori, namaFile, _, uri ->
                val bytes = uri?.let { u ->
                    try {
                        context.contentResolver.openInputStream(u)?.use { it.readBytes() }
                    } catch (_: Exception) {
                        null
                    }
                }
                when {
                    bytes == null || bytes.isEmpty() -> {
                        feedbackIsSuccess = false
                        feedbackTitle = "Gagal Membaca File"
                        feedbackMessage = "Berkas $namaFile kosong atau tidak dapat dibaca."
                        showFeedbackDialog = true
                    }
                    bytes.size > 5 * 1024 * 1024 -> {
                        feedbackIsSuccess = false
                        feedbackTitle = "Ukuran Terlalu Besar"
                        feedbackMessage = "Ukuran maksimal dokumen adalah 5 MB."
                        showFeedbackDialog = true
                    }
                    else -> {
                        showUploadDialog = false
                        onUploadDokumen(
                            kategori,
                            namaFile,
                            bytes,
                            {
                                feedbackIsSuccess = true
                                feedbackTitle = "Dokumen Berhasil Diunggah"
                                feedbackMessage = "Berkas $namaFile untuk kategori $kategori berhasil tersimpan."
                                showFeedbackDialog = true
                            },
                            { err ->
                                feedbackIsSuccess = false
                                feedbackTitle = "Gagal Mengunggah Dokumen"
                                feedbackMessage = err
                                showFeedbackDialog = true
                            }
                        )
                    }
                }
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

// ============================================================================
// Komponen Card Dokumen
// ============================================================================

@Composable
private fun DokumenCard(
    iconRes: Int,
    title: String,
    fileName: String,
    fileInfo: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(42.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = fileName,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = LenteraTextSecondary
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_open_answer),
                contentDescription = "Lihat Dokumen",
                tint = LenteraTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fileInfo,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = LenteraTextSecondary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(LenteraGreenBg)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Tersimpan",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = LenteraGreen
                )
            }
        }
    }
}

// ============================================================================
// Modal In-App Preview Dokumen (Visual Lembar A4 di Dalam Aplikasi)
// ============================================================================

@Composable
private fun InAppDocumentPreviewDialog(
    doc: DokumenModel,
    ownerName: String,
    ownerNim: String,
    ownerProdi: String,
    onDismiss: () -> Unit,
    onGanti: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                // Header Dialog Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onDismiss)
                            .padding(4.dp)
                    ) {
                        BackArrowIcon(modifier = Modifier.size(20.dp), tint = LenteraNavy)
                        Text(
                            text = "Tutup Preview",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LenteraNavy
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LenteraGreenBg)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Dokumen Sah",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = LenteraGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lembar Visual Preview Dokumen A4 (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF9FBFC))
                        .border(1.dp, LenteraBorder, RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    // Kop Dokumen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LENTERA KAMPUS",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = LenteraNavy
                            )
                            Text(
                                text = "Sistem Verifikasi Dokumen Terpadu",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = LenteraTextSecondary
                            )
                        }

                        Image(
                            painter = painterResource(id = doc.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    HorizontalDivider(
                        color = LenteraNavy,
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Judul Dokumen
                    Text(
                        text = doc.title.uppercase(),
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = LenteraNavy,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Identitas Pemilik Dokumen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEBF1F7))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PreviewInfoRow(label = "Nama Mahasiswa", value = ownerName)
                        PreviewInfoRow(label = "NIM", value = ownerNim)
                        PreviewInfoRow(label = "Program Studi", value = ownerProdi)
                        PreviewInfoRow(label = "Nama Berkas", value = doc.fileName)
                        PreviewInfoRow(label = "Informasi File", value = doc.fileInfo)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Keterangan Dokumen:",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LenteraNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val detailContent = when (doc.title) {
                        "CV" -> "Dokumen Curriculum Vitae resmi tersimpan pada arsip profil mahasiswa dan siap dilampirkan otomatis saat pengajuan magang."
                        "Transkrip" -> "Dokumen Transkrip Nilai akademik resmi tersimpan pada arsip profil mahasiswa untuk verifikasi persyaratan magang."
                        "Portofolio" -> "Dokumen Portofolio karya mahasiswa tersimpan pada arsip profil dan dapat dipilih langsung saat melamar posisi magang."
                        else -> "Dokumen ini telah tersimpan di arsip profil dan siap digunakan sebagai kelengkapan berkas magang."
                    }

                    Text(
                        text = detailContent,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        lineHeight = 19.sp,
                        color = LenteraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stempel Digital Kampus
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFC7D7E5), RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_information_icon),
                                contentDescription = null,
                                tint = LenteraTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Tervalidasi secara digital oleh Biro Administrasi Akademik & Kemahasiswaan LENTERA.",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = LenteraTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar Bawah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(0.85f)
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEBEB),
                            contentColor = LenteraRedMuted
                        )
                    ) {
                        Text(
                            text = "Hapus",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onGanti,
                        modifier = Modifier
                            .weight(1.35f)
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE2EBF4),
                            contentColor = LenteraNavy
                        )
                    ) {
                        Text(
                            text = "Ganti Berkas",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(0.95f)
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LenteraNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Tutup",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            color = LenteraTextSecondary
        )
        Text(
            text = value,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = LenteraNavy
        )
    }
}

// ============================================================================
// Modal Unggah Dokumen (Pilihan Kategori Baku + File Picker HP)
// ============================================================================

@Composable
private fun UploadDokumenDialog(
    initialCategory: String,
    onDismiss: () -> Unit,
    onUploadSuccess: (kategori: String, namaFile: String, ukuran: String, uri: Uri?) -> Unit
) {
    val context = LocalContext.current
    var selectedKategori by remember { mutableStateOf(initialCategory) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileNameDisplay by remember { mutableStateOf("") }
    var fileSizeDisplay by remember { mutableStateOf("") }

    var showUploadError by remember { mutableStateOf(false) }
    var uploadErrorTitle by remember { mutableStateOf("") }
    var uploadErrorMessage by remember { mutableStateOf("") }

    // Launcher File Picker Bawaan HP Android (System Storage)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            val (extractedName, extractedSize) = getFileDetails(context, uri)
            fileNameDisplay = extractedName
            fileSizeDisplay = extractedSize
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Unggah Dokumen",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LenteraNavy
            )

            Text(
                text = "Pilih kategori dokumen wajib:",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LenteraNavy
            )

            // Kategori Arsip Profil (CV, Transkrip, Portofolio)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 170.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KATEGORI_DOKUMEN_BAKU.forEach { kat ->
                    val isSelected = kat == selectedKategori
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFE2EBF4) else Color(0xFFF7FAFC))
                            .border(
                                1.dp,
                                if (isSelected) LenteraNavy else LenteraBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedKategori = kat }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = kat,
                            fontFamily = PlusJakartaSans,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = LenteraNavy
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(LenteraNavy)
                            )
                        }
                    }
                }
            }

            // Tombol Membuka File Picker Asli HP
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Berkas Dokumen (PDF):",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = LenteraNavy
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF5F8FA))
                        .border(1.dp, Color(0xFFC7D7E5), RoundedCornerShape(14.dp))
                        .clickable { filePickerLauncher.launch("application/pdf") }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_folder_open),
                        contentDescription = "Pilih File",
                        tint = LenteraNavy,
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (fileNameDisplay.isNotBlank()) fileNameDisplay else "Pilih file dari HP Anda...",
                            fontFamily = PlusJakartaSans,
                            fontWeight = if (fileNameDisplay.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (fileNameDisplay.isNotBlank()) LenteraNavy else LenteraTextSecondary
                        )
                        Text(
                            text = if (fileNameDisplay.isNotBlank()) "Ukuran: $fileSizeDisplay" else "Format PDF, Maksimal 5 MB",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = LenteraTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        val finalFileName = if (fileNameDisplay.isNotBlank()) {
                            fileNameDisplay
                        } else {
                            val sanitizedCat = selectedKategori.replace(" ", "_").replace("(", "").replace(")", "")
                            "${sanitizedCat}_Mahasiswa.pdf"
                        }

                        if (selectedUri == null && fileNameDisplay.isBlank()) {
                            uploadErrorTitle = "Berkas Belum Dipilih"
                            uploadErrorMessage = "Silakan ketuk tombol 'Pilih Berkas PDF' untuk memilih dokumen dari memori HP."
                            showUploadError = true
                            return@Button
                        }

                        if (!finalFileName.lowercase().endsWith(".pdf")) {
                            uploadErrorTitle = "Format Berkas Tidak Sesuai"
                            uploadErrorMessage = "Berkas yang dipilih harus berekstensi .pdf."
                            showUploadError = true
                            return@Button
                        }

                        onUploadSuccess(selectedKategori, finalFileName, fileSizeDisplay, selectedUri)
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
                        text = "Unggah",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    LenteraFeedbackDialog(
        show = showUploadError,
        isSuccess = false,
        title = uploadErrorTitle,
        message = uploadErrorMessage,
        onDismiss = { showUploadError = false },
        buttonText = "Pilih Ulang"
    )
}

// Helper untuk membaca nama & ukuran file asli dari Content Resolver Android
private fun getFileDetails(context: android.content.Context, uri: Uri): Pair<String, String> {
    var name = "Dokumen_Mahasiswa.pdf"
    var sizeStr = "512 KB"
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
                    sizeStr = if (bytes > 1024 * 1024) {
                        String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                    } else {
                        "${bytes / 1024} KB"
                    }
                }
            }
        }
    } catch (_: Exception) {
        name = uri.lastPathSegment ?: "Dokumen.pdf"
    }
    return Pair(name, sizeStr)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewDokumenSayaScreen() {
    DokumenSayaScreen()
}
