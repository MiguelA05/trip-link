package com.example.triplink.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.R
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun UserHomeHeader(
    locationText: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F6F8))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo TripLink",
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                AppTitle(fontSize = 26)
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        IconButton(onClick = onNotificationsClick) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notificaciones",
                tint = Color(0xFF9BB9FF),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SectionDividerTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8D8D8))
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF949494),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8D8D8))
    }
}

data class UserHomeNavItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun UserHomeBottomBar(
    items: List<UserHomeNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column {
            HorizontalDivider(color = Color(0xFFE4E8F0))
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(text = item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrincipalBlue,
                            selectedTextColor = PrincipalBlue,
                            unselectedIconColor = PrincipalBlue,
                            unselectedTextColor = PrincipalBlue,
                            indicatorColor = Color(0xFFDCE7FF)
                        )
                    )
                }
            }
        }
    }
}

fun defaultUserHomeNavItems(): List<UserHomeNavItem> = listOf(
    UserHomeNavItem(label = "Inicio", icon = Icons.Outlined.Home),
    UserHomeNavItem(label = "Explorar", icon = Icons.Outlined.Search),
    UserHomeNavItem(label = "Mi Perfil", icon = Icons.Outlined.PersonOutline)
)

