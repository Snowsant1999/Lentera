package com.PPBM.lentera.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Klasifikasi lebar jendela sesuai Material 3 Window Size Classes untuk tata letak adaptif.
 */
enum class WindowWidthTier {
    Compact,
    Medium,
    Expanded
}

val LenteraDialogMaxWidth: Dp = 460.dp
val LenteraContentMaxWidth: Dp = 640.dp

internal fun windowWidthTierFor(screenWidthDp: Int): WindowWidthTier = when {
    screenWidthDp < 600 -> WindowWidthTier.Compact
    screenWidthDp < 840 -> WindowWidthTier.Medium
    else -> WindowWidthTier.Expanded
}

@Composable
fun rememberWindowWidthTier(): WindowWidthTier {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return windowWidthTierFor(screenWidthDp)
}

/**
 * Menghitung lebar kartu horizontal adaptif berdasarkan tier lebar layar dan rentang [minWidth]..[maxWidth].
 */
@Composable
fun rememberAdaptiveHorizontalCardWidth(
    compactFraction: Float = 0.86f,
    minWidth: Dp = 280.dp,
    maxWidth: Dp = 420.dp
): Dp {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val fraction = when (windowWidthTierFor(screenWidthDp)) {
        WindowWidthTier.Compact -> compactFraction
        WindowWidthTier.Medium -> (compactFraction * 0.65f).coerceAtLeast(0.45f)
        WindowWidthTier.Expanded -> (compactFraction * 0.48f).coerceAtLeast(0.32f)
    }
    val safeMax = maxOf(minWidth, maxWidth)
    return (screenWidthDp * fraction).dp.coerceIn(minWidth, safeMax)
}
