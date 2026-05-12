package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.BADGE_UNLOCKS_COLLECTION
import com.example.triplink.data.repository.remote.FAVORITES_COLLECTION
import com.example.triplink.data.repository.remote.FirestoreBadgeUnlockDto
import com.example.triplink.data.repository.remote.FirestoreFavoriteDto
import com.example.triplink.data.repository.remote.FirestorePuntoInteresDto
import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.data.seed.seedPublications as seedPublicationsSeed
import com.example.triplink.data.seed.seedUsers as seedUsersSeed
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryStore @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _publications = MutableStateFlow<List<PuntoInteres>>(emptyList())
    val publications: StateFlow<List<PuntoInteres>> = _publications.asStateFlow()

    private val favorites = mutableMapOf<String, MutableSet<String>>()
    private val badgeUnlocksByUser = mutableMapOf<String, MutableMap<String, Long>>()

    private var usersListener: ListenerRegistration? = null
    private var publicationsListener: ListenerRegistration? = null

    init {
        observeUsers()
        observePublications()
        scope.launch {
            seedIfNeeded()
        }
    }

    suspend fun setUsers(value: List<Usuario>) {
        upsertUsersInternal(value)
    }

    suspend fun setPublications(value: List<PuntoInteres>) {
        upsertPublicationsInternal(value)
    }

    suspend fun findUserById(userId: String): Usuario? {
        val normalized = normalize(userId)
        _users.value.firstOrNull { matchesUserId(it, normalized) }?.let { return it }
        return fetchUserById(normalized)
    }

    suspend fun findUserByEmail(email: String): Usuario? = findUserById(email)

    suspend fun saveUser(user: Usuario): Boolean {
        if (fetchUserById(user.email) != null) return false
        return upsertUserInternal(user)
    }

    suspend fun updateUser(user: Usuario): Boolean {
        return upsertUserInternal(user)
    }

    suspend fun deactivateUser(email: String): Boolean {
        return deactivateUserInternal(email)
    }

    suspend fun findPublicationById(publicationId: String): PuntoInteres? {
        _publications.value.firstOrNull { it.id == publicationId }?.let { return it }
        return fetchPublicationById(publicationId)
    }

    suspend fun savePublication(publication: PuntoInteres): Boolean {
        if (fetchPublicationById(publication.id) != null) return false
        return upsertPublicationInternal(publication)
    }

    suspend fun updatePublication(publication: PuntoInteres): Boolean {
        return upsertPublicationInternal(publication)
    }

    suspend fun deletePublication(publicationId: String): Boolean {
        return deletePublicationInternal(publicationId)
    }

    fun userPublications(userId: String): List<PuntoInteres> {
        val normalized = normalize(userId)
        return _publications.value.filter { it.usuarioAutorId.equals(normalized, ignoreCase = true) }
    }

    fun publicationsByState(estado: EstadoPublicacion): List<PuntoInteres> {
        return _publications.value.filter { it.estado == estado }
    }

    suspend fun ensureFavoriteBucket(userId: String): MutableSet<String> {
        return loadFavoriteBucket(userId)
    }

    fun isFavorite(userId: String, publicationId: String): Boolean {
        return favorites[normalize(userId)]?.contains(publicationId) ?: false
    }

    suspend fun toggleFavorite(userId: String, publicationId: String): Boolean {
        return toggleFavoriteInternal(userId, publicationId)
    }

    fun favoriteCountForPublication(publicationId: String): Int {
        // Prefer in-memory value (fast). If not present, return 0 - store toggleFavorite updates counts.
        return _publications.value.firstOrNull { it.id == publicationId }?.favoriteCount ?: 0
    }

    fun favoritePublicationIds(userId: String): Set<String> {
        return favorites[normalize(userId)].orEmpty()
    }

    suspend fun removePublicationFromFavorites(publicationId: String) {
        firestore.collectionGroup(FAVORITES_COLLECTION)
            .whereEqualTo("publicationId", publicationId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete().await() }

        favorites.values.forEach { it.remove(publicationId) }
    }


    suspend fun badgeUnlocksFor(userId: String): MutableMap<String, Long> {
        return loadBadgeUnlocks(userId)
    }

    suspend fun unlockBadge(userId: String, badgeId: String, timestamp: Long = System.currentTimeMillis()): Boolean {
        return unlockBadgeInternal(userId, badgeId, timestamp)
    }

    suspend fun removeUserData(userId: String) {
        val normalized = normalize(userId)
        favorites.remove(normalized)
        badgeUnlocksByUser.remove(normalized)

        deleteSubCollection(normalized, FAVORITES_COLLECTION)
        deleteSubCollection(normalized, BADGE_UNLOCKS_COLLECTION)
    }

    fun persistState() = Unit

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

    private suspend fun seedIfNeeded() {
        try {
            val usersCount = firestore.collection(USERS_COLLECTION).get().await().size()
            val publicationsCount = firestore.collection(PUBLICATIONS_COLLECTION).get().await().size()

            if (usersCount == 0) {
                upsertUsersInternal(seedUsersSeed())
            }

            if (publicationsCount == 0) {
                upsertPublicationsInternal(seedPublicationsSeed())
            }
        } catch (e: Exception) {
            // If seeding fails, continue. Listeners may still populate data.
            e.printStackTrace()
        }
    }

    private suspend fun upsertUsersInternal(value: List<Usuario>) {
        if (value.isEmpty()) return
        val batch = firestore.batch()
        value.forEach { user ->
            batch.set(usersCollection().document(normalize(user.email)), user.toFirestoreDto(), SetOptions.merge())
        }
        batch.commit().await()
        _users.value = value
    }

    private suspend fun upsertUserInternal(user: Usuario): Boolean {
        val normalized = normalize(user.email)
        firestore.collection(USERS_COLLECTION)
            .document(normalized)
            .set(user.toFirestoreDto(), SetOptions.merge())
            .await()
        _users.value = _users.value
            .filterNot { matchesUserId(it, normalized) }
            .plus(user.copy(email = normalized))
        return true
    }

    private suspend fun deactivateUserInternal(email: String): Boolean {
        val normalized = normalize(email)
        val current = fetchUserById(normalized) ?: return false
        val deactivated = current.copy(activo = false)
        firestore.collection(USERS_COLLECTION)
            .document(normalized)
            .set(deactivated.toFirestoreDto(), SetOptions.merge())
            .await()
        _users.value = _users.value.map { existing ->
            if (matchesUserId(existing, normalized)) deactivated else existing
        }
        return true
    }

    private suspend fun fetchUserById(userId: String): Usuario? {
        val normalized = normalize(userId)
        val byEmailDoc = firestore.collection(USERS_COLLECTION)
            .document(normalized)
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

    private suspend fun upsertPublicationsInternal(value: List<PuntoInteres>) {
        if (value.isEmpty()) return
        val batch = firestore.batch()
        value.forEach { publication ->
            batch.set(publicationsCollection().document(publication.id), publication.toFirestoreDto(), SetOptions.merge())
        }
        batch.commit().await()
        _publications.value = value
    }

    private suspend fun upsertPublicationInternal(publication: PuntoInteres): Boolean {
        firestore.collection(PUBLICATIONS_COLLECTION)
            .document(publication.id)
            .set(publication.toFirestoreDto(), SetOptions.merge())
            .await()
        _publications.value = _publications.value
            .filterNot { it.id == publication.id }
            .plus(publication)
            .sortedByDescending { it.fechaCreacion }
        return true
    }

    private suspend fun deletePublicationInternal(publicationId: String): Boolean {
        val publicationRef = firestore.collection(PUBLICATIONS_COLLECTION).document(publicationId)
        val snapshot = publicationRef.get().await()
        if (!snapshot.exists()) return false

        publicationRef.delete().await()
        removePublicationFromFavorites(publicationId)
        _publications.value = _publications.value.filterNot { it.id == publicationId }
        return true
    }

    private suspend fun fetchPublicationById(publicationId: String): PuntoInteres? {
        val snapshot = firestore.collection(PUBLICATIONS_COLLECTION)
            .document(publicationId)
            .get()
            .await()
        return snapshot.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
    }

    private suspend fun toggleFavoriteInternal(userId: String, publicationId: String): Boolean {
        val normalizedUser = normalize(userId)
        val publication = fetchPublicationById(publicationId) ?: return false
        val bucket = loadFavoriteBucket(normalizedUser)
        val favoriteRef = favoritesCollection(normalizedUser).document(publicationId)
        val added = publicationId !in bucket

        if (added) {
            favoriteRef.set(
                FirestoreFavoriteDto(
                    publicationId = publicationId,
                    createdAt = System.currentTimeMillis()
                )
            ).await()
        } else {
            favoriteRef.delete().await()
        }

        bucket.apply {
            if (added) add(publicationId) else remove(publicationId)
        }

        firestore.collection(PUBLICATIONS_COLLECTION)
            .document(publicationId)
            .set(
                publication.copy(
                    favoriteCount = if (added) publication.favoriteCount + 1 else (publication.favoriteCount - 1).coerceAtLeast(0)
                ).toFirestoreDto(),
                SetOptions.merge()
            )
            .await()

        _publications.value = _publications.value.map { current ->
            if (current.id == publicationId) {
                current.copy(favoriteCount = if (added) current.favoriteCount + 1 else (current.favoriteCount - 1).coerceAtLeast(0))
            } else {
                current
            }
        }
        return added
    }

    private suspend fun loadFavoriteBucket(userId: String): MutableSet<String> {
        val normalized = normalize(userId)
        favorites[normalized]?.let { return it }

        val docs = favoritesCollection(normalized)
            .get()
            .await()
            .documents
        val bucket = docs.mapNotNull { document ->
            document.toObject(FirestoreFavoriteDto::class.java)?.toDomain()
        }.toMutableSet()

        favorites[normalized] = bucket
        return bucket
    }

    private suspend fun loadBadgeUnlocks(userId: String): MutableMap<String, Long> {
        val normalized = normalize(userId)
        badgeUnlocksByUser[normalized]?.let { return it }

        val docs = badgeUnlocksCollection(normalized)
            .get()
            .await()
            .documents
        val unlocks = docs.mapNotNull { document ->
            document.toObject(FirestoreBadgeUnlockDto::class.java)?.toDomain()
        }.toMap().toMutableMap()

        badgeUnlocksByUser[normalized] = unlocks
        return unlocks
    }

    private suspend fun unlockBadgeInternal(userId: String, badgeId: String, timestamp: Long): Boolean {
        val normalized = normalize(userId)
        val unlocks = loadBadgeUnlocks(normalized)
        if (unlocks.containsKey(badgeId)) return false

        badgeUnlocksCollection(normalized)
            .document(badgeId)
            .set(
                FirestoreBadgeUnlockDto(
                    badgeId = badgeId,
                    unlockedAtMillis = timestamp
                )
            )
            .await()

        unlocks[badgeId] = timestamp
        return true
    }

    private suspend fun deleteSubCollection(userId: String, collectionName: String) {
        val collectionRef = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(collectionName)

        val snapshot = collectionRef.get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    private fun usersCollection() = firestore.collection(USERS_COLLECTION)

    private fun publicationsCollection() = firestore.collection(PUBLICATIONS_COLLECTION)

    private fun favoritesCollection(userId: String) = firestore.collection(USERS_COLLECTION)
        .document(userId)
        .collection(FAVORITES_COLLECTION)

    private fun badgeUnlocksCollection(userId: String) = firestore.collection(USERS_COLLECTION)
        .document(userId)
        .collection(BADGE_UNLOCKS_COLLECTION)

    private fun matchesUserId(user: Usuario, normalizedId: String): Boolean {
        return user.email.equals(normalizedId, ignoreCase = true) ||
            user.firebaseUid?.equals(normalizedId, ignoreCase = true) == true
    }

    private fun normalize(value: String): String = value.trim().lowercase()
}


