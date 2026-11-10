package com.PPBM.lentera.core.designsystem.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun PeopleIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Figur depan
        drawCircle(
            color = tint,
            radius = w * 0.15f,
            center = Offset(w * 0.50f, h * 0.32f),
            style = Stroke(width = strokeW)
        )
        drawArc(
            color = tint,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.20f, h * 0.50f),
            size = Size(w * 0.60f, h * 0.40f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun HandStopIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Telapak tangan
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.80f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.32f)
            cubicTo(w * 0.30f, h * 0.26f, w * 0.38f, h * 0.26f, w * 0.38f, h * 0.32f)
            lineTo(w * 0.38f, h * 0.48f)

            // Jari tengah
            moveTo(w * 0.38f, h * 0.30f)
            lineTo(w * 0.38f, h * 0.22f)
            cubicTo(w * 0.38f, h * 0.16f, w * 0.46f, h * 0.16f, w * 0.46f, h * 0.22f)
            lineTo(w * 0.46f, h * 0.48f)

            // Jari manis
            moveTo(w * 0.46f, h * 0.32f)
            lineTo(w * 0.46f, h * 0.24f)
            cubicTo(w * 0.46f, h * 0.18f, w * 0.54f, h * 0.18f, w * 0.54f, h * 0.24f)
            lineTo(w * 0.54f, h * 0.50f)

            // Jari kelingking
            moveTo(w * 0.54f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.32f)
            cubicTo(w * 0.54f, h * 0.26f, w * 0.62f, h * 0.26f, w * 0.62f, h * 0.32f)
            lineTo(w * 0.62f, h * 0.60f)

            // Telapak bawah & jempol
            cubicTo(w * 0.62f, h * 0.74f, w * 0.54f, h * 0.84f, w * 0.44f, h * 0.84f)
            lineTo(w * 0.30f, h * 0.84f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ScaleIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Tiang tengah
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.18f),
            end = Offset(w * 0.50f, h * 0.82f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        // Alas bawah
        drawLine(
            color = tint,
            start = Offset(w * 0.32f, h * 0.82f),
            end = Offset(w * 0.68f, h * 0.82f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        // Palang timbangan atas
        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.32f),
            end = Offset(w * 0.82f, h * 0.32f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Mangkuk timbangan kiri
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.32f), end = Offset(w * 0.12f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.32f), end = Offset(w * 0.24f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawArc(color = tint, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.12f, h * 0.46f), size = Size(w * 0.12f, h * 0.16f), style = Stroke(width = strokeW * 0.8f))

        // Mangkuk timbangan kanan
        drawLine(color = tint, start = Offset(w * 0.82f, h * 0.32f), end = Offset(w * 0.76f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawLine(color = tint, start = Offset(w * 0.82f, h * 0.32f), end = Offset(w * 0.88f, h * 0.56f), strokeWidth = strokeW * 0.8f)
        drawArc(color = tint, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(w * 0.76f, h * 0.46f), size = Size(w * 0.12f, h * 0.16f), style = Stroke(width = strokeW * 0.8f))
    }
}
