package com.example.triplink.features.postCreation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.services.ImagenCompressionService
import com.example.triplink.core.storage.ImagenLocalStorage
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.ValidatedField
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.data.repository.remote.images.CloudinaryImageRepository
import com.example.triplink.domain.model.HorarioPuntoInteres
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.repository.user.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PostCreationViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val publicationRepository: PublicationRepository,
    private val sessionDataStore: SessionDataStore,
    private val imagenLocalStorage: ImagenLocalStorage,
    private val compressionService: ImagenCompressionService,
    private val cloudinaryRepository: CloudinaryImageRepository
) : ViewModel() {

    var placeName = ValidatedField("") { value ->
        if (value.isBlank()) appContext.getString(R.string.vm_post_creation_place_name_required) else null
    }

    var description by mutableStateOf("")

    var selectedCategory = ValidatedField<Categoria?>(null) { value ->
        if (value == null) appContext.getString(R.string.vm_post_creation_category_required) else null
    }

    var isOpenEveryDay by mutableStateOf(false)

    var selectedCity by mutableStateOf("")
    var latitude by mutableStateOf<Double?>(null)
    var longitude by mutableStateOf<Double?>(null)

    val hasSelectedLocation: Boolean
        get() = latitude != null && longitude != null

    val selectedLocationLabel: String
        get() = if (hasSelectedLocation) {
            appContext.getString(
                R.string.vm_post_creation_location_selected_format,
                latitude ?: 0.0,
                longitude ?: 0.0
            )
        } else {
            appContext.getString(R.string.vm_post_creation_location_not_selected)
        }

    var daySchedules by mutableStateOf(
        DiaSemana.entries.map {
            DayScheduleData(it)
        }
    )

    var selectedPriceRange by mutableStateOf(RangoPrecios.GRATUITO)
    var showSuccessModal by mutableStateOf(false)
        private set

    var prefilledFromPublicationId by mutableStateOf<String?>(null)
        private set

    private var prefilledPhotos by mutableStateOf<List<String>>(emptyList())

    // Estado de imágenes
    var imagenesTemporales by mutableStateOf<List<Uri>>(emptyList())
        private set

    var imagenesRemotasUrls by mutableStateOf<List<String>>(emptyList())
        private set

    var indiceBotonAgregar by mutableStateOf(0)
        private set

    private val _imagenSubidaResult = MutableStateFlow<RequestResult?>(null)
    val imagenSubidaResult: StateFlow<RequestResult?> = _imagenSubidaResult.asStateFlow()

    val submitButtonLabel: String
        get() = appContext.getString(R.string.vm_post_creation_submit_action)

    val categories = Categoria.entries

    private val _createResult = MutableStateFlow<RequestResult?>(null)
    val createResult: StateFlow<RequestResult?> = _createResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFormValid: Boolean
        get() {
            val areMandatoryFieldsValid = placeName.isValid &&
                placeName.value.isNotBlank() &&
                selectedCategory.isValid &&
                selectedCategory.value != null

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
        viewModelScope.launch {
            if (publicationId.isNullOrBlank()) {
                if (prefilledFromPublicationId != null) resetForm()
                return@launch
            }
            if (prefilledFromPublicationId == publicationId) return@launch

            val publication = publicationRepository.getPublicationById(publicationId) ?: return@launch
            prefilledFromPublicationId = publication.id
            prefilledPhotos = publication.fotos

            placeName.onChange(publication.titulo)
            description = publication.informacion
            selectedCategory.onChange(publication.categoria)

            selectedCity = publication.ubicacion.ciudad
            latitude = publication.ubicacion.latitud
            longitude = publication.ubicacion.longitud

            selectedPriceRange = publication.rangoPrecios ?: RangoPrecios.GRATUITO

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
    }

    private fun isTimeOrderValid(open: String, close: String): Boolean {
        return try {
            val openParts = open.split(":").map { it.toInt() }
            val closeParts = close.split(":").map { it.toInt() }
            if (openParts.size != 2 || closeParts.size != 2) return false
            
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

    fun onOpenTimeChange(index: Int, hours: String, minutes: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(openTime = "$hours:$minutes")
        }
    }

    fun onCloseTimeChange(index: Int, hours: String, minutes: String) {
        daySchedules = daySchedules.toMutableList().apply {
            this[index] = this[index].copy(closeTime = "$hours:$minutes")
        }
    }

    fun onCityChange(newCity: String) {
        selectedCity = newCity
    }

    fun onLocationChange(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun createPost() {
        if (!isFormValid) {
            _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_required_fields))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = sessionDataStore.sessionFlow.first()
                val userId = session?.userId
                if (userId.isNullOrBlank()) {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_session_expired))
                    _isLoading.value = false
                    return@launch
                }

                val category = selectedCategory.value
                if (category == null) {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_invalid_category))
                    _isLoading.value = false
                    return@launch
                }

                // Subir imágenes a Cloudinary si las hay
                var urlsImagenes = prefilledPhotos

                if (imagenesTemporales.isNotEmpty()) {
                    _imagenSubidaResult.value = RequestResult.Loading

                    val archivos = mutableListOf<File>()
                    for (uri in imagenesTemporales) {
                        val path = uri.path
                        if (!path.isNullOrEmpty()) {
                            val archivo = File(path)
                            if (archivo.exists()) {
                                archivos.add(archivo)
                            }
                        }
                    }

                    if (archivos.isNotEmpty()) {
                        val prefijo = "pub_${UUID.randomUUID()}"
                        val resultadoSubida = cloudinaryRepository.subirMultiples(archivos, prefijo)

                        if (resultadoSubida.isSuccess) {
                            urlsImagenes = resultadoSubida.getOrNull() ?: emptyList()
                            _imagenSubidaResult.value = RequestResult.Success("")
                        } else {
                            _createResult.value = RequestResult.Failure(
                                appContext.getString(R.string.error_uploading_images)
                            )
                            _isLoading.value = false
                            return@launch
                        }
                    }
                }

                val publication = PuntoInteres(
                    id = UUID.randomUUID().toString(),
                    titulo = placeName.value,
                    informacion = description,
                    usuarioAutorId = userId,
                    categoria = category,
                    ubicacion = Ubicacion(
                        latitud = latitude ?: 0.0,
                        longitud = longitude ?: 0.0,
                        ciudad = selectedCity.ifBlank {
                            appContext.getString(R.string.vm_post_creation_location_fallback_city)
                        }
                    ),
                    fotos = urlsImagenes,
                    horarios = buildSchedules(),
                    estado = EstadoPublicacion.PENDIENTE,
                    rangoPrecios = selectedPriceRange,
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
                    limpiarImagenes()
                } else {
                    _createResult.value = RequestResult.Failure(appContext.getString(R.string.vm_post_creation_create_failed))
                }
            } catch (e: Exception) {
                _createResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_post_creation_create_error, e.message ?: "")
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResult() {
        _createResult.value = null
    }

    fun dismissSuccessModal() {
        showSuccessModal = false
    }

    // Métodos para manejo de imágenes

    fun agregarImagen(uri: Uri) {
        if (imagenesTemporales.size >= 5) {
            _imagenSubidaResult.value = RequestResult.Failure(
                appContext.getString(R.string.error_max_images)
            )
            return
        }

        viewModelScope.launch {
            try {
                _imagenSubidaResult.value = RequestResult.Loading

                // Comprimir localmente
                val archivoComprimido = compressionService.comprimirImagen(uri)
                    ?: run {
                        _imagenSubidaResult.value = RequestResult.Failure(
                            appContext.getString(R.string.error_image_compression)
                        )
                        return@launch
                    }

                // Guardar en almacenamiento local
                val nombreLocal = "imagen_temporal_${System.currentTimeMillis()}.jpg"
                val archivoGuardado = imagenLocalStorage.guardarImagen(uri, nombreLocal)
                    ?: run {
                        _imagenSubidaResult.value = RequestResult.Failure(
                            appContext.getString(R.string.error_saving_image)
                        )
                        return@launch
                    }

                // Actualizar lista temporal (para UI)
                imagenesTemporales = imagenesTemporales + Uri.fromFile(archivoGuardado)
                indiceBotonAgregar = imagenesTemporales.size
                _imagenSubidaResult.value = RequestResult.Success("")

            } catch (e: Exception) {
                _imagenSubidaResult.value = RequestResult.Failure(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarImagen(indice: Int) {
        if (indice < 0 || indice >= imagenesTemporales.size) return

        viewModelScope.launch {
            try {
                imagenesTemporales = imagenesTemporales.toMutableList().apply {
                    removeAt(indice)
                }.toList()

                // También eliminar de URLs remotas si ya fue subida
                if (indice < imagenesRemotasUrls.size) {
                    imagenesRemotasUrls = imagenesRemotasUrls.toMutableList().apply {
                        removeAt(indice)
                    }.toList()
                }

                indiceBotonAgregar = imagenesTemporales.size
            } catch (e: Exception) {
                _imagenSubidaResult.value = RequestResult.Failure(e.message ?: "Error al eliminar imagen")
            }
        }
    }

    fun limpiarImagenes() {
        viewModelScope.launch {
            try {
                imagenLocalStorage.limpiarDir()
                imagenesTemporales = emptyList()
                imagenesRemotasUrls = emptyList()
                indiceBotonAgregar = 0
            } catch (e: Exception) {
                _imagenSubidaResult.value = RequestResult.Failure(e.message ?: "Error al limpiar imágenes")
            }
        }
    }

    fun clearImagenSubidaResult() {
        _imagenSubidaResult.value = null
    }

    fun resetForm() {
        placeName.reset()
        description = ""
        selectedCategory.reset()
        isOpenEveryDay = false
        selectedCity = ""
        latitude = null
        longitude = null
        daySchedules = DiaSemana.entries.map {
            DayScheduleData(it)
        }
        selectedPriceRange = RangoPrecios.GRATUITO
        showSuccessModal = false
        prefilledFromPublicationId = null
        prefilledPhotos = emptyList()
        imagenesTemporales = emptyList()
        imagenesRemotasUrls = emptyList()
        indiceBotonAgregar = 0
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

}
