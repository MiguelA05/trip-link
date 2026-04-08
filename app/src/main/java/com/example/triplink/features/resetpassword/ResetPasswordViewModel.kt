package com.example.triplink.features.resetpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()

    var password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contrasena es obligatoria"
            value.length < 6 -> "La contrasena debe tener al menos 6 caracteres"
            else -> null
        }
    }

    var confirmPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La confirmacion es obligatoria"
            value != password.value -> "Las contrasenas no coinciden"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = password.isValid && confirmPassword.isValid && password.value.isNotEmpty()

    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun resetRecoveryResult() {
        _recoveryResult.value = null
    }

    fun saveNewPassword() {
        _recoveryResult.value = if (isFormValid) {
            RequestResult.Success("Contrasena restablecida con exito")
        } else {
            RequestResult.Failure("Por favor, verifica los campos")
        }
    }
}