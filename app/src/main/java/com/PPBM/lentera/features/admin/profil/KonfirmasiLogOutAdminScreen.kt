package com.PPBM.lentera.features.admin.profil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

/**
 * Halaman Profil Admin dengan dialog konfirmasi keluar di atasnya.
 * Mengadopsi dialog konfirmasi logout sisi Mahasiswa secara identik.
 */
@Composable
fun KonfirmasiLogoutAdminScreen(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ProfilAdminScreen()
        LogoutConfirmationOverlay(
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

/**
 * Scrim + kartu dialog konfirmasi logout.
 * Desain seragam dengan Konfirmasi Logout Mahasiswa.
 */
@Composable
fun LogoutConfirmationOverlay(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                // Mencegah klik pada kartu ikut menutup dialog
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simbol Keluar (Sesuai referensi Mahasiswa)
            Image(
                painter = painterResource(id = R.drawable.ic_simbol_keluar),
                contentDescription = "Keluar",
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keluar dari akun?",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = LenteraNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Apakah Anda yakin ingin keluar?",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Batal
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEFF5FD),
                        contentColor = LenteraNavy
                    )
                ) {
                    Text(
                        text = "Batal",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Tombol Ya, Keluar
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraRedMuted,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Ya, Keluar",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun KonfirmasiLogoutAdminScreenPreview() {
    LenteraTheme {
        KonfirmasiLogoutAdminScreen()
    }
}
