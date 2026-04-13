package com.example.triplink.features.badges

import android.content.Context
import com.example.triplink.R
import com.example.triplink.domain.model.Insignia

fun Insignia.requirementLabel(context: Context): String {
    val requirements = mutableListOf<String>()

    if (requiredContributions > 0) {
        requirements.add(
            context.getString(R.string.badge_requirement_contributions, requiredContributions)
        )
    }

    if (requiredVerifiedContributions > 0) {
        requirements.add(
            if (requiredContributions > 0) {
                context.getString(
                    R.string.badge_requirement_verified,
                    requiredContributions,
                    requiredVerifiedContributions
                )
            } else {
                context.getString(
                    R.string.badge_requirement_verified_only,
                    requiredVerifiedContributions
                )
            }
        )
    }

    if (requiredFavorites > 0) {
        requirements.add(
            context.getString(R.string.badge_requirement_favorites, requiredFavorites)
        )
    }

    if (requiredComments > 0) {
        requirements.add(
            context.getString(R.string.badge_requirement_comments, requiredComments)
        )
    }

    return if (requirements.isEmpty()) "" else requirements.joinToString("\n")
}

