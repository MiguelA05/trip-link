package com.example.triplink.core.components.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.core.components.ModerationPublicationCard
import com.example.triplink.features.admin.moderation.ModerationPublicationCardUi

@Composable
fun ReportModerationPublicationCard(
    publication: ModerationPublicationCardUi,
    onConfirmReport: (String) -> Unit,
    onInvalidateReport: (String) -> Unit,
    onDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModerationPublicationCard(
        publication = publication,
        // En reportes, derecha confirma e izquierda invalida.
        onApproveRequested = onInvalidateReport,
        onRejectRequested = onConfirmReport,
        onDetailsClick = onDetailsClick,
        modifier = modifier,
        swipeHintText = stringResource(
            R.string.component_report_moderation_publication_card_swipe_hint
        )
    )
}

