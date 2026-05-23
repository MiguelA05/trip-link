package com.example.triplink.data.repository.remote.user

import android.util.Log
import com.example.triplink.core.utils.FirebaseAuthPersistenceManager
import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val authPersistence: FirebaseAuthPersistenceManager
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun login(email: String, password: String): Usuario? {
        try {
            Log.d(TAG, "Attempting login for email: $email")
            // Autenticar al usuario con Firebase Authentication
            val responseUser = auth.signInWithEmailAndPassword(email, password).await()
            // Obtener el UID del usuario autenticado
            val uid = responseUser.user?.uid ?: throw Exception("Usuario no encontrado")
            Log.d(TAG, "Firebase Auth login successful, uid: $uid")

            // Cachear el estado de autenticación
            authPersistence.cacheAuthState(uid = uid, email = email, provider = "email")

            // Recuperar los datos del usuario desde Firestore
            return findByEmail(email)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun findByEmail(email: String): Usuario? {
        return fetchUserById(normalize(email))
    }

    override suspend fun save(user: Usuario): Boolean {
        try {
            Log.d(TAG, "Attempting to register user with email: ${user.email}")
            // Crear usuario en Firebase Authentication
            val newUser = auth.createUserWithEmailAndPassword(user.email, user.password).await()
            val uid = newUser.user?.uid ?: throw Exception("Error al obtener el UID del usuario creado")
            Log.d(TAG, "Firebase Auth register successful, uid: $uid")

            // Se hace una copia del usuario con el UID generado por Firebase Authentication
            val userCopy = user.copy(
                firebaseUid = uid,
                password = "" // No guardar la contraseña en Firestore
            )

            // Guardar los datos del usuario en Firestore
            firestore.collection(USERS_COLLECTION)
                .document(normalize(user.email))
                .set(userCopy.toFirestoreDto())
                .await()

            // Cachear el estado de autenticación
            authPersistence.cacheAuthState(uid = uid, email = user.email, provider = "email")
            Log.d(TAG, "User registration and caching completed successfully")

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}", e)
            throw e
        }
    }

    private suspend fun fetchUserById(userId: String): Usuario? {
        val byEmailDoc = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
        byEmailDoc.toObject(FirestoreUsuarioDto::class.java)?.toDomain()?.let { return it }

        val byFirebaseUid = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("firebaseUid", userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
        return byFirebaseUid?.toObject(FirestoreUsuarioDto::class.java)?.toDomain()
    }

    private fun normalize(value: String): String = value.trim().lowercase()


    override suspend fun sendPasswordResetEmail(
        email: String
    ) {
        auth.setLanguageCode("es")
        /*
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://triplink-97bf5.firebaseapp.com/reset-password")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.example.triplink",
                true,
                null
            )
            .setHandleCodeInApp(true)
            .build()*/

        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun verifyPasswordResetCode(oobCode: String): String {
        return auth.verifyPasswordResetCode(oobCode).await()
    }

    override suspend fun confirmPasswordReset(
        oobCode: String,
        newPassword: String
    ) {
        auth.confirmPasswordReset(oobCode, newPassword).await()
    }

    override suspend fun updatePassword(currentPassword: String, newPassword: String): Boolean {
        val currentUser = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val email = currentUser.email ?: throw Exception("Email del usuario no disponible")

        // Reautenticar con credenciales de email para operaciones sensibles
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        currentUser.reauthenticate(credential).await()

        // Actualizar la contraseña
        currentUser.updatePassword(newPassword).await()
        return true
    }
}

