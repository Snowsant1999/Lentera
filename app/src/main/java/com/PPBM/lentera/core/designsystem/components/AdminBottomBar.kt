package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.LenteraColors
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

@Composable
fun AdminBottomBar(
    selected: LenteraNavItem,
    onItemClick: (LenteraNavItem) -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_beranda),
                    contentDescription = "Beranda",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Beranda", fontSize = 11.sp, fontFamily = PlusJakartaSans) },
            selected = selected == LenteraNavItem.BERANDA,
            onClick = { onItemClick(LenteraNavItem.BERANDA) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LenteraColors.SlateText,
                unselectedTextColor = LenteraColors.SlateText,
                selectedIconColor = Color.White,
                selectedTextColor = LenteraColors.NavyPrimary,
                indicatorColor = LenteraColors.NavyPrimary
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_mahasiswa),
                    contentDescription = "Mahasiswa",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Mahasiswa", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSans) },
            selected = selected == LenteraNavItem.MAHASISWA,
            onClick = { onItemClick(LenteraNavItem.MAHASISWA) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LenteraColors.SlateText,
                unselectedTextColor = LenteraColors.SlateText,
                selectedIconColor = Color.White,
                selectedTextColor = LenteraColors.NavyPrimary,
                indicatorColor = LenteraColors.NavyPrimary
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_laporan),
                    contentDescription = "Laporan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Laporan", fontSize = 11.sp, fontFamily = PlusJakartaSans) },
            selected = selected == LenteraNavItem.LAPORAN,
            onClick = { onItemClick(LenteraNavItem.LAPORAN) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LenteraColors.SlateText,
                unselectedTextColor = LenteraColors.SlateText,
                selectedIconColor = Color.White,
                selectedTextColor = LenteraColors.NavyPrimary,
                indicatorColor = LenteraColors.NavyPrimary
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_magang),
                    contentDescription = "Magang",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Magang", fontSize = 11.sp, fontFamily = PlusJakartaSans) },
            selected = selected == LenteraNavItem.MAGANG,
            onClick = { onItemClick(LenteraNavItem.MAGANG) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LenteraColors.SlateText,
                unselectedTextColor = LenteraColors.SlateText,
                selectedIconColor = Color.White,
                selectedTextColor = LenteraColors.NavyPrimary,
                indicatorColor = LenteraColors.NavyPrimary
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_profil),
                    contentDescription = "Profil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Profil", fontSize = 11.sp, fontFamily = PlusJakartaSans) },
            selected = selected == LenteraNavItem.PROFIL,
            onClick = { onItemClick(LenteraNavItem.PROFIL) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LenteraColors.SlateText,
                unselectedTextColor = LenteraColors.SlateText,
                selectedIconColor = Color.White,
                selectedTextColor = LenteraColors.NavyPrimary,
                indicatorColor = LenteraColors.NavyPrimary
            )
        )
    }
}
