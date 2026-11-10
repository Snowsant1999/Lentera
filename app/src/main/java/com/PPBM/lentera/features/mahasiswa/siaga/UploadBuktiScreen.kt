package com.PPBM.lentera.features.mahasiswa.siaga
import com.PPBM.lentera.core.designsystem.icons.*
import com.PPBM.lentera.core.designsystem.illustrations.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

// ============================================================================
// Model & Data
// ============================================================================

data class BuktiFileItem(
    val id: String,
    val fileName: String,
    val fileType: String,
    val fileSize: String,
    val isExcluded: Boolean = false
)

// ============================================================================
// Main Screen: UploadBuktiScreen
// ============================================================================

/**
 * Halaman Upload Bukti — Langkah 3 dari 4
 * Sesuai desain UI Bukti.png
 *
 * @param initialFiles Daftar file bukti yang telah diunggah
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onUploadBoxClick Aksi saat kotak dropzone/upload ditekan
 * @param onDeleteFile Aksi saat tombol sampah ditekan
 * @param onRestoreFile Aksi saat tombol restore (undo) ditekan
 * @param onKembaliClick Aksi tombol "Kembali"
 * @param onLanjutClick Aksi tombol "Lanjut" dengan membawa daftar file terbaru
 */
@Composable
fun UploadBuktiScreen(
    initialFiles: List<BuktiFileItem> = emptyList(),
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onUploadBoxClick: () -> Unit = {},
    onDeleteFile: (BuktiFileItem) -> Unit = {},
    onRestoreFile: (BuktiFileItem) -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onLanjutClick: (List<BuktiFileItem>) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var filesList by remember { mutableStateOf(initialFiles) }
    val originalSizes = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(initialFiles) {
        initialFiles.forEach { item ->
            if (!item.isExcluded && !item.fileSize.startsWith("Dihapus")) {
                originalSizes[item.id] = item.fileSize
            }
        }
        filesList = initialFiles
    }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            BottomNavActionButtons(
                onKembali = onKembaliClick,
                onLanjut = { onLanjutClick(filesList) },
                fontFamily = plusJakarta
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Bar
            TopBarUploadBukti(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            // 2. Stepper / Progress Bar (Langkah 3 dari 4)
            StepperProgressSectionBukti(
                currentStep = 3,
                totalSteps = 4,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Dropzone / Upload Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ErrorRedLight)
                            .border(1.dp, ErrorRed.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ErrorRed
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                UploadDropzoneBox(
                    onClick = onUploadBoxClick,
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Judul "Bukti terunggah"
                Text(
                    text = "Bukti terunggah",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Daftar File Bukti
                if (filesList.isEmpty()) {
                    Text(
                        text = "Belum ada berkas bukti yang dipilih. Langkah ini bersifat opsional.",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 13.sp,
                            color = SlateGray
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                } else {
                    filesList.forEach { fileItem ->
                        BuktiFileCard(
                            item = fileItem,
                            onDeleteClick = {
                                if (!fileItem.isExcluded) {
                                    originalSizes[fileItem.id] = fileItem.fileSize
                                }
                                filesList = filesList.map {
                                    if (it.id == fileItem.id) it.copy(
                                        isExcluded = true,
                                        fileSize = "Dihapus · tidak disertakan"
                                    ) else it
                                }
                                onDeleteFile(fileItem)
                            },
                            onRestoreClick = {
                                val restoredSize = originalSizes[fileItem.id] ?: fileItem.fileSize
                                filesList = filesList.map {
                                    if (it.id == fileItem.id) it.copy(
                                        isExcluded = false,
                                        fileSize = restoredSize
                                    ) else it
                                }
                                onRestoreFile(fileItem)
                            },
                            fontFamily = plusJakarta
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Banner Keamanan & Kerahasiaan Tim
                SecurityNoticeBanner(fontFamily = plusJakarta)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Alias composable
@Composable
fun BuktiScreen(
    onBackClick: () -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onLanjutClick: (List<BuktiFileItem>) -> Unit = {}
) {
    UploadBuktiScreen(
        onBackClick = onBackClick,
        onKembaliClick = onKembaliClick,
        onLanjutClick = onLanjutClick
    )
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarUploadBukti(
    onBackClick: () -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            BackArrowIcon(
                modifier = Modifier.size(24.dp),
                tint = NavyDark
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Upload Bukti",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark,
                letterSpacing = 0.3.sp
            )
        )
    }
}

// ============================================================================
// Stepper / Progress Bar (Langkah 3 dari 4)
// ============================================================================

@Composable
private fun StepperProgressSectionBukti(
    currentStep: Int = 3,
    totalSteps: Int = 4,
    fontFamily: FontFamily
) {
    val stepTitles = listOf("Kategori", "Detail", "Bukti", "Tinjau")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Langkah $currentStep dari $totalSteps",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = SiagaTealPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            stepTitles.forEachIndexed { index, title ->
                val stepNum = index + 1
                val isCompleted = stepNum < currentStep
                val isActive = stepNum == currentStep

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lingkaran Step
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> SiagaTealPrimary
                                    isActive -> SiagaAmberEmergency
                                    else -> Color(0xFFE2EBF1)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            CheckIcon(
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        } else {
                            Text(
                                text = "$stepNum",
                                style = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) Color.White else SlateGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Label Step
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isActive -> NavyDark
                                isCompleted -> SlateGray
                                else -> SlateGrayLight
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Garis Indikator di Bawah Tiap Step
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isCompleted -> SiagaTealPrimary
                                    isActive -> SiagaAmberEmergency
                                    else -> Color(0xFFE2EBF1)
                                }
                            )
                    )
                }
            }
        }
    }
}

// ============================================================================
// Dropzone / Upload Box dengan Garis Putus-Putus
// ============================================================================

@Composable
private fun UploadDropzoneBox(
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    val cornerRadius = 18.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(SiagaTealBannerBg)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.6.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
                )
                drawRoundRect(
                    color = Color(0xFF267375),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = stroke
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ikon Awan Upload
            CloudUploadIcon(
                modifier = Modifier.size(46.dp),
                tint = SiagaTealPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Teks Utama Dropzone
            Text(
                text = "Ketuk untuk unggah foto, video, atau dokumen",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle Ukuran Maksimal
            Text(
                text = "Opsional · maks. 20 MB",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

// ============================================================================
// Kartu Item Bukti Terunggah
// ============================================================================

@Composable
private fun BuktiFileCard(
    item: BuktiFileItem,
    onDeleteClick: () -> Unit,
    onRestoreClick: () -> Unit,
    fontFamily: FontFamily
) {
    val isExcluded = item.isExcluded

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExcluded) Color(0xFFE2EBF1) else CardWhite
        ),
        border = if (isExcluded) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExcluded) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Dokumen File
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isExcluded) Color(0xFFD4DFE7) else SiagaTealBannerBg
                    ),
                contentAlignment = Alignment.Center
            ) {
                FileDocIcon(
                    modifier = Modifier.size(24.dp),
                    tint = if (isExcluded) SlateGray else SiagaTealPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Info Nama File & Ukuran
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.fileName,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isExcluded) {
                        item.fileSize
                    } else {
                        "${item.fileType} · ${item.fileSize}"
                    },
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = SlateGrayLight
                    )
                )
            }

            // Tombol Aksi Kanan (Hapus atau Kembalikan)
            if (isExcluded) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRestoreClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    RestoreIcon(
                        modifier = Modifier.size(22.dp),
                        tint = NavyDark
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDeleteClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TrashIcon(
                        modifier = Modifier.size(22.dp),
                        tint = SlateGray
                    )
                }
            }
        }
    }
}

// ============================================================================
// Banner Informasi Keamanan & Akses Terbatas
// ============================================================================

@Composable
private fun SecurityNoticeBanner(
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SiagaTealBannerBg)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Gembok Terkunci
            LockIcon(
                modifier = Modifier.size(24.dp),
                tint = SiagaTealBannerText
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Hanya tim penanganan yang dapat mengakses bukti laporanmu.",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = SiagaTealBannerText,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

// ============================================================================
// Bottom Action Buttons (Kembali & Lanjut)
// ============================================================================

@Composable
private fun BottomNavActionButtons(
    onKembali: () -> Unit,
    onLanjut: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tombol "Kembali" (Outlined)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
                    .border(1.5.dp, SiagaTealPrimary, RoundedCornerShape(16.dp))
                    .clickable(onClick = onKembali),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kembali",
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SiagaTealPrimary
                    )
                )
            }

            // Tombol "Lanjut" (Filled)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealPrimary)
                    .clickable(onClick = onLanjut),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lanjut",
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

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// Catatan: Ikon di bawah dapat Anda ganti dengan painterResource / Icon bawaan
// jika aset SVG / PNG resmi sudah siap.
// ============================================================================






// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UploadBuktiScreenPreview() {
    LenteraTheme {
        UploadBuktiScreen()
    }
}
