package com.PPBM.lentera.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.PPBM.lentera.R
import com.PPBM.lentera.features.auth.*
import com.PPBM.lentera.ui.theme.*

/**
 * Navigation graph utama untuk alur autentikasi.
 * Splash → Login → (Lupa Password → Verifikasi → Reset Password)
 *                → Dashboard Placeholder
 */
@Composable
fun LenteraNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // Splash Screen
        composable(
            route = Routes.SPLASH,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD_PLACEHOLDER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(Routes.LUPA_PASSWORD)
                }
            )
        }

        // Lupa Password Screen
        composable(Routes.LUPA_PASSWORD) {
            LupaPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSendReset = { email ->
                    navController.navigate(Routes.verifikasiPemulihan(email))
                }
            )
        }

        // Verifikasi Pemulihan Screen
        composable(
            route = Routes.VERIFIKASI_PEMULIHAN,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifikasiPemulihanScreen(
                email = email,
                onBackToLupaPassword = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Routes.resetPassword(email)) {
                        popUpTo(Routes.LUPA_PASSWORD) { inclusive = true }
                    }
                },
                onChangeEmail = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Reset Password Screen
        composable(
            route = Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            ResetPasswordScreen(
                onBackToVerifikasi = { navController.popBackStack() },
                onPasswordSaved = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard Placeholder
        composable(Routes.DASHBOARD_PLACEHOLDER) {
            DashboardPlaceholderScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD_PLACEHOLDER) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardPlaceholderScreen(onLogout: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.lenteralogo),
                contentDescription = "Logo Lentera",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Selamat Datang di Lentera!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dashboard sedang dalam pengembangan.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = SlateGray
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyMedium
                )
            ) {
                Text("Keluar (Logout)")
            }
        }
    }
}
