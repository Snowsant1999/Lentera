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
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

@Composable
fun HomeIcon(
    modifier: Modifier = Modifier.size(22.dp),
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.16f)
            lineTo(w * 0.84f, h * 0.46f)
            lineTo(w * 0.84f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.82f)
            lineTo(w * 0.16f, h * 0.82f)
            lineTo(w * 0.16f, h * 0.46f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun BriefcaseIcon(
    modifier: Modifier = Modifier.size(22.dp),
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Badan tas
        val bodyRect = Path().apply {
            val left = w * 0.16f
            val top = h * 0.32f
            val right = w * 0.84f
            val bottom = h * 0.82f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(left + r, bottom)
            quadraticTo(left, bottom, left, bottom - r)
            lineTo(left, top + r)
            quadraticTo(left, top, left + r, top)
            close()
        }
        drawPath(
            path = bodyRect,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Gagang pegangan
        val handlePath = Path().apply {
            moveTo(w * 0.36f, h * 0.32f)
            lineTo(w * 0.36f, h * 0.20f)
            quadraticTo(w * 0.36f, h * 0.16f, w * 0.42f, h * 0.16f)
            lineTo(w * 0.58f, h * 0.16f)
            quadraticTo(w * 0.64f, h * 0.16f, w * 0.64f, h * 0.20f)
            lineTo(w * 0.64f, h * 0.32f)
        }
        drawPath(
            path = handlePath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Garis tengah tas
        drawLine(
            color = tint,
            start = Offset(w * 0.16f, h * 0.52f),
            end = Offset(w * 0.84f, h * 0.52f),
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Square
        )
    }
}

@Composable
fun ChatPlusIcon(
    modifier: Modifier = Modifier.size(22.dp),
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Balon pesan
        val path = Path().apply {
            val left = w * 0.16f
            val top = h * 0.18f
            val right = w * 0.84f
            val bottom = h * 0.72f
            val r = 4.dp.toPx()

            moveTo(left + r, top)
            lineTo(right - r, top)
            quadraticTo(right, top, right, top + r)
            lineTo(right, bottom - r)
            quadraticTo(right, bottom, right - r, bottom)
            lineTo(w * 0.42f, bottom)
            lineTo(w * 0.24f, h * 0.86f)
            lineTo(w * 0.28f, bottom)
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

        // '+' di dalam balon pesan
        val midX = w * 0.50f
        val midY = h * 0.45f
        val plusLen = w * 0.12f

        drawLine(
            color = tint,
            start = Offset(midX - plusLen, midY),
            end = Offset(midX + plusLen, midY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(midX, midY - plusLen),
            end = Offset(midX, midY + plusLen),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ProfileNavIcon(
    modifier: Modifier = Modifier.size(22.dp),
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.8.dp.toPx()
        val r = (size.minDimension / 2f) - (strokeWidth / 2f)
        val center = Offset(size.width / 2f, size.height / 2f)

        // Lingkaran luar
        drawCircle(
            color = tint,
            radius = r,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Kepala
        drawCircle(
            color = tint,
            radius = r * 0.32f,
            center = Offset(center.x, center.y - r * 0.18f),
            style = Stroke(width = strokeWidth)
        )

        // Garis bahu
        drawArc(
            color = tint,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - r * 0.65f, center.y + r * 0.15f),
            size = Size(r * 1.30f, r * 1.10f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AvatarIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = SlateGray
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Kepala
        drawCircle(
            color = tint,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.36f),
            style = Stroke(width = strokeWidth)
        )

        // Bahu / Torso
        drawArc(
            color = tint,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.16f, h * 0.54f),
            size = Size(w * 0.68f, h * 0.44f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BellIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = NavyDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Badan lonceng
        val bellPath = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            cubicTo(w * 0.38f, h * 0.22f, w * 0.26f, h * 0.42f, w * 0.24f, h * 0.64f)
            lineTo(w * 0.18f, h * 0.72f)
            cubicTo(w * 0.18f, h * 0.76f, w * 0.22f, h * 0.78f, w * 0.26f, h * 0.78f)
            lineTo(w * 0.74f, h * 0.78f)
            cubicTo(w * 0.78f, h * 0.78f, w * 0.82f, h * 0.76f, w * 0.82f, h * 0.72f)
            lineTo(w * 0.76f, h * 0.64f)
            cubicTo(w * 0.74f, h * 0.42f, w * 0.62f, h * 0.22f, w * 0.5f, h * 0.18f)
        }
        drawPath(
            path = bellPath,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Cincin atas lonceng
        drawCircle(
            color = tint,
            radius = w * 0.06f,
            center = Offset(w * 0.5f, h * 0.14f),
            style = Stroke(width = strokeWidth)
        )

        // Bandul bawah lonceng
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.42f, h * 0.78f),
            size = Size(w * 0.16f, h * 0.12f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PhoneIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.24f)
            cubicTo(w * 0.36f, h * 0.18f, w * 0.44f, h * 0.20f, w * 0.48f, h * 0.26f)
            lineTo(w * 0.52f, h * 0.38f)
            cubicTo(w * 0.54f, h * 0.44f, w * 0.50f, h * 0.48f, w * 0.44f, h * 0.52f)
            cubicTo(w * 0.48f, h * 0.60f, w * 0.54f, h * 0.66f, w * 0.62f, h * 0.70f)
            cubicTo(w * 0.66f, h * 0.64f, w * 0.70f, h * 0.60f, w * 0.76f, h * 0.62f)
            lineTo(w * 0.86f, h * 0.66f)
            cubicTo(w * 0.92f, h * 0.70f, w * 0.94f, h * 0.78f, w * 0.88f, h * 0.84f)
            cubicTo(w * 0.78f, h * 0.94f, w * 0.62f, h * 0.90f, w * 0.48f, h * 0.76f)
            cubicTo(w * 0.32f, h * 0.62f, w * 0.20f, h * 0.44f, w * 0.24f, h * 0.32f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}


@Composable
fun NavTabIcon(
    tab: com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab,
    modifier: Modifier = Modifier.size(20.dp),
    tint: Color = Color.White
) {
    val iconRes = when (tab) {
        com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab.BERANDA -> R.drawable.ic_house
        com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab.PENGADUAN -> R.drawable.ic_message_square_plus
        com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab.MAGANG_HUB -> R.drawable.ic_briefcase_business
        com.PPBM.lentera.features.mahasiswa.siaga.SiagaNavTab.PROFIL -> R.drawable.ic_profile_circle_thin
    }
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = tab.label,
        tint = tint,
        modifier = modifier
    )
}
