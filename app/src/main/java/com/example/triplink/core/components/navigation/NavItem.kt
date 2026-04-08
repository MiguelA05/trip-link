package com.example.triplink.core.components.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReportGmailerrorred
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.triplink.R

data class NavItem(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
)

fun defaultNavItems(): List<NavItem> = listOf(
    NavItem(labelRes = R.string.component_nav_item_home, icon = Icons.Outlined.Home),
    NavItem(labelRes = R.string.component_nav_item_explore, icon = Icons.Outlined.Search),
    NavItem(labelRes = R.string.component_nav_item_my_profile, icon = Icons.Outlined.PersonOutline)
)

fun adminNavItems(): List<NavItem> = listOf(
    NavItem(labelRes = R.string.component_nav_item_logout, icon = Icons.AutoMirrored.Outlined.Logout),
    NavItem(labelRes = R.string.component_nav_item_publications, icon = Icons.Outlined.Verified),
    NavItem(labelRes = R.string.component_nav_item_reports, icon = Icons.Outlined.ReportGmailerrorred)
)

