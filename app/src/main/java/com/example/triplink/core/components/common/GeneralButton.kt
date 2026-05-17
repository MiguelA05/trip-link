package com.example.triplink.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplink.ui.theme.TextTokens

@Composable
fun GeneralButton(
    primary: Boolean = true,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val content = @Composable {
        if (isLoading) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = if (primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription
                )
                Spacer(
                    modifier = Modifier.width(10.dp)
                )
            }
            Text(
                text = text,
                style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
            )
        }
    }

    if (primary) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            content()
        }
    } else {
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            content()
        }
    }
}

