package com.example.triplink

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import com.example.triplink.core.utils.NetworkUtils
import com.example.triplink.core.utils.PlayServicesUtils
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

@HiltAndroidApp
class TripLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Suscribirse al tema general para recibir avisos de nuevos lugares
        // Solo intentamos suscribirnos si hay conectividad y Play Services disponible.
        try {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (PlayServicesUtils.isPlayServicesAvailable(this)) {
                    FirebaseMessaging.getInstance().subscribeToTopic("new_places")
                } else {
                    Log.w("TripLinkApp", "Google Play Services no disponible, se omite suscripción a topics de FCM")
                }
            } else {
                Log.w("TripLinkApp", "Sin conectividad: no se intenta suscribir a topics de FCM")
            }
        } catch (e: Exception) {
            // Protegemos contra cualquier error de inicialización de Firebase/PlayServices
            Log.w("TripLinkApp", "Error al suscribir a topic FCM", e)
        }

        // Habilitar explicitamente Firestore offline persistence (si no está ya)
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            FirebaseFirestore.getInstance().firestoreSettings = settings
        } catch (e: Exception) {
            // Fallar no es crítico aquí; loguear si es necesario
        }

        // Inicializar cliente HTTP compartido con cache (utilizado por imagenes y llamadas de red)
        try {
            val diskCacheDir = File(cacheDir, "image_cache")
            val diskCacheSizeBytes = 50L * 1024L * 1024L // 50 MB
            val okHttpCache = Cache(diskCacheDir, diskCacheSizeBytes)
            val okHttpClient = OkHttpClient.Builder().cache(okHttpCache).build()
            AppNetwork.okHttpClient = okHttpClient
        } catch (e: Exception) {
            // No crítico
        }
    }
}

/** Simple holder for shared OkHttp client used across the app. */
object AppNetwork {
    // Will be initialized by TripLinkApp onCreate
    var okHttpClient: OkHttpClient? = null
}

