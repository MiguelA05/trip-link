package com.example.triplink.features.login

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

enum class LoginRole {
    USER,
    ADMIN
}

class LoginViewModel : ViewModel() {
    var email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }


    var password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    val isFormValid: Boolean
        get() = email.isValid
                && password.isValid


    var passwordError by
    mutableStateOf("")

    var passwordVisible by
    mutableStateOf(false)


    var showDialog by
    mutableStateOf(false)

    val forgotPasswordInteractionSource = MutableInteractionSource()

    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    private val _loginRole = MutableStateFlow<LoginRole?>(null)

    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()
    val loginRole: StateFlow<LoginRole?> = _loginRole.asStateFlow()

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }


    fun resetForm() {
        email.reset()
        password.reset()
    }

    fun resetLoginResult() {
        _loginResult.value = null
        _loginRole.value = null
    }


    fun login() {
        if (isFormValid) {
            // Simulación de un proceso de login con datos estáticos
            when {
                email.value == "carlos@email.com" && password.value == "123456" -> {
                    _loginRole.value = LoginRole.USER
                    _loginResult.value = RequestResult.Success("Login exitoso")
                }
                email.value == "admin@triplink.com" && password.value == "admin123" -> {
                    _loginRole.value = LoginRole.ADMIN
                    _loginResult.value = RequestResult.Success("Login de administrador exitoso")
                }
                else -> {
                    _loginRole.value = null
                    _loginResult.value = RequestResult.Failure("Credenciales inválidas")
                }
            }
        }
    }

}
