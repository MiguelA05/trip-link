package com.example.triplink.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun GeneralAlertDialogPreview() {
    GeneralAlertDialog(
        onDismissRequest = {},
        onConfirm = {},
        title = "Revisa tu correo",
        message = "Comprueba tu bandeja de entrada y sigue el enlace para reestablecer tu contraseña de forma segura"
    )
}

