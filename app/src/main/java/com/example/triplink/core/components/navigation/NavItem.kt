package com.example.triplink.core.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector
)

fun defaultNavItems(): List<NavItem> = listOf(
    NavItem(label = "Inicio", icon = Icons.Outlined.Home),
    NavItem(label = "Explorar", icon = Icons.Outlined.Search),
    NavItem(label = "Mi Perfil", icon = Icons.Outlined.PersonOutline)
)

