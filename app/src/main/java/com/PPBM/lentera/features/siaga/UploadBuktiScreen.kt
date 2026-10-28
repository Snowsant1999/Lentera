package com.PPBM.lentera.features.siaga

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
import com.PPBM.lentera.ui.theme.*

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

val sampleBuktiList = listOf(
    BuktiFileItem(
        id = "1",
        fileName = "foto_koridor.jpg",
        fileType = "JPG",
        fileSize = "2,4 MB",
        isExcluded = false
    ),
    BuktiFileItem(
        id = "2",
        fileName = "kronologi_kejadian.pdf",
        fileType = "PDF",
        fileSize = "480 KB",
        isExcluded = false
    ),
    BuktiFileItem(
        id = "3",
        fileName = "rekaman_lama.mp4",
        fileType = "MP4",
        fileSize = "Dihapus · tidak disertakan",
        isExcluded = true
    )
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
    initialFiles: List<BuktiFileItem> = sampleBuktiList,
    onBackClick: () -> Unit = {},
    onUploadBoxClick: () -> Unit = {},
    onDeleteFile: (BuktiFileItem) -> Unit = {},
    onRestoreFile: (BuktiFileItem) -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onLanjutClick: (List<BuktiFileItem>) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var filesList by remember { mutableStateOf(initialFiles) }

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
                filesList.forEach { fileItem ->
                    BuktiFileCard(
                        item = fileItem,
                        onDeleteClick = {
                            filesList = filesList.map {
                                if (it.id == fileItem.id) it.copy(
                                    isExcluded = true,
                                    fileSize = "Dihapus · tidak disertakan"
                                ) else it
                            }
                            onDeleteFile(fileItem)
                        },
                        onRestoreClick = {
                            filesList = filesList.map {
                                if (it.id == fileItem.id) it.copy(
                                    isExcluded = false,
                                    fileSize = if (it.fileType == "MP4") "3,2 MB" else "2,4 MB"
                                ) else it
                            }
                            onRestoreFile(fileItem)
                        },
                        fontFamily = plusJakarta
                    )
                    Spacer(modifier = Modifier.height(12.dp))
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
            SiagaBackArrowIcon(
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
                            SiagaCheckIcon(
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
            SiagaCloudUploadIcon(
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
                SiagaFileDocIcon(
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
                    SiagaRestoreIcon(
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
                    SiagaTrashIcon(
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
            SiagaLockIcon(
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

@Composable
fun SiagaCloudUploadIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        // Garis lengkung awan
        val cloud = Path().apply {
            moveTo(w * 0.28f, h * 0.64f)
            cubicTo(w * 0.16f, h * 0.64f, w * 0.16f, h * 0.44f, w * 0.26f, h * 0.40f)
            cubicTo(w * 0.24f, h * 0.22f, w * 0.48f, h * 0.18f, w * 0.54f, h * 0.28f)
            cubicTo(w * 0.62f, h * 0.18f, w * 0.82f, h * 0.24f, w * 0.80f, h * 0.42f)
            cubicTo(w * 0.90f, h * 0.46f, w * 0.90f, h * 0.64f, w * 0.76f, h * 0.64f)
        }
        drawPath(
            path = cloud,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Batang panah ke atas
        val arrowBaseY = h * 0.78f
        val arrowTopY = h * 0.48f
        val arrowMidX = w * 0.50f
        val arrowHead = w * 0.12f

        drawLine(
            color = tint,
            start = Offset(arrowMidX, arrowBaseY),
            end = Offset(arrowMidX, arrowTopY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Sayap panah kiri
        drawLine(
            color = tint,
            start = Offset(arrowMidX, arrowTopY),
            end = Offset(arrowMidX - arrowHead, arrowTopY + arrowHead),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Sayap panah kanan
        drawLine(
            color = tint,
            start = Offset(arrowMidX, arrowTopY),
            end = Offset(arrowMidX + arrowHead, arrowTopY + arrowHead),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaFileDocIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val left = w * 0.22f
        val right = w * 0.78f
        val top = h * 0.14f
        val bottom = h * 0.86f
        val fold = w * 0.20f

        val path = Path().apply {
            moveTo(left, top)
            lineTo(right - fold, top)
            lineTo(right, top + fold)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Lipatan sudut dokumen
        val foldPath = Path().apply {
            moveTo(right - fold, top)
            lineTo(right - fold, top + fold)
            lineTo(right, top + fold)
        }
        drawPath(
            path = foldPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Baris teks dokumen
        val lineLeft = left + (w * 0.14f)
        val lineRight = right - (w * 0.14f)
        drawLine(
            color = tint,
            start = Offset(lineLeft, h * 0.48f),
            end = Offset(lineRight, h * 0.48f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(lineLeft, h * 0.62f),
            end = Offset(lineRight, h * 0.62f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(lineLeft, h * 0.74f),
            end = Offset(lineLeft + (w * 0.22f), h * 0.74f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaTrashIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Tutup tempat sampah
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.26f),
            end = Offset(w * 0.80f, h * 0.26f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Gagang tutup
        val handle = Path().apply {
            moveTo(w * 0.38f, h * 0.26f)
            lineTo(w * 0.38f, h * 0.16f)
            lineTo(w * 0.62f, h * 0.16f)
            lineTo(w * 0.62f, h * 0.26f)
        }
        drawPath(
            path = handle,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Badan tempat sampah
        val bin = Path().apply {
            moveTo(w * 0.28f, h * 0.26f)
            lineTo(w * 0.32f, h * 0.84f)
            cubicTo(w * 0.32f, h * 0.88f, w * 0.36f, h * 0.90f, w * 0.40f, h * 0.90f)
            lineTo(w * 0.60f, h * 0.90f)
            cubicTo(w * 0.64f, h * 0.90f, w * 0.68f, h * 0.88f, w * 0.68f, h * 0.84f)
            lineTo(w * 0.72f, h * 0.26f)
        }
        drawPath(
            path = bin,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis vertikal di badan tempat sampah
        drawLine(
            color = tint,
            start = Offset(w * 0.44f, h * 0.40f),
            end = Offset(w * 0.44f, h * 0.76f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.56f, h * 0.40f),
            end = Offset(w * 0.56f, h * 0.76f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaRestoreIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth * 1.5f)
        val center = Offset(w / 2f, h / 2f)

        // Busur lingkaran undo
        drawArc(
            color = tint,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(center.x - r, center.y - r),
            size = Size(r * 2f, r * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Kepala panah busur
        val arrowSize = w * 0.16f
        val arrowTipX = center.x + (r * 0.707f)
        val arrowTipY = center.y + (r * 0.707f)

        drawLine(
            color = tint,
            start = Offset(arrowTipX, arrowTipY),
            end = Offset(arrowTipX - arrowSize, arrowTipY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(arrowTipX, arrowTipY),
            end = Offset(arrowTipX, arrowTipY - arrowSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaLockIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealBannerText
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        // Lengkungan gembok atas
        val shackle = Path().apply {
            moveTo(w * 0.32f, h * 0.46f)
            lineTo(w * 0.32f, h * 0.30f)
            cubicTo(w * 0.32f, h * 0.14f, w * 0.68f, h * 0.14f, w * 0.68f, h * 0.30f)
            lineTo(w * 0.68f, h * 0.46f)
        }
        drawPath(
            path = shackle,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Badan kotak gembok
        val bodyLeft = w * 0.22f
        val bodyRight = w * 0.78f
        val bodyTop = h * 0.44f
        val bodyBottom = h * 0.88f
        val r = 4.dp.toPx()

        drawRoundRect(
            color = tint,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyRight - bodyLeft, bodyBottom - bodyTop),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = strokeWidth)
        )

        // Titik lubang kunci
        drawCircle(
            color = tint,
            radius = strokeWidth * 0.9f,
            center = Offset(w * 0.50f, h * 0.62f)
        )
        // Garis lubang kunci
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.62f),
            end = Offset(w * 0.50f, h * 0.74f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
    }
}

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
