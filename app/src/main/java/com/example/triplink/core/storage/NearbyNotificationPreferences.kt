package com.example.triplink.core.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.nearbyNotificationDataStore by preferencesDataStore(name = "nearby_notification_prefs")

class NearbyNotificationPreferences(private val context: Context) {

    companion object {
        private val ENABLED = booleanPreferencesKey("enabled")
        private val RADIUS_KM = intPreferencesKey("radius_km")
        private val LAST_CHECKED_AT = longPreferencesKey("last_checked_at")
        private val NOTIFIED_PUBLICATION_IDS = stringSetPreferencesKey("notified_publication_ids")

        private const val DEFAULT_RADIUS_KM = 5
        private const val MAX_STORED_IDS = 200
    }

    suspend fun isEnabled(): Boolean = getPreferences()[ENABLED] ?: true

    suspend fun setEnabled(enabled: Boolean) {
        context.nearbyNotificationDataStore.edit { prefs ->
            prefs[ENABLED] = enabled
        }
    }

    suspend fun getRadiusKm(): Int = getPreferences()[RADIUS_KM] ?: DEFAULT_RADIUS_KM

    suspend fun setRadiusKm(radiusKm: Int) {
        val safeRadius = radiusKm.coerceIn(1, 100)
        context.nearbyNotificationDataStore.edit { prefs ->
            prefs[RADIUS_KM] = safeRadius
        }
    }

    suspend fun getLastCheckedAt(): Long = getPreferences()[LAST_CHECKED_AT] ?: 0L

    suspend fun setLastCheckedAt(timestampMillis: Long) {
        context.nearbyNotificationDataStore.edit { prefs ->
            prefs[LAST_CHECKED_AT] = timestampMillis.coerceAtLeast(0L)
        }
    }

    suspend fun getNotifiedPublicationIds(): Set<String> = getPreferences()[NOTIFIED_PUBLICATION_IDS].orEmpty()

    suspend fun addNotifiedPublicationIds(ids: Set<String>) {
        if (ids.isEmpty()) return

        val current = getNotifiedPublicationIds().toMutableSet()
        current.addAll(ids)
        val bounded = if (current.size > MAX_STORED_IDS) {
            current.sorted().takeLast(MAX_STORED_IDS).toSet()
        } else {
            current
        }

        context.nearbyNotificationDataStore.edit { prefs ->
            prefs[NOTIFIED_PUBLICATION_IDS] = bounded
        }
    }

    private suspend fun getPreferences(): Preferences = context.nearbyNotificationDataStore.data.first()
}


