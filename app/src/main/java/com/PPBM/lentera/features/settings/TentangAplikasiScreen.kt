package com.PPBM.lentera.features.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.components.LenteraBottomBar
import com.PPBM.lentera.ui.components.LenteraTab
import com.PPBM.lentera.ui.theme.LenteraBg
import com.PPBM.lentera.ui.theme.LenteraBorder
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.LenteraTextPrimary
import com.PPBM.lentera.ui.theme.LenteraTextSecondary
import com.PPBM.lentera.ui.theme.PlusJakartaSans

@Composable
fun TentangAplikasiScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Tombol Kembali: "← Profil"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBackClick)
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.open_answer),
                    contentDescription = "Kembali",
                    tint = LenteraNavy,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(180f)
                )
                Text(
                    text = "Profil",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LenteraNavy
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Judul dan Subjudul
            Text(
                text = "Tentang Aplikasi",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = LenteraNavy
            )

            Text(
                text = "Credit Aplikasi",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Card Utama Tentang Aplikasi Sesuai Bantuan-1.png
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        spotColor = Color(0x09173B57),
                        ambientColor = Color(0x09173B57),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.support_symbol),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Masih butuh bantuan?",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LenteraNavy
                        )
                        Text(
                            text = "Tim LENTERA siap membantu.",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = LenteraTextSecondary
                        )
                    }
                }

                HorizontalDivider(
                    color = LenteraBorder,
                    thickness = 1.dp
                )

                Text(
                    text = "LENTERA adalah aplikasi layanan pengaduan dan portal magang terpadu untuk civitas akademika. Dirancang untuk menghadirkan keamanan, kenyamanan, serta kemudahan akses dalam kehidupan kampus.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = LenteraTextSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Versi Aplikasi: 1.0.0",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = "Pengembang: Tim PPBM Lentera",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = LenteraTextSecondary
                    )
                    Text(
                        text = "Hak Cipta © 2026 LENTERA. Seluruh Hak Dilindungi.",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Navigation Bar
        LenteraBottomBar(
            currentTab = LenteraTab.PROFIL,
            onTabSelected = onTabSelected
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewTentangAplikasiScreen() {
    TentangAplikasiScreen()
}
