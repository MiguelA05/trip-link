package com.example.triplink.domain.model

import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios

data class PuntoInteres(
    val id: String,
    val titulo: String,
    val informacion: String,

    val usuarioAutorId: String,

    val categoria: Categoria,
    val ubicacion: Ubicacion,

    val fotos: List<String>,
    val horario: Pair<Long, Long>? = null,

    val estado: EstadoPublicacion = EstadoPublicacion.PENDIENTE,
    val rangoPrecios: RangoPrecios? = null,
    val motivoRechazo: String? = null
)
