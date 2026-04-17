# TripLink - Guía turística colaborativa

Aplicación móvil Android desarrollada con Kotlin + Jetpack Compose para publicar, descubrir y moderar puntos de interés turístico creados por la comunidad.

## 1) Descripción general del proyecto final

Este proyecto corresponde al **proyecto final de Desarrollo de Aplicaciones Móviles**. Aunque cada grupo trabaja una temática distinta, todos comparten:

- una estructura base común,
- requisitos funcionales compartidos (roles, publicaciones, moderación, interacción social),
- y criterios técnicos mínimos de implementación.

TripLink implementa estos lineamientos bajo la **Temática 3: Guía turística colaborativa**.

## 2) Adaptación a la Temática 3

### Contexto

Turistas y habitantes de una ciudad suelen desconocer lugares valiosos fuera de las guías tradicionales. TripLink busca cerrar esa brecha con conocimiento local colaborativo.

### Entidad principal

La entidad central es `PuntoInteres`, que modela una publicación turística con información clave como:

- título y descripción,
- categoría (`Categoria`),
- ubicación (`latitud`/`longitud`),
- imágenes,
- horarios sugeridos (`HorarioPuntoInteres`),
- rango de precio estimado (`RangoPrecios`).

### Categorías de la temática

Modeladas con `enum class Categoria`:

- Gastronomía
- Cultura
- Naturaleza
- Entretenimiento
- Historia

### Niveles de usuario

Modelados con `enum class Nivel`:

- Turista
- Explorador
- Aventurero
- Embajador Local

### Roles

Modelados con `enum class Rol`:

- `USUARIO`
- `MODERADOR`

## 3) Requisitos comunes del curso (referencia)

Las siguientes capacidades forman parte del alcance común solicitado para todas las temáticas:

- **Moderador:** autenticación, verificación/rechazo de publicaciones con motivo, y gestión de estados de publicaciones/reportes.
- **Usuario:** registro/login, feed en lista/mapa con filtros, crear/editar/borrar publicaciones, comentar, votar relevancia, ver detalle completo, editar perfil y eliminar cuenta.
- **Gamificación:** estadísticas, sistema de puntos, niveles e insignias.
- **Recuperación de contraseña:** flujo por correo electrónico.
- **Notificaciones:** alertas relevantes según ubicación.
- **Necesidad adicional no trivial:** definida por cada grupo.

> En TripLink, la necesidad no trivial priorizada es el **flujo integral de reportes** (usuario + moderador + regla de visibilidad).

## 4) Funcionalidad no trivial implementada: flujo de reportes

### Objetivo funcional

Permitir que la comunidad reporte publicaciones potencialmente problemáticas y que moderación decida sobre cada caso. Si una publicación acumula suficientes reportes aceptados, deja de mostrarse públicamente.

### Flujo de usuario

Desde detalle de publicación (`features/publicationDetails/PublicationDetailsViewModel.kt`):

1. El usuario selecciona motivo (`RazonReporte`) y, si corresponde, descripción.
2. Se valida que exista sesión.
3. Se evita duplicidad: un mismo usuario no puede reportar dos veces la misma publicación.
4. Se crea `Reporte` con estado inicial `PENDIENTE`.
5. El reporte se agrega a la publicación vía `ReportRepository.submitReport(...)`.

### Flujo de moderador

Desde módulo admin (`features/admin/reports/*` + `data/repository/admin/ReportRepositoryImpl.kt`):

1. Se listan casos pendientes (`EstadoReporte.PENDIENTE`).
2. El moderador puede **confirmar** (`APROBADO`) o **invalidar** (`RECHAZADO`) cada reporte.
3. Al confirmar, se incrementa el conteo de reportes aprobados de la publicación.

### Regla de visibilidad por umbral

En `ReportRepositoryImpl` existe un umbral `acceptedReportThreshold = 3`.

- Si una publicación alcanza **3 o más reportes aprobados**, se elimina del repositorio (`deletePublicationById(...)`).
- Efecto práctico: la publicación deja de aparecer en los feeds públicos del usuario.

## 5) Requisitos técnicos del curso (referencia)

Entre los requisitos técnicos globales solicitados están:

- Jetpack Compose con Kotlin.
- Publicaciones con título, categoría, descripción, ubicación e imagen.
- Uso de mapa para coordenadas de usuario/publicaciones.
- Recuperación de contraseña por enlace a correo.
- Notificaciones relevantes por ubicación.
- Almacenamiento de imágenes en servicio externo.
- Repositorio GitHub con contribución del equipo.
- Moderadores precargados.
- Uso de Firebase (u otro servicio) para auth/datos/notificaciones sin requerir backend propio.

## 6) Estado actual de implementación en este repositorio

### Implementado y visible en código

- Navegación por rol (`USUARIO` / `MODERADOR`).
- Flujo de autenticación (login, registro, recuperación).
- Publicaciones con categorías, comentarios, favoritos y reportes.
- Módulo de moderación y módulo de reportes para administración.
- Regla no trivial de umbral de reportes aprobados.
- Sistema de niveles e insignias en la experiencia de usuario.

### Observaciones de alcance

- Algunas capacidades del enunciado general pueden estar en desarrollo o con implementación local/mock (según módulos `data/*` y `seed` actuales).
- Este README diferencia explícitamente el **alcance académico esperado** del **estado actual implementado** para evitar ambigüedades.

## 7) Arquitectura y stack del proyecto

- Módulo Android único: `:app`.
- UI: Jetpack Compose + Material 3.
- Navegación: Navigation Compose con rutas tipadas y Kotlin Serialization.
- DI: Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `hiltViewModel()`).
- Imágenes remotas: Coil 3.

### Versiones relevantes

- Kotlin `2.2.21`
- AGP `9.0.1`
- Compose BOM `2024.09.00`
- Navigation Compose `2.9.6`
- Hilt Android `2.59.2`
- DataStore Preferences `1.2.0`
- `compileSdk = 36` (minor API 1), `minSdk = 28`

## 8) Estructura de carpetas (resumen)

```text
trip-link/
|- app/
|  |- src/main/java/com/example/triplink/
|  |  |- core/
|  |  |- data/
|  |  |- domain/
|  |  |- features/
|- gradle/libs.versions.toml
|- app/build.gradle.kts
```

## 9) Ejecución y pruebas

Desde la raíz del proyecto:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

En Windows:

```bat
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:testDebugUnitTest
./gradlew.bat :app:connectedDebugAndroidTest
```


