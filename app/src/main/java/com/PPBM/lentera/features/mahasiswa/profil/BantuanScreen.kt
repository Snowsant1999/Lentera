package com.PPBM.lentera.features.mahasiswa.profil

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.LenteraBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraBorder
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTextPrimary
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

data class FaqData(
    val id: Int,
    val question: String,
    val answer: String
)

@Composable
fun BantuanScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    var expandedFaqId by remember { mutableStateOf<Int?>(1) }

    var showPanduanPengaduan by remember { mutableStateOf(false) }
    var showPanduanMagang by remember { mutableStateOf(false) }
    var showDukunganDialog by remember { mutableStateOf(false) }

    val allFaqs = remember {
        listOf(
            FaqData(
                id = 1,
                question = "Bagaimana cara mengubah data diri?",
                answer = "Buka Profil → Data Diri, lalu pilih Ubah pada informasi pribadi. Untuk perubahan NIM atau program studi, hubungi admin kampus."
            ),
            FaqData(
                id = 2,
                question = "Dokumen apa yang perlu disiapkan?",
                answer = "Dokumen yang umumnya dibutuhkan antara lain Curriculum Vitae (CV) terbaru, Transkrip Nilai resmi dari kampus, dan Portofolio karya dalam format PDF (maks. 5MB)."
            ),
            FaqData(
                id = 3,
                question = "Bagaimana melihat status laporan?",
                answer = "Status laporan pengaduan dapat Anda pantau secara langsung di beranda pada kartu LENTERA SIAGA atau pada menu Aktivitas Terbaru."
            ),
            FaqData(
                id = 4,
                question = "Bagaimana keamanan privasi pelapor?",
                answer = "LENTERA menjamin kerahasiaan identitas setiap pelapor secara ketat. Anda juga dapat memilih opsi anonim saat mengirimkan laporan."
            )
        )
    }

    val filteredFaqs = allFaqs.filter {
        searchQuery.isBlank() ||
                it.question.contains(searchQuery, ignoreCase = true) ||
                it.answer.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
            .statusBarsPadding()
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
                BackArrowIcon(
                    modifier = Modifier.size(20.dp),
                    tint = LenteraNavy
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

            // Search Bar Interaktif
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, LenteraBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_alt),
                    contentDescription = "Cari",
                    tint = LenteraTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Cari topik bantuan...",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = LenteraTextSecondary
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LenteraNavy
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
                        .clickable { showPanduanPengaduan = true }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message_square_plus),
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
                        .clickable { showPanduanMagang = true }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_briefcase_business),
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
                if (filteredFaqs.isEmpty()) {
                    Text(
                        text = "Tidak ada hasil untuk \"$searchQuery\"",
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        color = LenteraTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    filteredFaqs.forEachIndexed { index, faq ->
                        FaqItem(
                            question = faq.question,
                            answer = faq.answer,
                            isExpanded = expandedFaqId == faq.id,
                            onToggle = {
                                expandedFaqId = if (expandedFaqId == faq.id) null else faq.id
                            }
                        )

                        if (index < filteredFaqs.size - 1) {
                            HorizontalDivider(
                                color = LenteraBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
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
                        painter = painterResource(id = R.drawable.ic_support_symbol),
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
                    onClick = { showDukunganDialog = true },
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
                            painter = painterResource(id = R.drawable.ic_message_circle),
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

    // Dialog Panduan Pengaduan
    if (showPanduanPengaduan) {
        PanduanInfoDialog(
            title = "Panduan Pengaduan",
            steps = listOf(
                "Pilih menu Pengaduan pada navigasi bawah atau tombol Buat Laporan.",
                "Pilih kategori pengaduan (Fasilitas, Akademik, atau Keamanan & Kesejahteraan).",
                "Tuliskan rincian kejadian dan lampirkan bukti foto jika ada.",
                "Kirim laporan dan pantau proses verifikasi hingga penanganan selesai."
            ),
            onDismiss = { showPanduanPengaduan = false }
        )
    }

    // Dialog Panduan Magang
    if (showPanduanMagang) {
        PanduanInfoDialog(
            title = "Panduan Magang Hub",
            steps = listOf(
                "Masuk ke tab Magang Hub untuk menelusuri mitra dan lowongan aktif.",
                "Siapkan dokumen di menu Dokumen Saya (CV, Transkrip, Portofolio).",
                "Pilih posisi magang yang sesuai dan klik Ajukan Lamaran.",
                "Periksa status lamaran secara berkala di kartu Lamaran Saya."
            ),
            onDismiss = { showPanduanMagang = false }
        )
    }

    // Dialog Hubungi Dukungan
    if (showDukunganDialog) {
        DukunganDialog(
            onDismiss = { showDukunganDialog = false }
        )
    }
}

@Composable
private fun PanduanInfoDialog(
    title: String,
    steps: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LenteraNavy
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                steps.forEachIndexed { i, step ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2EBF4))
                        ) {
                            Text(
                                text = "${i + 1}",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = LenteraNavy
                            )
                        }
                        Text(
                            text = step,
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            color = LenteraTextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LenteraNavy,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Mengerti",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DukunganDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pusat Bantuan LENTERA",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LenteraNavy
            )

            Text(
                text = "Silakan hubungi kami melalui salah satu saluran berikut:",
                fontFamily = PlusJakartaSans,
                fontSize = 13.sp,
                color = LenteraTextSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DukunganItem(label = "WhatsApp Helpdesk", value = "+62 812-3456-7890")
                DukunganItem(label = "Email Layanan", value = "bantuan@lentera.ac.id")
                DukunganItem(label = "Jam Layanan", value = "Senin - Jumat (08.00 - 16.00 WIB)")
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LenteraNavy,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Tutup",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DukunganItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAFC))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            color = LenteraTextSecondary
        )
        Text(
            text = value,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = LenteraNavy
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
                painter = painterResource(id = R.drawable.ic_open_answer),
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
