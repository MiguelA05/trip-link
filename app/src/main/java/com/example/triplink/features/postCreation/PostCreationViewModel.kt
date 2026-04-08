package com.example.triplink.features.postCreation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class PostCreationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    var placeName = ValidatedField("") { value ->
        if (value.isBlank()) "El nombre del lugar es obligatorio" else null
    }

    var description by mutableStateOf("")

    var selectedCategory = ValidatedField("") { value ->
        if (value.isBlank()) "La categoría es obligatoria" else null
    }

    var isOpenEveryDay by mutableStateOf(false)

    // Ubicación del punto de interés
    var selectedCity by mutableStateOf("")
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)

    var daySchedules by mutableStateOf(
        listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map {
            DayScheduleData(it)
        }
    )

    var selectedPriceRange by mutableStateOf("Gratuito")
    var showSuccessModal by mutableStateOf(false)

    val categories = listOf("Gastronomía", "Cultura", "Naturaleza", "Entretenimiento", "Historia")

    // Result flow for feedback
    private val _createResult = MutableStateFlow<RequestResult?>(null)
    val createResult: StateFlow<RequestResult?> = _createResult.asStateFlow()

    val isFormValid: Boolean
        get() {
            val areMandatoryFieldsValid = placeName.isValid && 
                                        placeName.value.isNotBlank() && 
                                        selectedCategory.isValid && 
                                        selectedCategory.value.isNotBlank()
            
            val areSchedulesValid = daySchedules.all { schedule ->
                if (schedule.isEnabled) {
                    schedule.openTime.isNotBlank() && 
                    schedule.closeTime.isNotBlank() && 
                    isTimeOrderValid(schedule.openTime, schedule.closeTime)
                } else {
                    true
                }
            }
            
            return areMandatoryFieldsValid && areSchedulesValid
        }

    private fun isTimeOrderValid(open: String, close: String): Boolean {
        return try {
            val openParts = open.split(":").map { it.toInt() }
            val closeParts = close.split(":").map { it.toInt() }
            val openMinutes = openParts[0] * 60 + openParts[1]
            val closeMinutes = closeParts[0] * 60 + closeParts[1]
            openMinutes < closeMinutes
        } catch (e: Exception) {
            false
        }
    }

    fun onOpenEveryDayChange(checked: Boolean) {
        isOpenEveryDay = checked
        daySchedules = daySchedules.map {
            it.copy(
                isEnabled = checked,
                openTime = if (checked) it.openTime else "",
                closeTime = if (checked) it.closeTime else ""
            )
        }
    }

    fun onDayToggle(index: Int, checked: Boolean) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(
                isEnabled = checked,
                openTime = if (checked) this[index].openTime else "",
                closeTime = if (checked) this[index].closeTime else ""
            )
        }
        
        if (!checked) {
            isOpenEveryDay = false
        } else if (daySchedules.all { it.isEnabled }) {
            isOpenEveryDay = true
        }
    }

    fun onOpenTimeChange(index: Int, time: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(openTime = time)
        }
    }

    fun onCloseTimeChange(index: Int, time: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(closeTime = time)
        }
    }

    fun onCityChange(newCity: String) {
        selectedCity = newCity
    }

    fun onLocationChange(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun createPost() {
        if (!isFormValid) {
            _createResult.value = RequestResult.Failure("Por favor, completa todos los campos requeridos")
            return
        }

        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.first()
                session?.userId?.let { userId ->
                    val publication = PuntoInteres(
                        id = UUID.randomUUID().toString(),
                        titulo = placeName.value,
                        informacion = description,
                        usuarioAutorId = userId,
                        categoria = Categoria.valueOf(selectedCategory.value.uppercase()),
                        ubicacion = Ubicacion(latitud = latitude, longitud = longitude, ciudad = selectedCity),
                        fotos = emptyList(),
                        horario = null,
                        estado = EstadoPublicacion.PENDIENTE
                    )

                    val wasSaved = userRepository.savePuntoInteres(publication)
                    _createResult.value = if (wasSaved) {
                        showSuccessModal = true
                        RequestResult.Success("Publicación creada exitosamente")
                    } else {
                        RequestResult.Failure("No fue posible crear la publicación")
                    }
                } ?: run {
                    _createResult.value = RequestResult.Failure("Sesión expirada")
                }
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure("Error al crear publicación: ${e.message}")
            }
        }
    }

    fun clearResult() {
        _createResult.value = null
    }

    fun resetForm() {
        placeName.reset()
        description = ""
        selectedCategory.reset()
        isOpenEveryDay = false
        selectedCity = ""
        latitude = 0.0
        longitude = 0.0
        daySchedules = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map {
            DayScheduleData(it)
        }
        selectedPriceRange = "Gratuito"
        showSuccessModal = false
    }
}
