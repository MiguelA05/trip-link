package com.example.triplink.core.localization

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.Nivel
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.Rol

@Composable
fun Categoria.localizedLabel(): String = when (this) {
    Categoria.GASTRONOMIA -> stringResource(R.string.enum_categoria_gastronomia)
    Categoria.CULTURA -> stringResource(R.string.enum_categoria_cultura)
    Categoria.NATURALEZA -> stringResource(R.string.enum_categoria_naturaleza)
    Categoria.ENTRETENIMIENTO -> stringResource(R.string.enum_categoria_entretenimiento)
    Categoria.HISTORIA -> stringResource(R.string.enum_categoria_historia)
}

@Composable
fun Categoria?.localizedLabelOrAll(): String = this?.localizedLabel()
    ?: stringResource(R.string.component_category_chips_all)

@Composable
fun RangoPrecios.localizedLabel(): String = when (this) {
    RangoPrecios.GRATUITO -> stringResource(R.string.component_publication_price_range_free)
    RangoPrecios.ECONOMICO -> stringResource(R.string.component_publication_price_range_economic)
    RangoPrecios.MODERADO -> stringResource(R.string.component_publication_price_range_moderate)
    RangoPrecios.COSTOSO -> stringResource(R.string.component_publication_price_range_expensive)
}

@Composable
fun RangoPrecios?.localizedLabelOrNoPrice(): String = this?.localizedLabel()
    ?: stringResource(R.string.component_publication_price_range_no_price)

@Composable
fun DiaSemana.localizedShortLabel(): String = when (this) {
    DiaSemana.LUNES -> stringResource(R.string.enum_dia_semana_lunes_short)
    DiaSemana.MARTES -> stringResource(R.string.enum_dia_semana_martes_short)
    DiaSemana.MIERCOLES -> stringResource(R.string.enum_dia_semana_miercoles_short)
    DiaSemana.JUEVES -> stringResource(R.string.enum_dia_semana_jueves_short)
    DiaSemana.VIERNES -> stringResource(R.string.enum_dia_semana_viernes_short)
    DiaSemana.SABADO -> stringResource(R.string.enum_dia_semana_sabado_short)
    DiaSemana.DOMINGO -> stringResource(R.string.enum_dia_semana_domingo_short)
}

@Composable
fun DiaSemana.localizedFullLabel(): String = when (this) {
    DiaSemana.LUNES -> stringResource(R.string.enum_dia_semana_lunes_full)
    DiaSemana.MARTES -> stringResource(R.string.enum_dia_semana_martes_full)
    DiaSemana.MIERCOLES -> stringResource(R.string.enum_dia_semana_miercoles_full)
    DiaSemana.JUEVES -> stringResource(R.string.enum_dia_semana_jueves_full)
    DiaSemana.VIERNES -> stringResource(R.string.enum_dia_semana_viernes_full)
    DiaSemana.SABADO -> stringResource(R.string.enum_dia_semana_sabado_full)
    DiaSemana.DOMINGO -> stringResource(R.string.enum_dia_semana_domingo_full)
}

@Composable
fun Rol.localizedLabel(): String = when (this) {
    Rol.USUARIO -> stringResource(R.string.enum_rol_usuario)
    Rol.MODERADOR -> stringResource(R.string.enum_rol_moderador)
}

fun Rol.localizedLabel(context: Context): String = when (this) {
    Rol.USUARIO -> context.getString(R.string.enum_rol_usuario)
    Rol.MODERADOR -> context.getString(R.string.enum_rol_moderador)
}

@Composable
fun Nivel.localizedLabel(): String = when (this) {
    Nivel.TURISTA -> stringResource(R.string.enum_nivel_turista)
    Nivel.EXPLORADOR -> stringResource(R.string.enum_nivel_explorador)
    Nivel.AVENTURARO -> stringResource(R.string.enum_nivel_aventurero)
    Nivel.EMBAJADOR_LOCAL -> stringResource(R.string.enum_nivel_embajador_local)
}

fun Nivel.localizedLabel(context: Context): String = when (this) {
    Nivel.TURISTA -> context.getString(R.string.enum_nivel_turista)
    Nivel.EXPLORADOR -> context.getString(R.string.enum_nivel_explorador)
    Nivel.AVENTURARO -> context.getString(R.string.enum_nivel_aventurero)
    Nivel.EMBAJADOR_LOCAL -> context.getString(R.string.enum_nivel_embajador_local)
}


