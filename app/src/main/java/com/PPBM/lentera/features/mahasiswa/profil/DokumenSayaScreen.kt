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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    val localUri: Uri? = null
)

// 6 Kategori Dokumen Baku & Terstandarisasi
val KATEGORI_DOKUMEN_BAKU = listOf(
    "Curriculum Vitae (CV)",
    "Transkrip Nilai",
    "Portofolio",
    "Sertifikat Pendukung",
    "Kartu Tanda Mahasiswa (KTM)",
    "Surat Pengantar Kampus"
)

@Composable
fun DokumenSayaScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val dokumenList = remember {
        mutableStateListOf(
            DokumenModel(
                id = "1",
                iconRes = R.drawable.ic_document_symbol,
                title = "Curriculum Vitae (CV)",
                fileName = "CV_Budi_Santoso.pdf",
                fileInfo = "PDF Ã‚· 245 KB Ã‚· 12 Okt 2026"
            ),
            DokumenModel(
                id = "2",
                iconRes = R.drawable.ic_document_symbol_2,
                title = "Transkrip Nilai",
                fileName = "Transkrip_Budi_Santoso.pdf",
                fileInfo = "PDF Ã‚· 1,2 MB Ã‚· 10 Okt 2026"
            ),
            DokumenModel(
                id = "3",
                iconRes = R.drawable.ic_document_symbol_3,
                title = "Portofolio",
                fileName = "Portofolio_Budi_Santoso.pdf",
                fileInfo = "PDF Ã‚· 3,4 MB Ã‚· 10 Okt 2026"
            )
        )
    }

    var showUploadDialog by remember { mutableStateOf(false) }
    var uploadTargetCategory by remember { mutableStateOf<String?>(null) }
    var selectedDocForPreview by remember { mutableStateOf<DokumenModel?>(null) }
    var terakhirDiperbarui by remember { mutableStateOf("12 Oktober 2026") }

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
            // Tombol Kembali: "Ã¢â€ Â Profil"
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
                        text = "${dokumenList.size} dokumen tersimpan",
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

            // Daftar Dokumen (Dapat diklik untuk In-App Preview)
            dokumenList.forEach { doc ->
                DokumenCard(
                    iconRes = doc.iconRes,
                    title = doc.title,
                    fileName = doc.fileName,
                    fileInfo = doc.fileInfo,
                    onClick = { selectedDocForPreview = doc }
                )
                Spacer(modifier = Modifier.height(14.dp))
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
                text = "Format PDF Ã‚· Maksimal 5 MB per dokumen",
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
            onDismiss = { selectedDocForPreview = null },
            onGanti = {
                uploadTargetCategory = doc.title
                selectedDocForPreview = null
                showUploadDialog = true
            },
            onDelete = {
                dokumenList.remove(doc)
                selectedDocForPreview = null
                feedbackIsSuccess = true
                feedbackTitle = "Dokumen Dihapus"
                feedbackMessage = "Berkas ${doc.title} (${doc.fileName}) telah berhasil dihapus dari daftar dokumen."
                showFeedbackDialog = true
            }
        )
    }

    // Modal Unggah Dokumen dengan Pilihan Kategori Baku & File Picker HP
    if (showUploadDialog) {
        UploadDokumenDialog(
            initialCategory = uploadTargetCategory ?: KATEGORI_DOKUMEN_BAKU[0],
            onDismiss = { showUploadDialog = false },
            onUploadSuccess = { kategori, namaFile, ukuran, uri ->
                val icon = when (kategori) {
                    "Curriculum Vitae (CV)" -> R.drawable.ic_document_symbol
                    "Transkrip Nilai" -> R.drawable.ic_document_symbol_2
                    "Portofolio" -> R.drawable.ic_document_symbol_3
                    "Kartu Tanda Mahasiswa (KTM)" -> R.drawable.ic_document_symbol
                    else -> R.drawable.ic_document_symbol_2
                }
                dokumenList.removeAll { it.title == kategori }
                dokumenList.add(
                    DokumenModel(
                        id = System.currentTimeMillis().toString(),
                        iconRes = icon,
                        title = kategori,
                        fileName = namaFile,
                        fileInfo = "PDF Ã‚· $ukuran Ã‚· Baru saja",
                        localUri = uri
                    )
                )
                terakhirDiperbarui = "Hari ini"
                showUploadDialog = false

                feedbackIsSuccess = true
                feedbackTitle = "Dokumen Berhasil Disimpan"
                feedbackMessage = "Berkas $namaFile untuk $kategori berhasil diunggah dan disimpan ke profil."
                showFeedbackDialog = true
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
                        PreviewInfoRow(label = "Nama Mahasiswa", value = "Budi Santoso")
                        PreviewInfoRow(label = "NIM", value = "2023110045")
                        PreviewInfoRow(label = "Program Studi", value = "Teknik Informatika")
                        PreviewInfoRow(label = "Nama Berkas", value = doc.fileName)
                        PreviewInfoRow(label = "Informasi File", value = doc.fileInfo)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Isi Konten Simulasi Dokumen Sesuai Jenisnya
                    Text(
                        text = "Ringkasan Muatan Dokumen:",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LenteraNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val detailContent = when (doc.title) {
                        "Curriculum Vitae (CV)" ->
                            "• Ringkasan Profesional: Mahasiswa aktif Teknik Informatika berfokus pada UI/UX Design & Front-End Android Development.\n" +
                            "• Keahlian Utama: Jetpack Compose, Figma, Kotlin, REST API, Git Version Control.\n" +
                            "• Pengalaman Organisasi: Koordinator Divisi Desain Himpunan Mahasiswa Informatika (2024–2025).\n" +
                            "• Riwayat Pelatihan: Google Developer Student Clubs UI/UX Intensive Bootcamp."
                        "Transkrip Nilai" ->
                            "• Indeks Prestasi Kumulatif (IPK): 3.85 / 4.00 (Cum Laude)\n" +
                            "• Total SKS Lulus: 84 SKS (Semester 1–4)\n" +
                            "• Mata Kuliah Relevan: Pemrograman Berbasis Bergerak (A), Rekayasa Perangkat Lunak (A), Interaksi Manusia & Komputer (A)."
                        "Portofolio" ->
                            "• Proyek 1: Redesign UI Aplikasi Mobile LENTERA Kampus (Figma & Compose UI Prototype)\n" +
                            "• Proyek 2: Website Portal Career & Magang Terpadu\n" +
                            "• Link Portofolio Daring: https://behance.net/budisantoso"
                        "Kartu Tanda Mahasiswa (KTM)" ->
                            "• Nomor Induk: 2023110045\n• Status: Terdaftar & Terverifikasi Aktif Semester Berjalan\n• Masa Berlaku: Hingga September 2027"
                        else ->
                            "Dokumen resmi ini telah diverifikasi dan siap diteruskan sebagai kelengkapan berkas magang mahasiswa."
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
    var fileSizeDisplay by remember { mutableStateOf("520 KB") }

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

            // 6 Kategori Baku
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
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
                            "${sanitizedCat}_Budi_Santoso.pdf"
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
