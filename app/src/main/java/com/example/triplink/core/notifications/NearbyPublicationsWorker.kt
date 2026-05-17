package com.example.triplink.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.triplink.core.storage.NearbyNotificationFeedStore
import com.example.triplink.core.storage.NearbyNotificationPreferences
import com.example.triplink.data.repository.remote.FIREBASE_UID_FIELD
import com.example.triplink.data.repository.remote.FirestorePuntoInteresDto
import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class NearbyPublicationsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val preferences = NearbyNotificationPreferences(appContext)
    private val feedStore = NearbyNotificationFeedStore(appContext)
    private val notifier = NearbyPublicationNotifier(appContext)

    override suspend fun doWork(): Result {
        return try {
            if (!preferences.isEnabled()) return Result.success()

            val currentUser = auth.currentUser ?: return Result.success()
            val currentEmail = currentUser.email?.trim()?.lowercase().orEmpty()
            val currentUserLocation = fetchUserLocation(currentUser.uid) ?: return Result.success()
            val radiusKm = preferences.getRadiusKm().toDouble()
            val lastCheckedAt = preferences.getLastCheckedAt()
            val notifiedIds = preferences.getNotifiedPublicationIds()

            val recentPublications = firestore.collection(PUBLICATIONS_COLLECTION)
                .whereGreaterThan("fechaCreacion", lastCheckedAt)
                .orderBy("fechaCreacion")
                .limit(50)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    document.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
                }

            val eligible = recentPublications.filter { publication ->
                publication.id !in notifiedIds &&
                    publication.usuarioAutorId != currentEmail &&
                    publication.estado == EstadoPublicacion.VERIFICADA &&
                    distanceKm(currentUserLocation, publication.ubicacion) <= radiusKm
            }

            eligible.forEach { publication ->
                feedStore.upsertFromPublication(publication)
                notifier.showNewPublication(publication)
            }

            if (eligible.isNotEmpty()) {
                preferences.addNotifiedPublicationIds(eligible.map(PuntoInteres::id).toSet())
            }

            val newestCreatedAt = recentPublications.maxOfOrNull { it.fechaCreacion } ?: lastCheckedAt
            val newCursor = maxOf(newestCreatedAt, System.currentTimeMillis())
            preferences.setLastCheckedAt(newCursor)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun fetchUserLocation(firebaseUid: String): Ubicacion? {
        val userDoc = firestore.collection(USERS_COLLECTION)
            .whereEqualTo(FIREBASE_UID_FIELD, firebaseUid)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(FirestoreUsuarioDto::class.java)
            ?.toDomain()

        return userDoc?.ubicacion
    }

    private fun distanceKm(from: Ubicacion, to: Ubicacion): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(to.latitud - from.latitud)
        val dLon = Math.toRadians(to.longitud - from.longitud)

        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(from.latitud)) *
            cos(Math.toRadians(to.latitud)) *
            sin(dLon / 2).pow(2.0)

        return 2 * earthRadiusKm * asin(sqrt(a.coerceIn(0.0, 1.0)))
    }
}



