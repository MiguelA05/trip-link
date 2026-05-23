package com.example.triplink.core.navigation.auth

import android.net.Uri

data class ResetPasswordDeepLink(
    val oobCode: String
)

fun Uri.toResetPasswordDeepLink(): ResetPasswordDeepLink? {
    val actionUri = getQueryParameter("link")?.let(Uri::parse) ?: this
    val mode = actionUri.getQueryParameter("mode")
    val oobCode = actionUri.getQueryParameter("oobCode")

    return if (mode == "resetPassword" && !oobCode.isNullOrBlank()) {
        ResetPasswordDeepLink(oobCode)
    } else {
        null
    }
}
