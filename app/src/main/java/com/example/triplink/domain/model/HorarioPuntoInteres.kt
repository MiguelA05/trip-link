package com.example.triplink.domain.model

import com.example.triplink.domain.model.enums.DiaSemana

data class HorarioPuntoInteres(
    val dia: DiaSemana,
    val fechaInicio: Long,
    val fechaFin: Long
)

