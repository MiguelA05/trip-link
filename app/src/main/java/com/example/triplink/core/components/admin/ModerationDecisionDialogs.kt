package com.example.triplink.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.ui.theme.TextTokens

@Composable
fun ApprovePublicationDialog(
    publicationTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseModerationDialog(
        onDismiss = onDismiss,
        icon = Icons.Outlined.ThumbUp,
        iconTint = MaterialTheme.colorScheme.primary,
        iconBackground = MaterialTheme.colorScheme.primaryContainer,
        title = stringResource(R.string.component_moderation_decision_dialog_approve_title),
        message = stringResource(
            R.string.component_moderation_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_moderation_decision_dialog_approve_action),
        confirmColor = MaterialTheme.colorScheme.primary,
        confirmEnabled = true,
        onConfirm = onConfirm,
        body = null
    )
}

@Composable
fun RejectPublicationDialog(
    publicationTitle: String,
    reason: String,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseModerationDialog(
        onDismiss = onDismiss,
        icon = Icons.Outlined.ThumbDown,
        iconTint = MaterialTheme.colorScheme.error,
        iconBackground = MaterialTheme.colorScheme.errorContainer,
        title = stringResource(R.string.component_moderation_decision_dialog_reject_title),
        message = stringResource(
            R.string.component_moderation_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_moderation_decision_dialog_reject_action),
        confirmColor = MaterialTheme.colorScheme.error,
        confirmEnabled = reason.isNotBlank(),
        onConfirm = onConfirm,
        body = {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.component_moderation_decision_dialog_reason_label),
                        style = TextTokens.emphasized(TextTokens.title(), FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { if (it.length <= 300) onReasonChange(it) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.component_moderation_decision_dialog_reason_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        minLines = 4,
                        maxLines = 4,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = stringResource(
                            R.string.component_moderation_decision_dialog_reason_counter,
                            reason.length,
                            300
                        ),
                        style = TextTokens.chip(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    )
}

@Composable
fun ConfirmReportDialog(
    publicationTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseModerationDialog(
        onDismiss = onDismiss,
        icon = Icons.Outlined.ThumbUp,
        iconTint = MaterialTheme.colorScheme.primary,
        iconBackground = MaterialTheme.colorScheme.primaryContainer,
        title = stringResource(R.string.component_report_decision_dialog_confirm_title),
        message = stringResource(
            R.string.component_report_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_report_decision_dialog_confirm_action),
        confirmColor = MaterialTheme.colorScheme.primary,
        confirmEnabled = true,
        onConfirm = onConfirm,
        body = null
    )
}

@Composable
fun InvalidateReportDialog(
    publicationTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseModerationDialog(
        onDismiss = onDismiss,
        icon = Icons.Outlined.ThumbDown,
        iconTint = MaterialTheme.colorScheme.error,
        iconBackground = MaterialTheme.colorScheme.errorContainer,
        title = stringResource(R.string.component_report_decision_dialog_invalidate_title),
        message = stringResource(
            R.string.component_report_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_report_decision_dialog_invalidate_action),
        confirmColor = MaterialTheme.colorScheme.error,
        confirmEnabled = true,
        onConfirm = onConfirm,
        body = null
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BaseModerationDialog(
    onDismiss: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    body: (@Composable (() -> Unit))?
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.component_moderation_decision_dialog_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(iconBackground, CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = title,
                    style = TextTokens.emphasized(TextTokens.sectionTitle(), FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = message,
                    style = TextTokens.input(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                body?.invoke()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.component_moderation_decision_dialog_cancel_action),
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = confirmColor,
                            contentColor = if (confirmColor == MaterialTheme.colorScheme.error) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = confirmText,
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}



