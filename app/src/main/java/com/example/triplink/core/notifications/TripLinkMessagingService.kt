package com.example.triplink.core.notifications

import android.util.Log
import com.example.triplink.core.storage.NearbyNotificationFeedStore
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking

class TripLinkMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token received: $token")
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message received: ${message.data}")

        // Aquí usaremos tu notifier existente para mostrar la notificación
        val notifier = NearbyPublicationNotifier(applicationContext)
        
        val title = message.notification?.title ?: message.data["title"] ?: "TripLink"
        val body = message.notification?.body ?: message.data["body"] ?: "Tienes una nueva actualización"
        val publicationId = message.data["publication_id"]
            ?: message.data["publicationId"]
            ?: message.data["puntoInteresId"]
            ?: ""
        val notificationId = message.data["notification_id"]
            ?: message.messageId
            ?: publicationId.takeIf { it.isNotBlank() }
            ?: System.currentTimeMillis().toString()

        runBlocking {
            NearbyNotificationFeedStore(applicationContext).upsertPushNotification(
                notificationId = notificationId,
                publicationId = publicationId,
                title = title,
                body = body
            )
        }
        
        // Puedes personalizar qué mostrar basándote en los datos del mensaje
        // Por ahora lanzamos una notificación genérica que usa tu diseño
        notifier.showGenericNotification(
            title = title,
            body = body,
            publicationId = publicationId,
            notificationId = notificationId
        )
    }

    private fun saveTokenToFirestore(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val email = currentUser.email?.trim()?.lowercase() ?: return

        FirebaseFirestore.getInstance()
            .collection(USERS_COLLECTION)
            .document(email)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCM", "Token saved successfully for $email")
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Error saving token: ${e.message}")
            }
    }
}
