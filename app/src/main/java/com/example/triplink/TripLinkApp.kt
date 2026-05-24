package com.example.triplink

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TripLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Suscribirse al tema general para recibir avisos de nuevos lugares
        FirebaseMessaging.getInstance().subscribeToTopic("new_places")
    }
}

