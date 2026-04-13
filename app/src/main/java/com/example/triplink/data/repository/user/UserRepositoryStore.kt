package com.example.triplink.data.repository.user

import com.example.triplink.data.persistence.UserStateSnapshot
import com.example.triplink.data.persistence.UserStateStorage
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryStore @Inject constructor(
    private val storage: UserStateStorage
) {

    private val _users = MutableStateFlow<List<Usuario>>(emptyList())
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _publications = MutableStateFlow<List<PuntoInteres>>(emptyList())
    val publications: StateFlow<List<PuntoInteres>> = _publications.asStateFlow()

    val favorites = mutableMapOf<String, MutableSet<String>>()
    val badgeUnlocksByUser = mutableMapOf<String, MutableMap<String, Long>>()

    init {
        val snapshot = storage.loadOrDefault()
        _users.value = snapshot.users
        _publications.value = snapshot.publications
        favorites.putAll(snapshot.favoritesByUser.mapValues { (_, value) -> value.toMutableSet() })
        badgeUnlocksByUser.putAll(snapshot.badgeUnlocksByUser.mapValues { (_, value) -> value.toMutableMap() })
    }

    fun setUsers(value: List<Usuario>) {
        _users.value = value
        persistState()
    }

    fun setPublications(value: List<PuntoInteres>) {
        _publications.value = value
        persistState()
    }

    fun persistState() {
        storage.save(
            UserStateSnapshot(
                users = _users.value,
                publications = _publications.value,
                favoritesByUser = favorites.mapValues { (_, value) -> value.toSet() },
                badgeUnlocksByUser = badgeUnlocksByUser.mapValues { (_, value) -> value.toMap() }
            )
        )
    }
}


