package com.example.triplink.data.persistence

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import kotlinx.serialization.Serializable

@Serializable
data class UserStateSnapshot(
    val users: List<Usuario> = emptyList(),
    val publications: List<PuntoInteres> = emptyList(),
    val favoritesByUser: Map<String, Set<String>> = emptyMap(),
    val badgeUnlocksByUser: Map<String, Map<String, Long>> = emptyMap()
)

