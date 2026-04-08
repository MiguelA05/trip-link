package com.example.triplink.features.user.accountEdit

import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountEditViewModel @Inject constructor() : ViewModel() {

    // Información Personal
    var fullName by mutableStateOf("Carlos Andrés Ruiz")
    var phone by mutableStateOf("")

    var fullNameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

    // Ubicación de Residencia
    val departments = listOf("Quindío", "Antioquia", "Valle del Cauca")
    val citiesMap = mapOf(
        "Quindío" to listOf("Armenia", "Calarca", "Circasia", "Filandia", "Salento"),
        "Antioquia" to listOf("Medellín", "Envigado", "Itagüí", "Rionegro"),
        "Valle del Cauca" to listOf("Cali", "Palmira", "Buga", "Tulua")
    )

    var selectedDepartment by mutableStateOf("Quindío")
    var selectedCity by mutableStateOf("Armenia")
    var address by mutableStateOf("Ej. Barrio La Candelaria, Bogotá")

    var departmentError by mutableStateOf<String?>(null)
    var cityError by mutableStateOf<String?>(null)
    var addressError by mutableStateOf<String?>(null)

    var addExactLocation by mutableStateOf(false)

    // Datos de Acceso
    var email by mutableStateOf("carlos.ruiz@universidad.edu.co")
    var emailError by mutableStateOf<String?>(null)

    // Result flow for feedback
    private val _updateResult = MutableStateFlow<RequestResult?>(null)
    val updateResult: StateFlow<RequestResult?> = _updateResult.asStateFlow()

    private val _deleteResult = MutableStateFlow<RequestResult?>(null)
    val deleteResult: StateFlow<RequestResult?> = _deleteResult.asStateFlow()

    val isFormValid by derivedStateOf {
        validateFullName(fullName) == null &&
                validateEmail(email) == null &&
                validatePhone(phone) == null &&
                validateDepartment(selectedDepartment) == null &&
                validateCity(selectedCity) == null &&
                (address.isNotEmpty() || validateAddress(address) == null)
    }

    fun validateFullName(value: String): String? {
        return if (value.isBlank()) "El nombre es obligatorio" else null
    }

    fun validatePhone(value: String): String? {
        return when {
            value.isBlank() -> "El teléfono es obligatorio"
            !value.matches(Regex("^[0-9]{7,15}$")) -> "Teléfono inválido"
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Correo inválido"
            else -> null
        }
    }

    fun validateAddress(value: String): String? {
        return if (value.isBlank()) "La dirección es obligatoria" else null
    }

    fun validateDepartment(value: String): String? {
        return if (value.isBlank()) "Seleccione un departamento" else null
    }

    fun validateCity(value: String): String? {
        return if (value.isBlank()) "Seleccione un municipio" else null
    }

    fun onFullNameChange(newValue: String) {
        fullName = newValue
        fullNameError = validateFullName(newValue)
    }

    fun onPhoneChange(newValue: String) {
        phone = newValue
        phoneError = validatePhone(newValue)
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        emailError = validateEmail(newValue)
    }

    fun onAddressChange(newValue: String) {
        address = newValue
        addressError = validateAddress(newValue)
    }

    fun onDepartmentChange(newDepartment: String) {
        selectedDepartment = newDepartment
        departmentError = validateDepartment(newDepartment)
        selectedCity = ""
        cityError = null
    }

    fun onCityChange(newCity: String) {
        selectedCity = newCity
        cityError = validateCity(newCity)
    }

    fun saveChanges() {
        // Mock implementation - in a real app, this would call an API
        _updateResult.value = RequestResult.Success("Cambios guardados exitosamente")
    }

    fun changePassword() {
        // Mock implementation - should navigate to password change screen
        _updateResult.value = RequestResult.Success("Abriendo cambio de contraseña")
    }

    fun deleteAccount() {
        // Mock implementation - in a real app, this would show a confirmation dialog
        _deleteResult.value = RequestResult.Success("Cuenta eliminada exitosamente")
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }

    fun getUserInitials(): String {
        val names = fullName.split(" ")
        return (names.getOrNull(0)?.firstOrNull()?.uppercaseChar()?.toString() ?: "C") +
                (names.getOrNull(1)?.firstOrNull()?.uppercaseChar()?.toString() ?: "A")
    }
}