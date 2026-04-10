package com.example.triplink.features.postCreation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.HorarioPuntoInteres
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @param:ApplicationContext private val appContext: Context,
    private val publicationRepository: PublicationRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    var placeName = ValidatedField("") { value ->
        if (value.isBlank()) appContext.getString(R.string.vm_post_creation_place_name_required) else null
    }

    var description by mutableStateOf("")

    var selectedCategory = ValidatedField("") { value ->
        if (value.isBlank()) appContext.getString(R.string.vm_post_creation_category_required) else null
    }

    var isOpenEveryDay by mutableStateOf(false)

    var selectedCity by mutableStateOf("")
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)

    var daySchedules by mutableStateOf(
        DiaSemana.entries.map {
            DayScheduleData(it)
        }
    )

    var selectedPriceRange by mutableStateOf(appContext.getString(R.string.vm_post_creation_price_free))
    var showSuccessModal by mutableStateOf(false)
        private set

    var prefilledFromPublicationId by mutableStateOf<String?>(null)
        private set

    private var prefilledPhotos by mutableStateOf<List<String>>(emptyList())

    val submitButtonLabel: String
        get() = appContext.getString(R.string.vm_post_creation_submit_action)

    val categories = Categoria.entries.map { it.label }

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

        val publication = publicationRepository.getPublicationById(publicationId) ?: return
        prefilledFromPublicationId = publication.id
        prefilledPhotos = publication.fotos

        placeName.onChange(publication.titulo)
        description = publication.informacion
        selectedCategory.onChange(publication.categoria.label)

        selectedCity = publication.ubicacion.ciudad
        latitude = publication.ubicacion.latitud
        longitude = publication.ubicacion.longitud

        selectedPriceRange = publication.rangoPrecios.toUiLabel()

        val scheduleByDay = publication.horarios.associateBy { it.dia }
        daySchedules = DiaSemana.entries.map { day ->
            val schedule = scheduleByDay[day]
            DayScheduleData(
                day = day,
                isEnabled = schedule != null,
                openTime = schedule?.fechaInicio?.toHHmm().orEmpty(),
                closeTime = schedule?.fechaFin?.toHHmm().orEmpty()
            )
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
            _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_required_fields))
            return
        }

        viewModelScope.launch {
            try {
                val session = sessionDataStore.sessionFlow.first()
                val userId = session?.userId
                if (userId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_session_expired))
                    return@launch
                }

                val category = selectedCategory.value.toDomainCategoryOrNull()
                if (category == null) {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_invalid_category))
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
                    horarios = buildSchedules(),
                    estado = EstadoPublicacion.PENDIENTE,
                    rangoPrecios = selectedPriceRange.toDomainPriceRange(),
                    motivoRechazo = null
                )

                val wasSaved = publicationRepository.savePuntoInteres(publication)

                if (wasSaved) {
                    showSuccessModal = true
                    _createResult.value = RequestResult.Success(
                        if (prefilledFromPublicationId != null) {
                            appContext.getString(R.string.vm_post_creation_resubmitted_success)
                        } else {
                            appContext.getString(R.string.vm_post_creation_success)
                        }
                    )
                } else {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_create_failed))
                }
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_post_creation_create_error, e.message ?: "")
                )
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
        daySchedules = DiaSemana.entries.map {
            DayScheduleData(it)
        }
        selectedPriceRange = appContext.getString(R.string.vm_post_creation_price_free)
        showSuccessModal = false
        prefilledFromPublicationId = null
        prefilledPhotos = emptyList()
    }

    private fun buildSchedules(): List<HorarioPuntoInteres> {
        return daySchedules
            .filter { it.isEnabled }
            .mapNotNull { schedule ->
                val open = schedule.openTime.toMillisOfDayOrNull() ?: return@mapNotNull null
                val close = schedule.closeTime.toMillisOfDayOrNull() ?: return@mapNotNull null
                HorarioPuntoInteres(
                    dia = schedule.day,
                    fechaInicio = open,
                    fechaFin = close
                )
            }
    }

    private fun String.toDomainCategoryOrNull(): Categoria? {
        val normalized = normalizeForEnum(this)
        return Categoria.entries.firstOrNull { normalizeForEnum(it.label) == normalized }
    }

    private fun String.toDomainPriceRange(): RangoPrecios = when (normalizeForEnum(this)) {
        normalizeForEnum(appContext.getString(R.string.vm_post_creation_price_free)) -> RangoPrecios.GRATUITO
        normalizeForEnum(appContext.getString(R.string.vm_post_creation_price_economic)) -> RangoPrecios.ECONOMICO
        normalizeForEnum(appContext.getString(R.string.vm_post_creation_price_moderate)) -> RangoPrecios.MODERADO
        normalizeForEnum(appContext.getString(R.string.vm_post_creation_price_expensive)) -> RangoPrecios.COSTOSO
        else -> RangoPrecios.GRATUITO
    }


    private fun RangoPrecios?.toUiLabel(): String = when (this) {
        null -> appContext.getString(R.string.vm_post_creation_price_free)
        RangoPrecios.GRATUITO -> appContext.getString(R.string.vm_post_creation_price_free)
        RangoPrecios.ECONOMICO -> appContext.getString(R.string.vm_post_creation_price_economic)
        RangoPrecios.MODERADO -> appContext.getString(R.string.vm_post_creation_price_moderate)
        RangoPrecios.COSTOSO -> appContext.getString(R.string.vm_post_creation_price_expensive)
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
