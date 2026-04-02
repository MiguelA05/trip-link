package com.example.triplink.features.user.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.common.BrandHeader
import com.example.triplink.core.components.common.SectionTitleDivider
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun UserHomeScreen(
    onPublicationClick: (String) -> Unit = {},
    onCommentsClick: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    onNotificationsClick: () -> Unit = {},
) {
    val viewModel: UserHomeViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        containerColor = Color(0xFFF5F6F8),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            Column {
                BrandHeader(
                    locationText = "Armenia, Quindio",
                    onNotificationsClick = onNotificationsClick
                )
                SectionTitleDivider(title = "RECOMENDADOS")
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
        }
    ) { scaffoldPaddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPaddingValues),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = viewModel.puntoInteres, key = { it.id }) { publication ->
                PublicationCard(
                    puntoInteres = publication,
                    onCardClick = { onPublicationClick(publication.id) },
                    onFavoriteToggle = { viewModel.toggleFavorite(publication.id) },
                    onCommentsClick = { onCommentsClick(publication.id) }
                )
            }
        }
    }
}

