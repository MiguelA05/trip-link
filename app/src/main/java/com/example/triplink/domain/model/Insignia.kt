package com.example.triplink.domain.model

import androidx.annotation.StringRes

data class Insignia(
    val id: String,
    val puntos: Int,
    val requiredContributions: Int = 0,
    val requiredVerifiedContributions: Int = 0,
    val requiredFavorites: Int = 0,
    val requiredComments: Int = 0,
    @StringRes val nameResId: Int,
    @StringRes val descriptionResId: Int,
    val iconKey: InsigniaIconKey
)

enum class InsigniaIconKey {
    SPARK,
    COMPASS,
    CAMERA,
    FOOD,
    PATH,
    TROPHY
}

data class UserInsigniaProgress(
    val insignia: Insignia,
    val unlockedAtMillis: Long? = null
) {
    val isUnlocked: Boolean get() = unlockedAtMillis != null
}

