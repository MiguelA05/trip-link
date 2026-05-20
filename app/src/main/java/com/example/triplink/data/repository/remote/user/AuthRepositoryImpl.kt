package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.AuthRepository
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Usuario? {
        // Autenticar al usuario con Firebase Authentication
        val responseUser = auth.signInWithEmailAndPassword(email, password).await()
        // Obtener el UID del usuario autenticado
        val uid = responseUser.user?.uid ?: throw Exception("Usuario no encontrado")
        // Recuperar los datos del usuario desde Firestore
        return findByEmail(email)
    }

    override suspend fun findByEmail(email: String): Usuario? {
        return fetchUserById(normalize(email))
    }

    override suspend fun save(user: Usuario): Boolean {
        // Crear usuario en Firebase Authentication
        val newUser = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        val uid = newUser.user?.uid ?: throw Exception("Error al obtener el UID del usuario creado")

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

        return true
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
}

