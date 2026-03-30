package com.example.triplink.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.triplink.ui.theme.FormFieldBackground
import com.example.triplink.ui.theme.FormFieldBorder
import com.example.triplink.ui.theme.FormFieldPlaceholder
import com.example.triplink.ui.theme.PrincipalBlack
import com.example.triplink.ui.theme.PrincipalRed

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left,
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = PrincipalBlack
            )
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = FormFieldPlaceholder
                    )
                ) 
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError,
            supportingText = if (isError && errorText != null) {
                { 
                    Text(
                        text = errorText, 
                        style = MaterialTheme.typography.bodySmall,
                        color = PrincipalRed
                    ) 
                }
            } else null,
            trailingIcon = trailingIcon,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrincipalBlack),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FormFieldBackground,
                unfocusedContainerColor = FormFieldBackground,
                disabledContainerColor = FormFieldBackground,
                errorContainerColor = FormFieldBackground,
                focusedBorderColor = FormFieldBorder,
                unfocusedBorderColor = FormFieldBorder,
                errorBorderColor = PrincipalRed,
                cursorColor = PrincipalBlack
            )
        )
    }
}

