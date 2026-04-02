package com.example.triplink.data.repository.user.publications

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.publications.UserPublicationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPublicationsRepositoryImpl @Inject constructor() : UserPublicationsRepository {
    private val seedState = createUserPublicationsSeedState()

    override val publications: List<PuntoInteres>
        get() = seedState.publications

    override fun homePublications(): List<PuntoInteres> = seedState.publications.take(2)

    override fun explorePublications(): List<PuntoInteres> = seedState.publications

    override fun getPublicationById(publicationId: String): PuntoInteres? {
        return seedState.publications.firstOrNull { it.id == publicationId }
    }
}
