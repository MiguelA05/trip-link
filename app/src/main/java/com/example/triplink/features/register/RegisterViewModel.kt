package com.example.triplink.features.register

import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

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

    val departments = listOf("Quindio", "Antioquia", "Valle del Cauca")
    val citiesMap = mapOf(
        "Quindio" to listOf("Armenia", "Calarca", "Circasia", "Filandia", "Salento"),
        "Antioquia" to listOf("Medellin", "Envigado", "Itagui", "Rionegro"),
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
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Correo invalido"
            else -> null
        }
    }

    fun validatePassword(value: String): String? {
        return when {
            value.isBlank() -> "La contrasena es obligatoria"
            value.length < 6 -> "Minimo 6 caracteres"
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
        if (!validateAll()) {
            _registerResult.value = RequestResult.Failure("Por favor, corrige los errores en el formulario")
            return
        }

        if (userRepository.findByEmail(email) != null) {
            _registerResult.value = RequestResult.Failure("El correo ya se encuentra registrado")
            return
        }

        val wasSaved = userRepository.save(
            Usuario(
                email = email,
                nombre = name,
                password = password,
                puntos = 0,
                ubicacion = Ubicacion(latitud = 0.0, longitud = 0.0, ciudad = selectedCity)
            )
        )

        _registerResult.value = if (wasSaved) {
            RequestResult.Success("Registro exitoso para $name")
        } else {
            RequestResult.Failure("No fue posible registrar el usuario")
        }
    }

    fun clearResult() {
        _registerResult.value = null
    }
}
