package com.example.triplink.features.badges

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

data class Badge(
    val name: String,
    val description: String,
    val category: String,
    val icon: ImageVector,
    val color: Color
)

class BadgesViewModel: ViewModel() {

    var selectedBadge by  mutableStateOf<Badge?>(null)

    val badges = obtainBadges()

    fun obtainBadges(): List<Badge> {
        return listOf(
            Badge(
                "Explorador Novato",
                "Has visitado 5 lugares y comenzado tu camino como explorador.",
                "Exploración",
                Icons.Default.Explore,
                Color(0xFF2196F3)
            ),
            Badge(
                "Crítico Gastronómico",
                "Has realizado 10 reseñas de restaurantes locales con fotos detalladas.",
                "Gastronomía",
                Icons.Default.Restaurant,
                Color(0xFFFF9800)
            ),
            Badge(
                "Fotógrafo Experto",
                "Has subido más de 50 fotos de alta calidad que han inspirado a otros.",
                "Fotografía",
                Icons.Default.CameraAlt,
                Color(0xFFFFC107)
            ),
            Badge(
                "Senderista Supremo",
                "Has completado todas las rutas de senderismo registradas en el departamento.",
                "Naturaleza",
                Icons.Default.Terrain,
                Color(0xFF4CAF50)
            ),
            Badge(
                "Historiador",
                "Has compartido información valiosa sobre la historia de 5 monumentos locales.",
                "Cultura",
                Icons.Default.HistoryEdu,
                Color(0xFF9C27B0)
            ),
            Badge(
                "Cafetero de Corazón",
                "Has visitado y reseñado 10 fincas cafeteras tradicionales de la región.",
                "Tradición",
                Icons.Default.Coffee,
                Color(0xFF795548)
            )
        )
    }

}