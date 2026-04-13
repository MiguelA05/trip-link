package com.example.triplink.domain.model

import com.example.triplink.domain.model.enums.DiaSemana
import kotlinx.serialization.Serializable

@Serializable
data class HorarioPuntoInteres(
    val dia: DiaSemana,
    val fechaInicio: Long,
    val fechaFin: Long
)
