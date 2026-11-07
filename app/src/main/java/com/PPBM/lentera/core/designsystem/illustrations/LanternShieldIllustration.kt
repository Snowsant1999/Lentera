package com.PPBM.lentera.core.designsystem.illustrations

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun LanternShieldIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Cahaya keemasan lentera di latar kiri
        drawCircle(
            color = Color(0xFFFFECC8),
            radius = w * 0.22f,
            center = Offset(w * 0.32f, h * 0.38f),
            style = Fill
        )

        // Perisai pelindung di sebelah kanan
        val shieldPath = Path().apply {
            moveTo(w * 0.70f, h * 0.22f)
            cubicTo(w * 0.88f, h * 0.22f, w * 0.94f, h * 0.28f, w * 0.94f, h * 0.38f)
            lineTo(w * 0.94f, h * 0.58f)
            cubicTo(w * 0.94f, h * 0.76f, w * 0.82f, h * 0.86f, w * 0.70f, h * 0.90f)
            cubicTo(w * 0.58f, h * 0.86f, w * 0.46f, h * 0.76f, w * 0.46f, h * 0.58f)
            lineTo(w * 0.46f, h * 0.38f)
            cubicTo(w * 0.46f, h * 0.28f, w * 0.52f, h * 0.22f, w * 0.70f, h * 0.22f)
            close()
        }
        drawPath(
            path = shieldPath,
            color = Color(0xFFF1F7FA),
            style = Fill
        )
        drawPath(
            path = shieldPath,
            color = Color(0xFF173B57),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Centang hijau teal di dalam perisai
        val check = Path().apply {
            moveTo(w * 0.60f, h * 0.54f)
            lineTo(w * 0.67f, h * 0.64f)
            lineTo(w * 0.82f, h * 0.42f)
        }
        drawPath(
            path = check,
            color = Color(0xFF267375),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Figur Pelapor / Mahasiswa di tengah-kiri
        // Kepala
        drawCircle(
            color = Color(0xFFFFDAB9),
            radius = w * 0.08f,
            center = Offset(w * 0.44f, h * 0.24f),
            style = Fill
        )
        // Rambut hitam
        drawArc(
            color = Color(0xFF1A2634),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.36f, h * 0.16f),
            size = Size(w * 0.16f, h * 0.14f),
            style = Fill
        )

        // Blazer teal
        val body = Path().apply {
            moveTo(w * 0.36f, h * 0.32f)
            lineTo(w * 0.52f, h * 0.32f)
            lineTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.34f, h * 0.60f)
            close()
        }
        drawPath(path = body, color = Color(0xFF267375), style = Fill)
        drawPath(path = body, color = Color(0xFF173B57), style = Stroke(width = 1.8.dp.toPx()))

        // Lentera di tangan kiri
        val lantern = Path().apply {
            moveTo(w * 0.28f, h * 0.34f)
            lineTo(w * 0.36f, h * 0.34f)
            lineTo(w * 0.38f, h * 0.52f)
            lineTo(w * 0.26f, h * 0.52f)
            close()
        }
        drawPath(path = lantern, color = Color(0xFFFFB74D), style = Fill)
        drawPath(path = lantern, color = Color(0xFF795548), style = Stroke(width = 1.8.dp.toPx()))

        // Gagang lentera
        drawArc(
            color = Color(0xFF795548),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.29f, h * 0.26f),
            size = Size(w * 0.06f, h * 0.08f),
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SuccessShieldIllustration(
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
