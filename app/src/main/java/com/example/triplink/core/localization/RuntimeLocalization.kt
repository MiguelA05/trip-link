package com.example.triplink.core.localization

import android.content.Context
import com.example.triplink.R
import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.RazonReporte

fun Categoria.localizedLabel(context: Context): String = when (this) {
    Categoria.GASTRONOMIA -> context.getString(R.string.enum_categoria_gastronomia)
    Categoria.CULTURA -> context.getString(R.string.enum_categoria_cultura)
    Categoria.NATURALEZA -> context.getString(R.string.enum_categoria_naturaleza)
    Categoria.ENTRETENIMIENTO -> context.getString(R.string.enum_categoria_entretenimiento)
    Categoria.HISTORIA -> context.getString(R.string.enum_categoria_historia)
}

fun RangoPrecios?.localizedLabelOrNoPrice(context: Context): String = when (this) {
    null -> context.getString(R.string.component_publication_price_range_no_price)
    RangoPrecios.GRATUITO -> context.getString(R.string.component_publication_price_range_free)
    RangoPrecios.ECONOMICO -> context.getString(R.string.component_publication_price_range_economic)
    RangoPrecios.MODERADO -> context.getString(R.string.component_publication_price_range_moderate)
    RangoPrecios.COSTOSO -> context.getString(R.string.component_publication_price_range_expensive)
}

fun RazonReporte.localizedLabel(context: Context): String = when (this) {
    RazonReporte.SPAM -> context.getString(R.string.enum_razon_reporte_ubicacion_erronea)
    RazonReporte.CONTENIDO_INAPROPIADO -> context.getString(R.string.enum_razon_reporte_contenido_inapropiado)
    RazonReporte.INFORMACION_FALSA -> context.getString(R.string.enum_razon_reporte_informacion_falsa)
    RazonReporte.LENGUAJE_OFENSIVO -> context.getString(R.string.enum_razon_reporte_lenguaje_ofensivo)
    RazonReporte.VIOLENCIA -> context.getString(R.string.enum_razon_reporte_violencia)
    RazonReporte.OTRO -> context.getString(R.string.enum_razon_reporte_otro)
}

fun Insignia.localizedName(context: Context): String = context.getString(nameResId)

fun Insignia.localizedDescription(context: Context): String = context.getString(descriptionResId)

fun Long.localizedRelativeTimeLabel(context: Context, now: Long = System.currentTimeMillis()): String {
    val delta = (now - this).coerceAtLeast(0L)
    val minutes = (delta / 60_000L).toInt()
    val hours = (minutes / 60)
    val days = (hours / 24)
    return when {
        minutes < 1 -> context.getString(R.string.component_publication_card_time_just_now)
        minutes < 60 -> context.resources.getQuantityString(
            R.plurals.component_publication_card_time_minutes_ago,
            minutes,
            minutes
        )
        hours < 24 -> context.resources.getQuantityString(
            R.plurals.component_publication_card_time_hours_ago,
            hours,
            hours
        )
        days < 7 -> context.resources.getQuantityString(
            R.plurals.component_publication_card_time_days_ago,
            days,
            days
        )
        else -> {
            val weeks = days / 7
            context.resources.getQuantityString(
                R.plurals.component_publication_card_time_weeks_ago,
                weeks,
                weeks
            )
        }
    }
}

