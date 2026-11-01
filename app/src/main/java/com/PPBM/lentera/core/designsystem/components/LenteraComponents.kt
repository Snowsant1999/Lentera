package com.PPBM.lentera.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PPBM.lentera.core.designsystem.theme.*
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import com.PPBM.lentera.features.admin.akunmahasiswa.StatusAkun

enum class LenteraNavItem {
    BERANDA,
    MAHASISWA,
    LAPORAN,
    MAGANG,
    PROFIL
}

@Composable
fun LenteraBackIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_back),
            contentDescription = "Back",
            tint = LenteraColors.NavyPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun LenteraBackHeader(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        LenteraBackIcon(onClick = onBackClick)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.SlateText
            )
        }
    }
}

@Composable
fun LenteraCompactHeader(
    backLabel: String,
    title: String,
    description: String,
    onBackClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LenteraBackIcon(onClick = onBackClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSans,
                    color = LenteraColors.NavyPrimary
                )
                Text(
                    text = backLabel,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaSans,
                    color = LenteraColors.SlateText
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.SlateText
        )
    }
}

@Composable
fun LenteraSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Cari...",
    height: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                color = LenteraColors.SlateText,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.search),
                contentDescription = "Search",
                tint = LenteraColors.SlateText,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LenteraColors.NavyPrimary,
            unfocusedBorderColor = LenteraColors.LightGrayBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = LenteraColors.NavyPrimary,
            unfocusedTextColor = LenteraColors.NavyPrimary
        ),
        singleLine = true
    )
}

@Composable
fun LenteraDivider(color: Color = Color(0xFFF0F0F0)) {
    HorizontalDivider(color = color)
}

@Composable
fun LenteraCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    withShadow: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val actualContainerColor = if (containerColor != Color.Unspecified) containerColor else LenteraColors.CardBackground
    val cardModifier = if (onClick != null) {
        modifier.fillMaxWidth().clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }
    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = actualContainerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (withShadow) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun LenteraCardTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = PlusJakartaSans,
        color = LenteraColors.NavyPrimary,
        modifier = modifier
    )
}

@Composable
fun ProfileAvatar(
    size: Dp = 42.dp,
    containerColor: Color = Color.Unspecified,
    iconTint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val actualBg = if (containerColor != Color.Unspecified) containerColor else LenteraColors.Background
    val actualTint = if (iconTint != Color.Unspecified) iconTint else LenteraColors.NavyPrimary
    Box(
        modifier = modifier
            .size(size)
            .background(actualBg, CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = com.PPBM.lentera.R.drawable.ic_nav_mahasiswa),
            contentDescription = "Avatar",
            tint = actualTint,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Composable
fun StatusBadge(status: StatusAkun) {
    val (bgColor, textColor) = when (status) {
        StatusAkun.AKTIF -> Pair(LenteraColors.GreenActiveBg, LenteraColors.GreenActive)
        StatusAkun.BELUM_AKTIVASI -> Pair(LenteraColors.RedInactiveBg, LenteraColors.RedInactive)
        StatusAkun.LULUS -> Pair(LenteraColors.Background, LenteraColors.SlateText)
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            color = textColor
        )
    }
}

@Composable
fun LenteraBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            fontFamily = PlusJakartaSans,
            color = contentColor
        )
    }
}

@Composable
fun LenteraBadge(text: String) {
    LenteraBadge(
        text = text,
        containerColor = LenteraColors.Gold,
        contentColor = LenteraColors.NavyDark,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LenteraReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.SlateText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LenteraColors.Background, RoundedCornerShape(12.dp))
                .border(1.dp, LenteraColors.LightGrayBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyPrimary
            )
        }
    }
}

@Composable
fun LenteraHelperText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontFamily = PlusJakartaSans,
        color = LenteraColors.SlateText,
        lineHeight = 18.sp
    )
}

@Composable
fun LenteraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    fontSize: TextUnit = 14.sp
) {
    val actualBg = if (containerColor != Color.Unspecified) containerColor else LenteraColors.NavyPrimary
    val actualContentColor = if (contentColor != Color.Unspecified) contentColor else Color.White
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = actualBg, contentColor = actualContentColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = actualContentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = actualContentColor
            )
        }
    }
}

@Composable
fun LenteraSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = LenteraColors.NavyPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, LenteraColors.LightGrayBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            color = LenteraColors.NavyPrimary
        )
    }
}

@Composable
fun LenteraInfoBanner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LenteraColors.BannerBg),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(id = com.PPBM.lentera.R.drawable.info),
                contentDescription = "Info",
                tint = LenteraColors.NavyPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans,
                color = LenteraColors.NavyPrimary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun LenteraBottomNavBar(
    selected: LenteraNavItem,
    onItemClick: (LenteraNavItem) -> Unit
) {
    AdminBottomBar(selected = selected, onItemClick = onItemClick)
}