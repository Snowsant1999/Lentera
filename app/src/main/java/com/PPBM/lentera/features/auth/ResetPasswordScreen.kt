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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.PPBM.lentera.ui.components.BackArrowIcon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

/**
 * Screen Reset Password (AUTH-005)
 * Menampilkan input password baru dan konfirmasi password baru
 * dengan validasi ketentuan password serta tombol Simpan Password Baru.
 */
@Composable
fun ResetPasswordScreen(
    onBackToVerifikasi: () -> Unit = {},
    onPasswordSaved: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    val plusJakarta = FontFamily(Font(R.font.plus_jakarta_sans))
    val focusManager = LocalFocusManager.current

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleSave() {
        if (newPassword.length < 8) {
            errorMessage = "Password minimal harus 8 karakter."
            return
        }
        if (newPassword != confirmPassword) {
            errorMessage = "Konfirmasi password tidak cocok."
            return
        }
        errorMessage = null
        onPasswordSaved()
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
                    ) { onBackToVerifikasi() }
            ) {
                BackArrowIcon(
                    tint = NavyDark,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Verifikasi Pemulihan",
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
                text = "RESET PASSWORD",
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
                text = "Identitas berhasil diverifikasi. Buat password baru untuk mengamankan akun LENTERA kamu.",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========== CARD FORM RESET ==========
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
                // Label Password Baru
                Text(
                    text = "Password Baru",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Password Baru
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(
                            width = 1.dp,
                            color = if (errorMessage != null && newPassword.isEmpty()) ErrorRedBorder else InputBorder,
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
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            if (errorMessage != null) errorMessage = null
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        ),
                        cursorBrush = SolidColor(NavyMedium),
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
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
                                if (newPassword.isEmpty()) {
                                    Text(
                                        text = "Masukkan password baru",
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

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { newPasswordVisible = !newPasswordVisible }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eye_off),
                            contentDescription = if (newPasswordVisible) "Sembunyikan" else "Tampilkan",
                            colorFilter = ColorFilter.tint(if (newPasswordVisible) NavyMedium else SlateGray),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Helper text Password Baru
                Text(
                    text = "Gunakan minimal 8 karakter dengan kombinasi huruf besar, huruf kecil, dan angka.",
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.Normal,
                        color = SlateGrayLight
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Label Konfirmasi Password Baru
                Text(
                    text = "Konfirmasi Password Baru",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = plusJakarta,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Konfirmasi Password Baru
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(
                            width = 1.dp,
                            color = if (errorMessage != null && confirmPassword.isEmpty()) ErrorRedBorder else InputBorder,
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
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (errorMessage != null) errorMessage = null
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = NavyDark
                        ),
                        cursorBrush = SolidColor(NavyMedium),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                handleSave()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (confirmPassword.isEmpty()) {
                                    Text(
                                        text = "Ulangi password baru",
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

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { confirmPasswordVisible = !confirmPasswordVisible }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eye_off),
                            contentDescription = if (confirmPasswordVisible) "Sembunyikan" else "Tampilkan",
                            colorFilter = ColorFilter.tint(if (confirmPasswordVisible) NavyMedium else SlateGray),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.Normal,
                            color = ErrorRed
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Simpan Password Baru
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(NavyMedium, RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { handleSave() }
                ) {
                    Text(
                        text = "Simpan Password Baru",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = plusJakarta,
                            fontWeight = FontWeight.SemiBold,
                            color = CardWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Link Kembali ke Login
            Text(
                text = "Kembali ke Login",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = plusJakarta,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackToLogin() }
            )

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
