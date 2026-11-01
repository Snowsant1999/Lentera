package com.PPBM.lentera.features.mahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

/** Halaman Profil Admin dengan dialog konfirmasi keluar di atasnya. */
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
 * Scrim + kartu dialog. Bisa dipasang langsung di ProfilAdminScreen
 * saat state `tampilkanDialogKeluar` bernilai true.
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
            .background(LenteraColors.Scrim)
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
                .clip(RoundedCornerShape(28.dp))
                .background(LenteraColors.Surface)
                // Mencegah klik pada kartu ikut menutup dialog.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IkonKeluar()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Keluar dari akun?",
                color = LenteraColors.NavyDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Apakah Anda yakin ingin keluar?",
                color = LenteraColors.SlateText,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LenteraButton(
                    text = "Batal",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    containerColor = LenteraColors.Background,
                    contentColor = LenteraColors.NavyDark
                )
                LenteraButton(
                    text = "Ya, Keluar",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    containerColor = LenteraColors.Rose
                )
            }
        }
    }
}

@Composable
private fun IkonKeluar() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(LenteraColors.RoseSoft),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_log_out),
            contentDescription = null,
            tint = LenteraColors.Rose,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun KonfirmasiLogoutAdminScreenPreview() {
    LenteraTheme {
        KonfirmasiLogoutAdminScreen()
    }
}