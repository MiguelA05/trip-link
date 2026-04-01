package com.example.triplink.features.admin.moderation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication

class ModerationViewModel : ViewModel() {

    private val pendingPublications = mutableStateListOf(
        ModerationPublication(
            id = "m-1",
            pointOfInterest = PuntoInteres(
                id = "poi-m-1",
                titulo = "Mercado Artesanal del Quindio",
                informacion = "Oferta gastronomica y artesanal en el centro del departamento.",
                usuarioAutorId = "u-1",
                categoria = Categoria.GASTRONOMIA,
                ubicacion = Ubicacion(latitud = 4.533, longitud = -75.681, ciudad = "Armenia, Quindio"),
                fotos = listOf("https://images.unsplash.com/photo-1601050690597-df0568f70950?q=80&w=1200&auto=format&fit=crop"),
                horario = 7L * 60L * 60L * 1000L to 14L * 60L * 60L * 1000L,
                estado = EstadoPublicacion.PENDIENTE,
                rangoPrecios = RangoPrecios.COSTOSO
            ),
            authorName = "Laura Fernandez",
            createdAtMillis = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L
        ),
        ModerationPublication(
            id = "m-2",
            pointOfInterest = PuntoInteres(
                id = "poi-m-2",
                titulo = "Plaza Principal Filandia",
                informacion = "Recorrido patrimonial y comercial de la zona central.",
                usuarioAutorId = "u-2",
                categoria = Categoria.CULTURA,
                ubicacion = Ubicacion(latitud = 4.668, longitud = -75.660, ciudad = "Filandia, Quindio"),
                fotos = listOf("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop"),
                horario = null,
                estado = EstadoPublicacion.PENDIENTE,
                rangoPrecios = RangoPrecios.ECONOMICO
            ),
            authorName = "Juan Pablo Torres",
            createdAtMillis = System.currentTimeMillis() - 5L * 60L * 60L * 1000L
        )
    )

    private val reviewedPublications = mutableStateListOf(
        ModerationPublication(
            id = "m-3",
            pointOfInterest = PuntoInteres(
                id = "poi-m-3",
                titulo = "Mirador Alto de la Cruz",
                informacion = "Mirador panoramico con vista al valle del Quindio.",
                usuarioAutorId = "u-3",
                categoria = Categoria.NATURALEZA,
                ubicacion = Ubicacion(latitud = 4.636, longitud = -75.571, ciudad = "Salento, Quindio"),
                fotos = listOf("https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop"),
                horario = 8L * 60L * 60L * 1000L to 17L * 60L * 60L * 1000L,
                estado = EstadoPublicacion.RECHAZADA,
                rangoPrecios = RangoPrecios.MODERADO
            ),
            authorName = "Valentina Rios",
            createdAtMillis = System.currentTimeMillis() - 24L * 60L * 60L * 1000L,
            moderationReason = "Informacion incompleta y sin ubicacion en mapa",
            rejectReason = "La publicacion no incluye coordenadas ni referencias verificables del lugar."
        )
    )

    var selectedFilter by mutableStateOf(ModerationFilter.ALL)
        private set

    val pendingCount: Int
        get() = pendingPublications.size

    val verifiedCount: Int
        get() = reviewedPublications.count { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }

    val rejectedCount: Int
        get() = reviewedPublications.count { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }

    val filteredPublications: List<ModerationPublication>
        get() = when (selectedFilter) {
            ModerationFilter.ALL -> buildList {
                // Prioriza pendientes y conserva histórico al final.
                addAll(pendingPublications)
                addAll(reviewedPublications)
            }
            ModerationFilter.PENDING -> pendingPublications
            ModerationFilter.VERIFIED -> reviewedPublications.filter { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }
            ModerationFilter.REJECTED -> reviewedPublications.filter { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }
        }

    fun onFilterSelected(filter: ModerationFilter) {
        selectedFilter = filter
    }

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        val publication = pendingPublications.firstOrNull { it.id == publicationId } ?: return
        pendingPublications.remove(publication)

        val updatedStatus = if (decision == DecisionModerador.APROBADA) {
            EstadoPublicacion.VERIFICADA
        } else {
            EstadoPublicacion.RECHAZADA
        }

        reviewedPublications.add(
            reviewedPublications.size,
            publication.copy(
                pointOfInterest = publication.pointOfInterest.copy(estado = updatedStatus),
                moderationReason = if (decision == DecisionModerador.RECHAZADA) reason else publication.moderationReason,
                rejectReason = if (decision == DecisionModerador.RECHAZADA) reason else null
            )
        )
    }
}

