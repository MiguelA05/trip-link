package com.example.triplink.features.publicationDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.core.components.publicationdetails.hero.ImageCarousel
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.DestructiveConfirmDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.publicationdetails.sections.DayScheduleUi
import com.example.triplink.core.components.publicationdetails.sections.PublicationLocationSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationPriceRangeSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationTextSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationWeeklyScheduleSection
import com.example.triplink.core.components.publicationdetails.utils.currentDayLocalizedLabel
import com.example.triplink.core.components.publicationdetails.utils.toWeeklyScheduleUi
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedLabelOrNoPrice
import com.example.triplink.core.navigation.SessionState
import com.example.triplink.core.navigation.SessionViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.R
import com.example.triplink.ui.theme.*
import kotlinx.coroutines.launch

data class Review(
    val username: String,
    val rating: Int,
    val comment: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailsScreen(
    publicationId: String,
    isOwnerPublicationView: Boolean = false,
    onBackClick: () -> Unit,
    onOwnerPublicationDeleted: () -> Unit = {},
    onSeeAllReviewsClick: (String) -> Unit
) {
    val viewModel: PublicationDetailsViewModel = hiltViewModel()
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val sessionState by sessionViewModel.sessionState.collectAsState()
    val publication by viewModel.publication.collectAsState()
    val publicationActionResult by viewModel.publicationActionResult.collectAsState()
    val favoriteToggleResult by viewModel.favoriteToggleResult.collectAsState()
    val commentResult by viewModel.commentResult.collectAsState()
    val isSavingComment by viewModel.isSavingComment.collectAsState()
    val isModeratingComment by viewModel.isModeratingComment.collectAsState()
    val commentModerationState by viewModel.commentModerationState.collectAsState()
    val isSubmittingReport by viewModel.isSubmittingReport.collectAsState()
    val reportResult by viewModel.reportResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showReportModal by remember { mutableStateOf(false) }
    var showRatingModal by remember { mutableStateOf(false) }
    var showDeletePublicationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
        viewModel.loadCommentsForPublication(publicationId)
    }

    LaunchedEffect(publicationId, sessionState) {
        val currentUserId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
        viewModel.checkIsFavorite(currentUserId, publicationId)
    }

    LaunchedEffect(publicationActionResult) {
        publicationActionResult?.let { result ->
            val message = result.messageText()
            if (result is RequestResult.Success && result.message.contains("eliminada", ignoreCase = true)) {
                if (isOwnerPublicationView) {
                    onOwnerPublicationDeleted()
                } else {
                    onBackClick()
                }
                viewModel.clearPublicationActionResult()
                return@let
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearPublicationActionResult()
        }
    }

    LaunchedEffect(commentResult) {
        commentResult?.let { result ->
            val message = result.messageText()
            snackbarHostState.showSnackbar(message)
            if (result is RequestResult.Success) {
                showRatingModal = false
            }
            viewModel.clearCommentResult()
        }
    }

    LaunchedEffect(favoriteToggleResult) {
        favoriteToggleResult?.let { result ->
            val message = result.messageText()
            snackbarHostState.showSnackbar(message)
            viewModel.clearFavoriteResult()
        }
    }

    LaunchedEffect(reportResult) {
        reportResult?.let { result ->
            val message = result.messageText()
            snackbarHostState.showSnackbar(message)
            if (result is RequestResult.Success) {
                showReportModal = false
            }
            viewModel.clearReportResult()
        }
    }

    if (!viewModel.publicationLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (publication == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.feature_publication_details_not_found),
                style = TextTokens.sectionTitle(),
                color = TextColors.Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBackClick) {
                Text(stringResource(R.string.feature_publication_details_back))
            }
        }
        return
    }

    val currentPublication = publication ?: return

    val schedules: List<DayScheduleUi> = currentPublication.horarios.toWeeklyScheduleUi()
    val today = currentDayLocalizedLabel()
    val loginRequiredForReviewMessage = stringResource(R.string.feature_publication_details_login_required_for_review)
    val defaultUserName = stringResource(R.string.vm_user_info_default_user_name)
    val isCurrentUserOwner = (sessionState as? SessionState.Authenticated)
        ?.session
        ?.userId
        ?.equals(currentPublication.usuarioAutorId, ignoreCase = true) == true
    val ownerViewEnabled = isOwnerPublicationView && isCurrentUserOwner
    val currentUserId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
    val alreadyReportedByCurrentUser = currentUserId.isNotBlank() && currentPublication.reportes.any {
        it.reportadorId.equals(currentUserId, ignoreCase = true)
    }
    val canOpenReportModal = !ownerViewEnabled && !alreadyReportedByCurrentUser

    val selectedPriceLevel = currentPublication.rangoPrecios.localizedLabelOrNoPrice()

    val reviews = viewModel.comments.map { Review(it.userName, it.rating.toInt(), it.text) }
    val generalRating = viewModel.getAverageRating()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                GeneralTopBar(
                    title = stringResource(R.string.feature_publication_details_title),
                    onBack = onBackClick
                )
                ImageCarousel(
                    imageUrls = currentPublication.fotos,
                    title = currentPublication.titulo,
                    categoryLabel = currentPublication.categoria.localizedLabel(),
                    showReportAction = !ownerViewEnabled,
                    reportActionEnabled = canOpenReportModal,
                    onReportClick = { if (canOpenReportModal) showReportModal = true }
                )
            }
        },
        bottomBar = {
            BottomActionsBar(
                isOwnerPublicationView = ownerViewEnabled,
                isInterested = viewModel.isFavorite,
                onInterestedClick = {
                    val userId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
                    if (userId.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(loginRequiredForReviewMessage)
                        }
                    } else {
                        viewModel.toggleFavorite(userId, publicationId)
                    }
                },
                onVisitedClick = {
                    if (sessionState is SessionState.Authenticated) {
                        showRatingModal = true
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(loginRequiredForReviewMessage)
                        }
                    }
                },
                onDeleteClick = { showDeletePublicationDialog = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            item {
                PublicationTextSection(
                    title = stringResource(R.string.feature_publication_details_description),
                    body = currentPublication.informacion
                )
            }
            item {
                PublicationPriceRangeSection(selectedLevel = selectedPriceLevel)
            }
            item {
                PublicationLocationSection(
                    city = currentPublication.ubicacion.ciudad,
                    coordinates = "${currentPublication.ubicacion.latitud}, ${currentPublication.ubicacion.longitud}",
                    latitude = currentPublication.ubicacion.latitud,
                    longitude = currentPublication.ubicacion.longitud
                )
            }
            item {
                PublicationWeeklyScheduleSection(
                    schedules = schedules,
                    today = today
                )
            }
            item {
                ReviewsSection(
                    publicationId = publicationId,
                    reviews = reviews,
                    generalRating = generalRating,
                    onSeeAllReviewsClick = onSeeAllReviewsClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (canOpenReportModal && showReportModal) {
        ReportModal(
            onDismiss = { showReportModal = false },
            onSubmit = { reason, customReason ->
                val userId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
                viewModel.submitReport(
                    publicationId = publicationId,
                    userId = userId,
                    reason = reason,
                    description = customReason
                )
            },
            isLoading = isSubmittingReport
        )
    }

    if (showRatingModal) {
        RatingModal(
            onDismiss = { showRatingModal = false },
            onSubmit = { rating, comment ->
                val userId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
                val userName = userId.substringBefore('@').ifBlank { defaultUserName }
                viewModel.saveComment(
                    publicationId = publicationId,
                    userId = userId,
                    userName = userName,
                    rating = rating,
                    text = comment
                )
            },
            // Disable submit while moderation or saving is in progress to avoid duplicate requests
            isLoading = isSavingComment || isModeratingComment
        )
    }

    // Show moderation suggestion modal when the moderation service provides a suggested rewrite
    when (val state = commentModerationState) {
        is CommentModerationState.Suggested -> {
            InappropriateContentModal(
                onDismiss = { viewModel.dismissModerationSuggestion() },
                onReplace = { viewModel.resolveModeratedComment(true) }
            )
        }
        else -> { /* no-op */ }
    }

    if (showDeletePublicationDialog) {
        DestructiveConfirmDialog(
            title = stringResource(R.string.feature_publication_details_delete_dialog_title),
            message = stringResource(R.string.feature_publication_details_delete_dialog_message),
            confirmText = stringResource(R.string.feature_publication_details_delete_dialog_confirm),
            dismissText = stringResource(R.string.feature_publication_details_delete_dialog_cancel),
            onDismissRequest = { showDeletePublicationDialog = false },
            onConfirm = {
                showDeletePublicationDialog = false
                viewModel.deletePublication(publicationId)
            }
        )
    }
}

    


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModal(
    onDismiss: () -> Unit,
    onSubmit: (rating: Float, comment: String) -> Unit,
    isLoading: Boolean = false
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val maxChars = 300

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.feature_publication_details_rating_modal_title),
                style = TextTokens.emphasized(TextTokens.sectionTitle(), FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.feature_publication_details_rating_modal_subtitle),
                style = TextTokens.body(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.feature_publication_details_rating_label),
                    style = TextTokens.emphasized(TextTokens.chip())
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_publication_details_required_label),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                            style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    val isSelected = starIndex <= rating
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = starIndex }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.feature_publication_details_review_rating_note),
                style = TextTokens.bodySecondary(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.feature_publication_details_comment_label),
                        style = TextTokens.emphasized(TextTokens.chip())
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_publication_details_comment_optional_label),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.feature_publication_details_comment_counter, comment.length, maxChars),
                    style = TextTokens.bodySecondary(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= maxChars) comment = it },
                placeholder = {
                    Text(
                        stringResource(R.string.feature_publication_details_comment_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            GeneralButton(
                text = stringResource(R.string.feature_publication_details_publish_review),
                onClick = {
                    onSubmit(rating.toFloat(), comment)
                },
                icon = Icons.AutoMirrored.Filled.Send,
                enabled = rating > 0,
                isLoading = isLoading
            )

            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.feature_publication_details_cancel),
                    style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun InappropriateContentModal(onDismiss: () -> Unit, onReplace: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Red indicator bar at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.error)
                )

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.component_general_alert_dialog_close_content_description),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Angry Face Icon
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SentimentVeryDissatisfied,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.feature_publication_details_inappropriate_title),
                        style = TextTokens.sectionTitle(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.feature_publication_details_inappropriate_message),
                        style = TextTokens.bodySecondary(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Suggestion Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.feature_publication_details_inappropriate_suggestion),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = TextTokens.body()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = stringResource(R.string.feature_publication_details_cancel),
                                style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = onReplace,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(
                                text = stringResource(R.string.feature_publication_details_replace),
                                style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportModal(
    onDismiss: () -> Unit,
    onSubmit: (RazonReporte, String?) -> Unit,
    isLoading: Boolean = false
) {
    var selectedOption by remember { mutableStateOf<RazonReporte?>(null) }
    var otherReason by remember { mutableStateOf("") }

    val options = listOf(
        ReportOptionData(
            reason = RazonReporte.INFORMACION_FALSA,
            stringResource(R.string.feature_publication_details_report_option_incorrect_info_title),
            stringResource(R.string.feature_publication_details_report_option_incorrect_info_subtitle),
            Icons.AutoMirrored.Outlined.LibraryBooks
        ),
        ReportOptionData(
            reason = RazonReporte.SPAM,
            stringResource(R.string.feature_publication_details_report_option_wrong_location_title),
            stringResource(R.string.feature_publication_details_report_option_wrong_location_subtitle),
            Icons.Outlined.LocationOn
        ),
        ReportOptionData(
            reason = RazonReporte.CONTENIDO_INAPROPIADO,
            stringResource(R.string.feature_publication_details_report_option_inappropriate_title),
            stringResource(R.string.feature_publication_details_report_option_inappropriate_subtitle),
            Icons.Outlined.Block
        ),
        ReportOptionData(
            reason = RazonReporte.OTRO,
            stringResource(R.string.feature_publication_details_report_option_other_title),
            stringResource(R.string.feature_publication_details_report_option_other_subtitle),
            Icons.Outlined.Edit
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.feature_publication_details_report_modal_title),
                            style = TextTokens.emphasized(TextTokens.screenTitle(), FontWeight.Bold)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.component_general_alert_dialog_close_content_description),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_publication_details_report_modal_subtitle),
                    style = TextTokens.body(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option.reason
                        ReportOptionItem(
                            option = option,
                            isSelected = isSelected,
                            onClick = { selectedOption = option.reason }
                        )
                    }
                }

                if (selectedOption == RazonReporte.OTRO) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FormField(
                        label = stringResource(R.string.feature_publication_details_report_reason_label),
                        value = otherReason,
                        onValueChange = { otherReason = it },
                        placeholder = stringResource(R.string.feature_publication_details_report_reason_placeholder)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = stringResource(R.string.feature_publication_details_report_reviewed_by_moderation),
                                style = TextTokens.bodySecondary(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val reportEnabled = selectedOption != null &&
                    (selectedOption != RazonReporte.OTRO || otherReason.isNotBlank())

                GeneralButton(
                    text = stringResource(R.string.feature_publication_details_send_report),
                    onClick = {
                        selectedOption?.let { selected ->
                            val customReason = if (selected == RazonReporte.OTRO) {
                                otherReason.trim().takeIf { it.isNotBlank() }
                            } else {
                                null
                            }
                            onSubmit(selected, customReason)
                        }
                    },
                    icon = Icons.AutoMirrored.Filled.Send,
                    enabled = reportEnabled,
                    isLoading = isLoading
                )

                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.feature_publication_details_cancel),
                        style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class ReportOptionData(
    val reason: RazonReporte,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun ReportOptionItem(option: ReportOptionData, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = option.subtitle,
                    style = TextTokens.bodySecondary(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}


@Composable
fun ReviewsSection(
    publicationId: String,
    reviews: List<Review>,
    generalRating: Double,
    onSeeAllReviewsClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feature_publication_details_reviews_title),
                style = TextTokens.sectionTitle(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (generalRating == 0.0) {
                        Text(
                            text = stringResource(R.string.feature_publication_details_no_reviews),
                            style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(generalRating),
                            style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewCard(review)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        GeneralButton(
            onClick = { onSeeAllReviewsClick(publicationId) },
            text = stringResource(R.string.feature_publication_details_see_all_reviews)
        )
    }
}

@Composable
fun ReviewCard(review: Review) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.username,
                    style = TextTokens.emphasized(TextTokens.title()),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextTokens.body()
            )
        }
    }
}

@Composable
fun BottomActionsBar(
    isOwnerPublicationView: Boolean,
    isInterested: Boolean,
    onInterestedClick: () -> Unit,
    onVisitedClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isOwnerPublicationView) {
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.feature_publication_details_delete_publication),
                        style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                    )
                }
            } else {
                // Botón Me interesa sincronizado con el repositorio compartido
                Button(
                    onClick = onInterestedClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isInterested) null else BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isInterested) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isInterested) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.feature_publication_details_interested_action),
                        style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                    )
                }

                // Botón Visitado
                OutlinedButton(
                    onClick = onVisitedClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.feature_publication_details_visited_action),
                        style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                    )
                }
            }
        }
    }
}


