package com.example.triplink.domain.model

import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.model.enums.RazonReporte

data class Reporte(
    //Solo se van a tratar reportar publicaciones de los puntos de interes
    val id: String,
    val reportadorId: String,
    val puntoInteresId: String,

    val motivo: RazonReporte,
    val descripcion: String?,   // opcional (cuando selecciona "OTRO")
    val estado: EstadoReporte = EstadoReporte.PENDIENTE,
    //TODO: Revisar como hacer el manejo de fechas en en Android
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaRevision: Long? = null
)
