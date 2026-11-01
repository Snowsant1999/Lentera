package com.PPBM.lentera.features.mahasiswa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.*

@Composable
fun DataAkunAdminScreen(
    nama: String = "Rina Wulandari",
    peran: String = "Admin Kampus",
    email: String? = null,
    nomorTelepon: String? = null,
    unitKerja: String = "LENTERA",
    aksesPengelolaan: String = "Mahasiswa, laporan, dan magang",
    onBackClick: () -> Unit = {},
    onUbahClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val belumDitambahkan = "Belum ditambahkan"

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
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LenteraCompactHeader(
                backLabel = "Profil",
                title = "DATA AKUN ADMIN",
                description = "Informasi akun, peran, dan akses pengelolaan.",
                onBackClick = onBackClick
            )
            RingkasanAdminCard(nama = nama, peran = peran)
            InformasiPribadiCard(
                nama = nama,
                email = email ?: belumDitambahkan,
                nomorTelepon = nomorTelepon ?: belumDitambahkan,
                onUbahClick = onUbahClick
            )
            InformasiPeranCard(
                peran = peran,
                unitKerja = unitKerja,
                aksesPengelolaan = aksesPengelolaan
            )
            LenteraInfoBanner(
                text = "Data akun admin dikelola oleh tim internal. " +
                        "Hubungi tim IT jika ada data yang perlu diperbarui."
            )
        }
    }
}

@Composable
private fun RingkasanAdminCard(nama: String, peran: String) {
    LenteraCard(containerColor = LenteraColors.BlueGray) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(size = 58.dp, containerColor = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = nama,
                    color = LenteraColors.NavyDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans
                )
                Spacer(modifier = Modifier.height(6.dp))
                LenteraBadge(text = peran)
            }
        }
    }
}

@Composable
private fun InformasiPribadiCard(
    nama: String,
    email: String,
    nomorTelepon: String,
    onUbahClick: () -> Unit
) {
    LenteraCard(withShadow = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LenteraCardTitle(text = "Informasi pribadi", modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.clickable(onClick = onUbahClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = LenteraColors.NavyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ubah",
                    color = LenteraColors.NavyPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PlusJakartaSans
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LenteraDivider()
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Nama lengkap", nilai = nama)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Email", nilai = email)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Nomor telepon", nilai = nomorTelepon)
    }
}

@Composable
private fun InformasiPeranCard(
    peran: String,
    unitKerja: String,
    aksesPengelolaan: String
) {
    LenteraCard(withShadow = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_circle),
                contentDescription = null,
                tint = LenteraColors.NavyDark,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Informasi peran & unit kerja",
                color = LenteraColors.NavyDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LenteraDivider()
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Peran", nilai = peran)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Unit kerja", nilai = unitKerja)
        Spacer(modifier = Modifier.height(14.dp))
        DataBaris(label = "Akses pengelolaan", nilai = aksesPengelolaan)
    }
}

@Composable
private fun DataBaris(label: String, nilai: String) {
    Column {
        Text(
            text = label,
            color = LenteraColors.SlateText,
            fontSize = 12.sp,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = nilai,
            color = LenteraColors.NavyDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DataAkunAdminScreenPreview() {
    LenteraTheme {
        DataAkunAdminScreen()
    }
}