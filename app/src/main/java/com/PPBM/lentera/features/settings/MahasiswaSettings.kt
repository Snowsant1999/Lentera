package com.PPBM.lentera.features.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBadgeBg
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraRedBg
import com.PPBM.lentera.ui.theme.LenteraRedMuted
import com.PPBM.lentera.ui.theme.LenteraTeal
import com.PPBM.lentera.ui.theme.LenteraTextPrimary
import com.PPBM.lentera.ui.theme.LenteraTextSecondary
import com.PPBM.lentera.ui.theme.PlusJakartaSans

class MahasiswaSettings {

    @Composable
    fun mhsProfilScreen(
        modifier: Modifier = Modifier,
        onNavigateToDataDiri: () -> Unit = {},
        onNavigateToDokumen: () -> Unit = {},
        onNavigateToNotifikasi: () -> Unit = {},
        onNavigateToBantuan: () -> Unit = {},
        onNavigateToTentang: () -> Unit = {},
        onLogoutConfirmed: () -> Unit = {},
        onTabSelected: (LenteraTab) -> Unit = {}
    ) {
        var showLogoutDialog by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LenteraBg)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Judul Layar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "PROFIL",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            color = LenteraNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bagian Avatar dan Identitas Mahasiswa
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .background(Color.White, CircleShape)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                spotColor = Color(0x15173B57)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_2),
                            contentDescription = "Avatar Profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Budi Santoso",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = LenteraNavy
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "NIM 2023110045",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = LenteraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Universitas Contoh - Teknik Informatika",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = LenteraTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Badge Mahasiswa Aktif
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(LenteraBadgeBg)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Mahasiswa aktif",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = LenteraTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Menu Card Putih (5 Menu Sesuai Desain)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color(0x09173B57),
                                ambientColor = Color(0x09173B57),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(24.dp))
                            .padding(vertical = 8.dp)
                    ) {
                        ProfilMenuItem(
                            iconRes = R.drawable.menu_symbol,
                            title = "Data Diri",
                            subtitle = "Informasi akun dan akademik",
                            onClick = onNavigateToDataDiri
                        )

                        ProfilMenuItem(
                            iconRes = R.drawable.menu_symbol_2,
                            title = "Dokumen Saya",
                            subtitle = "CV, transkrip, dan portofolio",
                            onClick = onNavigateToDokumen
                        )

                        ProfilMenuItem(
                            iconRes = R.drawable.menu_symbol_3,
                            title = "Notifikasi",
                            subtitle = "Atur pembaruan layanan",
                            onClick = onNavigateToNotifikasi
                        )

                        ProfilMenuItem(
                            iconRes = R.drawable.support_symbol,
                            title = "Bantuan",
                            subtitle = "Panduan dan dukungan",
                            onClick = onNavigateToBantuan
                        )

                        ProfilMenuItem(
                            iconRes = R.drawable.menu_symbol_4,
                            title = "Tentang Aplikasi",
                            subtitle = "Credit aplikasi",
                            onClick = onNavigateToTentang
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Keluar (Merah Muda Soft Sesuai Desain)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(LenteraRedBg)
                            .clickable { showLogoutDialog = true }
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.log),
                                contentDescription = "Keluar",
                                tint = LenteraRedMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Keluar",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LenteraRedMuted
                            )
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.open_answer),
                            contentDescription = null,
                            tint = LenteraRedMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Footer Versi Aplikasi
                    Text(
                        text = "LENTERA · Versi 1.0.0",
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Navigation Bar Aktif di Tab Profil
                LenteraBottomBar(
                    currentTab = LenteraTab.PROFIL,
                    onTabSelected = onTabSelected
                )
            }

            // Dialog Konfirmasi Logout (Sesuai Konfirmasi Logout Mahasiswa.png)
            if (showLogoutDialog) {
                KonfirmasiLogoutDialog(
                    onDismiss = { showLogoutDialog = false },
                    onConfirm = {
                        showLogoutDialog = false
                        onLogoutConfirmed()
                    }
                )
            }
        }
    }

    @Composable
    private fun ProfilMenuItem(
        iconRes: Int,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = subtitle,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.open_answer),
                contentDescription = null,
                tint = LenteraTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    @Composable
    fun KonfirmasiLogoutDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Simbol Keluar (Icon Simbol keluar.svg dari folder icon)
                Image(
                    painter = painterResource(id = R.drawable.simbol_keluar),
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

    @Preview(showBackground = true, widthDp = 412, heightDp = 917)
    @Composable
    fun PreviewMhsProfilScreen() {
        mhsProfilScreen()
    }
}