package com.PPBM.lentera.features.auth

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import com.PPBM.lentera.ui.components.BackArrowIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Screen Verifikasi Pemulihan (AUTH-004)
 * Menampilkan input 6 digit OTP, countdown timer kode, opsi kirim ulang kode,
 * serta tombol navigasi kembali dan ganti email.
 */
@Composable
fun VerifikasiPemulihanScreen(
    email: String = "mahasiswa@student.ac.id",
    onBackToLupaPassword: () -> Unit = {},
    onVerified: () -> Unit = {},
    onChangeEmail: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))

    var otpCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Countdown timer 5 menit (300 detik)
    var timeLeftInSeconds by remember { mutableIntStateOf(300) }
    var timerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && timeLeftInSeconds > 0) {
            delay(1000L)
            timeLeftInSeconds--
        }
        if (timeLeftInSeconds <= 0) {
            timerRunning = false
        }
    }

    val minutes = timeLeftInSeconds / 60
    val seconds = timeLeftInSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    // Masked email jika format email valid
    val maskedEmail = remember(email) {
        if (email.contains("@")) {
            val parts = email.split("@")
            val name = parts[0]
            val domain = parts[1]
            val maskedName = if (name.length > 2) {
                name.take(2) + "•••"
            } else {
                name + "•••"
            }
            "$maskedName@$domain"
        } else {
            email.ifEmpty { "di•••@student.ac.id" }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ========== TOP BAR ==========
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite)
                .padding(start = 4.dp, top = 44.dp, bottom = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackToLupaPassword() }
            ) {
                BackArrowIcon(
                    tint = NavyDark,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Lupa Password",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyDark
                )
            )
        }

        // ========== HEADER CARD ==========
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = CardWhite,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 16.dp, bottom = 28.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lenteralogo),
                contentDescription = "Logo LENTERA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(154.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Satu akses untuk kehidupan kampusmu.",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Medium,
                    color = SlateGray
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ========== TITLE & INSTRUCTIONS ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "VERIFIKASI PEMULIHAN",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masukkan kode 6 digit yang dikirim ke $maskedEmail untuk memverifikasi akunmu.",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========== CARD VERIFIKASI ==========
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = ShadowNavy.copy(alpha = 0.15f),
                        spotColor = ShadowNavy.copy(alpha = 0.15f)
                    )
                    .background(CardWhite, RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Kode Verifikasi",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 6 Boxes OTP Input (tersembunyi via BasicTextField)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = otpCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                otpCode = it
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        cursorBrush = SolidColor(NavyMedium),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        decorationBox = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (i in 0 until 6) {
                                    val char = otpCode.getOrNull(i)?.toString() ?: ""
                                    val isFocused = otpCode.length == i

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .border(
                                                width = if (isFocused) 1.5.dp else 1.dp,
                                                color = if (isFocused) NavyMedium else InputBorder,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(
                                                if (isFocused) CardWhite else InputBackground,
                                                RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Text(
                                            text = char,
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontFamily = plusJakarta,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyDark,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Kode berlaku selama $timeFormatted menit.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tombol Verifikasi & Lanjutkan
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            color = if (otpCode.length == 6) NavyMedium else NavyMedium.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (otpCode.length == 6) {
                                onVerified()
                            }
                        }
                ) {
                    Text(
                        text = "Verifikasi & Lanjutkan",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.SemiBold,
                            color = CardWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Kirim Ulang Kode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum menerima kode? Periksa folder spam.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = SlateGray
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kirim Ulang Kode",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            timeLeftInSeconds = 300
                            timerRunning = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Links
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email tidak sesuai? Ubah Email",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onChangeEmail() }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Kembali ke Login",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ========== FOOTER ==========
            Text(
                text = "LENTERA · Versi 1.0.0",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Medium,
                    color = SlateGrayLight
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun ResetPasswordPreview() {
    ResetPasswordScreen()
}