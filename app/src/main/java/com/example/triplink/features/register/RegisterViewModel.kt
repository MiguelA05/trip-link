package com.example.triplink.features.register

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.R
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.AuthRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var address by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)

    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var addressError by mutableStateOf<String?>(null)

    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var selectedLatitude by mutableStateOf<Double?>(null)
    var selectedLongitude by mutableStateOf<Double?>(null)

    fun onExactLocationSelected(longitude: Double, latitude: Double) {
        selectedLongitude = longitude
        selectedLatitude = latitude
    }

    val isFormValid by derivedStateOf {
        validateName(name) == null &&
            validatePhone(phone) == null &&
            validateEmail(email) == null &&
            validatePassword(password) == null &&
            validateAddress(address) == null &&
            selectedLatitude != null &&
            selectedLongitude != null
    }

    fun validateName(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_register_name_required) else null
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> appContext.getString(R.string.vm_register_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> appContext.getString(R.string.vm_register_email_invalid)
            else -> null
        }
    }

    fun validatePhone(value: String): String? {
        return when {
            value.isBlank() -> appContext.getString(R.string.vm_register_phone_required)
            !value.matches(Regex("^[0-9]{7,15}$")) -> appContext.getString(R.string.vm_register_phone_invalid)
            else -> null
        }
    }

    fun validatePassword(value: String): String? {
        return when {
            value.isBlank() -> appContext.getString(R.string.vm_register_password_required)
            value.length < 6 -> appContext.getString(R.string.vm_register_password_min_length)
            else -> null
        }
    }

    fun validateAddress(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_register_address_required) else null
    }

    fun onNameChange(newValue: String) {
        name = newValue
        nameError = validateName(newValue)
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        emailError = validateEmail(newValue)
    }

    fun onPhoneChange(newValue: String) {
        phone = newValue
        phoneError = validatePhone(newValue)
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordError = validatePassword(newValue)
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun onAddressChange(newValue: String) {
        address = newValue
        addressError = validateAddress(newValue)
    }

    fun validateAll(): Boolean {
        nameError = validateName(name)
        phoneError = validatePhone(phone)
        emailError = validateEmail(email)
        passwordError = validatePassword(password)
        addressError = validateAddress(address)

        return isFormValid
    }

    fun register() {
        if (!validateAll()) {
            _registerResult.value = RequestResult.Failure(appContext.getString(R.string.vm_register_form_invalid))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (authRepository.findByEmail(email) != null) {
                    _registerResult.value = RequestResult.Failure(appContext.getString(R.string.vm_register_email_already_exists))
                    _isLoading.value = false
                    return@launch
                }

                val wasSaved = authRepository.save(
                    Usuario(
                        email = email,
                        nombre = name,
                        password = password,
                        puntos = 0,
                        telefono = phone,
                        direccion = address,
                        departamento = "",
                        ubicacionExactaActiva = true,
                        ubicacion = Ubicacion(
                            latitud = selectedLatitude ?: 0.0,
                            longitud = selectedLongitude ?: 0.0,
                            ciudad = address
                        )
                    )
                )

                _registerResult.value = if (wasSaved) {
                    RequestResult.Success(appContext.getString(R.string.vm_register_success, name))
                } else {
                    RequestResult.Failure(appContext.getString(R.string.vm_register_save_failed))
                }
            } catch (e: Exception) {
                _registerResult.value = RequestResult.Failure(appContext.getString(R.string.vm_register_save_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResult() {
        _registerResult.value = null
    }
}
