package com.example.triplink.features.admin.moderation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.ApprovePublicationDialog
import com.example.triplink.core.components.ModerationPublicationCard
import com.example.triplink.core.components.RejectPublicationDialog
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.ui.theme.TextTokens

@Composable
fun ModerationScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPublicationDetailsClick: (String) -> Unit = {}
) {
    val viewModel: ModerationViewModel = hiltViewModel()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val filteredPublications by viewModel.filteredPublications.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()
    val verifiedCount by viewModel.verifiedCount.collectAsStateWithLifecycle()
    val rejectedCount by viewModel.rejectedCount.collectAsStateWithLifecycle()

    var pendingDecision by remember { mutableStateOf<DecisionModerador?>(null) }
    var selectedPublication by remember { mutableStateOf<ModerationPublication?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    fun openApproveDialog(publicationId: String) {
        selectedPublication = filteredPublications.firstOrNull { it.id == publicationId }
        if (selectedPublication != null) {
            pendingDecision = DecisionModerador.APROBADA
        }
    }

    fun openRejectDialog(publicationId: String) {
        selectedPublication = filteredPublications.firstOrNull { it.id == publicationId }
        if (selectedPublication != null) {
            rejectionReason = ""
            pendingDecision = DecisionModerador.RECHAZADA
        }
    }

    fun dismissApproveDialog() {
        pendingDecision = null
        selectedPublication = null
    }

    fun dismissRejectDialog() {
        pendingDecision = null
        selectedPublication = null
        rejectionReason = ""
    }

    fun handleFilterClick(filter: ModerationFilter) {
        if (selectedFilter == filter) {
            viewModel.onFilterSelected(ModerationFilter.ALL)
        } else {
            viewModel.onFilterSelected(filter)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.feature_admin_moderation_title),
                    style = TextTokens.sectionTitle()
                )
                Text(
                    text = stringResource(R.string.feature_admin_moderation_subtitle),
                    style = TextTokens.body(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = stringResource(R.string.feature_admin_moderation_badge),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = TextTokens.button()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatItem(
                value = pendingCount.toString(),
                label = stringResource(R.string.feature_admin_moderation_filter_pending),
                color = MaterialTheme.colorScheme.tertiary,
                isSelected = selectedFilter == ModerationFilter.PENDING,
                onClick = { handleFilterClick(ModerationFilter.PENDING) },
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = verifiedCount.toString(),
                label = stringResource(R.string.feature_admin_moderation_filter_verified),
                color = MaterialTheme.colorScheme.primary,
                isSelected = selectedFilter == ModerationFilter.VERIFIED,
                onClick = { handleFilterClick(ModerationFilter.VERIFIED) },
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = rejectedCount.toString(),
                label = stringResource(R.string.feature_admin_moderation_filter_rejected),
                color = MaterialTheme.colorScheme.error,
                isSelected = selectedFilter == ModerationFilter.REJECTED,
                onClick = { handleFilterClick(ModerationFilter.REJECTED) },
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPublications, key = { "${it.id}-${it.pointOfInterest.estado}" }) { publication ->
                ModerationPublicationCard(
                    publication = publication.toCardUi(context),
                    onApproveRequested = ::openApproveDialog,
                    onRejectRequested = ::openRejectDialog,
                    onDetailsClick = onPublicationDetailsClick
                )
            }

            if (filteredPublications.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_admin_moderation_empty_state),
                            modifier = Modifier.padding(20.dp),
                            style = TextTokens.input(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    val publicationTitle = selectedPublication?.pointOfInterest?.titulo.orEmpty()

    when (pendingDecision) {
        DecisionModerador.APROBADA -> {
            ApprovePublicationDialog(
                publicationTitle = publicationTitle,
                onDismiss = ::dismissApproveDialog,
                onConfirm = {
                    selectedPublication?.let { viewModel.applyDecision(it.id, DecisionModerador.APROBADA) }
                    dismissApproveDialog()
                }
            )
        }

        DecisionModerador.RECHAZADA -> {
            RejectPublicationDialog(
                publicationTitle = publicationTitle,
                reason = rejectionReason,
                onReasonChange = { rejectionReason = it },
                onDismiss = ::dismissRejectDialog,
                onConfirm = {
                    if (rejectionReason.isBlank()) return@RejectPublicationDialog
                    selectedPublication?.let {
                        viewModel.applyDecision(
                            publicationId = it.id,
                            decision = DecisionModerador.RECHAZADA,
                            reason = rejectionReason.trim()
                        )
                    }
                    dismissRejectDialog()
                }
            )
        }

        null -> Unit
    }
}


@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (isSelected) color.copy(alpha = 0.12f) else color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else color.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = TextTokens.screenTitle(),
                color = color
            )
            Text(
                text = label,
                style = TextTokens.label(),
                color = color
            )
        }
    }
}
