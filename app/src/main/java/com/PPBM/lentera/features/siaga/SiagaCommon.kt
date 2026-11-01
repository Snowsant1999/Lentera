package com.PPBM.lentera.features.siaga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

// ==========================================
// PALET WARNA & TIPOGRAFI DESIGN SYSTEM
// ==========================================
val SiagaBgColor = Color(0xFFEFF5F9)
val SiagaNavyDark = Color(0xFF173B57)
val SiagaNavyPrimary = Color(0xFF23496E)
val SiagaTealPrimary = Color(0xFF2F7078)
val SiagaTealLight = Color(0xFFE8F4F1)
val SiagaTextSecondary = Color(0xFF64748B)
val SiagaTextMuted = Color(0xFF94A3B8)
val SiagaBorderColor = Color(0xFFDCE6EE)
val SiagaCardBg = Color(0xFFFFFFFF)

val PlusJakartaFont = FontFamily(Font(R.font.plus_jakarta_sans))

// ==========================================
// DATA MODELS & ENUMS
// ==========================================
data class LaporanItem(
    val id: String,
    val judul: String,
    val tanggal: String,
    val status: String,
    val statusBgColor: Color,
    val statusTextColor: Color
)

enum class AdminSiagaRoute {
    LAPORAN_MASUK,
    DETAIL_LAPORAN,
    VERIFIKASI_KELENGKAPAN,
    MINTA_INFORMASI,
    PENUGASAN_LAPORAN,
    CATATAN_TINDAKAN,
    HASIL_PENYELESAIAN
}

// ==========================================
// KOMPONEN UMUM YANG DIGUNAKAN BERSAMA
// ==========================================

@Composable
fun AdminSiagaHeader(
    title: String,
    subtitle: String = "LENTERA · Admin Kampus",
    onBackClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(SiagaBgColor)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Kembali",
                tint = SiagaNavyDark,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.ExtraBold,
                    color = SiagaNavyDark
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Normal,
                    color = SiagaTextSecondary
                )
            )
        }
    }
}

@Composable
fun AdminInfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SiagaTealLight, shape = RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = "Info",
            tint = SiagaTealPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontFamily = PlusJakartaFont,
                fontWeight = FontWeight.Medium,
                color = SiagaTealPrimary
            )
        )
    }
}

@Composable
fun LaporanSummaryHeaderCard(
    id: String = "LTR-2026-00123",
    statusText: String = "Diperiksa",
    statusBg: Color = Color(0xFFFFEDD5),
    statusColor: Color = Color(0xFFC2410C),
    title: String = "Pelecehan Verbal",
    subtitle: String = "Dikirim 15 Oktober 2026, 15:00 WITA · Anonim",
    extraRow: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SiagaCardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = id,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Bold,
                        color = SiagaNavyDark
                    )
                )

                Text(
                    text = statusText,
                    modifier = Modifier
                        .background(color = statusBg, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Bold,
                    color = SiagaNavyDark
                )
            )

            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.Normal,
                        color = SiagaTextSecondary
                    )
                )
            }

            extraRow?.invoke()
        }
    }
}

@Composable
fun AdminSiagaBottomBar(
    currentRoute: String = "Laporan",
    onTabSelected: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = Color(0xFFE2E8F0))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Beranda: ic_nav_beranda (house icon - tintable)
        BottomNavItem(
            iconRes = R.drawable.ic_nav_beranda,
            label = "Beranda",
            isSelected = currentRoute == "Beranda",
            onClick = { onTabSelected("Beranda") }
        )

        // 2. Mahasiswa: ic_nav_mahasiswa (person icon - tintable)
        BottomNavItem(
            iconRes = R.drawable.ic_nav_mahasiswa,
            label = "Mahasiswa",
            isSelected = currentRoute == "Mahasiswa",
            onClick = { onTabSelected("Mahasiswa") }
        )

        // 3. Laporan: ic_nav_laporan (speech bubble + plus - tintable, highlighted pill saat aktif)
        BottomNavItem(
            iconRes = R.drawable.ic_nav_laporan,
            label = "Laporan",
            isSelected = currentRoute == "Laporan",
            isHighlightPill = true,
            onClick = { onTabSelected("Laporan") }
        )

        // 4. Magang: ic_nav_magang (briefcase icon - tintable)
        BottomNavItem(
            iconRes = R.drawable.ic_nav_magang,
            label = "Magang",
            isSelected = currentRoute == "Magang",
            onClick = { onTabSelected("Magang") }
        )

        // 5. Profil: ic_nav_profil (user circle icon - tintable)
        BottomNavItem(
            iconRes = R.drawable.ic_nav_profil,
            label = "Profil",
            isSelected = currentRoute == "Profil",
            onClick = { onTabSelected("Profil") }
        )
    }
}

@Composable
private fun BottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    isHighlightPill: Boolean = false,
    onClick: () -> Unit
) {
    if (isHighlightPill && isSelected) {
        Box(
            modifier = Modifier
                .background(color = SiagaNavyPrimary, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onClick() }
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = if (isSelected) SiagaNavyPrimary else SiagaNavyDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) SiagaNavyPrimary else SiagaNavyDark
                )
            )
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Header – Laporan Masuk", showBackground = true, backgroundColor = 0xFFEFF5F9)
@Composable
private fun PreviewAdminSiagaHeader() {
    AdminSiagaHeader(
        title = "Laporan Masuk",
        subtitle = "LENTERA · Admin Kampus",
        onBackClick = {}
    )
}

@Preview(name = "Info Banner", showBackground = true, backgroundColor = 0xFFEFF5F9)
@Composable
private fun PreviewAdminInfoBanner() {
    Box(modifier = Modifier.padding(16.dp)) {
        AdminInfoBanner(
            text = "Laporan bersifat rahasia. Akses hanya untuk admin dan petugas berwenang; identitas anonim tetap dirahasiakan."
        )
    }
}

@Preview(name = "Laporan Summary Card – Baru", showBackground = true, backgroundColor = 0xFFEFF5F9)
@Composable
private fun PreviewLaporanSummaryHeaderCardBaru() {
    Box(modifier = Modifier.padding(16.dp)) {
        LaporanSummaryHeaderCard(
            id = "LTR-2026-00126",
            statusText = "Baru",
            statusBg = Color(0xFFDCFCE7),
            statusColor = Color(0xFF166534),
            title = "Lampu Koridor Padam",
            subtitle = "17 Okt 2026 · 08:15 WITA · Identitas tersebut"
        )
    }
}

@Preview(name = "Laporan Summary Card – Diperiksa", showBackground = true, backgroundColor = 0xFFEFF5F9)
@Composable
private fun PreviewLaporanSummaryHeaderCardDiperiksa() {
    Box(modifier = Modifier.padding(16.dp)) {
        LaporanSummaryHeaderCard(
            id = "LTR-2026-00123",
            statusText = "Diperiksa",
            statusBg = Color(0xFFFFEDD5),
            statusColor = Color(0xFFC2410C),
            title = "Pelecehan Verbal",
            subtitle = "Dikirim 15 Oktober 2026, 15:00 WITA · Anonim"
        )
    }
}

@Preview(name = "Bottom Bar – Tab Laporan Aktif", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAdminSiagaBottomBarLaporanAktif() {
    AdminSiagaBottomBar(
        currentRoute = "Laporan",
        onTabSelected = {}
    )
}

@Preview(name = "Bottom Bar – Tab Beranda Aktif", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAdminSiagaBottomBarBerandaAktif() {
    AdminSiagaBottomBar(
        currentRoute = "Beranda",
        onTabSelected = {}
    )
}

@Preview(name = "Bottom Bar – Tab Profil Aktif", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewAdminSiagaBottomBarProfilAktif() {
    AdminSiagaBottomBar(
        currentRoute = "Profil",
        onTabSelected = {}
    )
}
