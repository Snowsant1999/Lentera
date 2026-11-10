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
import com.PPBM.lentera.backend.mahasiswa.model.DokumenHasilItem
import com.PPBM.lentera.core.designsystem.theme.*
import java.util.Locale

// ============================================================================
// Model & Data
// ============================================================================

data class DokumentasiData(
    val judul: String = "",
    val tanggal: String = "",
    val totalFoto: Int = 0,
    val deskripsiPeringatan: String = "Dokumentasi ini hanya dapat dilihat oleh mahasiswa pelapor dan tim penanganan yang berwenang."
)

// ============================================================================
// Main Screen: DokumentasiScreen
// ============================================================================

/**
 * Halaman Dokumentasi Penanganan Laporan
 * Sesuai desain UI Dokumentasi.png
 *
 * @param data Ringkasan metadata dokumentasi
 * @param dokumenList Daftar dokumen hasil tindak lanjut yang boleh dibagikan ke pelapor
 * @param onBackClick Aksi kembali ke halaman sebelumnya
 * @param onThumbnailClick Aksi saat indeks item ditekan (opsional/legacy)
 * @param onOpenDokumenClick Aksi membuka berkas dokumentasi (via signed URL)
 */
@Composable
fun DokumentasiScreen(
    data: DokumentasiData = DokumentasiData(),
    dokumenList: List<DokumenHasilItem> = emptyList(),
    onBackClick: () -> Unit = {},
    onThumbnailClick: (Int) -> Unit = {},
    onOpenDokumenClick: (DokumenHasilItem) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

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
                if (data.judul.isNotBlank()) {
                    Text(
                        text = data.judul,
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (data.tanggal.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CalendarIcon(
                            modifier = Modifier.size(18.dp),
                            tint = SiagaTealPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.tanggal,
                            style = TextStyle(
                                fontFamily = plusJakarta,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyDark
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Banner Privasi Dokumentasi
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
                        LockIcon(
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

                Spacer(modifier = Modifier.height(20.dp))

                if (dokumenList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FileDocIcon(
                                modifier = Modifier.size(40.dp),
                                tint = SlateGrayLight
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada dokumentasi tindak lanjut yang dibagikan untuk laporan ini.",
                                style = TextStyle(
                                    fontFamily = plusJakarta,
                                    fontSize = 13.5.sp,
                                    color = SlateGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Berkas Dokumentasi (${dokumenList.size})",
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    dokumenList.forEachIndexed { index, doc ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        onThumbnailClick(index)
                                        onOpenDokumenClick(doc)
                                    },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(NavyDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        FileDocIcon(
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.namaFile,
                                            style = TextStyle(
                                                fontFamily = plusJakarta,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyDark
                                            ),
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "${doc.tipeFile} · ${formatDokumenSize(doc.ukuranBytes)} · Ketuk untuk membuka",
                                            style = TextStyle(
                                                fontFamily = plusJakarta,
                                                fontSize = 12.sp,
                                                color = SlateGrayLight
                                            )
                                        )
                                    }
                                }
                                ChevronRightIcon(
                                    modifier = Modifier.size(18.dp),
                                    tint = SiagaTealPrimary
                                )
                            }
                        }
                    }
                }

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
            BackArrowIcon(
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
// Preview
// ============================================================================

private fun formatDokumenSize(bytes: Long): String {
    return when {
        bytes >= 1024L * 1024L -> String.format(Locale.US, "%.1f MB", bytes.toDouble() / (1024.0 * 1024.0))
        bytes >= 1024L -> "${bytes / 1024L} KB"
        else -> "$bytes B"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DokumentasiScreenPreview() {
    LenteraTheme {
        DokumentasiScreen(
            data = DokumentasiData(
                judul = "Berita Acara & Dokumentasi Penanganan",
                tanggal = "14 Oktober 2026"
            ),
            dokumenList = listOf(
                DokumenHasilItem(
                    id = "1",
                    laporanId = "1",
                    namaFile = "berita_acara_penanganan.pdf",
                    tipeFile = "PDF",
                    ukuranBytes = 420_000,
                    filePath = "1/berita_acara_penanganan.pdf",
                    bolehDibagikan = true,
                    createdAt = "2026-10-14T10:00:00Z"
                )
            )
        )
    }
}
