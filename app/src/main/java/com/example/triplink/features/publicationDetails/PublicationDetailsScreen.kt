package com.example.triplink.features.publicationDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.DestructiveConfirmDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.publicationdetails.sections.DayScheduleUi
import com.example.triplink.core.components.publicationdetails.sections.PublicationLocationSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationPriceRangeSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationTextSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationWeeklyScheduleSection
import com.example.triplink.core.components.publicationdetails.utils.currentDayLabelEs
import com.example.triplink.core.components.publicationdetails.utils.toWeeklyScheduleUi
import com.example.triplink.core.navigation.SessionState
import com.example.triplink.core.navigation.SessionViewModel
import com.example.triplink.core.utils.RequestResult
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
    val publication = viewModel.getPublicationById(publicationId)
    val publicationActionResult by viewModel.publicationActionResult.collectAsState()
    val commentResult by viewModel.commentResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(publicationId) {
        viewModel.loadCommentsForPublication(publicationId)
    }

    LaunchedEffect(publicationActionResult) {
        publicationActionResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    if (result.message.contains("eliminada", ignoreCase = true)) {
                        if (isOwnerPublicationView) {
                            onOwnerPublicationDeleted()
                        } else {
                            onBackClick()
                        }
                        viewModel.clearPublicationActionResult()
                        return@let
                    }
                    snackbarHostState.showSnackbar(result.message)
                }
                is RequestResult.Failure -> snackbarHostState.showSnackbar(result.errorMessage)
            }
            viewModel.clearPublicationActionResult()
        }
    }

    LaunchedEffect(commentResult) {
        commentResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearCommentResult()
        }
    }

    if (publication == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7)),
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

    var showReportModal by remember { mutableStateOf(false) }
    var showRatingModal by remember { mutableStateOf(false) }
    var showDeletePublicationDialog by remember { mutableStateOf(false) }

    val schedules: List<DayScheduleUi> = publication.horarios.toWeeklyScheduleUi()
    val today = currentDayLabelEs()
    val loginRequiredForReviewMessage = stringResource(R.string.feature_publication_details_login_required_for_review)
    val isCurrentUserOwner = (sessionState as? SessionState.Authenticated)
        ?.session
        ?.userId
        ?.equals(publication.usuarioAutorId, ignoreCase = true) == true
    val ownerViewEnabled = isOwnerPublicationView && isCurrentUserOwner

    val selectedPriceLevel = publication.rangoPrecios?.let {
        when (it.name) {
            "GRATUITO" -> stringResource(R.string.component_publication_price_range_free)
            "ECONOMICO" -> stringResource(R.string.component_publication_price_range_economic)
            "MODERADO" -> stringResource(R.string.component_publication_price_range_moderate)
            "COSTOSO" -> stringResource(R.string.component_publication_price_range_expensive)
            else -> stringResource(R.string.component_publication_price_range_no_price)
        }
    } ?: stringResource(R.string.component_publication_price_range_no_price)

    val reviews = viewModel.comments.map { Review(it.userName, it.rating.toInt(), it.text) }
    val generalRating = if (viewModel.comments.isNotEmpty()) {
        viewModel.comments.map { it.rating }.average()
    } else {
        0.0
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                GeneralTopBar(
                    title = stringResource(R.string.feature_publication_details_title),
                    onBack = onBackClick
                )
                ImageHeader(
                    categoryLabel = publication.categoria.name,
                    title = publication.titulo,
                    imageUrl = publication.fotos.firstOrNull().orEmpty(),
                    showReportAction = !ownerViewEnabled,
                    onReportClick = { showReportModal = true },
                    onBackClick = onBackClick
                )
            }
        },
        bottomBar = {
            BottomActionsBar(
                isOwnerPublicationView = ownerViewEnabled,
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
                .background(Color.White)
        ) {
            item {
                PublicationTextSection(
                    title = stringResource(R.string.feature_publication_details_description),
                    body = publication.informacion
                )
            }
            item {
                PublicationPriceRangeSection(selectedLevel = selectedPriceLevel)
            }
            item {
                PublicationLocationSection(
                    city = publication.ubicacion.ciudad,
                    coordinates = "${publication.ubicacion.latitud}, ${publication.ubicacion.longitud}"
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

    if (!ownerViewEnabled && showReportModal) {
        ReportModal(onDismiss = { showReportModal = false })
    }

    if (showRatingModal) {
        RatingModal(
            onDismiss = { showRatingModal = false },
            onSubmit = { rating, comment ->
                val userId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
                val userName = userId.substringBefore('@').ifBlank { "Usuario" }
                viewModel.saveComment(
                    publicationId = publicationId,
                    userId = userId,
                    userName = userName,
                    rating = rating,
                    text = comment
                )
            }
        )
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

@Composable
fun ImageHeader(
    categoryLabel: String,
    title: String,
    imageUrl: String,
    showReportAction: Boolean,
    onReportClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) 
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = categoryLabel.uppercase(),
                color = PrincipalGreen,
                style = TextTokens.chipLabel(),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = TextColors.OnImage,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }

        if (showReportAction) {
            IconButton(
                onClick = onReportClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = stringResource(R.string.feature_publication_details_report_content_description),
                    tint = PrincipalRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModal(
    onDismiss: () -> Unit,
    onSubmit: (rating: Float, comment: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val maxChars = 300

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE2E8F0), CircleShape)
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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.feature_publication_details_rating_modal_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.feature_publication_details_rating_label),
                    style = TextTokens.chipLabel()
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_publication_details_required_label),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFFEF5350),
                        style = TextTokens.counterLabel()
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
                        tint = if (isSelected) PrincipalOrange else Color(0xFFCBD5E1),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = starIndex }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.feature_publication_details_review_rating_note),
                style = TextTokens.helperText(),
                color = Color.LightGray
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
                        style = TextTokens.chipLabel()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_publication_details_comment_optional_label),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Gray,
                            style = TextTokens.counterLabel()
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.feature_publication_details_comment_counter, comment.length, maxChars),
                    style = TextTokens.helperText(),
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= maxChars) comment = it },
                placeholder = {
                    Text(
                        stringResource(R.string.feature_publication_details_comment_placeholder),
                        color = Color.LightGray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            GeneralButton(
                text = stringResource(R.string.feature_publication_details_publish_review),
                onClick = {
                    onSubmit(rating.toFloat(), comment)
                    onDismiss()
                },
                icon = Icons.AutoMirrored.Filled.Send,
                enabled = rating > 0
            )

            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.feature_publication_details_cancel),
                    style = TextTokens.buttonLabel(),
                    color = PrincipalBlue
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
            color = Color.White
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
                        .background(Color(0xFFEF5350))
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
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.component_general_alert_dialog_close_content_description),
                                modifier = Modifier.size(18.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    // Angry Face Icon
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SentimentVeryDissatisfied,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.feature_publication_details_inappropriate_title),
                        style = TextTokens.sectionTitle(),
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.feature_publication_details_inappropriate_message),
                        style = TextTokens.cardSubtitle(),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Suggestion Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.feature_publication_details_inappropriate_suggestion),
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray,
                            style = TextTokens.inputText()
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
                            border = BorderStroke(1.dp, PrincipalBlue)
                        ) {
                            Text(
                                text = stringResource(R.string.feature_publication_details_cancel),
                                style = TextTokens.buttonLabel(),
                                color = PrincipalBlue
                            )
                        }

                        Button(
                            onClick = onReplace,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                        ) {
                            Text(
                                text = stringResource(R.string.feature_publication_details_replace),
                                style = TextTokens.buttonLabel(),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportModal(onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    var otherReason by remember { mutableStateOf("") }

    val options = listOf(
        ReportOptionData(
            stringResource(R.string.feature_publication_details_report_option_incorrect_info_title),
            stringResource(R.string.feature_publication_details_report_option_incorrect_info_subtitle),
            Icons.AutoMirrored.Outlined.LibraryBooks
        ),
        ReportOptionData(
            stringResource(R.string.feature_publication_details_report_option_wrong_location_title),
            stringResource(R.string.feature_publication_details_report_option_wrong_location_subtitle),
            Icons.Outlined.LocationOn
        ),
        ReportOptionData(
            stringResource(R.string.feature_publication_details_report_option_inappropriate_title),
            stringResource(R.string.feature_publication_details_report_option_inappropriate_subtitle),
            Icons.Outlined.Block
        ),
        ReportOptionData(
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
            color = Color.White
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
                            color = PrincipalBlue,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.feature_publication_details_report_modal_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.component_general_alert_dialog_close_content_description),
                            modifier = Modifier.size(18.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_publication_details_report_modal_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option.title
                        ReportOptionItem(
                            option = option,
                            isSelected = isSelected,
                            onClick = { selectedOption = option.title }
                        )
                    }
                }

                if (selectedOption == stringResource(R.string.feature_publication_details_report_option_other_title)) {
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
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = stringResource(R.string.feature_publication_details_report_reviewed_by_moderation),
                                style = TextTokens.helperText(),
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GeneralButton(
                    text = stringResource(R.string.feature_publication_details_send_report),
                    onClick = { /* Enviar */ },
                    icon = Icons.AutoMirrored.Filled.Send,
                    enabled = selectedOption.isNotEmpty()
                )

                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.feature_publication_details_cancel),
                        style = TextTokens.buttonLabel(),
                        color = PrincipalBlue
                    )
                }
            }
        }
    }
}

data class ReportOptionData(val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun ReportOptionItem(option: ReportOptionData, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) PrincipalBlue else Color(0xFFF1F5F9)),
        color = if (isSelected) Color(0xFFF8FAFF) else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) PrincipalBlue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = TextTokens.cardTitle(),
                    color = if (isSelected) PrincipalBlue else Color.Black
                )
                Text(
                    text = option.subtitle,
                    style = TextTokens.helperText(),
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrincipalBlue,
                    unselectedColor = Color.LightGray
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
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PrincipalOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = generalRating.toString(),
                        style = TextTokens.cardTitle(),
                        color = Color.Black
                    )
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
        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
        color = Color(0xFFF8FAFC)
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
                    style = TextTokens.sectionAction(),
                    color = Color.Black
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = PrincipalOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                color = DarkGray,
                style = TextTokens.inputText(),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun BottomActionsBar(
    isOwnerPublicationView: Boolean,
    onVisitedClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    var isInterested by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
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
                    border = BorderStroke(1.5.dp, PrincipalRed),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = PrincipalRed
                    )
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.feature_publication_details_delete_publication),
                        style = TextTokens.buttonLabel()
                    )
                }
            } else {
                // Botón Me interesa (con lógica de estado)
                Button(
                    onClick = { isInterested = !isInterested },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isInterested) null else BorderStroke(1.5.dp, PrincipalBlue),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isInterested) PrincipalBlue else Color.White,
                        contentColor = if (isInterested) Color.White else PrincipalBlue
                    )
                ) {
                    Icon(
                        imageVector = if (isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.feature_publication_details_interested_action),
                        style = TextTokens.buttonLabel()
                    )
                }

                // Botón Visitado
                OutlinedButton(
                    onClick = onVisitedClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, PrincipalGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrincipalGreen)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.feature_publication_details_visited_action),
                        style = TextTokens.buttonLabel()
                    )
                }
            }
        }
    }
}


