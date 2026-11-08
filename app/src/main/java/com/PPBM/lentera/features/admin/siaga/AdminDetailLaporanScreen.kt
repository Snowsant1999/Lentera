package com.PPBM.lentera.features.admin.siaga
import com.PPBM.lentera.features.admin.siaga.components.*
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.backend.admin.model.AdminLaporan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

/**
 * FRAME 2: Detail Laporan
 * Menampilkan ringkasan laporan, menu kelola laporan, informasi pelapor anonim,
 * kronologi, lampiran bukti, timeline status, dan tombol aksi ke verifikasi.
 */
@Composable
fun AdminDetailLaporanScreen(
    laporan: AdminLaporan? = null,
    onNavigateToVerifikasi: () -> Unit = {},
    onNavigateToMintaInformasi: () -> Unit = {},
    onNavigateToPenugasan: () -> Unit = {},
    onNavigateToCatatanTindakan: () -> Unit = {},
    onNavigateToHasilPenyelesaian: () -> Unit = {},
    onBackToList: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val nomorLaporan = laporan?.nomorLaporan ?: "LTR-..."
    val judulLaporan = laporan?.judul ?: "Laporan"
    val kategori = laporan?.kategori ?: "Pengaduan"
    val tanggalKejadian = laporan?.tanggalKejadian ?: "-"
    val lokasiKejadian = laporan?.lokasiKejadian ?: "-"
    val kronologi = laporan?.kronologi ?: "-"
    val isAnonim = laporan?.isAnonim ?: true
    val pelaporDisplay = if (isAnonim) "Anonim" else (laporan?.pelaporNama ?: "Mahasiswa")
    val statusStr = (laporan?.status ?: "baru").replaceFirstChar { it.uppercase() }
    val (statusBg, statusColor) = when (laporan?.status?.lowercase()) {
        "baru" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "diproses", "penanganan", "investigasi" -> Pair(Color(0xFFFFE4E6), Color(0xFFE11D48))
        "selesai" -> Pair(Color(0xFFE8F4F1), Color(0xFF2F7078))
        "ditolak" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SiagaBgColor)
    ) {
        // 1. Header
        AdminSiagaHeader(
            title = "DETAIL LAPORAN",
            onBackClick = onBackToList
        )

        // 2. Konten Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Ringkasan Laporan Teratas
            LaporanSummaryHeaderCard(
                id = nomorLaporan,
                statusText = statusStr,
                statusBg = statusBg,
                statusColor = statusColor,
                title = judulLaporan,
                subtitle = "Tanggal Kejadian: $tanggalKejadian · ${if (isAnonim) "Anonim" else "Terverifikasi"}"
            )

            // Kartu: Kelola Laporan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Kelola Laporan",
                            tint = SiagaTealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kelola Laporan",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val menuItems = listOf(
                        Triple("Verifikasi Kelengkapan", "", onNavigateToVerifikasi),
                        Triple("Minta Informasi", "Bila data kurang", onNavigateToMintaInformasi),
                        Triple("Penugasan", "Setelah validasi", onNavigateToPenugasan),
                        Triple("Catatan Tindakan", "Saat penanganan", onNavigateToCatatanTindakan),
                        Triple("Hasil & Penyelesaian", "Saat penanganan", onNavigateToHasilPenyelesaian)
                    )

                    menuItems.forEach { (title, subtitle, onClick) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(color = Color(0xFFEFF5FD), shape = RoundedCornerShape(10.dp))
                                .clickable { onClick() }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SiagaNavyDark
                                )
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (subtitle.isNotEmpty()) {
                                    Text(
                                        text = subtitle,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontFamily = PlusJakartaFont,
                                            color = SiagaTextSecondary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right),
                                    contentDescription = "Buka",
                                    tint = SiagaNavyDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Saat penanganan, buka hasil untuk menyelesaikan laporan. Setelah selesai, hasil hanya dibaca. Membuka menu tidak mengubah status.",
                        style = TextStyle(
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )
                }
            }

            // Kartu: Informasi Pelapor
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informasi pelapor",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pelapor",
                        style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )
                    Text(
                        text = pelaporDisplay,
                        style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.Bold, color = SiagaNavyDark)
                    )

                    if (isAnonim) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AdminInfoBanner(
                            text = "Identitas pelapor dirahasiakan (Anonim). Komunikasi dilakukan melalui nomor laporan, tanpa membuka nama atau NIM."
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kategori",
                        style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )
                    Text(
                        text = kategori,
                        style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.Bold, color = SiagaNavyDark)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_date),
                            contentDescription = "Tanggal",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tanggal kejadian",
                            style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                        )
                    }
                    Text(
                        text = tanggalKejadian,
                        style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.SemiBold, color = SiagaNavyDark),
                        modifier = Modifier.padding(start = 22.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Lokasi kejadian",
                        style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )
                    Text(
                        text = lokasiKejadian,
                        style = TextStyle(fontSize = 14.sp, fontFamily = PlusJakartaFont, fontWeight = FontWeight.SemiBold, color = SiagaNavyDark)
                    )
                }
            }

            // Kartu: Kronologi Kejadian
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kronologi kejadian",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = kronologi,
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaNavyDark
                        )
                    )
                }
            }

            // Kartu: Bukti yang Dilampirkan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bukti yang dilampirkan",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tidak ada lampiran berkas dokumen tambahan.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaFont,
                            color = SiagaTextSecondary
                        )
                    )
                }
            }

            // Kartu: Timeline Status
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = "Timeline",
                            tint = SiagaNavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Timeline Status",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = PlusJakartaFont,
                                fontWeight = FontWeight.Bold,
                                color = SiagaNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_timeline_marker),
                            contentDescription = "Status dot",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp, 48.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Laporan dikirim",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = SiagaNavyDark
                                )
                            )
                            Text(
                                text = laporan?.createdAt?.take(16)?.replace("T", " ") ?: "Terkirim",
                                style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_timeline_marker_dot),
                            contentDescription = "Status dot",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp, 20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Status saat ini: $statusStr",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PlusJakartaFont,
                                    fontWeight = FontWeight.Bold,
                                    color = SiagaNavyDark
                                )
                            )
                            Text(
                                text = if (!laporan?.unitPenanganan.isNullOrBlank()) "Ditugaskan ke: ${laporan?.unitPenanganan}" else "Pembaruan: ${laporan?.updatedAt?.take(16)?.replace("T", " ") ?: "-"}",
                                style = TextStyle(fontSize = 12.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                            )
                        }
                    }
                }
            }

            // Kartu: Verifikasi Kelengkapan Aksi
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Verifikasi kelengkapan",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.Bold,
                            color = SiagaNavyDark
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Status: $statusStr · ${if (laporan?.status == "selesai") "Laporan telah ditutup & diselesaikan" else "Perlu pemeriksaan & penanganan admin"}",
                        modifier = Modifier
                            .background(color = statusBg, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = PlusJakartaFont,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Periksa kronologi, kategori, waktu, lokasi dan bukti sebelum memvalidasi laporan.",
                        style = TextStyle(fontSize = 12.sp, lineHeight = 18.sp, fontFamily = PlusJakartaFont, color = SiagaTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onNavigateToVerifikasi,
                        colors = ButtonDefaults.buttonColors(containerColor = SiagaTealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Lanjut ke Verifikasi",
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

            // Tombol: Kembali ke Daftar Laporan
            OutlinedButton(
                onClick = onBackToList,
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(SiagaBorderColor)
                ),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Kembali ke Daftar Laporan",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }

        // 3. Bottom Bar
        AdminSiagaBottomBar(currentRoute = "Laporan", onNavClick = onNavClick)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AdminDetailLaporanScreenPreview() {
    AdminDetailLaporanScreen()
}

