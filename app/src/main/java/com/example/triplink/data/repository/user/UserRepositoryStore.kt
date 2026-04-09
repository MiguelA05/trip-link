package com.example.triplink.data.repository.user

import com.example.triplink.data.seed.seedPublications
import com.example.triplink.data.seed.seedUsers
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryStore @Inject constructor() {

    private val _users = MutableStateFlow(seedUsers())
    val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _publications = MutableStateFlow(seedPublications())
    val publications: StateFlow<List<PuntoInteres>> = _publications.asStateFlow()

    val favorites = mutableMapOf<String, MutableSet<String>>()
    val comments = _publications.value.associate { publication ->
        publication.id to publication.comments.toMutableList()
    }.toMutableMap()

    fun setUsers(value: List<Usuario>) {
        _users.value = value
    }

    fun setPublications(value: List<PuntoInteres>) {
        _publications.value = value
        comments.clear()
        value.forEach { publication ->
            comments[publication.id] = publication.comments.toMutableList()
        }
    }
}


