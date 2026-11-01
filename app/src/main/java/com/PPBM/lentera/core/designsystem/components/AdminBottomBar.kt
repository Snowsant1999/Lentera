package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import com.PPBM.lentera.core.designsystem.theme.SlateGray

@Composable
fun AdminBottomBar(
    selected: LenteraNavItem,
    onItemClick: (LenteraNavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = Color(0xFFE2E8F0))
            .navigationBarsPadding()
            .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LenteraNavItem.entries.forEach { item ->
                val isSelected = item == selected
                val label = when (item) {
                    LenteraNavItem.BERANDA -> "Beranda"
                    LenteraNavItem.MAHASISWA -> "Mahasiswa"
                    LenteraNavItem.LAPORAN -> "Laporan"
                    LenteraNavItem.MAGANG -> "Magang"
                    LenteraNavItem.PROFIL -> "Profil"
                }
                val iconRes = when (item) {
                    LenteraNavItem.BERANDA -> R.drawable.ic_nav_beranda
                    LenteraNavItem.MAHASISWA -> R.drawable.ic_nav_mahasiswa
                    LenteraNavItem.LAPORAN -> R.drawable.ic_nav_laporan
                    LenteraNavItem.MAGANG -> R.drawable.ic_nav_magang
                    LenteraNavItem.PROFIL -> R.drawable.ic_nav_profil
                }

                val interactionSource = remember { MutableInteractionSource() }

                if (isSelected) {
                    // Pill Aktif Admin
                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .width(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LenteraNavy)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onItemClick(item) }
                            ),
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Tab Tidak Aktif Admin
                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .width(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onItemClick(item) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = label,
                                tint = SlateGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                color = SlateGray
                            )
                        }
                    }
                }
            }
        }
    }
}
