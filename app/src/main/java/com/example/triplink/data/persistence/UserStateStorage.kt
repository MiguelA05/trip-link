package com.example.triplink.data.persistence

import android.content.Context
import com.example.triplink.data.seed.seedPublications
import com.example.triplink.data.seed.seedUsers
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStateStorage @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val lock = Any()
    private val file = File(context.filesDir, FILE_NAME)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    fun loadOrDefault(): UserStateSnapshot {
        synchronized(lock) {
            if (!file.exists()) {
                return defaultSnapshot()
            }

            return try {
                json.decodeFromString(UserStateSnapshot.serializer(), file.readText())
            } catch (_: SerializationException) {
                defaultSnapshot()
            } catch (_: IllegalArgumentException) {
                defaultSnapshot()
            }
        }
    }

    fun save(snapshot: UserStateSnapshot) {
        synchronized(lock) {
            file.parentFile?.mkdirs()
            file.writeText(json.encodeToString(UserStateSnapshot.serializer(), snapshot))
        }
    }

    private fun defaultSnapshot(): UserStateSnapshot = UserStateSnapshot(
        users = seedUsers(),
        publications = seedPublications()
    )

    private companion object {
        const val FILE_NAME = "triplink_user_state.json"
    }
}

