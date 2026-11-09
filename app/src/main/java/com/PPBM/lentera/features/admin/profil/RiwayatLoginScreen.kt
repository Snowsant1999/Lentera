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

data class RiwayatLoginItem(
    val id: String,
    val namaPerangkat: String,
    val lokasi: String,
    val waktu: String,
    val isPhone: Boolean,
    val isCurrentSession: Boolean = false
)

@Composable
fun RiwayatLoginScreen(
    riwayatList: List<RiwayatLoginItem> = listOf(
        RiwayatLoginItem(
            id = "1",
            namaPerangkat = "${android.os.Build.MANUFACTURER.orEmpty().replaceFirstChar { it.uppercase() }} ${android.os.Build.MODEL.orEmpty()} (Lentera App)".trim(),
            lokasi = "Samarinda, Kalimantan Timur",
            waktu = "Sesi Aktif Sekarang",
            isPhone = true,
            isCurrentSession = true
        )
    ),
    onBackClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            AdminBottomBar(
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
                        text = "RIWAYAT LOGIN",
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
                text = "Pantau aktivitas login akun Anda selama 30 hari terakhir.",
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

            // Section: Perangkat Saat Ini
            Text(
                text = "Perangkat Saat Ini",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 16.sp
                )
            )

            // Card Container with Dividers
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LenteraColors.CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    riwayatList.forEachIndexed { index, item ->
                        RiwayatLoginRow(item = item)
                        if (index < riwayatList.size - 1) {
                            HorizontalDivider(
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RiwayatLoginRow(item: RiwayatLoginItem) {
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
                    id = if (item.isPhone) R.drawable.bi_phone_fill else R.drawable.bi_laptop
                ),
                contentDescription = null,
                tint = LenteraColors.NavyPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.namaPerangkat,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LenteraColors.NavyPrimary,
                    fontFamily = PlusJakartaSans,
                    fontSize = 15.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map_pin),
                    contentDescription = null,
                    tint = LenteraColors.SlateText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.lokasi,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LenteraColors.SlateText,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.akar_icons_clock),
                    contentDescription = null,
                    tint = LenteraColors.SlateText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.waktu,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LenteraColors.SlateText,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp
                    )
                )
            }
        }
        if (item.isCurrentSession) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Saat Ini",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RiwayatLoginScreenPreview() {
    RiwayatLoginScreen()
}
