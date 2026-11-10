package com.PPBM.lentera.core.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.PPBM.lentera.R

@Composable
fun LenteraNotificationBell(
    hasUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    tint: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bellTransition")

    val rotation by if (hasUnread) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2600
                    0f at 0
                    -14f at 120 using FastOutSlowInEasing
                    14f at 240 using FastOutSlowInEasing
                    -10f at 360 using FastOutSlowInEasing
                    10f at 480 using FastOutSlowInEasing
                    -4f at 600 using FastOutSlowInEasing
                    4f at 720 using FastOutSlowInEasing
                    0f at 840 using FastOutSlowInEasing
                    0f at 2600 // Jeda tenang sebelum bergetar kembali
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "bellRotation"
        )
    } else {
        rememberUpdatedState(0f)
    }

    val badgeScale by if (hasUnread) {
        infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "badgePulse"
        )
    } else {
        rememberUpdatedState(1f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bell_outline),
                contentDescription = "Notifikasi",
                tint = tint,
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
            )

            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(8.dp)
                        .graphicsLayer {
                            scaleX = badgeScale
                            scaleY = badgeScale
                        }
                        .background(Color(0xFFEF4444), CircleShape)
                )
            }
        }
    }
}
