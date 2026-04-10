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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.admin.ReportModerationPublicationCard
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun AdminReportsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onReportClick: (String) -> Unit = {}
) {
    val viewModel: AdminReportsViewModel = hiltViewModel()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1F26)
                )
                Text(
                    text = stringResource(R.string.feature_admin_reports_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF9AA3B2),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFE8F2FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB7D7FF))
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

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFFF7DE),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD36A))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 18.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = viewModel.pendingCount.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFF98A1F)
                )
                Text(
                    text = stringResource(R.string.feature_admin_reports_counter_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF98A1F),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(viewModel.reportCards, key = { it.id }) { publication ->
                ReportModerationPublicationCard(
                    publication = publication.toCardUi(),
                    onConfirmReport = viewModel::confirmReport,
                    onInvalidateReport = viewModel::invalidateReport,
                    onDetailsClick = { onReportClick(it) },
                )
            }

            if (viewModel.reportCards.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF0F2F7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E6EF))
                    ) {
                        Text(
                            text = stringResource(R.string.feature_admin_reports_empty_state),
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF7C889B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
