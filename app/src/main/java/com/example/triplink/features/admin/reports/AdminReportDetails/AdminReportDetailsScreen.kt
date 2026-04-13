package com.example.triplink.features.admin.reports.AdminReportDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.R
import com.example.triplink.core.components.DestructiveConfirmDialog
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@Composable
fun AdminReportDetailsScreen(
    reportId: String,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {}
) {
    val viewModel: AdminReportsDetailsViewModel = hiltViewModel()
    val report = viewModel.getReportById(reportId)

    if (report == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.feature_admin_report_details_not_found),
                style = TextTokens.sectionTitle(),
                color = TextColors.Primary
            )
        }
        return
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.component_general_top_bar_back_content_description),
                    tint = TextColors.Primary
                )
            }

            Text(
                text = stringResource(R.string.feature_admin_report_details_title),
                style = TextTokens.screenTitle(),
                color = TextColors.Primary
            )

            Spacer(modifier = Modifier.size(48.dp))
        }

        if (report.imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(report.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = report.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_admin_report_details_publication_info_title),
                        style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                        color = TextColors.Primary
                    )

                    Text(
                        text = report.title,
                        style = TextTokens.emphasized(TextTokens.screenTitle(), FontWeight.Bold),
                        color = TextColors.Primary
                    )

                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Text(
                            text = report.categoryLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = TextTokens.emphasized(TextTokens.chip()),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.cityLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.priceLabel,
                            style = TextTokens.emphasized(TextTokens.body()),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.scheduleLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_admin_report_details_report_info_title),
                        style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                        color = TextColors.Primary
                    )

                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.errorContainer) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.feature_admin_report_details_report_reason_label),
                                style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = report.reasonMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextColors.Primary
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.feature_admin_report_details_reported_by_label),
                            style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                            color = TextColors.Secondary
                        )
                        Text(
                            text = report.authorName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextColors.Primary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.feature_admin_report_details_report_date_label),
                            style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                            color = TextColors.Secondary
                        )
                        Text(
                            text = report.timeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextColors.Primary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.feature_admin_report_details_accepted_reports_label),
                            style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                            color = TextColors.Secondary
                        )
                        Text(
                            text = stringResource(
                                R.string.feature_publication_details_comment_counter,
                                report.acceptedReportsCount,
                                3
                            ),
                            style = TextTokens.emphasized(TextTokens.body(), FontWeight.Bold),
                            color = TextColors.Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Button(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_admin_report_details_reject_action),
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_admin_report_details_confirm_action),
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        GeneralAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            onConfirm = {
                viewModel.confirmReport(report.id)
                onBackClick()
            },
            title = stringResource(R.string.feature_admin_report_details_confirm_dialog_title),
            message = stringResource(R.string.feature_admin_report_details_confirm_dialog_message, report.title),
            icon = Icons.Default.CheckCircle,
            buttonText = stringResource(R.string.feature_admin_report_details_confirm_action),
            dismissButtonText = stringResource(R.string.feature_admin_report_details_cancel_action),
            onDismissAction = { showConfirmDialog = false }
        )
    }

    if (showRejectDialog) {
        DestructiveConfirmDialog(
            title = stringResource(R.string.feature_admin_report_details_reject_dialog_title),
            message = stringResource(R.string.feature_admin_report_details_reject_dialog_message, report.title),
            confirmText = stringResource(R.string.feature_admin_report_details_reject_action),
            dismissText = stringResource(R.string.feature_admin_report_details_cancel_action),
            onDismissRequest = { showRejectDialog = false },
            onConfirm = {
                viewModel.invalidateReport(report.id)
                onBackClick()
            }
        )
    }
}
