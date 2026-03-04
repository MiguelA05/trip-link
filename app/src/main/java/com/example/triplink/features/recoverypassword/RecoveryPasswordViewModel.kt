package com.example.triplink.features.recoverypassword

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecoveryPasswordViewModel : ViewModel() {

    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()

    // Controla si el correo ya se envió al menos una vez (para cambiar los textos de la UI)
    var isEmailSent by mutableStateOf(false)
        private set

    // Controla la visibilidad del modal de éxito
    var showSuccessDialog by mutableStateOf(false)
        private set

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = email.isValid

    fun resetRecoveryResult() {
        _recoveryResult.value = null
    }

    fun dismissDialog() {
        showSuccessDialog = false
    }

    fun sendPasswordResetEmail() {
        if (isFormValid) {
            isEmailSent = true
            showSuccessDialog = true
            // Simulate sending a password reset email
            _recoveryResult.value = RequestResult.Success("Se ha enviado un correo de recuperación")
        } else {
            _recoveryResult.value = RequestResult.Failure("Por favor, ingresa un email válido")
        }
    }
}