package com.example.triplink.domain.model.admin

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte

data class AdminReportCase(
    val report: Reporte,
    val pointOfInterest: PuntoInteres,
    val reporterName: String,
    val acceptedReportsCount: Int = 0
)

