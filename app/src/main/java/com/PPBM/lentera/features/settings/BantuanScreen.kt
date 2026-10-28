package com.PPBM.lentera.features.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun BantuanScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    var expandedFaq1 by remember { mutableStateOf(true) }
    var expandedFaq2 by remember { mutableStateOf(false) }
    var expandedFaq3 by remember { mutableStateOf(false) }

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
                text = "BANTUAN",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = LenteraNavy
            )

            Text(
                text = "Panduan dan dukungan untuk kamu.",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Search Bar "Cari topik bantuan..."
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, LenteraBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Cari",
                    tint = LenteraTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Cari topik bantuan...",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = LenteraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2 Action Cards: Panduan Pengaduan & Panduan Magang
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Card 1: Panduan Pengaduan
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE2EBF4))
                        .clickable { /* Buka Panduan Pengaduan */ }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.message_square_plus),
                        contentDescription = null,
                        tint = LenteraNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Panduan Pengaduan",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = "Buat & pantau laporan",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary
                    )
                }

                // Card 2: Panduan Magang
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE2EBF4))
                        .clickable { /* Buka Panduan Magang */ }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.briefcase_business),
                        contentDescription = null,
                        tint = LenteraNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Panduan Magang",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LenteraNavy
                    )
                    Text(
                        text = "Cari & lamar magang",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = LenteraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Bagian Pertanyaan Umum
            Text(
                text = "Pertanyaan umum",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = LenteraNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card Accordion FAQ
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
                    .padding(16.dp)
            ) {
                // FAQ 1 (Default Expanded)
                FaqItem(
                    question = "Bagaimana cara mengubah data diri?",
                    answer = "Buka Profil → Data Diri, lalu pilih Ubah pada informasi pribadi. Untuk perubahan NIM atau program studi, hubungi admin kampus.",
                    isExpanded = expandedFaq1,
                    onToggle = { expandedFaq1 = !expandedFaq1 }
                )

                HorizontalDivider(
                    color = LenteraBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // FAQ 2
                FaqItem(
                    question = "Dokumen apa yang perlu disiapkan?",
                    answer = "Dokumen yang umumnya dibutuhkan antara lain Curriculum Vitae (CV) terbaru, Transkrip Nilai resmi dari kampus, dan Portofolio karya dalam format PDF (maks. 5MB).",
                    isExpanded = expandedFaq2,
                    onToggle = { expandedFaq2 = !expandedFaq2 }
                )

                HorizontalDivider(
                    color = LenteraBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // FAQ 3
                FaqItem(
                    question = "Bagaimana melihat status laporan?",
                    answer = "Status laporan pengaduan dapat Anda pantau secara langsung di beranda pada kartu LENTERA SIAGA atau pada menu Aktivitas Terbaru.",
                    isExpanded = expandedFaq3,
                    onToggle = { expandedFaq3 = !expandedFaq3 }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card Masih Butuh Bantuan?
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
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
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

                // Tombol Hubungi Dukungan
                Button(
                    onClick = { /* Hubungi Dukungan */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraNavy,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.message_circle),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Hubungi Dukungan",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Text(
                    text = "Senin-Jumat · 08.00–16.00 WIB",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary,
                    textAlign = TextAlign.Center
                )
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

@Composable
private fun FaqItem(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LenteraNavy,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.open_answer),
                contentDescription = null,
                tint = LenteraNavy,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (isExpanded) -90f else 90f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Text(
                text = answer,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = LenteraTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewBantuanScreen() {
    BantuanScreen()
}
