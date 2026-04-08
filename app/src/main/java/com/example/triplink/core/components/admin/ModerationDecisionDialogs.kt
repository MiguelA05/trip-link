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
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGreen
import com.example.triplink.ui.theme.PrincipalRed
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun ApprovePublicationDialog(
    publicationTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseModerationDialog(
        onDismiss = onDismiss,
        icon = Icons.Outlined.ThumbUp,
        iconTint = PrincipalGreen,
        iconBackground = Color(0xFFDCF2D5),
        title = stringResource(R.string.component_moderation_decision_dialog_approve_title),
        message = stringResource(
            R.string.component_moderation_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_moderation_decision_dialog_approve_action),
        confirmColor = PrincipalGreen,
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
        iconTint = Color(0xFFB3261E),
        iconBackground = Color(0xFFF7DCDD),
        title = stringResource(R.string.component_moderation_decision_dialog_reject_title),
        message = stringResource(
            R.string.component_moderation_decision_dialog_message_publication,
            publicationTitle
        ),
        confirmText = stringResource(R.string.component_moderation_decision_dialog_reject_action),
        confirmColor = PrincipalRed,
        confirmEnabled = reason.isNotBlank(),
        onConfirm = onConfirm,
        body = {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFF7F7F8),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.component_moderation_decision_dialog_reason_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A2C33)
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { if (it.length <= 300) onReasonChange(it) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.component_moderation_decision_dialog_reason_placeholder),
                                color = Color(0xFF9C9FA8)
                            )
                        },
                        minLines = 4,
                        maxLines = 4,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFEEF0F4),
                            unfocusedContainerColor = Color(0xFFEEF0F4),
                            focusedBorderColor = Color(0xFFD3D8E3),
                            unfocusedBorderColor = Color(0xFFD3D8E3)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = stringResource(
                            R.string.component_moderation_decision_dialog_reason_counter,
                            reason.length,
                            300
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFA1A8B6),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
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
            color = PrincipalWhite,
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
                        .background(Color(0xFFF2F4F8), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.component_moderation_decision_dialog_close),
                        tint = Color(0xFF9EA5B2)
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF575B65),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = Color(0xFFE9EBF0))

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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrincipalBlue)
                    ) {
                        Text(
                            text = stringResource(R.string.component_moderation_decision_dialog_cancel_action),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
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
                            contentColor = PrincipalWhite
                        )
                    ) {
                        Text(
                            text = confirmText,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}



