package com.PPBM.lentera.features.admin.magang.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R

// ==========================================
// COLOR SYSTEM - ADMIN MAGANG
// ==========================================
val MagangBgColor = Color(0xFFEFF5F9)
val MagangNavyDark = Color(0xFF173B57)
val MagangNavyPrimary = Color(0xFF23496E)
val MagangGoldenOrange = Color(0xFFE5A95C)
val MagangCreamLight = Color(0xFFFFF8EC)
val MagangCreamBox = Color(0xFFFFF0D6)
val MagangPillOrange = Color(0xFFFDE8C7)
val MagangPillInactive = Color(0xFFE2EBF2)
val MagangTextSecondary = Color(0xFF64748B)
val MagangTextMuted = Color(0xFF94A3B8)
val MagangBorderColor = Color(0xFFDCE6EE)
val MagangCardBg = Color(0xFFFFFFFF)
val MagangActiveGreenBg = Color(0xFFD1FAE5)
val MagangActiveGreenText = Color(0xFF065F46)
val MagangRedBrown = Color(0xFF991B1B)

val PlusJakartaFont = FontFamily(Font(R.font.plus_jakarta_sans))

// ==========================================
// NAVIGATION ROUTES
// ==========================================
enum class AdminMagangRoute {
    TEMPAT_MAGANG,
    DAFTAR_LOWONGAN,
    DETAIL_TEMPAT_MAGANG,
    DETAIL_LOWONGAN,
    TAMBAH_TEMPAT_MAGANG,
    FORM_LOWONGAN,
    KELOLA_STATUS_TEMPAT,
    PUBLIKASI_LOWONGAN
}

// ==========================================
// SHARED COMPONENTS
// ==========================================

@Composable
fun AdminMagangHeader(
    title: String,
    subtitle: String = "LENTERA · Admin Kampus",
    onBackClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MagangBgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Kembali",
                tint = MagangNavyDark,
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
                    color = MagangNavyDark
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = FontWeight.Normal,
                    color = MagangTextSecondary
                )
            )
        }
    }
}

@Composable
fun MagangSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Cari perusahaan atau lokasi",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search_icon),
            contentDescription = "Cari",
            tint = MagangTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaFont,
                        color = MagangTextSecondary
                    )
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaFont,
                    color = MagangNavyDark
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun MagangFilterChips(
    chips: List<String>,
    selectedIndex: Int,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MagangGoldenOrange else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onChipSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaFont,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) MagangNavyDark else MagangNavyDark
                    )
                )
            }
        }
    }
}

@Composable
fun MagangInfoBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MagangCreamBox, shape = RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_information),
            contentDescription = "Informasi",
            tint = MagangNavyDark,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontFamily = PlusJakartaFont,
                fontWeight = FontWeight.Normal,
                color = MagangNavyDark
            )
        )
    }
}

@Composable
fun AdminMagangBottomBar(
    currentRoute: String = "Magang",
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
        // 1. Beranda: ic_nav_beranda
        MagangBottomNavItem(
            iconRes = R.drawable.ic_nav_beranda,
            label = "Beranda",
            isSelected = currentRoute == "Beranda",
            onClick = { onTabSelected("Beranda") }
        )

        // 2. Mahasiswa: ic_nav_mahasiswa
        MagangBottomNavItem(
            iconRes = R.drawable.ic_nav_mahasiswa,
            label = "Mahasiswa",
            isSelected = currentRoute == "Mahasiswa",
            onClick = { onTabSelected("Mahasiswa") }
        )

        // 3. Laporan: ic_nav_laporan
        MagangBottomNavItem(
            iconRes = R.drawable.ic_nav_laporan,
            label = "Laporan",
            isSelected = currentRoute == "Laporan",
            onClick = { onTabSelected("Laporan") }
        )

        // 4. Magang: ic_nav_magang (Briefcase, Active with Navy Pill)
        MagangBottomNavItem(
            iconRes = R.drawable.ic_nav_magang,
            label = "Magang",
            isSelected = currentRoute == "Magang",
            isHighlightPill = true,
            onClick = { onTabSelected("Magang") }
        )

        // 5. Profil: ic_nav_profil
        MagangBottomNavItem(
            iconRes = R.drawable.ic_nav_profil,
            label = "Profil",
            isSelected = currentRoute == "Profil",
            onClick = { onTabSelected("Profil") }
        )
    }
}

@Composable
private fun MagangBottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    isHighlightPill: Boolean = false,
    onClick: () -> Unit
) {
    if (isHighlightPill && isSelected) {
        Box(
            modifier = Modifier
                .background(color = MagangNavyPrimary, shape = RoundedCornerShape(16.dp))
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
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = if (isSelected) MagangNavyDark else MagangNavyPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontFamily = PlusJakartaFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MagangNavyDark else MagangNavyPrimary
                )
            )
        }
    }
}
