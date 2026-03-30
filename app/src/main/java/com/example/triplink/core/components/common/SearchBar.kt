package com.example.triplink.core.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = "Buscar lugares...") },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFF666666)
                )
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedBorderColor = Color(0xFFD0D0D0),
                unfocusedBorderColor = Color(0xFFD0D0D0)
            )
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF5F5F5),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filtros",
                    tint = Color(0xFF555555)
                )
            }
        }
    }
}

