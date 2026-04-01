package com.example.triplink.features.admin.moderation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ModerationViewModel : ViewModel() {

    private val pendingPublications = mutableStateListOf(
        ModerationPublicationUi(
            id = "m-1",
            title = "Mercado Artesanal del Quindio",
            categoryLabel = "Gastronomia",
            authorName = "Laura Fernandez",
            timeLabel = "Hace 3 dias",
            cityLabel = "Armenia, Quindio",
            priceLabel = "$$$",
            scheduleLabel = "7:00 - 2:00 pm (Lun - Vie)",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?q=80&w=1200&auto=format&fit=crop",
            status = PublicationModerationStatus.PENDING
        ),
        ModerationPublicationUi(
            id = "m-2",
            title = "Plaza Principal Filandia",
            categoryLabel = "Cultura",
            authorName = "Juan Pablo Torres",
            timeLabel = "Hace 5 horas",
            cityLabel = "Filandia, Quindio",
            priceLabel = "$",
            scheduleLabel = "Todo el dia",
            imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop",
            status = PublicationModerationStatus.PENDING
        )
    )

    private val reviewedPublications = mutableStateListOf(
        ModerationPublicationUi(
            id = "m-3",
            title = "Mirador Alto de la Cruz",
            categoryLabel = "Naturaleza",
            authorName = "Valentina Rios",
            timeLabel = "Hace 1 dia",
            cityLabel = "Salento, Quindio",
            priceLabel = "$$",
            scheduleLabel = "8:00 am - 5:00 pm",
            imageUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop",
            status = PublicationModerationStatus.REJECTED,
            reasonMessage = "Informacion incompleta y sin ubicacion en mapa",
            rejectReason = "La publicacion no incluye coordenadas ni referencias verificables del lugar."
        )
    )

    var selectedFilter by mutableStateOf(ModerationFilter.ALL)
        private set

    val pendingCount: Int
        get() = pendingPublications.size

    val verifiedCount: Int
        get() = reviewedPublications.count { it.status == PublicationModerationStatus.VERIFIED }

    val rejectedCount: Int
        get() = reviewedPublications.count { it.status == PublicationModerationStatus.REJECTED }

    val filteredPublications: List<ModerationPublicationUi>
        get() = when (selectedFilter) {
            ModerationFilter.ALL -> buildList {
                // Prioriza pendientes y conserva histórico al final.
                addAll(pendingPublications)
                addAll(reviewedPublications)
            }
            ModerationFilter.PENDING -> pendingPublications
            ModerationFilter.VERIFIED -> reviewedPublications.filter { it.status == PublicationModerationStatus.VERIFIED }
            ModerationFilter.REJECTED -> reviewedPublications.filter { it.status == PublicationModerationStatus.REJECTED }
        }

    fun onFilterSelected(filter: ModerationFilter) {
        selectedFilter = filter
    }

    fun applyDecision(
        publicationId: String,
        decision: ModerationDecision,
        reason: String? = null
    ) {
        val publication = pendingPublications.firstOrNull { it.id == publicationId } ?: return
        pendingPublications.remove(publication)

        reviewedPublications.add(
            reviewedPublications.size,
            publication.copy(
                status = if (decision == ModerationDecision.APPROVE) {
                    PublicationModerationStatus.VERIFIED
                } else {
                    PublicationModerationStatus.REJECTED
                },
                reasonMessage = if (decision == ModerationDecision.REJECT) reason else publication.reasonMessage,
                rejectReason = if (decision == ModerationDecision.REJECT) reason else null
            )
        )
    }
}

