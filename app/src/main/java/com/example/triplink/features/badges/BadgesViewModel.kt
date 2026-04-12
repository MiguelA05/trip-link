package com.example.triplink.features.badges

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.triplink.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class Badge(
    val name: String,
    val description: String,
    val category: String,
    val icon: ImageVector,
    val colorRole: BadgeColorRole
)

enum class BadgeColorRole {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    ERROR
}

@HiltViewModel
 class BadgesViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {

    var selectedBadge by  mutableStateOf<Badge?>(null)

    val badges = obtainBadges()

    fun obtainBadges(): List<Badge> {
        return listOf(
            Badge(
                appContext.getString(R.string.vm_badges_item_1_name),
                appContext.getString(R.string.vm_badges_item_1_description),
                appContext.getString(R.string.vm_badges_item_1_category),
                Icons.Default.Explore,
                BadgeColorRole.PRIMARY
            ),
            Badge(
                appContext.getString(R.string.vm_badges_item_2_name),
                appContext.getString(R.string.vm_badges_item_2_description),
                appContext.getString(R.string.vm_badges_item_2_category),
                Icons.Default.Restaurant,
                BadgeColorRole.TERTIARY
            ),
            Badge(
                appContext.getString(R.string.vm_badges_item_3_name),
                appContext.getString(R.string.vm_badges_item_3_description),
                appContext.getString(R.string.vm_badges_item_3_category),
                Icons.Default.CameraAlt,
                BadgeColorRole.TERTIARY
            ),
            Badge(
                appContext.getString(R.string.vm_badges_item_4_name),
                appContext.getString(R.string.vm_badges_item_4_description),
                appContext.getString(R.string.vm_badges_item_4_category),
                Icons.Default.Terrain,
                BadgeColorRole.SECONDARY
            ),
            Badge(
                appContext.getString(R.string.vm_badges_item_5_name),
                appContext.getString(R.string.vm_badges_item_5_description),
                appContext.getString(R.string.vm_badges_item_5_category),
                Icons.Default.HistoryEdu,
                BadgeColorRole.PRIMARY
            ),
            Badge(
                appContext.getString(R.string.vm_badges_item_6_name),
                appContext.getString(R.string.vm_badges_item_6_description),
                appContext.getString(R.string.vm_badges_item_6_category),
                Icons.Default.Coffee,
                BadgeColorRole.ERROR
            )
        )
    }

}