package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.BADGE_UNLOCKS_COLLECTION
import com.example.triplink.data.repository.remote.FAVORITES_COLLECTION
import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val publicationRepository: PublicationRepository
) : UserProfileRepository {

    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    override val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private var usersListener: ListenerRegistration? = null

    init {
        observeUsers()
    }

    override suspend fun getUserById(userId: String): Usuario? {
        val normalized = normalize(userId)
        _users.value.firstOrNull { matchesUserId(it, normalized) }?.let { return it }
        return fetchUserById(normalized)
    }

    override suspend fun findUserNameById(userId: String): String? {
        return getUserById(userId)?.nombre
    }

    override suspend fun updateUser(user: Usuario): Boolean {
        val normalizedEmail = normalize(user.email)
        firestore.collection(USERS_COLLECTION)
            .document(normalizedEmail)
            .set(user.copy(email = normalizedEmail).toFirestoreDto(), SetOptions.merge())
            .await()

        replaceUserInCache(user.copy(email = normalizedEmail))
        return true
    }

    override suspend fun deleteUser(email: String): Boolean {
        val normalized = normalize(email)
        val currentUser = getUserById(normalized) ?: return false
        val documentId = normalize(currentUser.email)

        publicationRepository.getUserPublications(documentId).forEach { publication ->
            publicationRepository.deletePublicationById(publication.id)
        }

        val deactivated = currentUser.copy(activo = false, email = documentId)
        firestore.collection(USERS_COLLECTION)
            .document(documentId)
            .set(deactivated.toFirestoreDto(), SetOptions.merge())
            .await()

        deleteSubCollection(documentId, FAVORITES_COLLECTION)
        deleteSubCollection(documentId, BADGE_UNLOCKS_COLLECTION)
        replaceUserInCache(deactivated)
        return true
    }

    private fun observeUsers() {
        try {
            usersListener?.remove()
            usersListener = firestore.collection(USERS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }

                    if (snapshot == null) return@addSnapshotListener

                    _users.value = snapshot.documents.mapNotNull { document ->
                        document.toObject(FirestoreUsuarioDto::class.java)?.toDomain()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun fetchUserById(userId: String): Usuario? {
        val byEmailDoc = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
        byEmailDoc.toObject(FirestoreUsuarioDto::class.java)?.toDomain()?.let { return it }

        val byFirebaseUid = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("firebaseUid", userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
        return byFirebaseUid?.toObject(FirestoreUsuarioDto::class.java)?.toDomain()
    }

    private suspend fun deleteSubCollection(userId: String, collectionName: String) {
        val collectionRef = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(collectionName)

        collectionRef.get()
            .await()
            .documents
            .forEach { it.reference.delete().await() }
    }

    private fun replaceUserInCache(user: Usuario) {
        val normalized = normalize(user.email)
        _users.value = _users.value
            .filterNot { matchesUserId(it, normalized) }
            .plus(user.copy(email = normalized))
    }

    private fun matchesUserId(user: Usuario, normalizedId: String): Boolean {
        return user.email.equals(normalizedId, ignoreCase = true) ||
            user.firebaseUid?.equals(normalizedId, ignoreCase = true) == true
    }

    private fun normalize(value: String): String = value.trim().lowercase()
}

