package com.PPBM.lentera.features.admin.akunmahasiswa
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme

val PlusJakartaSans = FontFamily(Font(com.PPBM.lentera.R.font.plus_jakarta_sans))

@Composable
fun BuatAkunMahasiswaScreen() {
    var namaLengkap by remember { mutableStateOf("Dewi Lestari") }
    var nim by remember { mutableStateOf("2026110009") }
    var perguruanTinggi by remember { mutableStateOf("Universitas Contoh") }
    var programStudi by remember { mutableStateOf("Teknik Informatika") }
    var angkatan by remember { mutableStateOf("2026") }
    var emailKampus by remember { mutableStateOf("dewi.lestari@kampus.ac.id") }
    var metodeAkses by remember { mutableStateOf("Kirim tautan aktivasi ke email") }
    var kirimEmail by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        bottomBar = { BuatAkunMahasiswaBottomBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar & Subtitle
            item {
                HeaderTopBar()
            }

            // Card 1: Identitas Mahasiswa
            item {
                CardIdentitasMahasiswa(
                    namaLengkap = namaLengkap,
                    onNamaChange = { namaLengkap = it },
                    nim = nim,
                    onNimChange = { nim = it }
                )
            }

            // Card 2: Informasi Akademik
            item {
                CardInformasiAkademik(
                    perguruanTinggi = perguruanTinggi,
                    onPerguruanTinggiChange = { perguruanTinggi = it },
                    programStudi = programStudi,
                    onProgramStudiChange = { programStudi = it },
                    angkatan = angkatan,
                    onAngkatanChange = { angkatan = it },
                    emailKampus = emailKampus,
                    onEmailKampusChange = { emailKampus = it }
                )
            }

            // Card 3: Akses Akun Awal
            item {
                CardAksesAkunAwal(
                    metodeAkses = metodeAkses,
                    onMetodeAksesChange = { metodeAkses = it },
                    kirimEmail = kirimEmail,
                    onKirimEmailChange = { kirimEmail = it }
                )
            }

            // Info Notice Banner
            item {
                InfoNoticeBanner()
            }

            // Action Buttons
            item {
                ActionButtons()
            }
        }
    }
}

@Composable
fun HeaderTopBar() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color(0xFF1E3A5F),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "BUAT AKUN MAHASISWA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "LENTERA · Admin Kampus",
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Tambahkan mahasiswa dan siapkan akses awalnya.",
            fontSize = 14.sp,
            fontFamily = PlusJakartaSans,
            color = Color(0xFF7A8B9B)
        )
    }
}

@Composable
fun CardIdentitasMahasiswa(
    namaLengkap: String,
    onNamaChange: (String) -> Unit,
    nim: String,
    onNimChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Identitas mahasiswa",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E3A5F)
            )

            // Nama lengkap *
            Column {
                Text(
                    text = "Nama lengkap *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = namaLengkap,
                    onValueChange = onNamaChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A5F)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A5F),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Nomor Induk Mahasiswa (NIM) *
            Column {
                Text(
                    text = "Nomor Induk Mahasiswa (NIM) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = nim,
                    onValueChange = onNimChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A5F)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A5F),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "10 digit · NIM harus unik dan sesuai data akademik.",
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE6F3F0), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "NIM belum terdaftar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
fun CardInformasiAkademik(
    perguruanTinggi: String,
    onPerguruanTinggiChange: (String) -> Unit,
    programStudi: String,
    onProgramStudiChange: (String) -> Unit,
    angkatan: String,
    onAngkatanChange: (String) -> Unit,
    emailKampus: String,
    onEmailKampusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Informasi akademik",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E3A5F)
            )

            // Perguruan tinggi *
            Column {
                Text(
                    text = "Perguruan tinggi *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = perguruanTinggi,
                    onValueChange = onPerguruanTinggiChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A5F)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A5F),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Program studi * & Angkatan *
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Program studi *
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "Program studi *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFF1E3A5F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = programStudi,
                        onValueChange = onProgramStudiChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A5F)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E3A5F),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = com.PPBM.lentera.R.drawable.chevron_down),
                                contentDescription = "Select Program Studi",
                                tint = Color(0xFF1E3A5F),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true
                    )
                }

                // Angkatan *
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Angkatan *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFF1E3A5F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = angkatan,
                        onValueChange = onAngkatanChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A5F)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E3A5F),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = com.PPBM.lentera.R.drawable.chevron_down),
                                contentDescription = "Select Angkatan",
                                tint = Color(0xFF1E3A5F),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true
                    )
                }
            }

            // Email kampus *
            Column {
                Text(
                    text = "Email kampus *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = emailKampus,
                    onValueChange = onEmailKampusChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A5F)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A5F),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gunakan email kampus yang dapat diakses mahasiswa.",
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
            }
        }
    }
}

@Composable
fun CardAksesAkunAwal(
    metodeAkses: String,
    onMetodeAksesChange: (String) -> Unit,
    kirimEmail: Boolean,
    onKirimEmailChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Akses akun awal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E3A5F)
            )

            // Metode pemberian akses
            Column {
                Text(
                    text = "Metode pemberian akses",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = metodeAkses,
                    onValueChange = onMetodeAksesChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A5F)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A5F),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = com.PPBM.lentera.R.drawable.chevron_down),
                            contentDescription = "Select Metode",
                            tint = Color(0xFF1E3A5F),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mahasiswa masuk dengan NIM atau email kampus, lalu membuat password sendiri melalui tautan yang berlaku 24 jam.",
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B),
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Kirim email setelah disimpan
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kirim email setelah disimpan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        color = Color(0xFF1E3A5F)
                    )
                    Switch(
                        checked = kirimEmail,
                        onCheckedChange = onKirimEmailChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Akun berstatus Belum aktivasi sampai mahasiswa membuat password. Jangan kirim password melalui pesan biasa.",
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun InfoNoticeBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0F7)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.info),
                contentDescription = "Info",
                tint = Color(0xFF1E3A5F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Kolom bertanda * wajib diisi. Periksa NIM dan email sebelum menyimpan akun.",
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E3A5F),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ActionButtons() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Simpan Akun Mahasiswa
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Simpan Akun Mahasiswa",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color.White
            )
        }

        // Kembali ke Daftar Mahasiswa
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1E3A5F)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Kembali ke Daftar Mahasiswa",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = Color(0xFF1E3A5F)
            )
        }
    }
}

@Composable
fun BuatAkunMahasiswaBottomBar() {
    AdminBottomBar(selected = LenteraNavItem.MAHASISWA)
}

@Preview(showBackground = true)
@Composable
fun BuatAkunMahasiswaScreenPreview() {
    LenteraTheme {
        BuatAkunMahasiswaScreen()
    }
}