package com.PPBM.lentera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.ui.theme.LenteraNavy
import com.PPBM.lentera.ui.theme.PlusJakartaSans

enum class LenteraTab {
    BERANDA,
    PENGADUAN,
    MAGANG_HUB,
    PROFIL
}

@Composable
fun LenteraBottomBar(
    currentTab: LenteraTab,
    onTabSelected: (LenteraTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6EE))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Beranda",
            iconRes = R.drawable.house,
            isSelected = currentTab == LenteraTab.BERANDA,
            onClick = { onTabSelected(LenteraTab.BERANDA) }
        )
        BottomNavItem(
            label = "Pengaduan",
            iconRes = R.drawable.message_square_plus,
            isSelected = currentTab == LenteraTab.PENGADUAN,
            onClick = { onTabSelected(LenteraTab.PENGADUAN) }
        )
        BottomNavItem(
            label = "Magang Hub",
            iconRes = R.drawable.briefcase_business,
            isSelected = currentTab == LenteraTab.MAGANG_HUB,
            onClick = { onTabSelected(LenteraTab.MAGANG_HUB) }
        )
        BottomNavItem(
            label = "Profil",
            iconRes = R.drawable.iconamoon_profile_circle_thin,
            isSelected = currentTab == LenteraTab.PROFIL,
            onClick = { onTabSelected(LenteraTab.PROFIL) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isSelected) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(18.dp))
                .background(LenteraNavy)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = label,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = LenteraNavy,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = label,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = LenteraNavy,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
