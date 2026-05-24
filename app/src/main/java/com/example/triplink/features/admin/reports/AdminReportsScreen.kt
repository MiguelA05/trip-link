package com.example.triplink.features.admin.reports

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
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.triplink.R
import com.example.triplink.core.components.ConfirmReportDialog
import com.example.triplink.core.components.InvalidateReportDialog
import com.example.triplink.core.components.admin.ReportModerationPublicationCard
import com.example.triplink.ui.theme.TextTokens

@Composable
fun AdminReportsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onReportClick: (String) -> Unit = {}
) {
    val viewModel: AdminReportsViewModel = hiltViewModel()
    val listState = rememberLazyListState()
    val reportCards by viewModel.reportCards.collectAsStateWithLifecycle()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var selectedReportId by remember { mutableStateOf("") }
    var selectedReportTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.feature_admin_reports_title),
                    style = TextTokens.sectionTitle(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.feature_admin_reports_subtitle),
                    style = TextTokens.input(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.feature_admin_moderation_badge),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = TextTokens.button()
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.tertiary
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 18.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = viewModel.pendingCount.toString(),
                    style = TextTokens.screenTitle(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = stringResource(R.string.feature_admin_reports_counter_label),
                    style = TextTokens.button(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(reportCards, key = { it.id }) { publication ->
                ReportModerationPublicationCard(
                    publication = publication.toCardUi(),
                    onConfirmReport = {
                        selectedReportId = it
                        selectedReportTitle = publication.title
                        showConfirmDialog = true
                    },
                    onInvalidateReport = {
                        selectedReportId = it
                        selectedReportTitle = publication.title
                        showRejectDialog = true
                    },
                    onDetailsClick = { onReportClick(it) },
                )
            }

            if (reportCards.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.feature_admin_reports_empty_state),
                            modifier = Modifier.padding(20.dp),
                            style = TextTokens.input(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog && selectedReportId.isNotEmpty()) {
        ConfirmReportDialog(
            publicationTitle = selectedReportTitle,
            onDismiss = {
                showConfirmDialog = false
                selectedReportId = ""
                selectedReportTitle = ""
            },
            onConfirm = {
                viewModel.confirmReport(selectedReportId)
                showConfirmDialog = false
                selectedReportId = ""
                selectedReportTitle = ""
            }
        )
    }

    if (showRejectDialog && selectedReportId.isNotEmpty()) {
        InvalidateReportDialog(
            publicationTitle = selectedReportTitle,
            onDismiss = {
                showRejectDialog = false
                selectedReportId = ""
                selectedReportTitle = ""
            },
            onConfirm = {
                viewModel.invalidateReport(selectedReportId)
                showRejectDialog = false
                selectedReportId = ""
                selectedReportTitle = ""
            }
        )
    }
}
