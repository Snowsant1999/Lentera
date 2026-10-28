package com.PPBM.lentera.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.PPBM.lentera.ui.theme.NavyDark
import com.PPBM.lentera.ui.theme.NavyMedium
import com.PPBM.lentera.ui.theme.SlateGray

/**
 * Komponen icon berbasis Compose Canvas murni.
 * Tidak memerlukan asset eksternal baru ataupun library tambahan.
 */

@Composable
fun BackArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        val w = size.width
        val h = size.height
        val midY = h / 2f
        val startX = w * 0.28f
        val endX = w * 0.72f
        val arrowSize = w * 0.22f

        // Garis horizontal panah
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(endX, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Sudut panah kiri atas
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(startX + arrowSize, midY - arrowSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Sudut panah kiri bawah
        drawLine(
            color = tint,
            start = Offset(startX, midY),
            end = Offset(startX + arrowSize, midY + arrowSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyMedium
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.6.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth / 2f)
        val center = Offset(size.width / 2f, size.height / 2f)

        // Lingkaran luar
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Titik atas huruf 'i'
        drawCircle(
            color = tint,
            radius = strokeWidth * 0.75f,
            center = Offset(center.x, center.y - r * 0.45f)
        )

        // Batang tegak huruf 'i'
        drawLine(
            color = tint,
            start = Offset(center.x, center.y - r * 0.15f),
            end = Offset(center.x, center.y + r * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun MailIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.6.dp.toPx()
        val w = size.width
        val h = size.height
        val pad = strokeWidth

        val rectWidth = w - (pad * 2)
        val rectHeight = (h * 0.72f)
        val top = (h - rectHeight) / 2f

        // Kotak amplop
        drawRoundRect(
            color = tint,
            topLeft = Offset(pad, top),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Lipatan 'V' amplop
        val path = Path().apply {
            moveTo(pad, top)
            lineTo(w / 2f, top + (rectHeight * 0.58f))
            lineTo(w - pad, top)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
