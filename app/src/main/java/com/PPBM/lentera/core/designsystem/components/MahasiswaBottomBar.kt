package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.background
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
import com.PPBM.lentera.core.designsystem.theme.MagangAmberPrimary
import com.PPBM.lentera.core.designsystem.theme.NavyDark
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import com.PPBM.lentera.core.designsystem.theme.SiagaNavBg
import com.PPBM.lentera.core.designsystem.theme.SiagaNavUnselected
import com.PPBM.lentera.core.designsystem.theme.SiagaTealPrimary

enum class LenteraTab {
    BERANDA,
    PENGADUAN,
    MAGANG_HUB,
    PROFIL
}

@Composable
fun MahasiswaBottomBar(
    currentTab: LenteraTab,
    onTabSelected: (LenteraTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SiagaNavBg)
            .navigationBarsPadding()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LenteraTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val label = when (tab) {
                    LenteraTab.BERANDA -> "Beranda"
                    LenteraTab.PENGADUAN -> "Pengaduan"
                    LenteraTab.MAGANG_HUB -> "Magang Hub"
                    LenteraTab.PROFIL -> "Profil"
                }
                val iconRes = when (tab) {
                    LenteraTab.BERANDA -> R.drawable.ic_house
                    LenteraTab.PENGADUAN -> R.drawable.ic_message_square_plus
                    LenteraTab.MAGANG_HUB -> R.drawable.ic_briefcase_business
                    LenteraTab.PROFIL -> R.drawable.ic_profile_circle_thin
                }
                val activeBgColor = when (tab) {
                    LenteraTab.BERANDA -> LenteraNavy
                    LenteraTab.PENGADUAN -> SiagaTealPrimary
                    LenteraTab.MAGANG_HUB -> MagangAmberPrimary
                    LenteraTab.PROFIL -> LenteraNavy
                }
                val activeContentColor = when (tab) {
                    LenteraTab.MAGANG_HUB -> NavyDark
                    else -> Color.White
                }

                val interactionSource = remember { MutableInteractionSource() }

                if (isSelected) {
                    // Pill tab aktif dengan warna aksen fitur
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .width(88.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(activeBgColor)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(tab) }
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
                                fontSize = 11.sp,
                                color = activeContentColor
                            )
                        }
                    }
                } else {
                    // Tab tidak aktif
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .width(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(tab) }
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
                                fontSize = 11.sp,
                                color = SiagaNavUnselected
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LenteraBottomBar(
    currentTab: LenteraTab,
    onTabSelected: (LenteraTab) -> Unit,
    modifier: Modifier = Modifier
) {
    MahasiswaBottomBar(
        currentTab = currentTab,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}
