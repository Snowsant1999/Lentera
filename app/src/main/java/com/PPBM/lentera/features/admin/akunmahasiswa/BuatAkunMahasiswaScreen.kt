package com.PPBM.lentera.features.admin.akunmahasiswa
import androidx.compose.foundation.Image
import com.PPBM.lentera.core.designsystem.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun BuatAkunMahasiswaScreen(
    onBackClick: () -> Unit = {},
    onSimpanSuccess: () -> Unit = onBackClick,
    onSimpanAkun: ((nama: String, nim: String, email: String, jurusan: String, prodi: String, angkatan: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit)? = null,
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var namaLengkap by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var jurusan by remember { mutableStateOf("") }
    var programStudi by remember { mutableStateOf("") }
    var angkatan by remember { mutableStateOf("") }
    var emailKampus by remember { mutableStateOf("") }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackTitle by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackIsSuccess by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AdminBottomBar(
                selected = LenteraNavItem.MAHASISWA,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar & Subtitle
            item {
                HeaderTopBar(onBackClick = onBackClick)
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

            // Card 2: Informasi Akademik (Jurusan, Prodi, Angkatan, Email)
            item {
                CardInformasiAkademik(
                    jurusan = jurusan,
                    onJurusanChange = { jurusan = it },
                    programStudi = programStudi,
                    onProgramStudiChange = { programStudi = it },
                    angkatan = angkatan,
                    onAngkatanChange = { angkatan = it },
                    emailKampus = emailKampus,
                    onEmailKampusChange = { emailKampus = it }
                )
            }

            // Action Buttons
            item {
                ActionButtons(
                    onSimpan = {
                        if (namaLengkap.isBlank() || nim.isBlank() || emailKampus.isBlank() || jurusan.isBlank()) {
                            feedbackTitle = "Data Belum Lengkap"
                            feedbackMessage = "Mohon lengkapi semua kolom wajib bertanda (*) seperti Nama Lengkap, NIM, Jurusan, dan Email Kampus."
                            feedbackIsSuccess = false
                            showFeedback = true
                        } else if (onSimpanAkun != null) {
                            onSimpanAkun(
                                namaLengkap,
                                nim,
                                emailKampus,
                                jurusan,
                                programStudi,
                                angkatan,
                                {
                                    feedbackTitle = "Akun Berhasil Dibuat"
                                    feedbackMessage = "Akun mahasiswa $namaLengkap ($nim) berhasil disimpan di Supabase."
                                    feedbackIsSuccess = true
                                    showFeedback = true
                                },
                                { errMsg ->
                                    feedbackTitle = "Gagal Membuat Akun"
                                    feedbackMessage = errMsg
                                    feedbackIsSuccess = false
                                    showFeedback = true
                                }
                            )
                        } else {
                            feedbackTitle = "Akun Berhasil Dibuat"
                            feedbackMessage = "Akun mahasiswa $namaLengkap ($nim) berhasil disimpan dan langsung aktif digunakan."
                            feedbackIsSuccess = true
                            showFeedback = true
                        }
                    },
                    onKembali = onBackClick
                )
            }
        }

        LenteraFeedbackDialog(
            show = showFeedback,
            isSuccess = feedbackIsSuccess,
            title = feedbackTitle,
            message = feedbackMessage,
            onDismiss = {
                showFeedback = false
                if (feedbackIsSuccess) {
                    onSimpanSuccess()
                }
            }
        )
    }
}

@Composable
fun HeaderTopBar(onBackClick: () -> Unit = {}) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_back_button),
                contentDescription = "Kembali",
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onBackClick)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "TAMBAH MAHASISWA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Text(
                    text = "LENTERA · Administrator Kampus",
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Formulir pendaftaran akun mahasiswa baru. Akun yang dibuat akan langsung aktif dan dapat digunakan untuk masuk ke aplikasi LENTERA.",
            fontSize = 13.sp,
            fontFamily = PlusJakartaSans,
            color = Color(0xFF7A8B9B),
            lineHeight = 18.sp
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
                    text = "NIM harus unik dan sesuai dengan data akademik mahasiswa.",
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
            }
        }
    }
}

@Composable
fun CardInformasiAkademik(
    jurusan: String,
    onJurusanChange: (String) -> Unit,
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

            // Jurusan * (Menggantikan Perguruan Tinggi)
            Column {
                Text(
                    text = "Jurusan *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF1E3A5F)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = jurusan,
                    onValueChange = onJurusanChange,
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
                    text = "Gunakan email resmi kampus yang aktif.",
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaSans,
                    color = Color(0xFF7A8B9B)
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    onSimpan: () -> Unit = {},
    onKembali: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Simpan Akun Mahasiswa
        Button(
            onClick = onSimpan,
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
            onClick = onKembali,
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

@Preview(showBackground = true)
@Composable
fun BuatAkunMahasiswaScreenPreview() {
    LenteraTheme {
        BuatAkunMahasiswaScreen()
    }
}
