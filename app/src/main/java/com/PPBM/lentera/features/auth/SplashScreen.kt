package com.PPBM.lentera.features.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.core.navigation.Routes
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay

/**
 * Splash Screen — AUTH-001
 *
 * Menampilkan logo LENTERA dengan tagline dan progress bar loading.
 * Otomatis mengecek sesi pengguna dan mengarahkan ke Dashboard atau Login.
 */
@Composable
fun SplashScreen(
    onSplashFinished: (destinationRoute: String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

    // Animasi progress bar
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    // Timer untuk navigasi & pengecekan sesi auto-login
    LaunchedEffect(Unit) {
        delay(2000)
        try {
            SupabaseConfig.client.auth.awaitInitialization()
        } catch (_: Exception) {}
        val session = SessionManager.getInstance(context)
        val profile = session.getProfile()
        val hasSupabaseSession = SupabaseConfig.client.auth.currentSessionOrNull() != null
        val destination = when {
            !session.isLoggedIn() || profile == null || !hasSupabaseSession -> {
                if (profile?.statusAkun.equals("nonaktif", ignoreCase = true)) {
                    session.clearSession()
                }
                Routes.LOGIN
            }
            profile.statusAkun.equals("nonaktif", ignoreCase = true) -> {
                try {
                    SupabaseConfig.client.auth.signOut()
                } catch (_: Exception) {}
                session.clearSession()
                Routes.LOGIN
            }
            profile.role.equals("admin", ignoreCase = true) -> Routes.ADMIN_DASHBOARD
            profile.statusAkun.equals("belum_aktivasi", ignoreCase = true) ->
                Routes.resetPassword(profile.email)
            else -> Routes.MAHASISWA_DASHBOARD
        }
        onSplashFinished(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo LENTERA
            Image(
                painter = painterResource(id = R.drawable.img_lentera_logo),
                contentDescription = "Logo LENTERA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Satu akses untuk kehidupan kampusmu.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Loading indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                // Progress bar track
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(InputBorder)
                ) {
                    // Progress bar fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = progress)
                            .clip(RoundedCornerShape(2.dp))
                            .background(NavyMedium)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Memuat LENTERA...",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
