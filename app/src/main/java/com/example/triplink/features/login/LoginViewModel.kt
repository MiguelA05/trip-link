package com.example.triplink.features.login

import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.domain.model.enums.Rol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore
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

        // Login mockeado: al validar credenciales se persiste la sesión para que AppNavigation cambie de grafo.
        val sessionToSave = when {
            email.value == "carlos@email.com" && password.value == "123456" -> {
                Triple("user-1", Rol.USUARIO, "Login exitoso")
            }
            email.value == "admin@triplink.com" && password.value == "admin123" -> {
                Triple("admin-1", Rol.MODERADOR, "Login de administrador exitoso")
            }
            else -> null
        }

        if (sessionToSave == null) {
            _loginResult.value = RequestResult.Failure("Credenciales inválidas")
            return
        }

        viewModelScope.launch {
            runCatching {
                sessionDataStore.saveSession(
                    userId = sessionToSave.first,
                    role = sessionToSave.second
                )
            }.onSuccess {
                _loginResult.value = RequestResult.Success(sessionToSave.third)
            }.onFailure {
                _loginResult.value = RequestResult.Failure("No fue posible iniciar sesión. Intenta nuevamente.")
            }
        }
    }

}
