package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.MagangAmberPrimary
import com.PPBM.lentera.core.designsystem.theme.NavyDark
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import com.PPBM.lentera.core.designsystem.theme.SiagaNavBg
import com.PPBM.lentera.core.designsystem.theme.SiagaNavUnselected
import com.PPBM.lentera.core.designsystem.theme.SiagaTealPrimary

@Composable
fun AdminBottomBar(
    selected: LenteraNavItem,
    onItemClick: (LenteraNavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SiagaNavBg)
            .navigationBarsPadding()
            .padding(vertical = 8.dp, horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LenteraNavItem.entries.forEach { item ->
                val isSelected = item == selected
                val label = when (item) {
                    LenteraNavItem.BERANDA -> "Beranda"
                    LenteraNavItem.MAHASISWA -> "Mahasiswa"
                    LenteraNavItem.LAPORAN -> "Laporan"
                    LenteraNavItem.MAGANG -> "Magang Hub"
                    LenteraNavItem.PROFIL -> "Profil"
                }
                val iconRes = when (item) {
                    LenteraNavItem.BERANDA -> R.drawable.ic_house
                    LenteraNavItem.MAHASISWA -> R.drawable.ic_nav_mahasiswa
                    LenteraNavItem.LAPORAN -> R.drawable.ic_message_square_plus
                    LenteraNavItem.MAGANG -> R.drawable.ic_briefcase_business
                    LenteraNavItem.PROFIL -> R.drawable.ic_profile_circle_thin
                }
                val activeBgColor = when (item) {
                    LenteraNavItem.BERANDA -> LenteraNavy
                    LenteraNavItem.MAHASISWA -> LenteraNavy
                    LenteraNavItem.LAPORAN -> SiagaTealPrimary
                    LenteraNavItem.MAGANG -> MagangAmberPrimary
                    LenteraNavItem.PROFIL -> LenteraNavy
                }
                val activeContentColor = when (item) {
                    LenteraNavItem.MAGANG -> NavyDark
                    else -> Color.White
                }

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .padding(horizontal = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        // Pill Aktif Admin (desain & ukuran harmonis dengan Mahasiswa)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(activeBgColor)
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
                                    tint = activeContentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = (-0.2).sp,
                                    color = activeContentColor,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    } else {
                        // Tab Tidak Aktif Admin (fleksibel dan proporsional)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
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
                                    tint = SiagaNavUnselected,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    letterSpacing = (-0.2).sp,
                                    color = SiagaNavUnselected,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
