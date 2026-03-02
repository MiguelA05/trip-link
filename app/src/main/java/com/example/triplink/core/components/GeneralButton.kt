package com.example.triplink.core.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.ui.theme.PrincipalBlueBlocked

@Composable
fun GeneralButton(
    primary: Boolean = true,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    val content = @Composable {
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (primary) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB), // Color normal
                contentColor = Color.White,         // Texto normal

                disabledContainerColor = PrincipalBlueBlocked, // Un azul más claro o gris
                disabledContentColor = Color.White.copy(alpha = 0.6f) // Texto semi-transparente
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
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.Gray
            )

        ) {
            content()
        }
    }
}
