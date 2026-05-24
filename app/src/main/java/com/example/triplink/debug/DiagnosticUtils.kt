package com.example.triplink.debug

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.triplink.core.auth.CustomTokenAuth

object DiagnosticUtils {
	suspend fun isGooglePlayServicesAvailable(context: Context): Pair<Boolean, Int> = withContext(Dispatchers.IO) {
		val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
		return@withContext Pair(code == ConnectionResult.SUCCESS, code)
	}

	suspend fun resolveHost(host: String): String = withContext(Dispatchers.IO) {
		try {
			val addrs = InetAddress.getAllByName(host)
			return@withContext "OK: ${addrs.joinToString(",") { it.hostAddress }}"
		} catch (e: UnknownHostException) {
			return@withContext "UnknownHostException: ${e.message}"
		} catch (e: Exception) {
			return@withContext "Error: ${e::class.simpleName} ${e.message}"
		}
	}

	suspend fun listImageHttpCache(context: Context): String = withContext(Dispatchers.IO) {
		try {
			val dir = File(context.cacheDir, "image_http_cache")
			if (!dir.exists()) return@withContext "No existe: ${dir.absolutePath}"
			val files = dir.listFiles()?.map { it.name + " (" + it.length() + " bytes)" } ?: emptyList()
			return@withContext if (files.isEmpty()) "Vacío: ${dir.absolutePath}" else files.joinToString("\n")
		} catch (e: Exception) {
			return@withContext "Error list cache: ${e::class.simpleName} ${e.message}"
		}
	}

	suspend fun testFirestoreRead(): String = withContext(Dispatchers.IO) {
		try {
			val db = FirebaseFirestore.getInstance()
			return@withContext suspendCoroutine { cont ->
				db.collection("_diagnostics").document("ping").get()
					.addOnSuccessListener { doc ->
						cont.resume("OK: exists=${doc.exists()} data=${doc.data}")
					}
					.addOnFailureListener { ex ->
						cont.resume("Failure: ${ex::class.simpleName} ${ex.message}")
					}
			}
		} catch (e: Exception) {
			return@withContext "Exception: ${e::class.simpleName} ${e.message}"
		}
	}

	suspend fun attemptCustomTokenSignIn(backendUrl: String): String = withContext(Dispatchers.IO) {
		try {
			return@withContext try {
				val result = CustomTokenAuth.signInWithBackendToken(backendUrl)
				if (result.isSuccess) {
					"Signed-in UID=${result.getOrNull()}"
				} else {
					"Sign-in failed: ${result.exceptionOrNull()?.message}"
				}
			} catch (e: Exception) {
				"Error calling CustomTokenAuth: ${e::class.simpleName} ${e.message}"
			}
		} catch (e: Exception) {
			return@withContext "Exception: ${e::class.simpleName} ${e.message}"
		}
	}
}


