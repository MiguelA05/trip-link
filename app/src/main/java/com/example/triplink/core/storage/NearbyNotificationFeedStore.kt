package com.example.triplink.core.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.triplink.core.notifications.NearbyNotificationRecord
import com.example.triplink.domain.model.PuntoInteres
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val Context.nearbyNotificationFeedDataStore by preferencesDataStore(name = "nearby_notification_feed")

class NearbyNotificationFeedStore(private val context: Context) {

    companion object {
        private val FEED_JSON = stringPreferencesKey("feed_json")
        private const val MAX_ITEMS = 200
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun getAll(): List<NearbyNotificationRecord> {
        val encoded = context.nearbyNotificationFeedDataStore.data.first()[FEED_JSON].orEmpty()
        if (encoded.isBlank()) return emptyList()

        return try {
            json.decodeFromString<List<NearbyNotificationRecord>>(encoded)
                .sortedByDescending { it.notifiedAtMillis }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upsertFromPublication(publication: PuntoInteres) {
        val current = getAll().toMutableList()
        val recordId = publication.id

        if (current.any { it.id == recordId }) return

        val record = NearbyNotificationRecord(
            id = recordId,
            publicationId = publication.id,
            publicationTitle = publication.titulo,
            publicationInfo = publication.informacion,
            publicationCreatedAtMillis = publication.fechaCreacion,
            notifiedAtMillis = System.currentTimeMillis(),
            isRead = false
        )

        current.add(0, record)
        persist(current.take(MAX_ITEMS))
    }

    suspend fun upsertPushNotification(
        notificationId: String,
        publicationId: String,
        title: String,
        body: String,
        notifiedAtMillis: Long = System.currentTimeMillis()
    ) {
        val recordId = notificationId.takeIf { it.isNotBlank() }
            ?: publicationId.takeIf { it.isNotBlank() }
            ?: notifiedAtMillis.toString()
        val current = getAll()
            .filterNot { it.id == recordId }
            .toMutableList()

        val record = NearbyNotificationRecord(
            id = recordId,
            publicationId = publicationId,
            publicationTitle = title,
            publicationInfo = body,
            publicationCreatedAtMillis = notifiedAtMillis,
            notifiedAtMillis = notifiedAtMillis,
            isRead = false
        )

        current.add(0, record)
        persist(current.take(MAX_ITEMS))
    }

    suspend fun markAllAsRead() {
        // Para mantener la vista simple, marcar todas como leídas elimina las notificaciones
        persist(emptyList())
    }

    suspend fun markAsRead(notificationId: String) {
        val updated = getAll().filter { it.id != notificationId }
        persist(updated)
    }

    suspend fun remove(notificationId: String) {
        val updated = getAll().filter { it.id != notificationId }
        persist(updated)
    }

    suspend fun clearAll() {
        persist(emptyList())
    }

    private suspend fun persist(items: List<NearbyNotificationRecord>) {
        context.nearbyNotificationFeedDataStore.edit { prefs ->
            prefs[FEED_JSON] = json.encodeToString(items)
        }
    }
}
