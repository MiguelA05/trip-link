package com.example.triplink.core.components.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        swipeHintText = "Desliza a la izquierda para confirmar • Desliza a la derecha para invalidar"
    )
}

