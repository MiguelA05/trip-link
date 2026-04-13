package com.example.triplink.data.seed

import com.example.triplink.R
import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.InsigniaIconKey

fun seedBadges(): List<Insignia> = listOf(
    // Insignia por crear primera publicación
    Insignia(
        id = "first_step",
        puntos = 20,
        requiredContributions = 1,
        nameResId = R.string.badge_first_step_name,
        descriptionResId = R.string.badge_first_step_description,
        iconKey = InsigniaIconKey.SPARK
    ),
    // Insignia por crear 2 publicaciones
    Insignia(
        id = "route_starter",
        puntos = 35,
        requiredContributions = 2,
        nameResId = R.string.badge_route_starter_name,
        descriptionResId = R.string.badge_route_starter_description,
        iconKey = InsigniaIconKey.COMPASS
    ),
    // Insignia por recibir 3 likes/favoritos
    Insignia(
        id = "photo_hunter",
        puntos = 50,
        requiredFavorites = 3,
        nameResId = R.string.badge_photo_hunter_name,
        descriptionResId = R.string.badge_photo_hunter_description,
        iconKey = InsigniaIconKey.CAMERA
    ),
    // Insignia por recibir 2 comentarios
    Insignia(
        id = "local_taster",
        puntos = 70,
        requiredComments = 2,
        nameResId = R.string.badge_local_taster_name,
        descriptionResId = R.string.badge_local_taster_description,
        iconKey = InsigniaIconKey.FOOD
    ),
    // Insignia por tener 5 publicaciones verificadas
    Insignia(
        id = "trail_keeper",
        puntos = 90,
        requiredVerifiedContributions = 5,
        nameResId = R.string.badge_trail_keeper_name,
        descriptionResId = R.string.badge_trail_keeper_description,
        iconKey = InsigniaIconKey.PATH
    ),
    // Insignia por tener 3 publicaciones verificadas y 10 favoritos totales
    Insignia(
        id = "verified_voice",
        puntos = 120,
        requiredVerifiedContributions = 3,
        requiredFavorites = 10,
        nameResId = R.string.badge_verified_voice_name,
        descriptionResId = R.string.badge_verified_voice_description,
        iconKey = InsigniaIconKey.TROPHY
    )
)



