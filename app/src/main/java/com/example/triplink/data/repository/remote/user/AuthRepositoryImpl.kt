package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Usuario? {
        val user = findByEmail(email) ?: return null
        return if (user.password == password && user.activo) user else null
    }

    override suspend fun findByEmail(email: String): Usuario? {
        return fetchUserById(normalize(email))
    }

    override suspend fun save(user: Usuario): Boolean {
        val normalizedEmail = normalize(user.email)
        if (fetchUserById(normalizedEmail) != null) return false

        firestore.collection(USERS_COLLECTION)
            .document(normalizedEmail)
            .set(user.copy(email = normalizedEmail).toFirestoreDto())
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
}

