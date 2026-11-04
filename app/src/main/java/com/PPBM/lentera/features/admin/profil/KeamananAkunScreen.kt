package com.PPBM.lentera.features.admin.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.*
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun KeamananAkunScreen(
    onBackClick: () -> Unit = {},
    onUbahKataSandiClick: () -> Unit = {},
    onPerangkatTerhubungClick: () -> Unit = {},
    onRiwayatLoginClick: () -> Unit = {},
    onEmailPemulihanClick: () -> Unit = {},
    onKunciBiometrikClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var isAutentikasi2Langkah by remember { mutableStateOf(true) }
    var isPeringatanKeamanan by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Kembali",
                        tint = LenteraColors.NavyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "KEAMANAN AKUN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LenteraColors.NavyPrimary,
                            fontFamily = PlusJakartaSans,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "LENTERA · Admin Kampus",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LenteraColors.SlateText,
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Description
            Text(
                text = "Kelola pengaturan keamanan untuk melindungi akun admin LENTERA Anda.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LenteraColors.SlateText,
                    fontFamily = PlusJakartaSans,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Section 1: Pengaturan Keamanan Utama
            KeamananMenuItem(
                title = "Ubah Kata Sandi",
                subtitle = "Terakhir diubah 2 bulan lalu",
                iconRes = R.drawable.ic_lock_keyhole,
                onClick = onUbahKataSandiClick
            )

            KeamananMenuSwitchItem(
                title = "Autentikasi 2 Langkah",
                subtitle = "Tingkatkan Keamanan Login",
                iconRes = R.drawable.ic_shield,
                checked = isAutentikasi2Langkah,
                onCheckedChange = { isAutentikasi2Langkah = it }
            )

            KeamananMenuItem(
                title = "Perangkat Terhubung",
                subtitle = "2 Perangkat Aktif",
                iconRes = R.drawable.bi_phone_fill,
                onClick = onPerangkatTerhubungClick
            )

            KeamananMenuItem(
                title = "Riwayat Login",
                subtitle = "Aktivitas 30 Hari Terakhir",
                iconRes = R.drawable.akar_icons_clock,
                onClick = onRiwayatLoginClick
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Section 2: Perlindungan Lanjutan
            Text(
                text = "Perlindungan Lanjutan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            KeamananMenuItem(
                title = "Email Pemulihan",
                subtitle = "r***n@kampus.ac.id",
                iconRes = R.drawable.andrsrxn_mail,
                onClick = onEmailPemulihanClick
            )

            KeamananMenuItem(
                title = "Kunci Biometrik",
                subtitle = "Gunakan Sidik Jari",
                iconRes = R.drawable.fa7_solid_fingerprint,
                onClick = onKunciBiometrikClick
            )

            KeamananMenuSwitchItem(
                title = "Peringatan Keamanan",
                subtitle = "Notifikasi Login Mencurigakan",
                iconRes = R.drawable.ic_shield_alert,
                checked = isPeringatanKeamanan,
                onCheckedChange = { isPeringatanKeamanan = it }
            )
        }
    }
}

@Composable
private fun KeamananMenuItem(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE2ECF5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = LenteraColors.NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LenteraColors.NavyPrimary,
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LenteraColors.SlateText,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp
                    )
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Buka",
                tint = LenteraColors.SlateText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun KeamananMenuSwitchItem(
    title: String,
    subtitle: String,
    iconRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE2ECF5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = LenteraColors.NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LenteraColors.NavyPrimary,
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LenteraColors.SlateText,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp
                    )
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = LenteraColors.NavyPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KeamananAkunScreenPreview() {
    KeamananAkunScreen()
}
