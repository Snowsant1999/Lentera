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
fun ChevronLeftIcon(
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
fun ChevronRightIcon(
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

@Composable
fun ChevronDownIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val startY = if (isExpanded) h * 0.65f else h * 0.35f
        val midY = if (isExpanded) h * 0.35f else h * 0.65f

        drawLine(
            color = tint,
            start = Offset(w * 0.25f, startY),
            end = Offset(w * 0.50f, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, midY),
            end = Offset(w * 0.75f, startY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.20f),
            end = Offset(w * 0.80f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.80f, h * 0.20f),
            end = Offset(w * 0.20f, h * 0.80f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ArrowForwardIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.15f, h * 0.50f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.55f, h * 0.20f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.55f, h * 0.80f),
            end = Offset(w * 0.85f, h * 0.50f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun PlusIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val w = size.width
        val h = size.height

        // Garis horizontal
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.5f),
            end = Offset(w * 0.80f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Garis vertikal
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.20f),
            end = Offset(w * 0.5f, h * 0.80f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()
        val r = w * 0.32f
        val center = Offset(w * 0.42f, h * 0.42f)

        // Lingkaran kaca pembesar
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeW)
        )

        // Gagang kaca pembesar
        val handleStart = Offset(center.x + (r * 0.707f), center.y + (r * 0.707f))
        val handleEnd = Offset(w * 0.88f, h * 0.88f)
        drawLine(
            color = tint,
            start = handleStart,
            end = handleEnd,
            strokeWidth = strokeW * 1.1f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.16f, h * 0.50f)
            lineTo(w * 0.86f, h * 0.14f)
            lineTo(w * 0.50f, h * 0.86f)
            lineTo(w * 0.42f, h * 0.56f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis lipatan pesawat kertas
        drawLine(
            color = tint,
            start = Offset(w * 0.42f, h * 0.56f),
            end = Offset(w * 0.86f, h * 0.14f),
            strokeWidth = strokeWidth * 0.9f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun MoreDotsIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = 2.2.dp.toPx()

        drawCircle(color = tint, radius = r, center = Offset(w * 0.25f, h * 0.50f))
        drawCircle(color = tint, radius = r, center = Offset(w * 0.50f, h * 0.50f))
        drawCircle(color = tint, radius = r, center = Offset(w * 0.75f, h * 0.50f))
    }
}

@Composable
fun CopyIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
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

@Composable
fun TrashIcon(
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
fun RestoreIcon(
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
fun CloudUploadIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
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
fun FileDocIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
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
fun ReportFileIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        // Balon dokumen persegi
        val path = Path().apply {
            val left = w * 0.16f
            val top = h * 0.16f
            val right = w * 0.84f
            val bottom = h * 0.74f

            moveTo(left, top)
            lineTo(right, top)
            lineTo(right, bottom)
            lineTo(w * 0.46f, bottom)
            lineTo(w * 0.28f, h * 0.88f)
            lineTo(w * 0.32f, bottom)
            lineTo(left, bottom)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis horizontal teks dokumen
        drawLine(
            color = tint,
            start = Offset(w * 0.30f, h * 0.36f),
            end = Offset(w * 0.70f, h * 0.36f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.30f, h * 0.52f),
            end = Offset(w * 0.60f, h * 0.52f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ListIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        val rows = listOf(0.28f, 0.50f, 0.72f)
        for (yRel in rows) {
            val y = h * yRel
            // Titik / bullet
            drawCircle(
                color = tint,
                radius = strokeW * 0.9f,
                center = Offset(w * 0.22f, y)
            )
            // Baris garis teks
            drawLine(
                color = tint,
                start = Offset(w * 0.38f, y),
                end = Offset(w * 0.84f, y),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun ImagePlaceholderIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
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
fun MusicNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Batang not musik
        drawLine(
            color = tint,
            start = Offset(w * 0.65f, h * 0.20f),
            end = Offset(w * 0.65f, h * 0.68f),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )

        // Kepala not musik
        drawCircle(
            color = tint,
            radius = w * 0.16f,
            center = Offset(w * 0.52f, h * 0.68f)
        )

        // Bendera not musik atas
        val flag = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.34f)
            close()
        }
        drawPath(path = flag, color = tint)
    }
}

@Composable
fun MailIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyDark
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
