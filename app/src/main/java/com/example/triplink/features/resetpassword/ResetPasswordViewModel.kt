package com.example.triplink.features.resetpassword

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.domain.repository.user.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> appContext.getString(R.string.vm_reset_password_required)
            value.length < 6 -> appContext.getString(R.string.vm_reset_password_min_length)
            else -> null
        }
    }

    var confirmPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> appContext.getString(R.string.vm_reset_confirm_required)
            value != password.value -> appContext.getString(R.string.vm_reset_password_mismatch)
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

    fun saveNewPassword( oobCode: String) {
        if (!isFormValid) {
            _recoveryResult.value = RequestResult.Failure(appContext.getString(R.string.vm_reset_invalid_form))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.confirmPasswordReset(
                    oobCode = oobCode,
                    newPassword = password.value
                )

                _isLoading.value = false
                _recoveryResult.value = RequestResult.Success(
                    appContext.getString(R.string.vm_reset_success)
                )
            } catch (e: Exception) {
                _recoveryResult.value = RequestResult.Failure(
                    appContext.getString(
                        R.string.vm_reset_error,
                        e.localizedMessage ?: e.message.orEmpty()
                    )
                )
            }
        }
    }
}
