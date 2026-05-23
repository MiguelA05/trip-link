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
import com.example.triplink.ui.theme.DescubreuqTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_TRIGGER_NEARBY_NOTIFICATIONS = "trigger_nearby_notifications"
    }

    @Inject
    lateinit var nearbyNotificationsScheduler: NearbyNotificationsScheduler

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var pendingPublicationId by mutableStateOf<String?>(null)
    private var deepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLink = intent?.data
        pendingPublicationId = extractPublicationId(intent)
        markNotificationAsRead(pendingPublicationId)
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
        markNotificationAsRead(pendingPublicationId)
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

    private fun markNotificationAsRead(publicationId: String?) {
        val id = publicationId?.takeIf { it.isNotBlank() } ?: return
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
}

