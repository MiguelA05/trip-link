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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.core.components.ApprovePublicationDialog
import com.example.triplink.core.components.ModerationPublicationCard
import com.example.triplink.core.components.RejectPublicationDialog
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.ui.theme.PrincipalBlue
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun ModerationScreen(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ModerationViewModel = viewModel()
) {
    val moderationChipLabels = listOf("Todas", "Pendientes", "Verificadas", "Rechazadas")
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var pendingDecision by remember { mutableStateOf<ModerationDecision?>(null) }
    var selectedPublication by remember { mutableStateOf<ModerationPublicationUi?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    fun openApproveDialog(publicationId: String) {
        selectedPublication = viewModel.filteredPublications.firstOrNull { it.id == publicationId }
        if (selectedPublication != null) {
            pendingDecision = ModerationDecision.APPROVE
        }
    }

    fun openRejectDialog(publicationId: String) {
        selectedPublication = viewModel.filteredPublications.firstOrNull { it.id == publicationId }
        if (selectedPublication != null) {
            rejectionReason = ""
            pendingDecision = ModerationDecision.REJECT
        }
    }

    fun dismissDialog() {
        pendingDecision = null
        selectedPublication = null
        rejectionReason = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(contentPadding)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Moderación",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Publicaciones recientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF97A0AF)
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFE8F2FF)
            ) {
                Text(
                    text = "MOD",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = PrincipalBlue,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatItem(
                value = viewModel.pendingCount.toString(),
                label = "Pendientes",
                color = Color(0xFFF58C1E),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = viewModel.verifiedCount.toString(),
                label = "Verificadas",
                color = Color(0xFF3AA454),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = viewModel.rejectedCount.toString(),
                label = "Rechazadas",
                color = Color(0xFFE24A4A),
                modifier = Modifier.weight(1f)
            )
        }

        CategoryChips(
            categories = moderationChipLabels,
            selectedCategory = viewModel.selectedFilter.toChipLabel(),
            onCategorySelected = { selectedLabel ->
                viewModel.onFilterSelected(selectedLabel.toModerationFilter())
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.filteredPublications, key = { "${it.id}-${it.status}" }) { publication ->
                ModerationPublicationCard(
                    publication = publication,
                    onApproveRequested = ::openApproveDialog,
                    onRejectRequested = ::openRejectDialog,
                    onDetailsClick = {}
                )
            }

            if (viewModel.filteredPublications.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = Color(0xFFF0F2F7),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "No hay publicaciones para mostrar en este filtro",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF7C889B)
                        )
                    }
                }
            }
        }
    }

    val publicationTitle = selectedPublication?.title.orEmpty()

    when (pendingDecision) {
        ModerationDecision.APPROVE -> {
            ApprovePublicationDialog(
                publicationTitle = publicationTitle,
                onDismiss = ::dismissDialog,
                onConfirm = {
                    selectedPublication?.let { viewModel.applyDecision(it.id, ModerationDecision.APPROVE) }
                    coroutineScope.launch { listState.scrollToItem(0) }
                    dismissDialog()
                }
            )
        }

        ModerationDecision.REJECT -> {
            RejectPublicationDialog(
                publicationTitle = publicationTitle,
                reason = rejectionReason,
                onReasonChange = { rejectionReason = it },
                onDismiss = ::dismissDialog,
                onConfirm = {
                    if (rejectionReason.isBlank()) return@RejectPublicationDialog
                    selectedPublication?.let {
                        viewModel.applyDecision(
                            publicationId = it.id,
                            decision = ModerationDecision.REJECT,
                            reason = rejectionReason.trim()
                        )
                    }
                    coroutineScope.launch { listState.scrollToItem(0) }
                    dismissDialog()
                }
            )
        }

        null -> Unit
    }
}

private fun ModerationFilter.toChipLabel(): String = when (this) {
    ModerationFilter.ALL -> "Todas"
    ModerationFilter.PENDING -> "Pendientes"
    ModerationFilter.VERIFIED -> "Verificadas"
    ModerationFilter.REJECTED -> "Rechazadas"
}

private fun String.toModerationFilter(): ModerationFilter = when (this) {
    "Pendientes" -> ModerationFilter.PENDING
    "Verificadas" -> ModerationFilter.VERIFIED
    "Rechazadas" -> ModerationFilter.REJECTED
    else -> ModerationFilter.ALL
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

