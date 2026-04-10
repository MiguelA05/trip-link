package com.example.triplink.features.admin.moderation.ModerationPublicationDetails

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.ApprovePublicationDialog
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.RejectPublicationDialog
import com.example.triplink.core.components.publicationdetails.hero.PublicationPreviewHero
import com.example.triplink.core.components.publicationdetails.sections.PublicationLocationSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationPriceRangeSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationTextSection
import com.example.triplink.core.components.publicationdetails.sections.PublicationWeeklyScheduleSection
import com.example.triplink.core.components.publicationdetails.utils.currentDayLocalizedLabel
import com.example.triplink.core.components.publicationdetails.utils.toWeeklyScheduleUi
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedLabelOrNoPrice
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.ui.theme.PrincipalGreen
import com.example.triplink.ui.theme.PrincipalRed

@Composable
fun ModerationPublicationDetailsScreen(
    publicationId: String,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {}
) {
    val viewModel: ModerationPublicationDetailsViewModel = hiltViewModel()
    val publication = viewModel.getPublicationById(publicationId)

    if (publication == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.feature_publication_details_not_found),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2430)
            )
        }
        return
    }

    val interactionEnabled = publication.pointOfInterest.estado == EstadoPublicacion.PENDIENTE
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7)),
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_moderation_publication_details_title),
                onBack = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(bottom = 112.dp)
            ) {
                item {
                    PublicationPreviewHero(
                        imageUrl = publication.pointOfInterest.fotos.firstOrNull().orEmpty(),
                        categoryLabel = publication.pointOfInterest.categoria.localizedLabel(),
                        title = publication.pointOfInterest.titulo
                    )
                }

                item {
                    PublicationTextSection(
                        title = stringResource(R.string.feature_publication_details_description),
                        body = publication.pointOfInterest.informacion
                    )
                }

                item {
                    PublicationPriceRangeSection(
                        selectedLevel = publication.pointOfInterest.rangoPrecios.localizedLabelOrNoPrice()
                    )
                }

                item {
                    PublicationLocationSection(
                        city = publication.pointOfInterest.ubicacion.ciudad,
                        coordinates = "${publication.pointOfInterest.ubicacion.latitud}, ${publication.pointOfInterest.ubicacion.longitud}"
                    )
                }

                item {
                    PublicationWeeklyScheduleSection(
                        schedules = publication.pointOfInterest.horarios.toWeeklyScheduleUi(),
                        today = currentDayLocalizedLabel()
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!interactionEnabled) {
                        Text(
                            text = stringResource(R.string.feature_moderation_publication_details_already_reviewed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF7C889B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (interactionEnabled) showRejectDialog = true },
                            enabled = interactionEnabled,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, PrincipalRed),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrincipalRed,
                                disabledContentColor = PrincipalRed.copy(alpha = 0.45f)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.feature_moderation_publication_details_reject_action),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { if (interactionEnabled) showApproveDialog = true },
                            enabled = interactionEnabled,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrincipalGreen,
                                disabledContainerColor = PrincipalGreen.copy(alpha = 0.35f)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.feature_moderation_publication_details_approve_action),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showApproveDialog) {
        ApprovePublicationDialog(
            publicationTitle = publication.pointOfInterest.titulo,
            onDismiss = {
                showApproveDialog = false
            },
            onConfirm = {
                viewModel.applyDecision(publication.id, DecisionModerador.APROBADA)
                onBackClick()
            }
        )
    }

    if (showRejectDialog) {
        RejectPublicationDialog(
            publicationTitle = publication.pointOfInterest.titulo,
            reason = rejectionReason,
            onReasonChange = {
                rejectionReason = it
            },
            onDismiss = {
                showRejectDialog = false
            },
            onConfirm = {
                if (rejectionReason.isBlank()) return@RejectPublicationDialog
                viewModel.applyDecision(
                    publicationId = publication.id,
                    decision = DecisionModerador.RECHAZADA,
                    reason = rejectionReason.trim()
                )
                onBackClick()
            }
        )
    }
}
