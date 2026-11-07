package com.PPBM.lentera.features.admin.akunmahasiswa
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.PPBM.lentera.core.designsystem.components.LenteraFeedbackDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.*

private val SampleDetailMahasiswa = Mahasiswa(
    nama = "Nadia Putri",
    nim = "2026110008",
    prodi = "Teknik Informatika",
    status = StatusAkun.BELUM_AKTIVASI
)

@Composable
fun DetailAkunMahasiswaScreen(
    mahasiswa: Mahasiswa = SampleDetailMahasiswa,
    perguruanTinggi: String = "Universitas Contoh",
    angkatan: String = "2026",
    emailKampus: String = "email kampus tersimpan",
    metodeAkses: String = "Kirim tautan aktivasi ke email",
    onBackClick: () -> Unit = {},
    onKirimUlangTautanClick: () -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.MAHASISWA,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LenteraBackHeader(
                title = "DETAIL AKUN MAHASISWA",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackClick
            )
            Text(
                text = "Tinjau identitas, akademik, dan akses akun mahasiswa.",
                color = LenteraColors.SlateText,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.padding(top = 14.dp)
            )
            IdentitasCard(mahasiswa = mahasiswa)
            InformasiAkademikCard(
                perguruanTinggi = perguruanTinggi,
                programStudi = mahasiswa.prodi,
                angkatan = angkatan,
                emailKampus = emailKampus
            )
            AksesAkunCard(
                metodeAkses = metodeAkses,
                onKirimUlangTautanClick = {
                    showFeedbackDialog = true
                    onKirimUlangTautanClick()
                }
            )
            LenteraSecondaryButton(
                text = "Kembali ke Daftar Mahasiswa",
                onClick = onKembaliClick
            )
        }
    }

    LenteraFeedbackDialog(
        show = showFeedbackDialog,
        isSuccess = true,
        title = "Tautan Aktivasi Terkirim",
        message = "Tautan aktivasi baru telah dikirimkan ke $emailKampus dan berlaku selama 24 jam.",
        onDismiss = { showFeedbackDialog = false }
    )
}

@Composable
private fun IdentitasCard(mahasiswa: Mahasiswa) {
    LenteraCard {
        LenteraCardTitle(text = "Identitas mahasiswa")
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(size = 42.dp, iconTint = LenteraColors.NavyPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = mahasiswa.nama,
                    color = LenteraColors.NavyDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans
                )
                Text(
                    text = "${mahasiswa.nim} · ${mahasiswa.prodi}",
                    color = LenteraColors.SlateText,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        StatusBadge(status = mahasiswa.status)
    }
}

@Composable
private fun InformasiAkademikCard(
    perguruanTinggi: String,
    programStudi: String,
    angkatan: String,
    emailKampus: String
) {
    LenteraCard {
        LenteraCardTitle(text = "Informasi akademik")
        Spacer(modifier = Modifier.height(14.dp))
        LenteraReadOnlyField(label = "Perguruan tinggi", value = perguruanTinggi)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LenteraReadOnlyField(
                label = "Program studi",
                value = programStudi,
                modifier = Modifier.weight(1f)
            )
            LenteraReadOnlyField(
                label = "Angkatan",
                value = angkatan,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        LenteraReadOnlyField(label = "Email kampus", value = emailKampus)
        Spacer(modifier = Modifier.height(8.dp))
        LenteraHelperText(text = "Tautan aktivasi dikirim ke email kampus yang terdaftar.")
    }
}

@Composable
private fun AksesAkunCard(
    metodeAkses: String,
    onKirimUlangTautanClick: () -> Unit
) {
    LenteraCard {
        LenteraCardTitle(text = "Akses akun")
        Spacer(modifier = Modifier.height(14.dp))
        LenteraReadOnlyField(label = "Metode pemberian akses", value = metodeAkses)
        Spacer(modifier = Modifier.height(12.dp))
        LenteraButton(
            text = "Kirim Ulang Tautan Aktivasi",
            onClick = onKirimUlangTautanClick
        )
        Spacer(modifier = Modifier.height(10.dp))
        LenteraHelperText(
            text = "Tautan aktivasi berlaku 24 jam. Setelah dikirim ulang, mahasiswa dapat " +
                    "membuat password sendiri dari email kampus yang terdaftar."
        )
        Spacer(modifier = Modifier.height(12.dp))
        LenteraInfoBanner(
            text = "Jangan bagikan password melalui pesan biasa. Akun tetap Belum aktivasi " +
                    "sampai mahasiswa membuat password sendiri."
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetailAkunMahasiswaScreenPreview() {
    LenteraTheme {
        DetailAkunMahasiswaScreen()
    }
}