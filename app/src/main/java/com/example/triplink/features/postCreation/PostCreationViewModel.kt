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
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.UUID
import javax.inject.Inject

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
        private set

    var prefilledFromPublicationId by mutableStateOf<String?>(null)
        private set

    private var prefilledPhotos by mutableStateOf<List<String>>(emptyList())

    val submitButtonLabel: String
        get() = "Publicar"

    val categories = listOf("Gastronomía", "Cultura", "Naturaleza", "Entretenimiento", "Historia")

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

    fun loadPublicationForEdit(publicationId: String?) {
        if (publicationId.isNullOrBlank()) {
            if (prefilledFromPublicationId != null) resetForm()
            return
        }
        if (prefilledFromPublicationId == publicationId) return

        val publication = userRepository.getPublicationById(publicationId) ?: return
        prefilledFromPublicationId = publication.id
        prefilledPhotos = publication.fotos

        placeName.onChange(publication.titulo)
        description = publication.informacion
        selectedCategory.onChange(publication.categoria.toUiLabel())

        selectedCity = publication.ubicacion.ciudad
        latitude = publication.ubicacion.latitud
        longitude = publication.ubicacion.longitud

        selectedPriceRange = publication.rangoPrecios.toUiLabel()

        val schedule = publication.horario
        daySchedules = if (schedule != null) {
            val open = schedule.first.toHHmm()
            val close = schedule.second.toHHmm()
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map {
                DayScheduleData(day = it, isEnabled = true, openTime = open, closeTime = close)
            }
        } else {
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").map {
                DayScheduleData(it)
            }
        }
        isOpenEveryDay = daySchedules.all { it.isEnabled }
    }

    private fun isTimeOrderValid(open: String, close: String): Boolean {
        return try {
            val openParts = open.split(":").map { it.toInt() }
            val closeParts = close.split(":").map { it.toInt() }
            val openMinutes = openParts[0] * 60 + openParts[1]
            val closeMinutes = closeParts[0] * 60 + closeParts[1]
            openMinutes < closeMinutes
        } catch (_: Exception) {
            false
        }
    }

    fun onOpenEveryDayChange(checked: Boolean) {
        isOpenEveryDay = checked
        daySchedules = daySchedules.map {
            it.copy(
                isEnabled = checked,
                openTime = if (checked) it.openTime.ifBlank { "08:00" } else "",
                closeTime = if (checked) it.closeTime.ifBlank { "17:00" } else ""
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
                val userId = session?.userId
                if (userId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure("Sesión expirada")
                    return@launch
                }

                val category = selectedCategory.value.toDomainCategoryOrNull()
                if (category == null) {
                    _createResult.value = RequestResult.Failure("La categoría seleccionada no es válida")
                    return@launch
                }

                val publication = PuntoInteres(
                    id = UUID.randomUUID().toString(),
                    titulo = placeName.value,
                    informacion = description,
                    usuarioAutorId = userId,
                    categoria = category,
                    ubicacion = Ubicacion(latitud = latitude, longitud = longitude, ciudad = selectedCity),
                    fotos = prefilledPhotos,
                    horario = buildScheduleOrNull(),
                    estado = EstadoPublicacion.PENDIENTE,
                    rangoPrecios = selectedPriceRange.toDomainPriceRange(),
                    motivoRechazo = null
                )

                val wasSaved = userRepository.savePuntoInteres(publication)

                if (wasSaved) {
                    showSuccessModal = true
                    _createResult.value = RequestResult.Success(
                        if (prefilledFromPublicationId != null) {
                            "Nueva publicación enviada con las correcciones"
                        } else {
                            "Publicación creada exitosamente"
                        }
                    )
                } else {
                    _createResult.value = RequestResult.Failure("No fue posible crear la publicación")
                }
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure("Error al crear publicación: ${e.message}")
            }
        }
    }

    fun clearResult() {
        _createResult.value = null
    }

    fun dismissSuccessModal() {
        showSuccessModal = false
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
        prefilledFromPublicationId = null
        prefilledPhotos = emptyList()
    }

    private fun buildScheduleOrNull(): Pair<Long, Long>? {
        val firstEnabled = daySchedules.firstOrNull { it.isEnabled } ?: return null
        val open = firstEnabled.openTime.toMillisOfDayOrNull() ?: return null
        val close = firstEnabled.closeTime.toMillisOfDayOrNull() ?: return null
        return open to close
    }

    private fun String.toDomainCategoryOrNull(): Categoria? {
        val normalized = normalizeForEnum(this)
        return Categoria.entries.firstOrNull { normalizeForEnum(it.name) == normalized }
    }

    private fun String.toDomainPriceRange(): RangoPrecios = when (normalizeForEnum(this)) {
        "GRATUITO" -> RangoPrecios.GRATUITO
        "ECONOMICO" -> RangoPrecios.ECONOMICO
        "MODERADO" -> RangoPrecios.MODERADO
        "COSTOSO" -> RangoPrecios.COSTOSO
        else -> RangoPrecios.GRATUITO
    }

    private fun Categoria.toUiLabel(): String = when (this) {
        Categoria.GASTRONOMIA -> "Gastronomía"
        Categoria.CULTURA -> "Cultura"
        Categoria.NATURALEZA -> "Naturaleza"
        Categoria.ENTRETENIMIENTO -> "Entretenimiento"
        Categoria.HISTORIA -> "Historia"
    }

    private fun RangoPrecios?.toUiLabel(): String = when (this) {
        null -> "Gratuito"
        RangoPrecios.GRATUITO -> "Gratuito"
        RangoPrecios.ECONOMICO -> "Economico"
        RangoPrecios.MODERADO -> "Moderado"
        RangoPrecios.COSTOSO -> "Costoso"
    }

    private fun Long.toHHmm(): String {
        val totalMinutes = this / 60_000L
        val h = (totalMinutes / 60L).toInt()
        val m = (totalMinutes % 60L).toInt()
        return "%02d:%02d".format(h, m)
    }

    private fun String.toMillisOfDayOrNull(): Long? {
        return try {
            val parts = split(":")
            if (parts.size != 2) return null
            val h = parts[0].toLong()
            val m = parts[1].toLong()
            if (h !in 0..23 || m !in 0..59) return null
            (h * 60L + m) * 60_000L
        } catch (_: Exception) {
            null
        }
    }

    private fun normalizeForEnum(value: String): String {
        val normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
        return normalized.uppercase()
    }
}
