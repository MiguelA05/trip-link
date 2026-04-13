package com.example.triplink.data.repository.user

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.publication.PublicationRepository
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

    override fun getUserById(userId: String): Usuario? {
        return store.users.value.firstOrNull { it.email.equals(userId, ignoreCase = true) }
    }

    override fun findUserNameById(userId: String): String? {
        return getUserById(userId)?.nombre
    }

    override fun updateUser(user: Usuario): Boolean {
        val index = store.users.value.indexOfFirst { it.email.equals(user.email, ignoreCase = true) }
        if (index == -1) return false

        val updated = store.users.value.toMutableList()
        updated[index] = user
        store.setUsers(updated)
        return true
    }

    override fun deleteUser(email: String): Boolean {
        val initialSize = store.users.value.size
        val normalizedEmail = email.trim().lowercase()
        publicationRepository.getUserPublications(normalizedEmail).forEach { publication ->
            publicationRepository.deletePublicationById(publication.id)
        }
        store.setUsers(store.users.value.filter { !it.email.equals(normalizedEmail, ignoreCase = true) })
        store.favorites.remove(normalizedEmail)
        store.badgeUnlocksByUser.remove(normalizedEmail)
        store.persistState()
        return store.users.value.size < initialSize
    }
}

