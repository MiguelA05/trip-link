package com.example.triplink.data.repository.remote.user

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
    , private val publicationRepository: PublicationRepository
) : UserProfileRepository {

    override val users: StateFlow<List<Usuario>> = store.users

    override suspend fun getUserById(userId: String): Usuario? {
        return store.findUserById(userId)
    }

    override suspend fun findUserNameById(userId: String): String? {
        return getUserById(userId)?.nombre
    }

    override suspend fun updateUser(user: Usuario): Boolean {
        return store.updateUser(user)
    }

    override suspend fun deleteUser(email: String): Boolean {
        val normalizedEmail = email.trim().lowercase()
        publicationRepository.getUserPublications(normalizedEmail).forEach { publication ->
            // deletePublicationById is suspend; ensure we call sequentially
            publicationRepository.deletePublicationById(publication.id)
        }
        val deactivated = store.deactivateUser(normalizedEmail)
        store.removeUserData(normalizedEmail)
        return deactivated
    }
}

