package com.PPBM.lentera.features.siaga

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
// Main Screen: LaporanTerkirimScreen
// ============================================================================

/**
 * Halaman Konfirmasi Laporan Berhasil Dikirim
 * Sesuai desain UI Terkirim.png
 *
 * @param nomorReferensi Nomor tiket/referensi laporan yang diterbitkan
 * @param onLihatStatusClick Aksi tombol "Lihat Status Laporan"
 * @param onKembaliKeBerandaClick Aksi tombol "Kembali ke Beranda"
 */
@Composable
fun LaporanTerkirimScreen(
    nomorReferensi: String = "LTR-2026-00123",
    onLihatStatusClick: () -> Unit = {},
    onKembaliKeBerandaClick: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Ilustrasi Perisai Sukses
            SiagaSuccessShieldIllustration(
                modifier = Modifier
                    .size(240.dp)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Judul "Laporan Berhasil Dikirim"
            Text(
                text = "Laporan Berhasil\nDikirim",
                style = TextStyle(
                    fontFamily = plusJakarta,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Deskripsi Keterangan
            Text(
                text = "Simpan nomor referensi ini untuk memantau laporanmu.",
                style = TextStyle(
                    fontFamily = plusJakarta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pill Box Nomor Referensi dengan Tombol Copy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SiagaTealBannerBg)
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Nomor Referensi", nomorReferensi)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Nomor referensi berhasil disalin", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = nomorReferensi,
                        style = TextStyle(
                            fontFamily = plusJakarta,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SiagaTealPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    SiagaCopyIcon(
                        modifier = Modifier.size(22.dp),
                        tint = SiagaTealPrimary
                    )
                }
            }
        }

        // Bagian Tombol Aksi & Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 24.dp)
        ) {
            // Tombol "Lihat Status Laporan" (Filled)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SiagaTealPrimary)
                    .clickable(onClick = onLihatStatusClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lihat Status Laporan",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tombol "Kembali ke Beranda" (Outlined)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
                    .border(1.5.dp, SiagaTealPrimary, RoundedCornerShape(16.dp))
                    .clickable(onClick = onKembaliKeBerandaClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kembali ke Beranda",
                    style = TextStyle(
                        fontFamily = plusJakarta,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SiagaTealPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Penutup
            Text(
                text = "Terima kasih sudah berani bercerita.",
                style = TextStyle(
                    fontFamily = plusJakarta,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = SlateGrayLight,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

// Alias composable
@Composable
fun TerkirimScreen(
    nomorReferensi: String = "LTR-2026-00123",
    onLihatStatusClick: () -> Unit = {},
    onKembaliKeBerandaClick: () -> Unit = {}
) {
    LaporanTerkirimScreen(
        nomorReferensi = nomorReferensi,
        onLihatStatusClick = onLihatStatusClick,
        onKembaliKeBerandaClick = onKembaliKeBerandaClick
    )
}

// ============================================================================
// Ikon & Aset Visual (Compose Canvas Murni)
// ============================================================================

/**
 * Ilustrasi Perisai Sukses dengan background biru muda dan centang putih tebal
 */
@Composable
fun SiagaSuccessShieldIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 3.5.dp.toPx()

        // Garis luar perisai (Dark Navy)
        val outerShield = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            cubicTo(w * 0.76f, h * 0.12f, w * 0.82f, h * 0.18f, w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.52f)
            cubicTo(w * 0.82f, h * 0.72f, w * 0.68f, h * 0.84f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.32f, h * 0.84f, w * 0.18f, h * 0.72f, w * 0.18f, h * 0.52f)
            lineTo(w * 0.18f, h * 0.28f)
            cubicTo(w * 0.18f, h * 0.18f, w * 0.24f, h * 0.12f, w * 0.50f, h * 0.12f)
            close()
        }

        // Fill perisai biru muda (#8EC5E3)
        val innerShield = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            cubicTo(w * 0.72f, h * 0.16f, w * 0.76f, h * 0.22f, w * 0.76f, h * 0.30f)
            lineTo(w * 0.76f, h * 0.50f)
            cubicTo(w * 0.76f, h * 0.68f, w * 0.64f, h * 0.78f, w * 0.50f, h * 0.84f)
            cubicTo(w * 0.36f, h * 0.78f, w * 0.24f, h * 0.68f, w * 0.24f, h * 0.50f)
            lineTo(w * 0.24f, h * 0.30f)
            cubicTo(w * 0.24f, h * 0.22f, w * 0.28f, h * 0.16f, w * 0.50f, h * 0.16f)
            close()
        }

        // Gambar fill biru
        drawPath(
            path = innerShield,
            color = Color(0xFF8EC5E3),
            style = Fill
        )

        // Gambar border luar dark navy
        drawPath(
            path = outerShield,
            color = Color(0xFF173B57),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Centang putih tebal di tengah
        val checkPath = Path().apply {
            moveTo(w * 0.36f, h * 0.50f)
            cubicTo(w * 0.40f, h * 0.54f, w * 0.44f, h * 0.60f, w * 0.48f, h * 0.64f)
            cubicTo(w * 0.54f, h * 0.52f, w * 0.60f, h * 0.40f, w * 0.66f, h * 0.34f)
        }
        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Ikon Salin / Copy (Dua lembar kertas bertumpuk)
 */
@Composable
fun SiagaCopyIcon(
    modifier: Modifier = Modifier,
    tint: Color = SiagaTealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Lembar belakang
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.30f, h * 0.12f),
            size = Size(w * 0.56f, h * 0.64f),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Lembar depan
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.14f, h * 0.28f),
            size = Size(w * 0.56f, h * 0.64f),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
    }
}

// ============================================================================
// Preview
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LaporanTerkirimScreenPreview() {
    LenteraTheme {
        LaporanTerkirimScreen()
    }
}
