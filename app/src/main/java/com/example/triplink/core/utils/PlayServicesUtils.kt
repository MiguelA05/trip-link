package com.example.triplink.core.utils

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/** Helper to check Google Play Services availability before calling Play Services APIs. */
object PlayServicesUtils {
    fun isPlayServicesAvailable(context: Context): Boolean {
        return try {
            val gApi = GoogleApiAvailability.getInstance()
            val code = gApi.isGooglePlayServicesAvailable(context)
            code == ConnectionResult.SUCCESS
        } catch (t: Throwable) {
            false
        }
    }
}

