package com.example.triplink.data.repository.remote

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.HorarioPuntoInteres
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.model.enums.Nivel
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.domain.model.enums.Rol
import com.google.firebase.firestore.IgnoreExtraProperties

internal const val USERS_COLLECTION = "users"
internal const val PUBLICATIONS_COLLECTION = "publications"
internal const val FAVORITES_COLLECTION = "favorites"
internal const val BADGE_UNLOCKS_COLLECTION = "badgeUnlocks"
internal const val ACTIVE_FIELD = "activo"
internal const val FIREBASE_UID_FIELD = "firebaseUid"

@IgnoreExtraProperties
data class FirestoreUbicacionDto(
    var latitud: Double = 0.0,
    var longitud: Double = 0.0,
    var ciudad: String = ""
)

@IgnoreExtraProperties
data class FirestoreHorarioPuntoInteresDto(
    var dia: String = DiaSemana.LUNES.name,
    var fechaInicio: Long = 0L,
    var fechaFin: Long = 0L
)

@IgnoreExtraProperties
data class FirestoreComentarioDto(
    var id: String = "",
    var usuarioId: String = "",
    var puntoInteresId: String = "",
    var userName: String = "",
    var date: Long = 0L,
    var rating: Float = 0f,
    var text: String = ""
)

@IgnoreExtraProperties
data class FirestoreReporteDto(
    var id: String = "",
    var reportadorId: String = "",
    var puntoInteresId: String = "",
    var motivo: String = EstadoReporte.PENDIENTE.name,
    var descripcion: String? = null,
    var estado: String = EstadoReporte.PENDIENTE.name,
    var fechaCreacion: Long = 0L,
    var fechaRevision: Long? = null
)

@IgnoreExtraProperties
data class FirestorePuntoInteresDto(
    var id: String = "",
    var titulo: String = "",
    var informacion: String = "",
    var usuarioAutorId: String = "",
    var categoria: String = Categoria.NATURALEZA.name,
    var ubicacion: FirestoreUbicacionDto = FirestoreUbicacionDto(),
    var fotos: List<String> = emptyList(),
    var horarios: List<FirestoreHorarioPuntoInteresDto> = emptyList(),
    var comments: List<FirestoreComentarioDto> = emptyList(),
    var reportes: List<FirestoreReporteDto> = emptyList(),
    var commentCount: Int = 0,
    var favoriteCount: Int = 0,
    var fechaCreacion: Long = 0L,
    var estado: String = EstadoPublicacion.PENDIENTE.name,
    var rangoPrecios: String? = null,
    var motivoRechazo: String? = null
)

@IgnoreExtraProperties
data class FirestoreUsuarioDto(
    var email: String = "",
    var nombre: String = "",
    var password: String = "",
    var puntos: Int = 0,
    var ubicacion: FirestoreUbicacionDto? = null,
    var nivel: String = Nivel.TURISTA.name,
    var rol: String = Rol.USUARIO.name,
    var telefono: String = "",
    var direccion: String = "",
    var departamento: String = "",
    var ubicacionExactaActiva: Boolean = false,
    var insignias: List<String> = emptyList(),
    var activo: Boolean = true,
    var firebaseUid: String? = null,
    var fcmToken: String? = null
)

@IgnoreExtraProperties
data class FirestoreFavoriteDto(
    var publicationId: String = "",
    var createdAt: Long = 0L
)

@IgnoreExtraProperties
data class FirestoreBadgeUnlockDto(
    var badgeId: String = "",
    var unlockedAtMillis: Long = 0L
)

internal fun Usuario.toFirestoreDto(): FirestoreUsuarioDto = FirestoreUsuarioDto(
    email = email.trim().lowercase(),
    nombre = nombre,
    password = password,
    puntos = puntos,
    ubicacion = ubicacion?.toFirestoreDto(),
    nivel = nivel.name,
    rol = rol.name,
    telefono = telefono,
    direccion = direccion,
    departamento = departamento,
    ubicacionExactaActiva = ubicacionExactaActiva,
    insignias = insignias,
    activo = activo,
    firebaseUid = firebaseUid,
    fcmToken = fcmToken
)

internal fun FirestoreUsuarioDto.toDomain(): Usuario = Usuario(
    email = email.trim().lowercase(),
    nombre = nombre,
    password = password,
    puntos = puntos,
    ubicacion = ubicacion?.toDomain(),
    nivel = nivel.toEnumOrDefault(Nivel.TURISTA),
    rol = rol.toEnumOrDefault(Rol.USUARIO),
    telefono = telefono,
    direccion = direccion,
    departamento = departamento,
    ubicacionExactaActiva = ubicacionExactaActiva,
    insignias = insignias,
    activo = activo,
    firebaseUid = firebaseUid,
    fcmToken = fcmToken
)

internal fun Ubicacion.toFirestoreDto(): FirestoreUbicacionDto = FirestoreUbicacionDto(
    latitud = latitud,
    longitud = longitud,
    ciudad = ciudad
)

internal fun FirestoreUbicacionDto.toDomain(): Ubicacion = Ubicacion(
    latitud = latitud,
    longitud = longitud,
    ciudad = ciudad
)

internal fun HorarioPuntoInteres.toFirestoreDto(): FirestoreHorarioPuntoInteresDto = FirestoreHorarioPuntoInteresDto(
    dia = dia.name,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin
)

internal fun FirestoreHorarioPuntoInteresDto.toDomain(): HorarioPuntoInteres = HorarioPuntoInteres(
    dia = dia.toEnumOrDefault(DiaSemana.LUNES),
    fechaInicio = fechaInicio,
    fechaFin = fechaFin
)

internal fun Comentario.toFirestoreDto(): FirestoreComentarioDto = FirestoreComentarioDto(
    id = id,
    usuarioId = usuarioId,
    puntoInteresId = puntoInteresId,
    userName = userName,
    date = date,
    rating = rating,
    text = text
)

internal fun FirestoreComentarioDto.toDomain(): Comentario = Comentario(
    id = id,
    usuarioId = usuarioId,
    puntoInteresId = puntoInteresId,
    userName = userName,
    date = date,
    rating = rating,
    text = text
)

internal fun Reporte.toFirestoreDto(): FirestoreReporteDto = FirestoreReporteDto(
    id = id,
    reportadorId = reportadorId,
    puntoInteresId = puntoInteresId,
    motivo = motivo.name,
    descripcion = descripcion,
    estado = estado.name,
    fechaCreacion = fechaCreacion,
    fechaRevision = fechaRevision
)

internal fun FirestoreReporteDto.toDomain(): Reporte = Reporte(
    id = id,
    reportadorId = reportadorId,
    puntoInteresId = puntoInteresId,
    motivo = motivo.toEnumOrDefault(RazonReporte.OTRO),
    descripcion = descripcion,
    estado = estado.toEnumOrDefault(EstadoReporte.PENDIENTE),
    fechaCreacion = fechaCreacion.takeIf { it > 0L } ?: System.currentTimeMillis(),
    fechaRevision = fechaRevision
)

internal fun PuntoInteres.toFirestoreDto(): FirestorePuntoInteresDto = FirestorePuntoInteresDto(
    id = id,
    titulo = titulo,
    informacion = informacion,
    usuarioAutorId = usuarioAutorId.trim().lowercase(),
    categoria = categoria.name,
    ubicacion = ubicacion.toFirestoreDto(),
    fotos = fotos,
    horarios = horarios.map { it.toFirestoreDto() },
    comments = comments.map { it.toFirestoreDto() },
    reportes = reportes.map { it.toFirestoreDto() },
    commentCount = commentCount,
    favoriteCount = favoriteCount,
    fechaCreacion = fechaCreacion,
    estado = estado.name,
    rangoPrecios = rangoPrecios?.name,
    motivoRechazo = motivoRechazo
)

internal fun FirestorePuntoInteresDto.toDomain(): PuntoInteres = PuntoInteres(
    id = id,
    titulo = titulo,
    informacion = informacion,
    usuarioAutorId = usuarioAutorId.trim().lowercase(),
    categoria = categoria.toEnumOrDefault(Categoria.NATURALEZA),
    ubicacion = ubicacion.toDomain(),
    fotos = fotos,
    horarios = horarios.map { it.toDomain() },
    comments = comments.map { it.toDomain() },
    reportes = reportes.map { it.toDomain() },
    commentCount = commentCount.takeIf { it > 0 } ?: comments.size,
    favoriteCount = favoriteCount,
    fechaCreacion = fechaCreacion.takeIf { it > 0L } ?: System.currentTimeMillis(),
    estado = estado.toEnumOrDefault(EstadoPublicacion.PENDIENTE),
    rangoPrecios = rangoPrecios?.toEnumOrNull<RangoPrecios>(),
    motivoRechazo = motivoRechazo
)

internal fun FirestoreFavoriteDto.toDomain(): String = publicationId

internal fun FirestoreBadgeUnlockDto.toDomain(): Pair<String, Long> = badgeId to unlockedAtMillis

internal inline fun <reified T : Enum<T>> String?.toEnumOrDefault(defaultValue: T): T {
    return enumValues<T>().firstOrNull { it.name.equals(this?.trim(), ignoreCase = true) } ?: defaultValue
}

internal inline fun <reified T : Enum<T>> String?.toEnumOrNull(): T? {
    return enumValues<T>().firstOrNull { it.name.equals(this?.trim(), ignoreCase = true) }
}



