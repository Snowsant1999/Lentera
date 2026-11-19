package com.PPBM.lentera.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PPBM.lentera.core.designsystem.icons.CheckIcon
import com.PPBM.lentera.core.designsystem.icons.CloseIcon
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

@Composable
fun LenteraFeedbackDialog(
    show: Boolean,
    isSuccess: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = if (isSuccess) "Selesai" else "Mengerti"
) {
    if (!show) return

    Dialog(onDismissRequest = onDismiss) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }
        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0.88f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            label = "dialogScale"
        )
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            label = "dialogAlpha"
        )

        Column(
            modifier = Modifier
                .widthIn(max = LenteraDialogMaxWidth)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon status (Bulat Hijau untuk sukses, Merah untuk gagal/salah)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                if (isSuccess) {
                    CheckIcon(
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF2E7D32)
                    )
                } else {
                    CloseIcon(
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFFD32F2F)
                    )
                }
            }

            // Title
            Text(
                text = title,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isSuccess) LenteraNavy else Color(0xFFC62828),
                textAlign = TextAlign.Center
            )

            // Message
            Text(
                text = message,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = LenteraTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) LenteraNavy else Color(0xFFD32F2F),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = buttonText,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

