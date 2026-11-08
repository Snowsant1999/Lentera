package com.PPBM.lentera.features.admin.akunmahasiswa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.components.*
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
    perguruanTinggi: String = "Politeknik Negeri Samarinda",
    programStudi: String = mahasiswa.prodi,
    angkatan: String = "2026",
    emailKampus: String = "email kampus tersimpan",
    metodeAkses: String = "Kirim tautan aktivasi ke email",
    onBackClick: () -> Unit = {},
    onKirimUlangTautanClick: () -> Unit = {},
    onKembaliClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHapusClick: () -> Unit = {},
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
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Layar
            LenteraBackHeader(
                title = "DETAIL AKUN MAHASISWA",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackClick
            )

            // 1. Teks Pengantar
            Text(
                text = "Tinjau identitas, akademik, dan akses akun mahasiswa.",
                color = LenteraColors.SlateText,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 2. Card 1: Identitas Mahasiswa
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Identitas mahasiswa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileAvatar(
                            size = 46.dp,
                            iconTint = LenteraColors.NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = mahasiswa.nama,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PlusJakartaSans,
                                color = LenteraColors.NavyDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${mahasiswa.nim} · ${mahasiswa.prodi}",
                                fontSize = 13.sp,
                                fontFamily = PlusJakartaSans,
                                color = LenteraColors.SlateText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val (badgeBg, badgeText) = when (mahasiswa.status) {
                        StatusAkun.BELUM_AKTIVASI -> Pair(Color(0xFFFEF3EB), Color(0xFFD97706)) // Oranye/krem muda
                        StatusAkun.AKTIF -> Pair(LenteraColors.GreenActiveBg, LenteraColors.GreenActive)
                        StatusAkun.LULUS -> Pair(LenteraColors.Background, LenteraColors.SlateText)
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (mahasiswa.status == StatusAkun.BELUM_AKTIVASI) "Belum aktivasi" else mahasiswa.status.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans,
                            color = badgeText
                        )
                    }
                }
            }

            // 3. Card 2: Informasi Akademik
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Informasi akademik",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Perguruan tinggi (lebar penuh)
                    ReadOnlyField(
                        label = "Perguruan tinggi",
                        value = perguruanTinggi
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Row: Program studi dan Angkatan (proporsional 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReadOnlyField(
                            label = "Program studi",
                            value = programStudi,
                            modifier = Modifier.weight(1f)
                        )
                        ReadOnlyField(
                            label = "Angkatan",
                            value = angkatan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Email kampus (lebar penuh)
                    ReadOnlyField(
                        label = "Email kampus",
                        value = emailKampus
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tautan aktivasi dikirim ke email kampus yang terdaftar.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.SlateText,
                        lineHeight = 18.sp
                    )
                }
            }

            // 4. Card 3: Akses Akun
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Akses akun",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReadOnlyField(
                        label = "Metode pemberian akses",
                        value = metodeAkses
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onKirimUlangTautanClick()
                            showFeedbackDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LenteraColors.NavyPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Kirim Ulang Tautan Aktivasi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tautan aktivasi berlaku 24 jam. Setelah dikirim ulang, mahasiswa dapat membuat password sendiri dari email kampus yang terdaftar.",
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.SlateText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Box peringatan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F4F8), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = LenteraColors.NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Jangan bagikan password melalui pesan biasa. Akun tetap Belum aktivasi sampai mahasiswa membuat password sendiri.",
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            color = LenteraColors.NavyDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. Tombol Aksi Bawah (Edit & Hapus) - Disusun vertikal dalam Column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Edit (Atas)
                OutlinedButton(
                    onClick = {
                        // TODO: Tangani aksi edit data mahasiswa
                        onEditClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LenteraColors.NavyPrimary
                    ),
                    border = BorderStroke(1.dp, LenteraColors.NavyPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Data Mahasiswa",
                        tint = LenteraColors.NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Data Mahasiswa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = LenteraColors.NavyPrimary
                    )
                }

                // Tombol Hapus (Bawah)
                OutlinedButton(
                    onClick = {
                        // TODO: Tangani aksi hapus akun mahasiswa
                        onHapusClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Akun",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hapus Akun",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFFE53935)
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi Kirim Ulang Tautan
    LenteraFeedbackDialog(
        show = showFeedbackDialog,
        isSuccess = true,
        title = "Tautan Aktivasi Terkirim",
        message = "Tautan aktivasi baru telah dikirimkan ke $emailKampus dan berlaku selama 24 jam.",
        onDismiss = { showFeedbackDialog = false }
    )
}

/**
 * Komponen reusable field read-only bergaya form input bergaris
 */
@Composable
private fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.SlateText
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyDark
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetailAkunMahasiswaScreenPreview() {
    LenteraTheme {
        DetailAkunMahasiswaScreen()
    }
}
