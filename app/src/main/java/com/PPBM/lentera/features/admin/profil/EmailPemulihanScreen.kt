package com.PPBM.lentera.features.admin.profil

import androidx.compose.foundation.background
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
fun EmailPemulihanScreen(
    currentEmail: String = "halobandung@gmail.com",
    onBackClick: () -> Unit = {},
    onKirimVerifikasiClick: (String) -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var emailBaru by remember { mutableStateOf("") }

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
                        text = "EMAIL PEMULIHAN",
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
                text = "Kelola email pemulihan untuk mereset kata sandi dan menerima notifikasi keamanan penting.",
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

            // Section 1: Email Saat Ini
            Text(
                text = "Email saat ini",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE2ECF5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.andrsrxn_mail),
                                contentDescription = null,
                                tint = LenteraColors.NavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentEmail,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LenteraColors.NavyPrimary,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ditambahkan 1 tahun lalu",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LenteraColors.SlateText,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Terverifikasi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSans,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 2: Ubah Email Pemulihan
            Text(
                text = "Ubah Email pemulihan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Alamat Email Baru",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LenteraColors.NavyPrimary,
                            fontFamily = PlusJakartaSans,
                            fontSize = 15.sp
                        )
                    )

                    OutlinedTextField(
                        value = emailBaru,
                        onValueChange = { emailBaru = it },
                        placeholder = {
                            Text(
                                text = "Masukkan email baru",
                                color = LenteraColors.SlateText,
                                fontFamily = PlusJakartaSans,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.andrsrxn_mail),
                                contentDescription = null,
                                tint = LenteraColors.NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LenteraColors.NavyPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedTextColor = LenteraColors.NavyPrimary,
                            unfocusedTextColor = LenteraColors.NavyPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kami akan mengirimkan kode verifikasi ke email baru Anda untuk konfirmasi.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LenteraColors.SlateText,
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Kirim Kode Verifikasi
            Button(
                onClick = { onKirimVerifikasiClick(emailBaru) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LenteraColors.NavyPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Kirim kode verifikasi",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSans,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailPemulihanScreenPreview() {
    EmailPemulihanScreen()
}
