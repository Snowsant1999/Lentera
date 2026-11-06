package com.PPBM.lentera.features.admin.akunmahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import com.PPBM.lentera.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.PPBM.lentera.core.designsystem.components.AdminBottomBar
import com.PPBM.lentera.core.designsystem.components.LenteraNavItem
import com.PPBM.lentera.core.designsystem.theme.*

@Composable
fun AdminMahasiswaScreen(
    onNavigateToBuatAkun: () -> Unit = {},
    onNavigateToDetail: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AdminBottomBar(
                selected = LenteraNavItem.MAHASISWA,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            TopBarMahasiswa(onAddClick = onNavigateToBuatAkun)
            SearchBarMahasiswa()
            TabsMahasiswa()
            Spacer(modifier = Modifier.height(14.dp))
            ListMahasiswa(onItemClick = onNavigateToDetail)
        }
    }
}

@Composable
fun TopBarMahasiswa(
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Akun Mahasiswa",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onAddClick() }
                .background(NavyDark, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun SearchBarMahasiswa() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            tint = SlateGray,
            contentDescription = "Search",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cari nama atau NIM...",
            style = TextStyle(
                color = SlateGray,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabsMahasiswa() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { TabItem(text = "Semua 128", isSelected = true) }
        item { TabItem(text = "Aktif 124", isSelected = false) }
        item { TabItem(text = "Belum Aktivasi 4", isSelected = false) }
        item { TabItem(text = "Lulus 0", isSelected = false) }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) NavyDark else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else CardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = if (isSelected) Color.White else SlateGray,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun ListMahasiswa(onItemClick: () -> Unit = {}) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            StudentCard(
                name = "Rina Wulandari",
                nim = "NIM: 22001011",
                status = "Aktif",
                statusColor = LenteraGreen,
                bgColor = LenteraGreenBg,
                onClick = onItemClick
            )
        }
        item {
            StudentCard(
                name = "Budi Santoso",
                nim = "NIM: 22001012",
                status = "Belum Aktivasi",
                statusColor = ErrorRed,
                bgColor = ErrorRedLight,
                onClick = onItemClick
            )
        }
        item {
            StudentCard(
                name = "Siti Aminah",
                nim = "NIM: 22001013",
                status = "Aktif",
                statusColor = LenteraGreen,
                bgColor = LenteraGreenBg,
                onClick = onItemClick
            )
        }
        item {
            StudentCard(
                name = "Andi Wijaya",
                nim = "NIM: 22001014",
                status = "Aktif",
                statusColor = LenteraGreen,
                bgColor = LenteraGreenBg,
                onClick = onItemClick
            )
        }
    }
}

@Composable
fun StudentCard(
    name: String,
    nim: String,
    status: String,
    statusColor: Color,
    bgColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_avatar_2),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyDark
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = nim,
                    style = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = SlateGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AdminMahasiswaScreenPreview() {
    LenteraTheme {
        AdminMahasiswaScreen()
    }
}