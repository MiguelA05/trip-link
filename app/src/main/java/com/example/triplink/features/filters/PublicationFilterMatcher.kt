package com.example.triplink.features.filters

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.UbicacionFiltro
import java.util.Locale
import kotlin.math.abs

private const val ARMENIA_LAT = 4.5339
private const val ARMENIA_LNG = -75.6811
private const val NEARBY_DELTA = 0.25

fun publicationMatchesFilters(
    publication: PuntoInteres,
    filters: AppliedFilters,
    query: String
): Boolean {
    val normalizedQuery = query.trim().lowercase(Locale.ROOT)
    val cityLabel = publication.ubicacion.ciudad.lowercase(Locale.ROOT)

    val categoryMatches = filters.categories.isEmpty() || filters.categories.contains(publication.categoria)
    val locationMatches = filters.locations.isEmpty() || filters.locations.any { locationMatches(publication, it) }
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

private fun locationMatches(publication: PuntoInteres, location: UbicacionFiltro): Boolean {
    val cityLabel = publication.ubicacion.ciudad.lowercase(Locale.ROOT)
    return when (location) {
        UbicacionFiltro.CERCANOS -> {
            abs(publication.ubicacion.latitud - ARMENIA_LAT) <= NEARBY_DELTA &&
                abs(publication.ubicacion.longitud - ARMENIA_LNG) <= NEARBY_DELTA
        }

        UbicacionFiltro.CIUDAD -> cityLabel.contains("armenia")
        UbicacionFiltro.DEPARTAMENTO -> cityLabel.contains("quindio")
        UbicacionFiltro.PAIS -> true
    }
}

