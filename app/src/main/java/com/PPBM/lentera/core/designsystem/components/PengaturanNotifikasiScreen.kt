package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.LenteraBorder
import com.PPBM.lentera.core.designsystem.theme.LenteraColors
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTeal
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import com.PPBM.lentera.core.notification.LenteraNotificationHelper

/**
 * Layar Pengaturan Notifikasi Khusus Lentera (N1.8).
 * Menampilkan status izin notifikasi, konfigurasi saluran lentera_alerts_v1,
 * dan tombol uji bunyi langsung di HP pengguna.
 */
@Composable
fun PengaturanNotifikasiScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(false) }

    fun refreshPermission() {
        isPermissionGranted = LenteraNotificationHelper.isNotificationPermissionGranted(context)
    }

    LaunchedEffect(Unit) {
        LenteraNotificationHelper.createNotificationChannel(context)
        refreshPermission()
    }

    Scaffold(
        containerColor = LenteraColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        com.PPBM.lentera.core.designsystem.icons.BackArrowIcon(
                            tint = LenteraNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "PENGATURAN NOTIFIKASI",
                            style = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LenteraNavy,
                                fontFamily = PlusJakartaSans
                            )
                        )
                        Text(
                            text = "Suara & Izin Notifikasi Khas LENTERA",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = LenteraTextSecondary,
                                fontFamily = PlusJakartaSans
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Status Izin Notifikasi HP
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isPermissionGranted) Color(0xFFE8F5E9) else Color(0xFFFEF3C7),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPermissionGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isPermissionGranted) Color(0xFF2E7D32) else Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Izin Notifikasi HP",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LenteraNavy,
                                    fontFamily = PlusJakartaSans
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isPermissionGranted) "Aktif & Diizinkan" else "Belum Diaktifkan",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isPermissionGranted) Color(0xFF2E7D32) else Color(0xFFD97706),
                                    fontFamily = PlusJakartaSans
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Izin notifikasi diperlukan agar kabar laporan Siaga dan tahapan magang langsung tampil di status bar HP kamu.",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = LenteraTextSecondary,
                            fontFamily = PlusJakartaSans
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            LenteraNotificationHelper.openNotificationSettings(context)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Buka Pengaturan Notifikasi HP",
                            color = LenteraNavy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSans
                        )
                    }
                }
            }

            // 2. Konfigurasi Saluran & Suara Khas Lentera
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Saluran Khas LENTERA",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LenteraNavy,
                            fontFamily = PlusJakartaSans
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Saluran ID",
                                fontSize = 11.sp,
                                color = LenteraTextSecondary,
                                fontFamily = PlusJakartaSans
                            )
                            Text(
                                text = LenteraNotificationHelper.CHANNEL_ID,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LenteraNavy,
                                fontFamily = PlusJakartaSans
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Audio Notif",
                                fontSize = 11.sp,
                                color = LenteraTextSecondary,
                                fontFamily = PlusJakartaSans
                            )
                            Text(
                                text = "notif_sound.wav",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LenteraTeal,
                                fontFamily = PlusJakartaSans
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Uji Bunyi
                    Button(
                        onClick = {
                            LenteraNotificationHelper.playTestSound(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LenteraTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Uji Bunyi Suara Lentera",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSans
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Kirim Notifikasi Uji
                    OutlinedButton(
                        onClick = {
                            LenteraNotificationHelper.triggerTestNotification(context)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = LenteraNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kirim Notifikasi Uji ke Status Bar",
                            color = LenteraNavy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSans
                        )
                    }
                }
            }

            // 3. Panduan Hemat Baterai Pabrikan HP
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tips Notifikasi di HP Tertutup",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LenteraNavy,
                            fontFamily = PlusJakartaSans
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pada beberapa merek HP (Xiaomi, Oppo, Vivo, Samsung), pastikan fitur hemat baterai untuk Lentera disetel ke 'Tidak ada pembatasan' agar notifikasi dapat langsung berbunyi saat aplikasi ditutup.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = LenteraTextSecondary,
                            fontFamily = PlusJakartaSans
                        )
                    )
                }
            }
        }
    }
}
