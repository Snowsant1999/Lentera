package com.PPBM.lentera.features.admin.profil
import com.PPBM.lentera.core.designsystem.components.*

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.PPBM.lentera.R
import com.PPBM.lentera.core.designsystem.theme.*

data class MenuProfilItem(
    val judul: String,
    val deskripsi: String,
    @DrawableRes val iconRes: Int
)

private val SampleMenuProfil = listOf(
    MenuProfilItem("Data Akun", "Informasi pribadi dan peran admin", R.drawable.ic_menu_symbol),
    MenuProfilItem("Notifikasi", "Atur pembaruan layanan dan operasional", R.drawable.ic_menu_symbol_3),
    MenuProfilItem("Bantuan Admin", "Panduan dan dukungan operasional", R.drawable.ic_support_symbol),
    MenuProfilItem("Tentang Aplikasi", "Versi 1.0.0 · LENTERA POLNES", R.drawable.ic_menu_symbol_4)
)

@Composable
fun ProfilAdminScreen(
    nama: String = "Admin Lentera",
    peran: String = "NIP. 198803152015042001 · Admin Kampus",
    badge: String = "Admin aktif",
    versi: String = "1.0.0",
    photoUrl: String? = null,
    menu: List<MenuProfilItem> = SampleMenuProfil,
    onMenuClick: (MenuProfilItem) -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onUploadFoto: (ByteArray) -> Unit = {},
    onHapusFoto: () -> Unit = {},
    onKeluarClick: () -> Unit = {},
    onNavClick: (LenteraNavItem) -> Unit = {}
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var rawUriToCrop by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    var showConfirmHapusFotoDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            rawUriToCrop = uri
        }
    }

    if (rawUriToCrop != null) {
        ProfilePhotoCropDialog(
            sourceUri = rawUriToCrop!!,
            onDismiss = { rawUriToCrop = null },
            onCropSuccess = { croppedUri, _ ->
                imageUri = croppedUri
                rawUriToCrop = null
                runCatching {
                    context.contentResolver.openInputStream(croppedUri)?.use { stream ->
                        val bytes = stream.readBytes()
                        if (bytes.isNotEmpty()) {
                            onUploadFoto(bytes)
                        }
                    }
                }
            }
        )
    }

    if (showPhotoOptionsDialog) {
        OpsiFotoProfilDialog(
            onDismiss = { showPhotoOptionsDialog = false },
            onGantiFoto = {
                showPhotoOptionsDialog = false
                imagePickerLauncher.launch("image/*")
            },
            onHapusFoto = {
                showPhotoOptionsDialog = false
                showConfirmHapusFotoDialog = true
            }
        )
    }

    if (showConfirmHapusFotoDialog) {
        KonfirmasiHapusFotoDialog(
            onDismiss = { showConfirmHapusFotoDialog = false },
            onConfirm = {
                showConfirmHapusFotoDialog = false
                imageUri = null
                onHapusFoto()
            }
        )
    }

    if (showLogoutDialog) {
        KonfirmasiLogoutAdminDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onKeluarClick()
            }
        )
    }

    Scaffold(
        containerColor = LenteraColors.Background,
        bottomBar = {
            LenteraBottomNavBar(
                selected = LenteraNavItem.PROFIL,
                onItemClick = onNavClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Judul Layar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "PROFIL ADMIN",
                    color = LenteraNavy,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar dan Ringkasan Profil
            RingkasanProfil(
                nama = nama,
                peran = peran,
                badge = badge,
                imageUri = imageUri,
                photoUrl = photoUrl,
                onAvatarClick = {
                    if (!photoUrl.isNullOrBlank() || imageUri != null) {
                        showPhotoOptionsDialog = true
                    } else {
                        imagePickerLauncher.launch("image/*")
                    }
                    onAvatarClick()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Profil Card (Identik dengan Mahasiswa)
            MenuProfilCard(menu = menu, onMenuClick = onMenuClick)

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Keluar (Merah Muda Soft Identik Mahasiswa)
            KeluarCard(onClick = { showLogoutDialog = true })

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Versi Aplikasi
            Text(
                text = "LENTERA · Versi $versi",
                color = LenteraTextSecondary,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RingkasanProfil(
    nama: String,
    peran: String,
    badge: String,
    imageUri: Uri? = null,
    photoUrl: String? = null,
    onAvatarClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable(onClick = onAvatarClick)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = Color(0x15173B57)
                    )
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                AsyncImage(
                    model = imageUri ?: photoUrl ?: R.drawable.ic_avatar_2,
                    contentDescription = "Foto Profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay ikon edit di pojok kanan bawah
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(4.dp, CircleShape)
                    .background(LenteraNavy, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Ubah Foto Profil",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = nama,
            color = LenteraNavy,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = peran,
            color = LenteraTextSecondary,
            fontSize = 13.sp,
            fontFamily = PlusJakartaSans
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(LenteraBadgeBg)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = badge,
                color = LenteraTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = PlusJakartaSans
            )
        }
    }
}

@Composable
private fun MenuProfilCard(
    menu: List<MenuProfilItem>,
    onMenuClick: (MenuProfilItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                spotColor = Color(0x09173B57),
                ambientColor = Color(0x09173B57),
                shape = RoundedCornerShape(24.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp)
    ) {
        menu.forEach { item ->
            MenuProfilRow(
                item = item,
                onClick = { onMenuClick(item) }
            )
        }
    }
}

@Composable
private fun MenuProfilRow(
    item: MenuProfilItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.judul,
                modifier = Modifier.size(40.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.judul,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LenteraNavy
                )
                Text(
                    text = item.deskripsi,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = LenteraTextSecondary
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_open_answer),
            contentDescription = null,
            tint = LenteraTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun KeluarCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LenteraRedBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_log),
                contentDescription = "Keluar",
                tint = LenteraRedMuted,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = "Keluar",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = LenteraRedMuted
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_open_answer),
            contentDescription = null,
            tint = LenteraRedMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun KonfirmasiLogoutAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_simbol_keluar),
                contentDescription = "Keluar",
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keluar dari akun?",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = LenteraNavy
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Apakah Anda yakin ingin keluar?",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = LenteraTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEFF5FD),
                        contentColor = LenteraNavy
                    )
                ) {
                    Text(
                        text = "Batal",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LenteraRedMuted,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Ya, Keluar",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ProfilAdminScreenPreview() {
    LenteraTheme {
        ProfilAdminScreen()
    }
}
