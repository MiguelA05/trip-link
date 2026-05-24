package com.example.triplink

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.features.filters.AppliedFilters
import com.example.triplink.features.filters.publicationMatchesFilters
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterFunctionalTest {

    private val userLocation = Ubicacion(4.4687891, -75.6491181, "Armenia") // Armenia, Quindío

    private val basePublication = PuntoInteres(
        id = "1",
        titulo = "Restaurante Prueba",
        informacion = "Info",
        usuarioAutorId = "user@test.com",
        categoria = Categoria.GASTRONOMIA,
        ubicacion = Ubicacion(4.469, -75.649, "Armenia"), // Muy cerca del usuario (~0.1km)
        fotos = emptyList(),
        horarios = emptyList(),
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.ECONOMICO,
        comments = listOf(
            Comentario("c1", "u1", "1", "User1", 0L, 5f, "Excelente"),
            Comentario("c2", "u2", "1", "User2", 0L, 4f, "Bueno")
        ) // Promedio: 4.5
    )

    @Test
    fun `Filter by Category - Success`() {
        val filters = AppliedFilters(categories = setOf(Categoria.GASTRONOMIA))
        assertTrue("Debe coincidir con la categoría GASTRONOMIA", 
            publicationMatchesFilters(basePublication, filters, ""))
        
        val wrongFilters = AppliedFilters(categories = setOf(Categoria.NATURALEZA))
        assertFalse("No debe coincidir con NATURALEZA", 
            publicationMatchesFilters(basePublication, wrongFilters, ""))
    }

    @Test
    fun `Filter by Price Range - Success`() {
        val filters = AppliedFilters(prices = setOf(RangoPrecios.ECONOMICO))
        assertTrue("Debe coincidir con precio ECONOMICO", 
            publicationMatchesFilters(basePublication, filters, ""))
        
        val expensiveFilters = AppliedFilters(prices = setOf(RangoPrecios.COSTOSO))
        assertFalse("No debe coincidir con precio COSTOSO", 
            publicationMatchesFilters(basePublication, expensiveFilters, ""))
    }

    @Test
    fun `Filter by Minimum Rating - Success`() {
        val filters = AppliedFilters(ratings = setOf(4)) // Promedio es 4.5
        assertTrue("4.5 >= 4 debe ser verdadero", 
            publicationMatchesFilters(basePublication, filters, ""))
        
        val highFilters = AppliedFilters(ratings = setOf(5))
        assertFalse("4.5 >= 5 debe ser falso", 
            publicationMatchesFilters(basePublication, highFilters, ""))
    }

    @Test
    fun `Filter by Proximity (Nearby) - Success`() {
        val filters = AppliedFilters(locations = setOf(UbicacionFiltro.CERCANOS))
        
        // Caso: Cerca (Armenia)
        val resultNear = publicationMatchesFilters(basePublication, filters, "", userLocation)
        assertTrue("Debe coincidir por estar a menos de 10km", resultNear)
        
        // Caso: Lejos (Bogotá ~180km)
        val farPublication = basePublication.copy(
            ubicacion = Ubicacion(4.711, -74.072, "Bogotá")
        )
        val resultFar = publicationMatchesFilters(farPublication, filters, "", userLocation)
        assertFalse("No debe coincidir con CERCANOS (>10km)", resultFar)
    }

    @Test
    fun `Multiple Combined Filters - Success`() {
        val combinedFilters = AppliedFilters(
            categories = setOf(Categoria.GASTRONOMIA),
            prices = setOf(RangoPrecios.ECONOMICO),
            ratings = setOf(4)
        )
        assertTrue("Debe cumplir todos los filtros simultáneamente", 
            publicationMatchesFilters(basePublication, combinedFilters, ""))
    }
}
