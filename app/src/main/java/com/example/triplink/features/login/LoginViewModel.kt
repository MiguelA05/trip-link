package com.example.triplink.features.login

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.R
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.repository.user.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val sessionDataStore: SessionDataStore,
    private val authRepository: AuthRepository
) : ViewModel() {
    var email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> appContext.getString(R.string.vm_login_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> appContext.getString(R.string.vm_login_email_invalid)
            else -> null
        }
    }


    var password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> appContext.getString(R.string.vm_login_password_required)
            value.length < 6 -> appContext.getString(R.string.vm_login_password_min_length)
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
            _loginResult.value = RequestResult.Failure(appContext.getString(R.string.vm_login_form_incomplete))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authenticatedUser = authRepository.login(email.value, password.value)
                if (authenticatedUser == null) {
                    _loginResult.value = RequestResult.Failure(appContext.getString(R.string.vm_login_invalid_credentials))
                    _isLoading.value = false
                    return@launch
                }

                sessionDataStore.saveSession(
                    userId = authenticatedUser.email, // Email se usa como identificador único del usuario
                    role = authenticatedUser.rol
                )

                _loginResult.value = RequestResult.Success(appContext.getString(R.string.vm_login_success))
            } catch (e: Exception) {
                _loginResult.value = RequestResult.Failure(appContext.getString(R.string.vm_login_unexpected_error))
            } finally {
                _isLoading.value = false
            }
        }
    }

}
