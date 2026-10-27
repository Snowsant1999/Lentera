package com.PPBM.lentera.features.siaga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

@Preview(showBackground = true, backgroundColor = 0xFFEFFBFD)
@Composable
fun LaporanMasukScreenPreview() {
    LaporanMasukScreen()
}
data class LaporanItem(
    val id: String,
    val judul: String,
    val tanggal: String,
    val status : String,
    val statusBgColor : Color,
    val statusTextColor: Color
)

@Composable
fun LaporanMasukScreen(modifier: Modifier = Modifier) {
    val daftarLaporan = listOf(
        LaporanItem("LTR-2026-00126", "Lampu Koridor Padam", "17 Okt 2026 • 08:15 WITA • Identitas terbatas", "Baru", Color(0xFFFFF7D0), Color(0xFFB78300)),
        LaporanItem("LTR-2026-00123", "Pelecehan Verbal", "15 Okt 2026 • 15:00 WITA • Anonim", "Diproses", Color(0xFFFFEDD5), Color(0xFFC2410C)),
        LaporanItem("LTR-2026-00122", "Keracunan Makanan", "12 Okt 2026 • 13:20 WITA • Identitas terbatas", "Penanganan", Color(0xFFFFE4E6), Color(0xFFE11D48)),
        LaporanItem("LTR-2026-00120", "Pintu Mau Roboh", "10 Okt 2026 • 09:10 WITA • Identitas terbatas", "Selesai", Color(0xFFE8F4F1), Color(0xFF2F7078))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFEFFBFD)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // --- 1. HEADER (IKON KEMBALI & JUDUL) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                modifier = Modifier
                    .padding(1.dp)
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "LAPORAN MASUK",
                style = TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 33.6.sp,
                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                    fontWeight = FontWeight(800),
                    color = Color(0xFF173B57)
                )
            )
        }
        // --- 1.5. KOTAK INFO HIJAU ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = Color(0xFFE8F4F1), // Warna hijau muda (bisa disesuaikan hex-nya dari Figma)
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.Top // Agar ikon dan teks sejajar di atas
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = Color(0xFF2F7078), // Warna hijau gelap khas LENTERA
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Laporan bersifat rahasia. Akses hanya untuk admin dan petugas berwenang; identitas anonim tetap disembunyikan.",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF2F7078),
                    lineHeight = 18.sp
                )
            )
        }

        // --- 2. DERETAN FILTER STATUS ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp, end = 4.dp)
        ) {
            // Tombol "Semua 24"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color(0xFF2F7078),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Semua 24",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(600),
                        color = Color.White
                    )
                )
            }

            // Tombol "Baru 3"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(start = 12.dp, top = 9.dp, end = 12.dp, bottom = 9.dp)
            ) {
                Text(
                    text = "Baru 3",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF64748B)
                    )
                )
            }

            // Tombol "Proses 5"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(start = 12.dp, top = 9.dp, end = 12.dp, bottom = 9.dp)
            ) {
                Text(
                    text = "Proses 5",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF64748B)
                    )
                )
            }
        }

        // --- 3. KOTAK PENCARIAN (SEARCH BAR) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 8.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cari",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Cari nomor atau judul laporan",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF94A3B8)
                )
            )
        }

        // --- 4. LAZYCOLUMN DAFTAR LAPORAN ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(daftarLaporan) { laporan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Baris 1: Nomor Laporan & Badge Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = laporan.id,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFF2F7078)
                                )
                            )

                            // Badge Status
                            Text(
                                text = laporan.status,
                                modifier = Modifier
                                    .background(
                                        color = laporan.statusBgColor,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                    fontWeight = FontWeight(700),
                                    color = laporan.statusTextColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Baris 2: Judul Laporan & Ikon Panah
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = laporan.judul,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFF173B57)
                                )
                            )

                            // Ikon Panah Kanan
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Lihat Detail",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Baris 3: Tanggal & Keterangan
                        Text(
                            text = laporan.tanggal,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.plus_jakarta_sans)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }
            }
        }
    }
}