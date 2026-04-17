# TripLink - Guia turistica colaborativa

TripLink es una app Android desarrollada con Kotlin y Jetpack Compose para descubrir y compartir puntos de interes turistico creados por la comunidad.

## Tematica del proyecto

**Tematica 3: Guia turistica colaborativa**

- **Contexto:** turistas y habitantes suelen desconocer lugares interesantes fuera de las guias tradicionales.
- **Problema:** falta una plataforma comunitaria para descubrir y validar informacion local.
- **Entidad principal:** `PuntoInteres`.

## Objetivo

Construir una experiencia colaborativa donde los usuarios puedan:

- descubrir lugares recomendados por la comunidad,
- publicar nuevos puntos de interes,
- compartir opiniones y reportar contenido,
- y apoyar la calidad de la informacion mediante moderacion.

## Categorias de puntos de interes

El dominio maneja categorias con `enum class Categoria`:

- Gastronomia (restaurantes, cafes, comida callejera)
- Cultura (museos, monumentos, arte urbano)
- Naturaleza (parques, miradores, senderos)
- Entretenimiento (bares, discotecas, actividades)
- Historia (sitios historicos, arquitectura)

## Particularidades funcionales

En esta tematica, cada punto de interes considera:

- horario de atencion sugerido (`HorarioPuntoInteres`),
- rango de precio estimado (`RangoPrecios`: gratuito, economico, moderado, costoso),
- interacciones de comunidad (comentarios, favoritos, reportes),
- flujo de moderacion para validar existencia y precision de la informacion.

## Niveles y roles de usuario

### Niveles

Se contemplan niveles de progresion con `enum class Nivel`:

- Turista
- Explorador
- Aventurero
- Embajador Local

### Roles

La app diferencia accesos por rol (`enum class Rol`):

- `USUARIO`: exploracion, publicacion, comentarios y perfil.
- `MODERADOR`: revision de publicaciones y gestion de reportes.

## Arquitectura actual (resumen)

- App Android en un solo modulo: `:app`.
- UI con Jetpack Compose + Material 3.
- Navegacion tipada con Kotlin Serialization.
- Inyeccion de dependencias con Hilt (`@HiltAndroidApp`, `hiltViewModel()`).
- Carga de imagenes remotas con Coil 3.

## Flujos principales

- **No autenticado:** home, login, registro, recuperacion de contrasena.
- **Usuario:** home, explorar/lista y mapa, detalles, comentarios, filtros, creacion de publicaciones, perfil, insignias.
- **Moderador:** cola de moderacion, reportes y detalle de casos.

## Estructura del proyecto

```text
trip-link/
|- app/
|  |- src/main/java/com/example/triplink/
|  |  |- core/navigation/
|  |  |- core/components/
|  |  |- features/
|  |  |- domain/model/
|  |  |- data/
|- gradle/libs.versions.toml
|- app/build.gradle.kts
```

## Stack tecnico

- Kotlin `2.2.21`
- AGP `9.0.1`
- Jetpack Compose BOM `2024.09.00`
- Navigation Compose `2.9.6`
- Kotlinx Serialization `1.9.0`
- Hilt Android `2.59.2`
- DataStore Preferences `1.2.0`
- Coil 3 `3.3.0`
- `compileSdk = 36` (minor API 1), `minSdk = 28`

## Requisitos para ejecutar

- Android Studio (version reciente)
- SDK de Android con API 36 instalado
- JDK 11

## Comandos utiles

Desde la raiz del proyecto:

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

## Estado actual

- El proyecto tiene navegacion y pantallas para flujo de usuario y moderacion.
- Existen componentes y modelos para categorias, precios, reportes, niveles y roles.
- Parte de la capa `data/domain` aun usa datos mock/seed para desarrollo.

---

Si quieres, en un siguiente paso puedo convertir este README a una version mas "academica" (con alcance, historias de usuario, criterios de aceptacion y backlog de pendientes) para entrega de curso.

