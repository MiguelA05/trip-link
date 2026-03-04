package com.example.triplink.features.register

import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var nameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var departmentError by mutableStateOf<String?>(null)
    var cityError by mutableStateOf<String?>(null)

    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

    // Data for Departments and Cities
    val departments = listOf("Quindío", "Antioquia", "Valle del Cauca")
    val citiesMap = mapOf(
        "Quindío" to listOf("Armenia", "Calarca", "Circasia", "Filandia", "Salento"),
        "Antioquia" to listOf("Medellín", "Envigado", "Itagüí", "Rionegro"),
        "Valle del Cauca" to listOf("Cali", "Palmira", "Buga", "Tulua")
    )

    var selectedDepartment by mutableStateOf("")
    var selectedCity by mutableStateOf("")

    var addExactLocation by mutableStateOf(false)

    val isFormValid by derivedStateOf {
        validateName(name) == null &&
                validateEmail(email) == null &&
                validatePassword(password) == null &&
                validateDepartment(selectedDepartment) == null &&
                validateCity(selectedCity) == null
    }

    fun validateName(value: String): String? {
        return if (value.isBlank()) "El nombre es obligatorio" else null
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Correo inválido"
            else -> null
        }
    }

    fun validatePassword(value: String): String? {
        return when {
            value.isBlank() -> "La contraseña es obligatoria"
            value.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
    }

    fun validateDepartment(value: String): String? {
        return if (value.isBlank()) "Seleccione un departamento" else null
    }

    fun validateCity(value: String): String? {
        return if (value.isBlank()) "Seleccione un municipio" else null
    }

    fun onNameChange(newValue: String) {
        name = newValue
        nameError = validateName(newValue)
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        emailError = validateEmail(newValue)
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        passwordError = validatePassword(newValue)
    }

    fun onDepartmentChange(newDepartment: String) {
        selectedDepartment = newDepartment
        departmentError = validateDepartment(newDepartment)
        // Reset city when department changes
        selectedCity = ""
        cityError = null 
    }

    fun onCityChange(newCity: String) {
        selectedCity = newCity
        cityError = validateCity(newCity)
    }

    fun validateAll(): Boolean {
        nameError = validateName(name)
        emailError = validateEmail(email)
        passwordError = validatePassword(password)
        departmentError = validateDepartment(selectedDepartment)
        cityError = validateCity(selectedCity)

        return isFormValid
    }

    fun register() {
        if (validateAll()) {
            _registerResult.value = RequestResult.Success("Registro exitoso para $name")
        } else {
            _registerResult.value = RequestResult.Failure("Por favor, corrige los errores en el formulario")
        }
    }

    fun clearResult() {
        _registerResult.value = null
    }
}
