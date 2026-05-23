package com.example.triplink.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.example.triplink.core.storage.NearbyNotificationPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyNotificationsScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    companion object {
        const val UNIQUE_WORK_NAME = "nearby_publications_notification_work"
    }

    private val preferences = NearbyNotificationPreferences(context)

    suspend fun syncFromPreferences() = withContext(Dispatchers.IO) {
        if (preferences.isEnabled()) {
            schedule()
        } else {
            cancel()
        }
    }

    suspend fun enable(radiusKm: Int = 5) = withContext(Dispatchers.IO) {
        preferences.setEnabled(true)
        preferences.setRadiusKm(radiusKm)
        schedule()
    }

    suspend fun disable() = withContext(Dispatchers.IO) {
        preferences.setEnabled(false)
        cancel()
    }

    fun triggerNow() {
        // Deshabilitado: Migración a Notificaciones Push Reales
        /*
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<NearbyPublicationsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
        */
    }

    private fun schedule() {
        // Deshabilitado: Migración a Notificaciones Push Reales
        /*
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<NearbyPublicationsWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
        */
    }

    private fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}



