package com.PPBM.lentera.features.admin.profil
import com.PPBM.lentera.core.designsystem.components.*

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

data class MenuProfilItem(
    val judul: String,
    val deskripsi: String,
    @DrawableRes val iconRes: Int
)

private val SampleMenuProfil = listOf(
    MenuProfilItem("Data Akun", "Informasi pribadi dan peran admin", R.drawable.ic_profile_circle),
    MenuProfilItem("Keamanan Akun", "Ubah kata sandi dan autentikasi", R.drawable.ic_shield_alert),
    MenuProfilItem("Notifikasi", "Pembaruan layanan kampus", R.drawable.ic_bell),
    MenuProfilItem("Bantuan Admin", "Panduan dan dukungan operasional", R.drawable.ic_help_circle)
)

@Composable
fun ProfilAdminScreen(
    nama: String = "Rina Wulandari",
    peran: String = "Admin Kampus · LENTERA",
    badge: String = "Akses admin penuh",
    versi: String = "1.0.0",
    menu: List<MenuProfilItem> = SampleMenuProfil,
    onMenuClick: (MenuProfilItem) -> Unit = {},
    onKeluarClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
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
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PROFIL ADMIN",
                color = LenteraColors.NavyDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(30.dp))
            RingkasanProfil(nama = nama, peran = peran, badge = badge)
            Spacer(modifier = Modifier.height(24.dp))
            MenuProfilCard(menu = menu, onMenuClick = onMenuClick)
            Spacer(modifier = Modifier.height(14.dp))
            KeluarCard(onClick = onKeluarClick)
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "LENTERA · Versi $versi",
                color = LenteraColors.SlateText,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans
            )
        }
    }
}

@Composable
private fun RingkasanProfil(
    nama: String,
    peran: String,
    badge: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileAvatar(size = 100.dp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = nama,
            color = LenteraColors.NavyDark,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = peran,
            color = LenteraColors.SlateText,
            fontSize = 14.sp,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(10.dp))
        LenteraBadge(text = badge)
    }
}

@Composable
private fun MenuProfilCard(
    menu: List<MenuProfilItem>,
    onMenuClick: (MenuProfilItem) -> Unit
) {
    LenteraCard(
        contentPadding = PaddingValues(vertical = 8.dp),
        withShadow = true
    ) {
        menu.forEach { item ->
            MenuProfilRow(item = item, onClick = { onMenuClick(item) })
        }
    }
}

@Composable
private fun MenuProfilRow(
    item: MenuProfilItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(LenteraColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                tint = LenteraColors.NavyDark,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.judul,
                color = LenteraColors.NavyDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans
            )
            Text(
                text = item.deskripsi,
                color = LenteraColors.SlateText,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = LenteraColors.SlateText,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun KeluarCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LenteraColors.RoseSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_log_out),
            contentDescription = null,
            tint = LenteraColors.Rose,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "Keluar",
            color = LenteraColors.Rose,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = LenteraColors.Rose,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ProfilAdminScreenPreview() {
    LenteraTheme {
        ProfilAdminScreen()
    }
}