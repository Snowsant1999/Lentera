package com.PPBM.lentera.features.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.PPBM.lentera.ui.components.InfoIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

/**
 * Login Screen — Halaman utama login
 *
 * Berisi form NIM/Email + Password, dengan validasi error state,
 * toggle show/hide password, dan link ke Lupa Password.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    val focusManager = LocalFocusManager.current

    var nimOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Simulasi login
    fun doLogin() {
        if (nimOrEmail.isBlank() || password.isBlank()) {
            showError = true
            return
        }
        isLoading = true
        showError = false
        // Untuk saat ini, login selalu berhasil jika field terisi
        onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ========== HEADER ==========
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = CardWhite,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 52.dp, bottom = 20.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.lenteralogo),
                contentDescription = "Logo LENTERA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(154.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tagline
            Text(
                text = "Satu akses untuk kehidupan kampusmu.",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )
            )
        }

        // ========== BODY ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            // Heading
            Text(
                text = "SELAMAT DATANG",
                style = TextStyle(
                    fontSize = 28.sp,
                    lineHeight = 35.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masuk untuk mengakses layanan pengaduan dan\npeluang magang di LENTERA.",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        spotColor = NavyDark.copy(alpha = 0.04f),
                        ambientColor = NavyDark.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(CardWhite, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                // Error banner
                AnimatedVisibility(
                    visible = showError,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    ErrorRedLight,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, ErrorRedBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            InfoIcon(
                                tint = ErrorRed,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Login gagal",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = plusJakarta,
                                        fontWeight = FontWeight.Bold,
                                        color = ErrorRed
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "NIM / email atau password salah. Periksa kembali dan coba lagi.",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontFamily = plusJakarta,
                                        fontWeight = FontWeight.Normal,
                                        color = ErrorRed.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // ---- NIM / Email Field ----
                Text(
                    text = "NIM / Email Kampus",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(
                            width = 1.dp,
                            color = if (showError) ErrorRedBorder else InputBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(InputBackground, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iconamoon_profile_circle_thin),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = nimOrEmail,
                        onValueChange = {
                            nimOrEmail = it
                            if (showError) showError = false
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        ),
                        cursorBrush = SolidColor(NavyMedium),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (nimOrEmail.isEmpty()) {
                                    Text(
                                        text = "Masukkan NIM atau email kampus",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = plusJakarta,
                                            fontWeight = FontWeight.Normal,
                                            color = SlateGray
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Helper text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    InfoIcon(
                        tint = SlateGrayLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Contoh NIM: 2024010001 atau email@kampus.ac.id",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = SlateGrayLight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---- Password Field ----
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(
                            width = 1.dp,
                            color = if (showError) ErrorRedBorder else InputBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(InputBackground, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lock_keyhole),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (showError) showError = false
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        ),
                        cursorBrush = SolidColor(NavyMedium),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                doLogin()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (password.isEmpty()) {
                                    Text(
                                        text = "Masukkan password",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = plusJakarta,
                                            fontWeight = FontWeight.Normal,
                                            color = SlateGray
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Toggle password visibility
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { passwordVisible = !passwordVisible }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eye_off),
                            contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
                            colorFilter = ColorFilter.tint(if (passwordVisible) NavyMedium else SlateGray),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Lupa Password link
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lupa password?",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Bold,
                            color = NavyMedium
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onForgotPassword() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Button Masuk
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(NavyMedium, RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { doLogin() }
                ) {
                    Text(
                        text = if (showError) "Coba Masuk Lagi" else "Masuk",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Bold,
                            color = CardWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Keterangan
            Text(
                text = "Akun dikelola oleh kampus.\nHubungi admin kampus jika belum memiliki akses.",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ========== FOOTER ==========
        Text(
            text = "LENTERA · Versi 1.0.0",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = plusJakarta,
                fontWeight = FontWeight.Normal,
                color = SlateGray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}