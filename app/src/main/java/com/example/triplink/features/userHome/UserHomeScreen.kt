package com.example.triplink.features.userHome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.systemBarsPadding
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.SectionDividerTitle
import com.example.triplink.core.components.UserHomeBottomBar
import com.example.triplink.core.components.UserHomeHeader
import com.example.triplink.core.components.defaultUserHomeNavItems
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun UserHomeScreen(
    viewModel: UserHomeViewModel = viewModel(),
    onCommentsClick: (String) -> Unit = {}
) {
    val navItems = defaultUserHomeNavItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F6F8),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            Column(
                modifier = Modifier.systemBarsPadding()
            ) {
                UserHomeHeader(
                    locationText = "Armenia, Quindio",
                    onNotificationsClick = {}
                )
                SectionDividerTitle(title = "RECOMENDADOS")
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PrincipalBlue,
                contentColor = PrincipalWhite
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear publicacion"
                )
            }
        },
        bottomBar = {
            UserHomeBottomBar(
                items = navItems,
                selectedIndex = viewModel.selectedTabIndex,
                onItemSelected = viewModel::selectTab
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = viewModel.publications, key = { it.id }) { publication ->
                PublicationCard(
                    publication = publication,
                    onFavoriteToggle = { viewModel.toggleFavorite(publication.id) },
                    onCommentsClick = { onCommentsClick(publication.id) }
                )
            }
        }
    }
}
