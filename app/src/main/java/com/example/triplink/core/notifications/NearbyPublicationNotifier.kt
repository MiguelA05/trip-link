package com.example.triplink.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.triplink.MainActivity
import com.example.triplink.R
import com.example.triplink.domain.model.PuntoInteres

class NearbyPublicationNotifier(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "nearby_publications"
        private const val CHANNEL_NAME = "Publicaciones cercanas"
        private const val CHANNEL_DESCRIPTION = "Avisos de nuevas publicaciones cerca de tu ubicacion"
    }

    fun ensureChannel() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = CHANNEL_DESCRIPTION
        }
        manager.createNotificationChannel(channel)
    }

    fun showNewPublication(publication: PuntoInteres) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return
        }

        ensureChannel()

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("publication_id", publication.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            publication.id.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Nueva publicacion cerca de ti")
            .setContentText(publication.titulo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(publication.informacion))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(publication.id.hashCode(), notification)
        } catch (_: SecurityException) {
            // No-op: permission can be revoked while the worker is running.
        }
    }
}




