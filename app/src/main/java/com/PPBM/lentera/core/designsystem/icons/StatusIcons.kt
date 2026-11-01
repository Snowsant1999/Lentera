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
fun CheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val checkPath = Path().apply {
            moveTo(w * 0.22f, h * 0.50f)
            lineTo(w * 0.42f, h * 0.70f)
            lineTo(w * 0.78f, h * 0.32f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth / 2f)
        val center = Offset(w / 2f, h / 2f)

        // Lingkaran jam
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Jarum pendek & jarum panjang
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x, center.y - (r * 0.55f)),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(center.x + (r * 0.45f), center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val top = h * 0.22f
        val bottom = h * 0.88f
        val left = w * 0.14f
        val right = w * 0.86f
        val r = 3.dp.toPx()

        // Kotak kalender
        drawRoundRect(
            color = tint,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = strokeWidth)
        )

        // Cincin penjepit atas (kiri & kanan)
        drawLine(
            color = tint,
            start = Offset(w * 0.34f, h * 0.10f),
            end = Offset(w * 0.34f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.66f, h * 0.10f),
            end = Offset(w * 0.66f, h * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Garis pemisah header kalender
        drawLine(
            color = tint,
            start = Offset(left, h * 0.42f),
            end = Offset(right, h * 0.42f),
            strokeWidth = strokeWidth * 0.85f
        )

        // Titik-titik tanggal di dalam kalender
        val dotR = strokeWidth * 0.65f
        val cols = listOf(0.32f, 0.50f, 0.68f)
        val rows = listOf(0.58f, 0.74f)
        for (col in cols) {
            for (row in rows) {
                drawCircle(
                    color = tint,
                    radius = dotR,
                    center = Offset(w * col, h * row)
                )
            }
        }
    }
}

@Composable
fun LocationPinIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Kontur pin lokasi
        val pinPath = Path().apply {
            moveTo(w * 0.5f, h * 0.88f)
            cubicTo(w * 0.36f, h * 0.64f, w * 0.20f, h * 0.48f, w * 0.20f, h * 0.36f)
            cubicTo(w * 0.20f, h * 0.18f, w * 0.34f, h * 0.12f, w * 0.5f, h * 0.12f)
            cubicTo(w * 0.66f, h * 0.12f, w * 0.80f, h * 0.18f, w * 0.80f, h * 0.36f)
            cubicTo(w * 0.80f, h * 0.48f, w * 0.64f, h * 0.64f, w * 0.5f, h * 0.88f)
            close()
        }
        drawPath(
            path = pinPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Titik lingkaran di tengah pin
        drawCircle(
            color = tint,
            radius = w * 0.10f,
            center = Offset(w * 0.5f, h * 0.36f),
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun LockIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
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

@Composable
fun ShieldCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        // Kontur perisai
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.14f)
            lineTo(w * 0.82f, h * 0.24f)
            cubicTo(w * 0.82f, h * 0.56f, w * 0.72f, h * 0.78f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.28f, h * 0.78f, w * 0.18f, h * 0.56f, w * 0.18f, h * 0.24f)
            close()
        }
        drawPath(
            path = shieldPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Centang di dalam perisai
        val checkPath = Path().apply {
            moveTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ShieldAlertIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        val shield = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            lineTo(w * 0.82f, h * 0.24f)
            cubicTo(w * 0.82f, h * 0.56f, w * 0.72f, h * 0.78f, w * 0.50f, h * 0.88f)
            cubicTo(w * 0.28f, h * 0.78f, w * 0.18f, h * 0.56f, w * 0.18f, h * 0.24f)
            close()
        }
        drawPath(
            path = shield,
            color = tint,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Tanda seru
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.34f),
            end = Offset(w * 0.50f, h * 0.54f),
            strokeWidth = strokeW * 1.1f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = tint,
            radius = strokeW * 0.8f,
            center = Offset(w * 0.50f, h * 0.66f)
        )
    }
}

@Composable
fun ExclamationCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val r = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Lingkaran hitam fill
        drawCircle(
            color = Color.Black,
            radius = r,
            center = center,
            style = Fill
        )

        // Batang tanda seru putih
        val strokeW = 2.2.dp.toPx()
        drawLine(
            color = Color.White,
            start = Offset(center.x, center.y - (r * 0.45f)),
            end = Offset(center.x, center.y + (r * 0.12f)),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Titik tanda seru putih
        drawCircle(
            color = Color.White,
            radius = strokeW * 0.75f,
            center = Offset(center.x, center.y + (r * 0.45f))
        )
    }
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
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
fun ChatOutlineIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val path = Path().apply {
            val left = w * 0.14f
            val top = h * 0.16f
            val right = w * 0.86f
            val bottom = h * 0.72f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(w * 0.40f, bottom)
            lineTo(w * 0.22f, h * 0.88f)
            lineTo(w * 0.26f, bottom)
            lineTo(left + r, bottom)
            quadraticTo(left, bottom, left, bottom - r)
            lineTo(left, top + r)
            quadraticTo(left, top, left + r, top)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
