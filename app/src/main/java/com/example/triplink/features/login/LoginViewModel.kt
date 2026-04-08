package com.example.triplink.features.login

import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val userRepository: UserRepository
) : ViewModel() {
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

    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }


    fun resetForm() {
        email.reset()
        password.reset()
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }


    fun login() {
        if (!isFormValid) {
            _loginResult.value = RequestResult.Failure("Completa los campos requeridos")
            return
        }

        val authenticatedUser = userRepository.login(email.value, password.value)
        if (authenticatedUser == null) {
            _loginResult.value = RequestResult.Failure("Credenciales inválidas")
            return
        }

        viewModelScope.launch {
            runCatching {
                sessionDataStore.saveSession(
                    userId = authenticatedUser.email, // Email se usa como identificador único del usuario
                    role = authenticatedUser.rol
                )
            }.onSuccess {
                _loginResult.value = RequestResult.Success("Login exitoso")
            }.onFailure {
                _loginResult.value = RequestResult.Failure("No fue posible iniciar sesión. Intenta nuevamente.")
            }
        }
    }

}
