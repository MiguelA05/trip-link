package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.FAVORITES_COLLECTION
import com.example.triplink.data.repository.remote.FirestorePuntoInteresDto
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.PublicationRepository
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
class PublicationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PublicationRepository {

    private val _publications = MutableStateFlow<List<PuntoInteres>>(emptyList())
    override val publications: StateFlow<List<PuntoInteres>> = _publications.asStateFlow()

    private var publicationsListener: ListenerRegistration? = null

    init {
        observePublications()
    }

    override suspend fun homePublications(): List<PuntoInteres> = fetchAllPublications()
        .filter { it.estado == EstadoPublicacion.VERIFICADA }
        .take(10)

    override suspend fun explorePublications(): List<PuntoInteres> = fetchAllPublications()
        .filter { it.estado == EstadoPublicacion.VERIFICADA }

    override suspend fun getPublicationById(publicationId: String): PuntoInteres? {
        val snapshot = firestore.collection(PUBLICATIONS_COLLECTION)
            .document(publicationId)
            .get()
            .await()
        return snapshot.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
    }

    override suspend fun savePuntoInteres(publication: PuntoInteres): Boolean {
        val normalized = publication.copy(commentCount = publication.comments.size)
        val documentRef = firestore.collection(PUBLICATIONS_COLLECTION).document(normalized.id)
        if (documentRef.get().await().exists()) return false

        documentRef.set(normalized.toFirestoreDto(), SetOptions.merge()).await()
        replacePublicationInCache(normalized)
        return true
    }

    override suspend fun savePuntoInteresWithFcmToken(publication: PuntoInteres, fcmToken: String?): Boolean {
        val normalized = publication.copy(commentCount = publication.comments.size)
        val documentRef = firestore.collection(PUBLICATIONS_COLLECTION).document(normalized.id)
        if (documentRef.get().await().exists()) return false

        // Convertir a DTO y añadir FCM token para acceso rápido en Cloud Functions
        val dto = normalized.toFirestoreDto().copy(
            authorFcmToken = fcmToken
        )
        documentRef.set(dto, SetOptions.merge()).await()
        replacePublicationInCache(normalized)
        return true
    }

    override suspend fun updatePuntoInteres(publication: PuntoInteres): Boolean {
        val normalized = publication.copy(commentCount = publication.comments.size)
        val documentRef = firestore.collection(PUBLICATIONS_COLLECTION).document(normalized.id)
        if (!documentRef.get().await().exists()) return false

        documentRef.set(normalized.toFirestoreDto(), SetOptions.merge()).await()
        replacePublicationInCache(normalized)
        return true
    }

    override suspend fun deletePublicationById(publicationId: String): Boolean {
        val publicationRef = firestore.collection(PUBLICATIONS_COLLECTION).document(publicationId)
        val snapshot = publicationRef.get().await()
        if (!snapshot.exists()) return false

        publicationRef.delete().await()
        deleteFavoritesForPublication(publicationId)
        _publications.value = _publications.value.filterNot { it.id == publicationId }
        return true
    }

    override suspend fun getUserPublications(userId: String): List<PuntoInteres> {
        val normalized = normalize(userId)
        return fetchAllPublications()
            .filter { it.usuarioAutorId.equals(normalized, ignoreCase = true) }
    }

    override suspend fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres> {
        return fetchAllPublications().filter { it.estado == estado }
    }

    private fun observePublications() {
        try {
            publicationsListener?.remove()
            publicationsListener = firestore.collection(PUBLICATIONS_COLLECTION)
                .orderBy("fechaCreacion")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }

                    if (snapshot == null) return@addSnapshotListener

                    _publications.value = snapshot.documents.mapNotNull { document ->
                        document.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
                    }.sortedByDescending { it.fechaCreacion }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun deleteFavoritesForPublication(publicationId: String) {
        firestore.collectionGroup(FAVORITES_COLLECTION)
            .whereEqualTo("publicationId", publicationId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete().await() }
        // After deleting favorites, ensure denormalized favoriteCount in publication is consistent
        try {
            val publicationRef = firestore.collection(PUBLICATIONS_COLLECTION).document(publicationId)
            if (publicationRef.get().await().exists()) {
                publicationRef.set(mapOf("favoriteCount" to 0), SetOptions.merge()).await()
                // Update cache if publication present
                _publications.value = _publications.value.map { p ->
                    if (p.id == publicationId) p.copy(favoriteCount = 0) else p
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun replacePublicationInCache(publication: PuntoInteres) {
        _publications.value = _publications.value
            .filterNot { it.id == publication.id }
            .plus(publication)
            .sortedByDescending { it.fechaCreacion }
    }

    private suspend fun fetchAllPublications(): List<PuntoInteres> {
        return firestore.collection(PUBLICATIONS_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { document -> document.toObject(FirestorePuntoInteresDto::class.java)?.toDomain() }
            .sortedByDescending { it.fechaCreacion }
    }

    override suspend fun recalculateFavoriteCount(publicationId: String): Boolean {
        return try {
            val count = firestore.collectionGroup(FAVORITES_COLLECTION)
                .whereEqualTo("publicationId", publicationId)
                .get()
                .await()
                .documents
                .size

            val publicationRef = firestore.collection(PUBLICATIONS_COLLECTION).document(publicationId)
            val snapshot = publicationRef.get().await()
            if (!snapshot.exists()) return false

            publicationRef.set(mapOf("favoriteCount" to count), SetOptions.merge()).await()

            // Update local cache if present
            _publications.value = _publications.value.map { p ->
                if (p.id == publicationId) p.copy(favoriteCount = count) else p
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun normalize(value: String): String = value.trim().lowercase()
}

