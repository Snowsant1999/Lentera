package com.PPBM.lentera.features.mahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.ui.theme.LenteraTheme

@Composable
fun AdminMahasiswaScreen() {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        bottomBar = { AdminMahasiswaBottomBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopBarMahasiswa()
            SearchBarMahasiswa()
            TabsMahasiswa()
            Spacer(modifier = Modifier.height(16.dp))
            ListMahasiswa()
        }
    }
}

@Composable
fun TopBarMahasiswa() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            tint = Color(0xFF1E3A5F),
            contentDescription = "Back",
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Akun Mahasiswa",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A5F)
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF1E3A5F), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Normal)
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
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            tint = Color.Gray,
            contentDescription = "Search",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cari nama atau NIM...",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "Filter",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TabsMahasiswa() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
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
                color = if (isSelected) Color(0xFF1E3A5F) else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF1E3A5F),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ListMahasiswa() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StudentCard(
                name = "Rina Wulandari",
                nim = "NIM: 22001011",
                status = "Aktif",
                statusColor = Color(0xFF2E7D32),
                bgColor = Color(0xFFE8F5E9)
            )
        }
        item {
            StudentCard(
                name = "Budi Santoso",
                nim = "NIM: 22001012",
                status = "Belum Aktivasi",
                statusColor = Color(0xFFD32F2F),
                bgColor = Color(0xFFFFEBEE)
            )
        }
        item {
            StudentCard(
                name = "Siti Aminah",
                nim = "NIM: 22001013",
                status = "Aktif",
                statusColor = Color(0xFF2E7D32),
                bgColor = Color(0xFFE8F5E9)
            )
        }
        item {
            StudentCard(
                name = "Andi Wijaya",
                nim = "NIM: 22001014",
                status = "Aktif",
                statusColor = Color(0xFF2E7D32),
                bgColor = Color(0xFFE8F5E9)
            )
        }
    }
}

@Composable
fun StudentCard(name: String, nim: String, status: String, statusColor: Color, bgColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = Color(0xFF1E3A5F),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E3A5F))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = nim, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AdminMahasiswaBottomBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Beranda", modifier = Modifier.size(24.dp)) },
            label = { Text("Beranda", fontSize = 10.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Mahasiswa", modifier = Modifier.size(24.dp)) },
            label = { Text("Mahasiswa", fontSize = 10.sp) },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E3A5F),
                indicatorColor = Color(0xFF1E3A5F)
            )
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = "Laporan", modifier = Modifier.size(24.dp)) },
            label = { Text("Laporan", fontSize = 10.sp) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Magang", modifier = Modifier.size(24.dp)) },
            label = { Text("Magang", fontSize = 10.sp) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Profil", modifier = Modifier.size(24.dp)) },
            label = { Text("Profil", fontSize = 10.sp) },
            selected = false,
            onClick = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AdminMahasiswaScreenPreview() {
    LenteraTheme {
        AdminMahasiswaScreen()
    }
}