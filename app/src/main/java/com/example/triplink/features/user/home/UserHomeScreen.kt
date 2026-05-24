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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.triplink.core.utils.RequestResult
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.common.BrandHeader
import com.example.triplink.core.components.common.SectionTitleDivider
import com.example.triplink.core.navigation.SessionViewModel
import com.example.triplink.core.utils.messageText

@Composable
fun UserHomeScreen(
    onPublicationClick: (String) -> Unit = {},
    onCommentsClick: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    onNotificationsClick: () -> Unit = {},
    onPostCreationClick: () -> Unit = {}
) {
    val viewModel: UserHomeViewModel = hiltViewModel()
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val favoriteResult by viewModel.favoriteToggleResult.collectAsState()
    val publications by viewModel.publications.collectAsState()
    val favoriteIds by viewModel.favoritePublicationIds.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Obtain current session to get userId
    val currentUserId by sessionViewModel.sessionState.collectAsState()
    val userId = (currentUserId as? com.example.triplink.core.navigation.SessionState.Authenticated)?.session?.userId ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadFavoriteIds(userId)
        }
    }

    // Pre-read the localized loading label in composable scope so it can be used inside
    // coroutine-based LaunchedEffect (stringResource is @Composable and cannot be called
    // from the LaunchedEffect lambda directly).
    val loadingLabel = stringResource(R.string.loading)

    LaunchedEffect(favoriteResult) {
        favoriteResult?.let { result ->
            val message = if (result is RequestResult.Loading) {
                loadingLabel
            } else {
                result.messageText()
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearFavoriteResult()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            topBar = {
            Column {
                BrandHeader(
                    locationText = stringResource(R.string.feature_user_home_location),
                    onNotificationsClick = onNotificationsClick,
                    showLocation = false
                )
                SectionTitleDivider(title = stringResource(R.string.feature_user_home_recommended_title))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPostCreationClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.feature_user_home_create_publication_content_description)
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
            items(items = viewModel.toHomePublications(publications), key = { it.id }) { publication ->
                PublicationCard(
                    puntoInteres = publication,
                    ratingLabel = viewModel.ratingLabelForPublication(publication),
                    isFavorite = favoriteIds.contains(publication.id),
                    showLocation = false,
                    onCardClick = { onPublicationClick(publication.id) },
                    onFavoriteToggle = { viewModel.toggleFavorite(userId, publication.id) },
                    onCommentsClick = { onCommentsClick(publication.id) }
                )
            }
        }
    }

        // Colocamos el SnackbarHost después del Scaffold dentro del Box para que se muestre
        // por encima del FAB y anclado al fondo (como si no existiera el FAB).
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
