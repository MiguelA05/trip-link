package com.example.triplink.core.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun GeneralButton(
    primary: Boolean = true,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    text: String
) {
    val content = @Composable {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Text(
            text = text
        )

    }

    if (primary) {
        Button(
            onClick = onClick,

            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(width = 300.dp, height = 50.dp),
            shape = RoundedCornerShape(40)

        ) {
            content()
        }
    } else {
        FilledTonalButton(
            onClick = onClick,

            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(width = 300.dp, height = 50.dp),
            shape = RoundedCornerShape(40)

        ) {
            content()
        }
    }
}