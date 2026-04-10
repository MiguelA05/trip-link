package com.example.triplink.features.user.accountEdit

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.data.seed.GeoSeedData
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.repository.user.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountEditViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val userProfileRepository: UserProfileRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    // Información Personal
    var fullName by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_full_name))
    var phone by mutableStateOf("")

    var fullNameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

    // Ubicación de Residencia
    val departments = GeoSeedData.departments
    val citiesMap = GeoSeedData.citiesByDepartment

    var selectedDepartment by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_department))
    var selectedCity by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_city))
    var address by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_address))

    var departmentError by mutableStateOf<String?>(null)
    var cityError by mutableStateOf<String?>(null)
    var addressError by mutableStateOf<String?>(null)

    var addExactLocation by mutableStateOf(false)

    // Datos de Acceso
    var email by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_email))
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

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.first()
                session?.userId?.let { userId ->
                    val user = userProfileRepository.getUserById(userId)
                    user?.let {
                        fullName = it.nombre
                        email = it.email
                        it.ubicacion?.let { ubicacion ->
                            selectedCity = ubicacion.ciudad
                        }
                    }
                }
            } catch (e: Exception) {
                // Graceful error handling - keep defaults
            }
        }
    }

    fun validateFullName(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_account_edit_name_required) else null
    }

    fun validatePhone(value: String): String? {
        return when {
            value.isBlank() -> appContext.getString(R.string.vm_account_edit_phone_required)
            !value.matches(Regex("^[0-9]{7,15}$")) -> appContext.getString(R.string.vm_account_edit_phone_invalid)
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> appContext.getString(R.string.vm_account_edit_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> appContext.getString(R.string.vm_account_edit_email_invalid)
            else -> null
        }
    }

    fun validateAddress(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_account_edit_address_required) else null
    }

    fun validateDepartment(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_account_edit_department_required) else null
    }

    fun validateCity(value: String): String? {
        return if (value.isBlank()) appContext.getString(R.string.vm_account_edit_city_required) else null
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
        if (!isFormValid) {
            _updateResult.value = RequestResult.Failure(appContext.getString(R.string.vm_account_edit_required_fields))
            return
        }

        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.first()
                session?.userId?.let { userId ->
                    val user = userProfileRepository.getUserById(userId)
                    user?.let {
                        val updatedUser = it.copy(
                            nombre = fullName,
                            ubicacion = Ubicacion(
                                latitud = 0.0,
                                longitud = 0.0,
                                ciudad = selectedCity
                            )
                        )
                        val wasUpdated = userProfileRepository.updateUser(updatedUser)
                        _updateResult.value = if (wasUpdated) {
                            RequestResult.Success(appContext.getString(R.string.vm_account_edit_save_success))
                        } else {
                            RequestResult.Failure(appContext.getString(R.string.vm_account_edit_save_failed))
                        }
                    } ?: run {
                        _updateResult.value = RequestResult.Failure(appContext.getString(R.string.vm_account_edit_user_not_found))
                    }
                } ?: run {
                    _updateResult.value = RequestResult.Failure(appContext.getString(R.string.vm_account_edit_session_expired))
                }
            } catch (e: Exception) {
                _updateResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_account_edit_save_error, e.message ?: "")
                )
            }
        }
    }

    fun changePassword() {
        // This should navigate to password change screen
        _updateResult.value = RequestResult.Success(appContext.getString(R.string.vm_account_edit_open_change_password))
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.first()
                session?.userId?.let { userId ->
                    val wasDeleted = userProfileRepository.deleteUser(userId)
                    _deleteResult.value = if (wasDeleted) {
                        sessionDataStore.clearSession()
                        RequestResult.Success(appContext.getString(R.string.vm_account_edit_delete_success))
                    } else {
                        RequestResult.Failure(appContext.getString(R.string.vm_account_edit_user_not_found))
                    }
                } ?: run {
                    _deleteResult.value = RequestResult.Failure(appContext.getString(R.string.vm_account_edit_session_expired))
                }
            } catch (e: Exception) {
                _deleteResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_account_edit_delete_error, e.message ?: "")
                )
            }
        }
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }

    fun getUserInitials(): String {
        val names = fullName.split(" ")
        return (names.getOrNull(0)?.firstOrNull()?.uppercaseChar()?.toString()
            ?: appContext.getString(R.string.vm_account_edit_default_initial_one)) +
                (names.getOrNull(1)?.firstOrNull()?.uppercaseChar()?.toString()
                    ?: appContext.getString(R.string.vm_account_edit_default_initial_two))
    }
}