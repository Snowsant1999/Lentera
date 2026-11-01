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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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

data class DokumentasiData(
    val judul: String = "Rapat Koordinasi",
    val tanggal: String = "14 Oktober 2026",
    val totalFoto: Int = 4,
    val deskripsiPeringatan: String = "Dokumentasi ini hanya dapat dilihat oleh mahasiswa pelapor dan tim penanganan yang berwenang."
)

// ============================================================================
// Main Screen: DokumentasiScreen
// ============================================================================

/**
 * Halaman Dokumentasi Penanganan Laporan
 * Sesuai desain UI Dokumentasi.png
 *
 * Catatan: Area gambar utama dan thumbnail menggunakan placeholder visual
 * yang siap Anda ganti dengan Image / AsyncImage (Coil/Glide) secara manual.
 *
 * @param data Data dokumentasi
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onThumbnailClick Aksi saat thumbnail foto ditekan
 */
@Composable
fun DokumentasiScreen(
    data: DokumentasiData = DokumentasiData(),
    onBackClick: () -> Unit = {},
    onThumbnailClick: (Int) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    var currentPhotoIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top Bar
            TopBarDokumentasi(
                onBackClick = onBackClick,
                fontFamily = plusJakarta
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // 2. Foto Utama / Gambar Placeholder dengan Navigasi Carousel
                MainImageCarouselPlaceholder(
                    currentIndex = currentPhotoIndex,
                    totalPhotos = data.totalFoto,
                    onPrevClick = {
                        if (currentPhotoIndex > 0) currentPhotoIndex--
                    },
                    onNextClick = {
                        if (currentPhotoIndex < data.totalFoto - 1) currentPhotoIndex++
                    },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Indikator Titik Pager
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isActive = index == currentPhotoIndex.coerceAtMost(2)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isActive) 7.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) NavyDark else SlateGrayLight
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Judul & Tanggal Kegiatan
                Text(
                    text = data.judul,
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SiagaCalendarIcon(
                        modifier = Modifier.size(20.dp),
                        tint = SiagaTealPrimary
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = data.tanggal,
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Banner Privasi Dokumentasi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF4E7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF7E4CB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        SiagaLockIcon(
                            modifier = Modifier.size(24.dp),
                            tint = NavyDark
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = data.deskripsiPeringatan,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = SlateGray,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Galeri Thumbnail di Bagian Bawah
                ThumbnailGalleryRow(
                    onThumbnailClick = { idx ->
                        currentPhotoIndex = idx
                        onThumbnailClick(idx)
                    },
                    fontFamily = plusJakarta
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

// Alias composable
@Composable
fun Dokumentasi(
    onBackClick: () -> Unit = {}
) {
    DokumentasiScreen(onBackClick = onBackClick)
}

// ============================================================================
// Top App Bar
// ============================================================================

@Composable
private fun TopBarDokumentasi(
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
            text = "DOKUMENTASI",
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDark,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ============================================================================
// Placeholder Gambar Utama dengan Navigasi Carousel
// ============================================================================

@Composable
private fun MainImageCarouselPlaceholder(
    currentIndex: Int,
    totalPhotos: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2C394B))
    ) {
        // Konten placeholder: siap diganti dengan AsyncImage atau Image
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SiagaImagePlaceholderIcon(
                modifier = Modifier.size(54.dp),
                tint = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Foto Dokumentasi ${currentIndex + 1}",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ruang Rapat Koordinasi Tim Penanganan",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.5.sp,
                    color = Color.White.copy(alpha = 0.70f),
                    textAlign = TextAlign.Center
                )
            )
        }

        // Badge Nomor Foto (1/4) di pojok kanan atas
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${currentIndex + 1}/$totalPhotos",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        // Tombol Panah Kiri (<)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
                .size(38.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onPrevClick),
            contentAlignment = Alignment.Center
        ) {
            SiagaChevronLeftIcon(
                modifier = Modifier.size(18.dp),
                tint = SlateGray
            )
        }

        // Tombol Panah Kanan (>)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(38.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onNextClick),
            contentAlignment = Alignment.Center
        ) {
            SiagaChevronRightIcon(
                modifier = Modifier.size(18.dp),
                tint = SlateGray
            )
        }
    }
}

// ============================================================================
// Galeri Thumbnail Bawah
// ============================================================================

@Composable
private fun ThumbnailGalleryRow(
    onThumbnailClick: (Int) -> Unit,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail 1
        ThumbnailItemBox(
            modifier = Modifier.weight(1f),
            label = "Foto 1",
            bgColor = Color(0xFF334257),
            onClick = { onThumbnailClick(0) }
        )

        // Thumbnail 2
        ThumbnailItemBox(
            modifier = Modifier.weight(1f),
            label = "Foto 2",
            bgColor = Color(0xFF476072),
            onClick = { onThumbnailClick(1) }
        )

        // Thumbnail 3
        ThumbnailItemBox(
            modifier = Modifier.weight(1f),
            label = "Foto 3",
            bgColor = Color(0xFF548CA8),
            onClick = { onThumbnailClick(2) }
        )

        // Thumbnail 4: "+2" Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardWhite)
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                .clickable { onThumbnailClick(3) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+2",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
            )
        }
    }
}

@Composable
private fun ThumbnailItemBox(
    modifier: Modifier = Modifier,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        SiagaImagePlaceholderIcon(
            modifier = Modifier.size(26.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// ============================================================================

@Composable
fun SiagaImagePlaceholderIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Kotak foto
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.16f),
            size = Size(w * 0.76f, h * 0.68f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Matahari di pojok kanan atas
        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = Offset(w * 0.64f, h * 0.36f),
            style = Stroke(width = strokeWidth)
        )

        // Siluet bukit / gunung
        val mountain = Path().apply {
            moveTo(w * 0.12f, h * 0.74f)
            lineTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.52f, h * 0.66f)
            lineTo(w * 0.68f, h * 0.44f)
            lineTo(w * 0.88f, h * 0.74f)
        }
        drawPath(
            path = mountain,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SiagaChevronLeftIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.60f, h * 0.25f),
            end = Offset(w * 0.35f, h * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.50f),
            end = Offset(w * 0.60f, h * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SiagaChevronRightIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.40f, h * 0.25f),
            end = Offset(w * 0.65f, h * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.65f, h * 0.50f),
            end = Offset(w * 0.40f, h * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DokumentasiScreenPreview() {
    LenteraTheme {
        DokumentasiScreen()
    }
}
