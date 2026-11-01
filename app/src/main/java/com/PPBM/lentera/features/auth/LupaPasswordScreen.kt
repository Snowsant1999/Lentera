package com.PPBM.lentera.features.auth

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
import androidx.compose.material3.Text
import com.PPBM.lentera.ui.components.BackArrowIcon
import com.PPBM.lentera.ui.components.InfoIcon
import com.PPBM.lentera.ui.components.MailIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

/**
 * Lupa Password Screen
 *
 * Menampilkan form input email terdaftar untuk mengirim petunjuk reset password.
 * Ada info box berisi "Masukkan email akun LENTERA mahasiswa kamu"
 * dan tips pemulihan password di bagian bawah.
 */
@Composable
fun LupaPasswordScreen(
    onBackToLogin: () -> Unit = {},
    onSendReset: (String) -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }

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
                    ) { onBackToLogin() }
            ) {
                BackArrowIcon(
                    tint = NavyDark,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Login",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyDark
                )
            )
        }

        // ========== HEADER ==========
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = CardWhite,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(bottom = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lenteralogo),
                contentDescription = "Logo LENTERA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

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
                text = "LUPA PASSWORD",
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
                text = "Masukkan email yang terdaftar. Kami akan mengirimkan petunjuk untuk mengatur ulang password kamu.",
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
                // Info box
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InfoBlueLight, RoundedCornerShape(12.dp))
                        .border(1.dp, InfoBlueBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iconamoon_profile_circle_thin),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Masukkan email akun LENTERA mahasiswa kamu",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Medium,
                            color = NavyMedium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Email field
                Text(
                    text = "Email Terdaftar",
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
                        .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                        .background(InputBackground, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp)
                ) {
                    MailIcon(
                        tint = SlateGray,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = email,
                        onValueChange = { email = it },
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
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (email.isEmpty()) {
                                    Text(
                                        text = "contoh@student.ac.id",
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

                Spacer(modifier = Modifier.height(20.dp))

                // Button Kirim Petunjuk Reset
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(NavyMedium, RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (email.isNotBlank()) {
                                onSendReset(email)
                            }
                        }
                ) {
                    Text(
                        text = "Kirim Petunjuk Reset",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Bold,
                            color = CardWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tips Pemulihan Password
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InfoBlueLight, RoundedCornerShape(16.dp))
                    .border(1.dp, InfoBlueBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InfoIcon(
                        tint = NavyMedium,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tips Pemulihan Password",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Pastikan email masih aktif dan dapat diakses. Periksa juga folder spam atau junk setelah mengirim permintaan reset.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.Normal,
                        color = SlateGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kembali ke Login
            Text(
                text = "Kembali ke Login",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Bold,
                    color = NavyMedium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackToLogin() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
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
fun LupaPasswordScreenPreview() {
    LupaPasswordScreen()
}
