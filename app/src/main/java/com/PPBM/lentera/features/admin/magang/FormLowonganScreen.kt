package com.PPBM.lentera.features.admin.magang
import com.PPBM.lentera.features.admin.magang.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

@Composable
fun FormLowonganScreen(
    onNavigateToPublikasiLowongan: () -> Unit = {},
    onSimpanLowongan: () -> Unit = {},
    onBackToDaftarLowongan: () -> Unit = {}
) {
    var posisi by remember { mutableStateOf("UI/UX Design") }
    var lokasi by remember { mutableStateOf("Bontang") }
    var sistemKerja by remember { mutableStateOf("Di kantor (On-site)") }
    var deskripsiPekerjaan by remember {
        mutableStateOf("Mendukung riset pengguna, membuat wireframe dan prototipe, serta berkolaborasi dengan tim pengembang untuk menyempurnakan pengalaman produk digital.")
    }
    var persyaratan by remember {
        mutableStateOf(
            "• Mahasiswa aktif minimal semester 5\n" +
            "• Menguasai Figma dan dasar desain UI/UX\n" +
            "• Melampirkan CV dan portofolio\n" +
            "• Bersedia mengikuti magang selama 3 bulan"
        )
    }
    var periodeMagang by remember { mutableStateOf("Januari–Maret 2027 · 3 bulan") }
    var batasPendaftaran by remember { mutableStateOf("30 November 2026") }
    var kuota by remember { mutableStateOf("2 mahasiswa") }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "FORM LOWONGAN",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToDaftarLowongan
            )
        },
        bottomBar = {
            AdminMagangBottomBar(currentRoute = "Magang")
        },
        containerColor = MagangBgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intro
            item {
                Text(
                    text = "Tambah lowongan atau ubah data lowongan yang dipilih dari daftar.",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            // Card 1: Tempat magang
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Tempat magang",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAF2F8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logo_company_mark),
                                    contentDescription = "Logo",
                                    tint = MagangNavyDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PT. Maju Mundur",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Kantor Bontang · Bontang, Kalimantan Timur",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                            }

                            Icon(
                                painter = painterResource(id = R.drawable.ic_vector_chevron),
                                contentDescription = "Pilih",
                                tint = MagangNavyDark,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Card 2: Detail lowongan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Detail lowongan",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Posisi *
                        FormInputField(
                            label = "Posisi *",
                            value = posisi,
                            onValueChange = { posisi = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Row: Lokasi & Sistem kerja
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                FormDropdownField(
                                    label = "Lokasi *",
                                    value = lokasi
                                )
                            }
                            Box(modifier = Modifier.weight(1.2f)) {
                                FormDropdownField(
                                    label = "Sistem kerja *",
                                    value = sistemKerja
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deskripsi pekerjaan *
                        FormInputField(
                            label = "Deskripsi pekerjaan *",
                            value = deskripsiPekerjaan,
                            onValueChange = { deskripsiPekerjaan = it },
                            isMultiLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Persyaratan *
                        FormInputField(
                            label = "Persyaratan *",
                            value = persyaratan,
                            onValueChange = { persyaratan = it },
                            isMultiLine = true
                        )
                    }
                }
            }

            // Card 3: Jadwal & kuota
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Jadwal & kuota",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Periode magang *
                        FormInputField(
                            label = "Periode magang *",
                            value = periodeMagang,
                            onValueChange = { periodeMagang = it }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Batas pendaftaran
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_date_alt),
                                contentDescription = "Date",
                                tint = MagangNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Batas pendaftaran",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = batasPendaftaran,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Kuota *
                        FormInputField(
                            label = "Kuota *",
                            value = kuota,
                            onValueChange = { kuota = it }
                        )
                    }
                }
            }

            // Notice Box
            item {
                MagangInfoBox(
                    text = "Lowongan baru disimpan sebagai draf. Saat diubah, status tetap. Pilih Tinjau & Terbitkan untuk publikasi."
                )
            }

            // Button 1: Simpan Lowongan
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangNavyDark, RoundedCornerShape(14.dp))
                        .clickable { onSimpanLowongan() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Simpan Lowongan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // Button 2: Tinjau & Terbitkan
            item {
                Button(
                    onClick = onNavigateToPublikasiLowongan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MagangGoldenOrange,
                        contentColor = MagangNavyDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Tinjau & Terbitkan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            // Caption Info
            item {
                Text(
                    text = "Publikasi dilakukan oleh admin. Setelah terbit, lowongan akan ditampilkan kepada mahasiswa.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Button 3: Kembali ke Daftar Lowongan
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToDaftarLowongan() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Daftar Lowongan",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = MagangNavyDark
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun FormLowonganPreview() {
    FormLowonganScreen()
}
