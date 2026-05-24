package com.example.triplink.features.filters

import android.util.Log
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.UbicacionFiltro
import java.util.Locale
import kotlin.math.*

private const val TAG = "PublicationFilterMatch"

/**
 * Matcher de filtros de publicación que soporta filtrado por proximidad
 * usando como referencia la ubicación registrada del usuario (si está disponible).
 */
fun publicationMatchesFilters(
    publication: PuntoInteres,
    filters: AppliedFilters,
    query: String,
    userLocation: Ubicacion? = null
): Boolean {
    val normalizedQuery = query.trim().lowercase(Locale.ROOT)
    val cityLabel = publication.ubicacion.ciudad.lowercase(Locale.ROOT)

    val categoryMatches = filters.categories.isEmpty() || filters.categories.contains(publication.categoria)
    val locationMatches = filters.locations.isEmpty() || filters.locations.any { locationMatches(publication, it, userLocation) }
    val priceMatches = filters.prices.isEmpty() || publication.rangoPrecios?.let(filters.prices::contains) == true

    val averageRating = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
    val minSelectedRating = filters.ratings.minOrNull()
    val ratingMatches = minSelectedRating == null || averageRating >= minSelectedRating

    val queryMatches = normalizedQuery.isBlank() ||
        publication.titulo.lowercase(Locale.ROOT).contains(normalizedQuery) ||
        cityLabel.contains(normalizedQuery) ||
        publication.categoria.label.lowercase(Locale.ROOT).contains(normalizedQuery)

    return categoryMatches && locationMatches && priceMatches && ratingMatches && queryMatches
}

private fun locationMatches(publication: PuntoInteres, location: UbicacionFiltro, userLocation: Ubicacion?): Boolean {
    // Radios aproximados en kilómetros para Colombia
    val nearbyKm = 10.0        // "Cercanos" ~ 10 km
    val cityKm = 50.0          // "En la ciudad" ~ 50 km
    val departmentKm = 200.0   // "En el departamento" ~ 200 km

    // Si no hay ubicación de referencia, sólo el filtro PAIS coincide por defecto
    if (userLocation == null) {
        return when (location) {
            UbicacionFiltro.CERCANOS -> false
            UbicacionFiltro.CIUDAD -> false
            UbicacionFiltro.DEPARTAMENTO -> false
            UbicacionFiltro.PAIS -> true
        }
    }

    val distKm = distanceKm(userLocation, publication.ubicacion)

    val result = when (location) {
        UbicacionFiltro.CERCANOS -> distKm <= nearbyKm
        UbicacionFiltro.CIUDAD -> distKm <= cityKm
        UbicacionFiltro.DEPARTAMENTO -> distKm <= departmentKm
        UbicacionFiltro.PAIS -> true // App solo opera en Colombia
    }
    return result
}

private fun distanceKm(from: Ubicacion, to: Ubicacion): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(to.latitud - from.latitud)
    val dLon = Math.toRadians(to.longitud - from.longitud)

    val a = sin(dLat / 2).pow(2.0) +
        cos(Math.toRadians(from.latitud)) *
        cos(Math.toRadians(to.latitud)) *
        sin(dLon / 2).pow(2.0)

    return 2 * earthRadiusKm * asin(sqrt(a.coerceIn(0.0, 1.0)))
}

