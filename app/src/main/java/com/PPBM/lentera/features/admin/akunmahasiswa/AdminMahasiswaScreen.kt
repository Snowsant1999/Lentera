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
import androidx.compose.runtime.*
import androidx.compose.foundation.text.BasicTextField
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
    studentList: List<Triple<String, String, String>> = emptyList(),
    onNavigateToBuatAkun: () -> Unit = {},
    onNavigateToDetail: (nim: String) -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val actualList = studentList

    val totalAll = actualList.size
    val totalAktif = actualList.count { it.third.equals("Aktif", ignoreCase = true) }
    val totalBelum = actualList.count { it.third.contains("Belum", ignoreCase = true) }
    val totalNonaktif = actualList.count { it.third.equals("Nonaktif", ignoreCase = true) }
    val totalLulus = actualList.count { it.third.equals("Lulus", ignoreCase = true) }

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
            SearchBarMahasiswa(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            TabsMahasiswaDynamic(
                totalAll = totalAll,
                totalAktif = totalAktif,
                totalBelum = totalBelum,
                totalNonaktif = totalNonaktif,
                totalLulus = totalLulus,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            Spacer(modifier = Modifier.height(14.dp))
            ListMahasiswaDynamic(
                students = actualList,
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
                onItemClick = onNavigateToDetail
            )
        }
    }
}

@Composable
fun TabsMahasiswaDynamic(
    totalAll: Int,
    totalAktif: Int,
    totalBelum: Int,
    totalNonaktif: Int,
    totalLulus: Int,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "Semua $totalAll",
        "Aktif $totalAktif",
        "Belum Aktivasi $totalBelum",
        "Nonaktif $totalNonaktif",
        "Lulus $totalLulus"
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { index ->
            val filterText = filters[index]
            val filterKey = filterText.split(" ").firstOrNull() ?: ""
            val isSelected = selectedFilter.startsWith(filterKey)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = if (isSelected) NavyDark else Color.White
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else CardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(filterKey) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filterText,
                    style = TextStyle(
                        color = if (isSelected) Color.White else SlateGray,
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun ListMahasiswaDynamic(
    students: List<Triple<String, String, String>>,
    searchQuery: String = "",
    selectedFilter: String = "Semua",
    onItemClick: (nim: String) -> Unit = {}
) {
    val filtered = students.filter { (name, rawNim, status) ->
        val matchesSearch = searchQuery.isBlank() ||
                name.contains(searchQuery, ignoreCase = true) ||
                rawNim.contains(searchQuery, ignoreCase = true)
        val matchesTab = when {
            selectedFilter.startsWith("Semua") -> true
            selectedFilter.startsWith("Aktif") -> status.equals("Aktif", ignoreCase = true)
            selectedFilter.startsWith("Belum") -> status.contains("Belum", ignoreCase = true)
            selectedFilter.startsWith("Nonaktif") -> status.equals("Nonaktif", ignoreCase = true)
            selectedFilter.startsWith("Lulus") -> status.equals("Lulus", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesTab
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (filtered.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Tidak ada akun mahasiswa yang sesuai dengan pencarian atau filter.",
                        style = TextStyle(
                            fontFamily = PlusJakartaSans,
                            fontSize = 13.sp,
                            color = SlateGray
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filtered.size) { index ->
                val (name, rawNim, status) = filtered[index]
                val formattedNim = if (rawNim.startsWith("NIM:")) rawNim else "NIM: $rawNim"
                val (statusColor, bgColor) = when {
                    status.equals("Aktif", ignoreCase = true) -> LenteraGreen to LenteraGreenBg
                    status.contains("Belum", ignoreCase = true) -> Color(0xFFD97706) to Color(0xFFFEF3EB)
                    status.equals("Lulus", ignoreCase = true) -> SlateGray to ScreenBackground
                    else -> ErrorRed to ErrorRedLight
                }
                StudentCard(
                    name = name,
                    nim = formattedNim,
                    status = status,
                    statusColor = statusColor,
                    bgColor = bgColor,
                    onClick = { onItemClick(rawNim) }
                )
            }
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
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "AKUN MAHASISWA",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "LENTERA · Admin Kampus",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    color = SlateGray
                )
            )
        }

        Box(
            modifier = Modifier
                .height(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onAddClick() }
                .background(NavyDark)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "+",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Tambah",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun SearchBarMahasiswa(
    query: String,
    onQueryChange: (String) -> Unit
) {
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
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Cari nama atau NIM...",
                    style = TextStyle(
                        color = SlateGray,
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaSans
                    )
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = NavyDark,
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth()
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