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
import android.content.Context
import android.util.Log
import com.example.triplink.core.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
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
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w("UserProfileRepo", "updateUser: sin conectividad, se omite operación")
            return false
        }

        val normalizedEmail = normalize(user.email)
        try {
            firestore.collection(USERS_COLLECTION)
                .document(normalizedEmail)
                .set(user.copy(email = normalizedEmail).toFirestoreDto(), SetOptions.merge())
                .await()

            replaceUserInCache(user.copy(email = normalizedEmail))
            return true
        } catch (e: Exception) {
            Log.w("UserProfileRepo", "Error actualizando usuario", e)
            return false
        }
    }

    override suspend fun deleteUser(email: String): Boolean {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w("UserProfileRepo", "deleteUser: sin conectividad, se omite operación")
            return false
        }

        val normalized = normalize(email)
        val currentUser = getUserById(normalized) ?: return false
        val documentId = normalize(currentUser.email)

        try {
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
        } catch (e: Exception) {
            Log.w("UserProfileRepo", "Error eliminando usuario", e)
            return false
        }
    }

    private fun observeUsers() {
        try {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                Log.w("UserProfileRepo", "observeUsers: sin conectividad, no se registra listener")
                return
            }

            usersListener?.remove()
            usersListener = firestore.collection(USERS_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("UserProfileRepo", "Error en snapshot listener", error)
                        return@addSnapshotListener
                    }

                    if (snapshot == null) return@addSnapshotListener

                    _users.value = snapshot.documents.mapNotNull { document ->
                        document.toObject(FirestoreUsuarioDto::class.java)?.toDomain()
                    }
                }
        } catch (e: Exception) {
            Log.w("UserProfileRepo", "observeUsers: excepción", e)
        }
    }

    private suspend fun fetchUserById(userId: String): Usuario? {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w("UserProfileRepo", "fetchUserById: sin conectividad, devolviendo null")
            return null
        }

        try {
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
        } catch (e: Exception) {
            Log.w("UserProfileRepo", "fetchUserById: error consultando Firestore", e)
            return null
        }
    }

    private suspend fun deleteSubCollection(userId: String, collectionName: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w("UserProfileRepo", "deleteSubCollection: sin conectividad, se omite")
            return
        }

        try {
            val collectionRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(collectionName)

            collectionRef.get()
                .await()
                .documents
                .forEach { it.reference.delete().await() }
        } catch (e: Exception) {
            Log.w("UserProfileRepo", "deleteSubCollection: error eliminando subcolección", e)
        }
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

