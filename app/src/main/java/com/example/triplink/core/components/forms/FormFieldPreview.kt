package com.example.triplink.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun FormFieldPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        FormField(
            label = "Correo electrónico",
            value = "",
            onValueChange = {},
            placeholder = "tu@email.com"
        )
    }
}

