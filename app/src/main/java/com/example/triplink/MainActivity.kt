package com.example.triplink

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.triplink.core.navigation.AppNavigation
import com.example.triplink.core.notifications.NearbyNotificationsScheduler
import com.example.triplink.core.storage.NearbyNotificationFeedStore
import com.example.triplink.core.utils.FirebaseAuthPersistenceManager
import com.example.triplink.ui.theme.DescubreuqTheme
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_TRIGGER_NEARBY_NOTIFICATIONS = "trigger_nearby_notifications"
    }

    @Inject
    lateinit var nearbyNotificationsScheduler: NearbyNotificationsScheduler

    @Inject
    lateinit var authPersistence: FirebaseAuthPersistenceManager

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var pendingPublicationId by mutableStateOf<String?>(null)
    private var deepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore Firebase Auth session from cache if it exists
        lifecycleScope.launch {
            try {
                val authState = authPersistence.ensureAuthSession()
                if (authState != null) {
                    Log.i("MainActivity", "Firebase Auth session restored: uid=${authState.uid}, provider=${authState.provider}")
                } else {
                    Log.d("MainActivity", "No cached Firebase Auth session to restore")
                }
            } catch (e: Exception) {
                Log.w("MainActivity", "Error restoring Firebase Auth session: ${e.message}")
            }
        }

        // Configure Firebase Functions emulator only when the app is running on an Android emulator.
        // On a physical device we keep the default Firebase Functions endpoint so the app can
        // talk to the deployed backend instead of localhost on the computer.
        if (BuildConfig.DEBUG && isRunningOnEmulator()) {
            try {
                // 10.0.2.2 is the special IP that allows the Android emulator to reach localhost on the host
                FirebaseFunctions.getInstance().useEmulator("10.0.2.2", 5001)
                Log.i("MainActivity", "Firebase Functions emulator configured: 10.0.2.2:5001")
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to configure Firebase Functions emulator", e)
            }
        } else {
            Log.i("MainActivity", "Using default Firebase Functions endpoint (physical device or non-debug build)")
        }

        deepLink = intent?.data
        pendingPublicationId = extractPublicationId(intent)
        markNotificationAsRead(intent)
        triggerNearbyNotificationsIfRequested(intent)
        requestNotificationPermissionIfNeeded()
        lifecycleScope.launch {
            nearbyNotificationsScheduler.syncFromPreferences()
        }
        enableEdgeToEdge()
        setContent {
            DescubreuqTheme {
                AppNavigation(
                    pendingPublicationId = pendingPublicationId,
                    onPendingPublicationConsumed = { pendingPublicationId = null },
                    deepLink = deepLink,
                    onDeepLinkConsumed = { deepLink = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLink = intent.data
        pendingPublicationId = extractPublicationId(intent)
        markNotificationAsRead(intent)
        triggerNearbyNotificationsIfRequested(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val alreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!alreadyGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun extractPublicationId(intent: Intent?): String? {
        return intent?.getStringExtra("publication_id")?.takeIf { it.isNotBlank() }
    }

    private fun markNotificationAsRead(intent: Intent?) {
        val id = intent?.getStringExtra("notification_id")?.takeIf { it.isNotBlank() }
            ?: extractPublicationId(intent)
            ?: return
        lifecycleScope.launch {
            // Al tocar la notificación del sistema, eliminarla del feed local
            NearbyNotificationFeedStore(applicationContext).remove(id)
        }
    }

    private fun triggerNearbyNotificationsIfRequested(intent: Intent?) {
        val shouldTrigger = intent?.getBooleanExtra(EXTRA_TRIGGER_NEARBY_NOTIFICATIONS, false) == true
        if (!shouldTrigger) return

        nearbyNotificationsScheduler.triggerNow()
    }

    private fun isRunningOnEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.contains("emulator") ||
            Build.MODEL.contains("Android SDK built for") ||
            Build.MODEL.contains("sdk_gphone") ||
            Build.MANUFACTURER.contains("Genymotion")
    }
}
