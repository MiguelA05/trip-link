package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<Usuario>>
    val publications: StateFlow<List<PuntoInteres>>

    fun save(user: Usuario): Boolean
    fun findByEmail(email: String): Usuario?
    fun login(email: String, password: String): Usuario?

    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
}

