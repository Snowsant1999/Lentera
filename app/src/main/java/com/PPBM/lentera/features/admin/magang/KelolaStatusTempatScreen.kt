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
fun KelolaStatusTempatScreen(
    onSimpanStatus: () -> Unit = {},
    onBackToDetailTempat: () -> Unit = {}
) {
    var selectedStatus by remember { mutableStateOf("Draf") }
    var catatanAdmin by remember {
        mutableStateOf("Lengkapi NIB dan dokumen legalitas sebelum tempat diaktifkan.")
    }

    Scaffold(
        topBar = {
            AdminMagangHeader(
                title = "KELOLA STATUS TEMPAT",
                subtitle = "LENTERA · Admin Kampus",
                onBackClick = onBackToDetailTempat
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
            // 1. Company Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEAF2F8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.building_2),
                                    contentDescription = "Building",
                                    tint = MagangNavyDark,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PT. Cahaya Digital",
                                    style = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Bontang · Teknologi Informasi",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Normal,
                                        color = MagangTextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MagangCreamBox)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Draf",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MagangNavyDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Dicatat admin 16 Okt 2026, 14:20 WITA",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )
                    }
                }
            }

            // 2. Status Tempat Magang Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MagangCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Status tempat magang",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Aktif: dapat dipilih untuk lowongan. Draf: data belum lengkap. Arsip: tidak dipakai untuk lowongan baru.",
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status Selection Buttons: Aktif | Draf | Arsip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Aktif
                            val isAktif = selectedStatus == "Aktif"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isAktif) MagangGoldenOrange else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isAktif) Color.Transparent else MagangNavyDark,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedStatus = "Aktif" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aktif",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                            }

                            // Draf (Default active in design)
                            val isDraf = selectedStatus == "Draf"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDraf) MagangGoldenOrange else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isDraf) Color.Transparent else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedStatus = "Draf" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Draf",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = MagangNavyDark
                                    )
                                )
                            }

                            // Arsip
                            val isArsip = selectedStatus == "Arsip"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isArsip) MagangGoldenOrange else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isArsip) Color.Transparent else MagangRedBrown,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedStatus = "Arsip" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Arsip",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = PlusJakartaFont,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isArsip) MagangNavyDark else MagangRedBrown
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Catatan internal admin
                        Text(
                            text = "Catatan internal admin",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = MagangNavyDark
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Textarea
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = catatanAdmin,
                                onValueChange = { catatanAdmin = it },
                                textStyle = TextStyle(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Normal,
                                    color = MagangNavyDark
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Catatan hanya untuk admin, bukan pesan ke perusahaan.",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Normal,
                                color = MagangTextSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simpan Status Tempat Button (Navy primary button)
                        Button(
                            onClick = onSimpanStatus,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MagangNavyPrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Simpan Status Tempat",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Note below card
            item {
                Text(
                    text = "Status tempat dikelola admin. Mengaktifkan tempat tidak otomatis menerbitkan lowongan.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = MagangTextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Back button: Kembali ke Detail Tempat
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, MagangBorderColor, RoundedCornerShape(14.dp))
                        .clickable { onBackToDetailTempat() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kembali ke Detail Tempat",
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
fun KelolaStatusTempatPreview() {
    KelolaStatusTempatScreen()
}
