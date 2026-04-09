package com.example.triplink.data.seed

object GeoSeedData {
    val departments = listOf("Quindio", "Antioquia", "Valle del Cauca")

    val citiesByDepartment = mapOf(
        "Quindio" to listOf("Armenia", "Calarca", "Circasia", "Filandia", "Salento"),
        "Antioquia" to listOf("Medellin", "Envigado", "Itagui", "Rionegro"),
        "Valle del Cauca" to listOf("Cali", "Palmira", "Buga", "Tulua")
    )
}

