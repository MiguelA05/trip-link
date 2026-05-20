package com.example.triplink.features.recoverypassword

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.R
import com.example.triplink.domain.repository.user.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoveryPasswordViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Controla si el correo ya se envió al menos una vez (para cambiar los textos de la UI)
    var isEmailSent by mutableStateOf(false)
        private set

    // Controla la visibilidad del modal de éxito
    var showSuccessDialog by mutableStateOf(false)
        private set

    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> appContext.getString(R.string.vm_recovery_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> appContext.getString(R.string.vm_recovery_email_invalid)
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
            _recoveryResult.value = RequestResult.Failure(appContext.getString(R.string.vm_recovery_invalid_form))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Validar que el email existe en el sistema
                /*
                val userExists = authRepository.findByEmail(email.value) != null
                if (!userExists) {
                    _recoveryResult.value = RequestResult.Failure(appContext.getString(R.string.vm_recovery_email_not_found))
                    _isLoading.value = false
                    return@launch
                }
                */

                // En una aplicación real, aquí se enviaría un email
                // Por ahora, simulamos el envío exitoso
                authRepository.sendPasswordResetEmail(email.value)
                _isLoading.value = false
                //TODO reemplazar por string.xml
                _recoveryResult.value = RequestResult.Success("se ha enviado un correo de recuperación a ${email.value}")
            } catch (e: Exception) {
                _recoveryResult.value = RequestResult.Failure(appContext.getString(R.string.vm_recovery_invalid_form))
            } finally {
                _isLoading.value = false
            }
        }
    }
}