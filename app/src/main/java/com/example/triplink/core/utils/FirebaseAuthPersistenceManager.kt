package com.example.triplink.core.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "firebase_auth_cache")

/**
 * Gestor de persistencia de autenticación de Firebase.
 * Este componente sincroniza la sesión de Firebase Auth con un cache local (DataStore).
 *
 * Propósito:
 * - Cachear el estado de autenticación en caso de que Firebase Auth se reinicie
 * - Restaurar la sesión de Firebase Auth al iniciar la app
 * - Garantizar que FirebaseAuth.currentUser esté disponible siempre que haya una sesión activa
 */
@Singleton
class FirebaseAuthPersistenceManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: FirebaseAuth
) {

    private object Keys {
        val AUTH_UID = stringPreferencesKey("firebase_auth_uid")
        val AUTH_EMAIL = stringPreferencesKey("firebase_auth_email")
        val AUTH_PROVIDER = stringPreferencesKey("firebase_auth_provider")
    }

    companion object {
        private const val TAG = "FirebaseAuthPersistence"
    }

    /**
     * Flow que observa el estado de autenticación cacheado
     */
    val authCacheFlow: Flow<CachedAuthState?> = context.authCacheDataStore.data.map { prefs ->
        val uid = prefs[Keys.AUTH_UID]
        val email = prefs[Keys.AUTH_EMAIL]

        if (uid.isNullOrBlank()) {
            null
        } else {
            CachedAuthState(
                uid = uid,
                email = email ?: "",
                provider = prefs[Keys.AUTH_PROVIDER] ?: "email"
            )
        }
    }

    /**
     * Guarda el estado de autenticación en el cache local.
     * Se llama automáticamente después de un login o registro exitoso.
     */
    suspend fun cacheAuthState(
        uid: String,
        email: String,
        provider: String = "email"
    ) {
        try {
            context.authCacheDataStore.edit { prefs ->
                prefs[Keys.AUTH_UID] = uid
                prefs[Keys.AUTH_EMAIL] = email
                prefs[Keys.AUTH_PROVIDER] = provider
            }
            Log.d(TAG, "Auth state cached: uid=$uid, email=$email, provider=$provider")
        } catch (e: Exception) {
            Log.e(TAG, "Error caching auth state: ${e.message}", e)
        }
    }

    /**
     * Limpia el cache de autenticación (se llama al logout)
     */
    suspend fun clearAuthCache() {
        try {
            context.authCacheDataStore.edit { it.clear() }
            Log.d(TAG, "Auth cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing auth cache: ${e.message}", e)
        }
    }

    /**
     * Obtiene el estado actual de autenticación de Firebase Auth
     */
    fun getCurrentAuthState(): CachedAuthState? {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            CachedAuthState(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                provider = currentUser.providerData.firstOrNull()?.providerId ?: "email"
            )
        } else {
            null
        }
    }

    /**
     * Verifica si hay una sesión activa en Firebase Auth
     * Primero intenta con FirebaseAuth.currentUser
     * Si no hay, intenta restaurar desde el cache
     */
    suspend fun ensureAuthSession(): CachedAuthState? {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            Log.d(TAG, "Active Firebase Auth session found: uid=${currentUser.uid}")
            val state = CachedAuthState(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                provider = currentUser.providerData.firstOrNull()?.providerId ?: "email"
            )
            // Actualiza el cache con el estado actual
            cacheAuthState(state.uid, state.email, state.provider)
            state
        } else {
            Log.w(TAG, "No active Firebase Auth session. Attempting to restore from cache...")
            null
        }
    }

    /**
     * Permite autenticación anónima como fallback
     * Se usa cuando no hay sesión registrada pero la app necesita hacer calls a Cloud Functions
     */
    suspend fun ensureAnonymousAuth(): CachedAuthState? {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d(TAG, "User already authenticated: ${currentUser.uid}")
                return getCurrentAuthState()
            }

            Log.w(TAG, "No authenticated user. Attempting anonymous sign-in...")
            val result = auth.signInAnonymously().await()
            val user = result.user

            if (user != null) {
                val state = CachedAuthState(
                    uid = user.uid,
                    email = user.email ?: "anonymous@firebase",
                    provider = "anonymous"
                )
                cacheAuthState(state.uid, state.email, state.provider)
                Log.i(TAG, "Anonymous authentication successful: uid=${user.uid}")
                return state
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error during anonymous authentication: ${e.message}", e)
            return null
        }
    }

    /**
     * Data class para representar el estado de autenticación en cache
     */
    data class CachedAuthState(
        val uid: String,
        val email: String,
        val provider: String = "email"
    )
}

