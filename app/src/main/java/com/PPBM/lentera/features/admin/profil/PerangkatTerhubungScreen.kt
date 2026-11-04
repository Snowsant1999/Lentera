package com.PPBM.lentera.features.admin.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

data class PerangkatItem(
    val id: String,
    val nama: String,
    val lokasi: String,
    val isPhone: Boolean,
    val statusText: String? = null,
    val isCurrentDevice: Boolean = false
)

@Composable
fun PerangkatTerhubungScreen(
    currentDevice: PerangkatItem = PerangkatItem(
        id = "1",
        nama = "MacBook Air M1",
        lokasi = "Samarinda, Kalimantan Timur",
        isPhone = false,
        statusText = "Sedang Aktif",
        isCurrentDevice = true
    ),
    otherDevices: List<PerangkatItem> = listOf(
        PerangkatItem(
            id = "2",
            nama = "Android – Lentera App",
            lokasi = "Samarinda, Kalimantan Timur",
            isPhone = true,
            statusText = "Terakhir Aktif : Hari ini, 09.41"
        ),
        PerangkatItem(
            id = "3",
            nama = "Windows – Lentera App",
            lokasi = "Samarinda, Kalimantan Timur",
            isPhone = false,
            statusText = "Terakhir Aktif : Hari ini, 09.41"
        )
    ),
    onBackClick: () -> Unit = {},
    onCabutAksesClick: (PerangkatItem) -> Unit = {},
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
                        text = "PERANGKAT TERHUBUNG",
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
                text = "Tinjau perangkat yang telah login ke akun Anda. Segera cabut akses jika ada perangkat yang tidak dikenali.",
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

            // Section 1: Perangkat Saat Ini
            Text(
                text = "Perangkat Saat Ini",
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE9EEF3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (currentDevice.isPhone) R.drawable.bi_phone_fill else R.drawable.bi_laptop
                            ),
                            contentDescription = null,
                            tint = LenteraColors.NavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentDevice.nama,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = LenteraColors.NavyPrimary,
                                fontFamily = PlusJakartaSans,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentDevice.lokasi,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LenteraColors.SlateText,
                                fontFamily = PlusJakartaSans,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentDevice.statusText ?: "Sedang Aktif",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF22C55E),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 2: Perangkat Lainnya
            Text(
                text = "Perangkat Lainnya",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp
                )
            )

            otherDevices.forEach { device ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE9EEF3), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (device.isPhone) R.drawable.bi_phone_fill else R.drawable.bi_laptop
                                ),
                                contentDescription = null,
                                tint = LenteraColors.NavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = device.nama,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LenteraColors.NavyPrimary,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = device.lokasi,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LenteraColors.SlateText,
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 13.sp
                                )
                            )
                            if (device.statusText != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = device.statusText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = LenteraColors.SlateText,
                                        fontFamily = PlusJakartaSans,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                        IconButton(
                            onClick = { onCabutAksesClick(device) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.akar_icons_sign_out),
                                contentDescription = "Cabut Akses",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerangkatTerhubungScreenPreview() {
    PerangkatTerhubungScreen()
}
