package com.example.triplink.features.resetpassword

import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResetPasswordViewModel : ViewModel() {


    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()
    var password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    var confirmPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            value != password.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() =  password.isValid && confirmPassword.isValid


    var passwordError by
    mutableStateOf("")

    var passwordVisible by
    mutableStateOf(false)

    var confirmPasswordVisible by mutableStateOf(false)

    var showDialog by
    mutableStateOf(false)


    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility(){
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun resetForm() {
        password.reset()
        confirmPassword.reset()
    }


    fun resetPassword():Boolean{
        return  password.equals("admin")
    }

    fun saveNewPassword(){
        if (isFormValid) {
            // Simulate sending a password reset email
            _recoveryResult.value = RequestResult.Success("Se ha enviado un correo de recuperación")
        } else {
            _recoveryResult.value = RequestResult.Failure("Por favor, ingresa un email válido")
        }
    }

}