package com.example.triplink.core.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun BottomBar(
    items: List<NavItem>,
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

