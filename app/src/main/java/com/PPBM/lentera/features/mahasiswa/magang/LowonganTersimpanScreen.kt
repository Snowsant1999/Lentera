package com.PPBM.lentera.features.mahasiswa.magang

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.components.LenteraBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraTab
import com.PPBM.lentera.core.designsystem.icons.BackArrowIcon
import com.PPBM.lentera.core.designsystem.theme.CardBorder
import com.PPBM.lentera.core.designsystem.theme.LenteraBg
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.LenteraTextSecondary
import com.PPBM.lentera.core.designsystem.theme.MagangAmberPillBg
import com.PPBM.lentera.core.designsystem.theme.MagangAmberPrimary
import com.PPBM.lentera.core.designsystem.theme.NavyDark
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans

/**
 * Layar Daftar Lowongan Magang Tersimpan (Bookmark) di menu Profil Mahasiswa.
 */
@Composable
fun LowonganTersimpanScreen(
    savedList: List<LowonganItem> = emptyList(),
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onLowonganClick: (LowonganItem) -> Unit = {},
    onBookmarkToggle: (LowonganItem) -> Unit = {},
    onJelajahiClick: () -> Unit = {},
    onTabSelected: (LenteraTab) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LenteraBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header: Back link "← Profil" & Judul
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onBackClick)
                                .padding(vertical = 8.dp, horizontal = 2.dp)
                        ) {
                            BackArrowIcon(
                                modifier = Modifier.size(18.dp),
                                tint = LenteraNavy
                            )
                            Text(
                                text = "Profil",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = LenteraNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "LOWONGAN TERSIMPAN",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = LenteraNavy
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Daftar peluang magang yang kamu simpan untuk ditinjau.",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.5.sp,
                            color = LenteraTextSecondary
                        )
                    }
                }

                // Banner Ringkasan (Gaya Dokumen Saya)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEAF2FB))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bookmark_filled),
                                contentDescription = null,
                                tint = MagangAmberPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${savedList.size} lowongan tersimpan",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LenteraNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (savedList.isNotEmpty()) {
                                    "Ketuk kartu untuk melihat rincian & melamar."
                                } else {
                                    "Tambahkan lowongan dari halaman Magang Hub."
                                },
                                fontFamily = PlusJakartaSans,
                                fontSize = 12.sp,
                                color = LenteraTextSecondary
                            )
                        }
                    }
                }

                // Konten Utama
                if (isLoading && savedList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LenteraNavy)
                        }
                    }
                } else if (savedList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MagangAmberPillBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_bookmark),
                                        contentDescription = null,
                                        tint = MagangAmberPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Belum Ada Lowongan Tersimpan",
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = LenteraNavy,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Simpan lowongan magang menarik dengan menekan ikon bookmark agar mudah dipantau kembali di sini.",
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = LenteraTextSecondary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onJelajahiClick,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NavyDark,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = "Jelajahi Lowongan",
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(savedList, key = { it.id }) { item ->
                        LowonganItemCard(
                            item = item,
                            onClick = { onLowonganClick(item) },
                            onBookmarkClick = { onBookmarkToggle(item) },
                            fontFamily = PlusJakartaSans
                        )
                    }
                }
            }

            LenteraBottomBar(
                currentTab = LenteraTab.PROFIL,
                onTabSelected = onTabSelected
            )
        }
    }
}
