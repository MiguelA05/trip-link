package com.example.triplink.features.recoverypassword

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecoveryPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

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
        if (!isFormValid) {
            _recoveryResult.value = RequestResult.Failure("Por favor, ingresa un email válido")
            return
        }

        // Validar que el email existe en el sistema
        val userExists = userRepository.findByEmail(email.value) != null
        if (!userExists) {
            _recoveryResult.value = RequestResult.Failure("Este email no está registrado en el sistema")
            return
        }

        // En una aplicación real, aquí se enviaría un email
        // Por ahora, simulamos el envío exitoso
        isEmailSent = true
        showSuccessDialog = true
        _recoveryResult.value = RequestResult.Success("Se ha enviado un correo de recuperación a ${email.value}")
    }
}