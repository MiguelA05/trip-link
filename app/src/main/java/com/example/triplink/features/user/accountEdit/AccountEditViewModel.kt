package com.example.triplink.features.user.accountEdit

import android.content.Context
import android.net.Uri
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

    private data class EditableAccountSnapshot(
        val fullName: String,
        val phone: String,
        val address: String,
        val latitude: Double?,
        val longitude: Double?
    )

    // Información Personal
    var fullName by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_full_name))
    var phone by mutableStateOf("")

    var fullNameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

    // Ubicación de Residencia
    var address by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_address))

    var addressError by mutableStateOf<String?>(null)

    var selectedLatitude by mutableStateOf<Double?>(null)
    var selectedLongitude by mutableStateOf<Double?>(null)

    // Datos de Acceso
    var email by mutableStateOf(appContext.getString(R.string.vm_account_edit_default_email))
    var emailError by mutableStateOf<String?>(null)
    private var originalSnapshot by mutableStateOf<EditableAccountSnapshot?>(null)

    // Result flow for feedback
    private val _updateResult = MutableStateFlow<RequestResult?>(null)
    val updateResult: StateFlow<RequestResult?> = _updateResult.asStateFlow()

    private val _deleteResult = MutableStateFlow<RequestResult?>(null)
    val deleteResult: StateFlow<RequestResult?> = _deleteResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    // Foto de Perfil
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    val isFormValid by derivedStateOf {
        validateFullName(fullName) == null &&
                validatePhone(phone) == null &&
                validateAddress(address) == null &&
                selectedLatitude != null &&
                selectedLongitude != null
    }

    val hasEditableChanges by derivedStateOf {
        val baseline = originalSnapshot ?: return@derivedStateOf false
        currentSnapshot() != baseline
    }

    val canSaveChanges by derivedStateOf {
        isFormValid && hasEditableChanges
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
                        phone = it.telefono
                        address = it.direccion

                        selectedLatitude = it.ubicacion?.latitud
                        selectedLongitude = it.ubicacion?.longitud
                        originalSnapshot = currentSnapshot()
                    }
                }
            } catch (e: Exception) {
                // Graceful error handling - keep defaults
            }
        }
    }

    private fun currentSnapshot(): EditableAccountSnapshot = EditableAccountSnapshot(
        fullName = fullName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        latitude = selectedLatitude,
        longitude = selectedLongitude
    )

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

    fun saveChanges() {
        if (!canSaveChanges) {
            _updateResult.value = RequestResult.Failure(appContext.getString(R.string.vm_account_edit_required_fields))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = sessionDataStore.sessionFlow.first()
                session?.userId?.let { userId ->
                    val user = userProfileRepository.getUserById(userId)
                    user?.let {
                        val current = currentSnapshot()
                        val updatedUser = it.copy(
                            nombre = current.fullName,
                            telefono = current.phone,
                            direccion = current.address,
                            ubicacionExactaActiva = true,
                            ubicacion = Ubicacion(
                                latitud = current.latitude ?: 0.0,
                                longitud = current.longitude ?: 0.0,
                                ciudad = current.address
                            )
                        )
                        val wasUpdated = userProfileRepository.updateUser(updatedUser)
                        _updateResult.value = if (wasUpdated) {
                            originalSnapshot = current
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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword() {
        // This should navigate to password change screen
        _updateResult.value = RequestResult.Success(appContext.getString(R.string.vm_account_edit_open_change_password))
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isDeleting.value = true
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
            } finally {
                _isDeleting.value = false
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

    fun onPhotoUriChange(newUri: Uri?) {
        _photoUri.value = newUri
    }

    fun clearPhotoUri() {
        _photoUri.value = null
    }

    fun onLocationSelected(longitude: Double, latitude: Double) {
        selectedLongitude = longitude
        selectedLatitude = latitude
    }
}