# Mapas y Geolocalización en Android

**Universidad del Quindío**  
**Programa de Ingeniería de Sistemas y Computación**  
**Docente:** Carlos Andrés Florez V.  
**Espacio académico:** Construcción de Aplicaciones Móviles

---

## Introducción

El acceso al servicio de mapas y la geolocalización es una característica común en muchas aplicaciones móviles modernas. Todos los dispositivos cuentan con un sistema de posicionamiento global (GPS) que permite determinar la ubicación geográfica del dispositivo. Además, los servicios de mapas proporcionan una representación visual de la ubicación y permiten la interacción con mapas, como la visualización de rutas, puntos de interés y más.

---

## Librerías de mapas

Algunas de las librerías de mapas más populares para Android incluyen:

- **Google Maps SDK para Android:** La librería oficial de Google para integrar mapas en aplicaciones Android. Proporciona funcionalidades como visualización de mapas, marcadores, rutas y capas personalizadas.
- **Mapbox:** Una plataforma de mapas con SDK para Android que ofrece características avanzadas como mapas personalizables, navegación y análisis de ubicación.
- **OpenStreetMap (OSM):** Una alternativa de código abierto a Google Maps. Hay varias librerías disponibles para integrar OSM en Android, como `osmdroid` y `Mapsforge`.

En esta guía nos enfocaremos en **Mapbox** debido a su flexibilidad, personalización y uso gratuito más amplio que Google Maps.

Documentación oficial: [Mapbox Maps SDK for Android](https://docs.mapbox.com/android/maps/guides/)

---

## Arquitectura de integración de Mapbox

En el centro está el composable como una sola pieza reutilizable que concentra tres responsabilidades: gestionar el estado de la cámara del mapa, dibujar marcadores a partir de los reportes recibidos y capturar los clics del usuario sobre el mapa.

```
┌─────────────────────────────────────────────┐
│             Pantalla · Compose              │
│    Pasa reports y recibe Point del clic     │
└─────────────────────┬───────────────────────┘
                      │ reports / Point
                      ▼
┌─────────────────────────────────────────────┐
│           Composable MapBox                 │
│                                             │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │   Cámara    │  │  Marcadores  │  │   Clic en mapa     │ │
│  │ zoom+center │  │PointAnnotation│  │ onMapClickListener │ │
│  │seguir usuario│  │por cada Report│  │ devuelve Point a UI│ │
│  └─────────────┘  └──────────────┘  └────────────────────┘ │
└──────────┬──────────────────────────────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
┌──────────┐  ┌──────────────────────┐
│Mapbox SDK│  │  Ubicación dispositivo│
│AccessToken│  │  GPS + permisos      │
│  + tiles  │  └──────────────────────┘
└──────────┘
```

Esta separación permite que la pantalla se mantenga simple y enfocada en su lógica, mientras toda la complejidad de mapas y geolocalización queda encapsulada en un único componente.

---

## Configuración de Mapbox en Android

### 1. Crear una cuenta en Mapbox

Cree una cuenta en [Mapbox](https://www.mapbox.com/) y obtenga una clave de API (**Access Token**) para autenticar las solicitudes a los servicios de Mapbox. Mapbox ofrece un plan gratuito con un límite generoso de uso mensual y no requiere tarjeta de crédito para registrarse.

> ⚠️ **Importante:** Al crear la cuenta, seleccione el plan **Individual**, no el plan Business. Use su correo institucional para registrarse.

### 2. Agregar dependencias de Mapbox

En el archivo `libs.versions.toml`, agregue las versiones y librerías necesarias:

```toml
[versions]
mapsCompose = "11.22.0"

[libraries]
maps-android = { module = "com.mapbox.maps:android", version.ref = "mapsCompose" }
maps-compose = { module = "com.mapbox.extension:maps-compose", version.ref = "mapsCompose" }
```

En el archivo `settings.gradle.kts`, incluya el repositorio de Mapbox:

```kotlin
dependencyResolutionManagement {
    // ...
    repositories {
        // ...
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
    }
}
```

En el archivo `build.gradle.kts` del módulo de la aplicación, agregue las dependencias:

```kotlin
dependencies {
    implementation(libs.maps.android)
    implementation(libs.maps.compose)
}
```

> Recuerde sincronizar el proyecto después de agregar las dependencias.

### 3. Configurar el Access Token

Cree el archivo `mapbox_access_token.xml` en el directorio `res/values`, reemplazando `YOUR_MAPBOX_ACCESS_TOKEN` con su clave de API:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools">
    <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources">YOUR_MAPBOX_ACCESS_TOKEN</string>
</resources>
```

### 4. Inicializar Mapbox en la aplicación

Cree el archivo `MapBox.kt` en el paquete `core/component` con un composable que muestre un mapa básico:

```kotlin
package com.example.demoapp.core.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
) {
    // Configurar el estado inicial del mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0) // Entre más alto el valor, más cerca estará la cámara del suelo
            center(Point.fromLngLat(-75.6491181, 4.4687891)) // Coordenadas fijas iniciales
        }
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        )
    }
}
```

> `setCameraOptions` recibe un objeto `CameraOptions` con configuraciones como posición inicial, nivel de zoom, inclinación y orientación.

### 5. Probar la aplicación

Para que el mapa ocupe toda la pantalla, llame al composable `MapBox` con el modificador `fillMaxSize()`:

```kotlin
MapBox(
    modifier = Modifier.fillMaxSize()
)
```

Pruebe la aplicación en un dispositivo físico o emulador con soporte de ubicación.

### 6. Ubicación actual del usuario

Primero, agregue los permisos necesarios en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

- `ACCESS_COARSE_LOCATION`: Ubicación aproximada usando torres de telefonía y puntos Wi-Fi.
- `ACCESS_FINE_LOCATION`: Ubicación precisa usando señales GPS.

Luego, modifique el composable `MapBox` para incluir un botón que permita centrar el mapa en la ubicación actual del usuario:

```kotlin
package com.example.demoapp.core.component

// Importaciones necesarias ...

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    showMyLocationButton: Boolean = true // Mostrar botón de mi ubicación
) {
    // Estado para manejar permisos de ubicación
    val permissionState = rememberLocationPermissionState()
    var shouldFollowUser by remember { mutableStateOf(false) }

    // Configurar el estado inicial del mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
        }
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState
        ) {
            // Configurar ubicación del usuario si tiene permiso y quiere seguirla
            if (permissionState.hasPermission && shouldFollowUser) {
                MapEffect(key1 = "follow_puck") { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }
                    mapViewportState.transitionToFollowPuckState(
                        defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
                            .maxDurationMs(1500)
                            .build()
                    )
                }
            }
        }

        // Botón de mi ubicación
        if (showMyLocationButton) {
            FloatingActionButton(
                onClick = {
                    if (permissionState.hasPermission) {
                        shouldFollowUser = true
                    } else {
                        permissionState.requestPermission() // Solicitar permiso si no lo tiene
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi ubicación"
                )
            }
        }
    }
}
```

En el mismo archivo, agregue la clase para manejar el estado del permiso de ubicación:

```kotlin
/**
 * Estado para manejar el permiso de ubicación de forma controlada
 */
class LocationPermissionState(
    hasPermission: Boolean = false,
    val requestPermission: () -> Unit = {}
) {
    var hasPermission by mutableStateOf(hasPermission)
        internal set

    var wasJustGranted by mutableStateOf(false)
        internal set
}
```

Y la función composable para recordar el estado del permiso:

```kotlin
@Composable
fun rememberLocationPermissionState(
    permission: String = android.Manifest.permission.ACCESS_FINE_LOCATION
): LocationPermissionState {
    val context = LocalContext.current

    // Verificar el estado inicial del permiso
    val initialPermission = remember {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Estado para manejar el permiso
    val state = remember { LocationPermissionState(hasPermission = initialPermission) }

    // Lanzador para solicitar el permiso
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        state.wasJustGranted = granted && !state.hasPermission
        state.hasPermission = granted
    }

    // Recordar el estado del permiso
    return remember(state, launcher) {
        LocationPermissionState(
            hasPermission = state.hasPermission,
            requestPermission = { launcher.launch(permission) }
        ).also {
            it.wasJustGranted = state.wasJustGranted
        }
    }
}
```

Con estos cambios, el usuario podrá tocar el botón de "Mi ubicación" para centrar el mapa en su posición actual. Si el permiso no ha sido otorgado, se solicitará al usuario que lo conceda.

### 7. Uso de marcadores en el mapa

Para agregar marcadores se utiliza `PointAnnotation` de Mapbox. En este proyecto, los marcadores representan la ubicación exacta de un reporte hecho por un usuario.

Modifique el composable `MapBox` para recibir una lista de reportes y renderizar sus marcadores:

```kotlin
package com.example.demoapp.core.component

// Importaciones necesarias ...

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    reports: List<Report> = emptyList(), // Lista de reportes con ubicaciones
    showMyLocationButton: Boolean = true
) {
    // Todo el código previo...

    // Cargar el ícono del marcador
    val marker = rememberIconImage(
        key = R.drawable.red_marker,
        painter = painterResource(R.drawable.red_marker)
    )

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState
        ) {
            // Todo el código previo...

            // Mostrar marcadores de los reportes
            reports.forEach { report ->
                PointAnnotation(
                    point = Point.fromLngLat(report.location.longitude, report.location.latitude)
                ) {
                    iconImage = marker
                }
            }
        }

        // Todo el resto del código...
    }
}
```

> Se asume que cada objeto `Report` tiene una propiedad `location` con coordenadas de longitud y latitud. Debe tener un `Repository` con reportes y un `ViewModel` que los exponga para que el composable pueda recibirlos.

### 8. Obtener la ubicación a partir de un evento de clic

Para capturar la ubicación en el mapa cuando el usuario hace clic en un punto específico, modifique el composable `MapBox` para incluir un manejador de clics:

```kotlin
package com.example.demoapp.core.component

// Importaciones necesarias ...

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    reports: List<Report> = emptyList(),
    showMyLocationButton: Boolean = true,
    activateClick: Boolean = false,          // Activar detección de clics (no siempre se necesita)
    onMapClickListener: (Point) -> Unit = {} // Callback para manejar el clic en el mapa
) {
    // Todo el código previo...

    // Estado para almacenar el punto clickeado
    var clickedPoint by remember { mutableStateOf<Point?>(null) }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = { point ->
                // Manejar el clic en el mapa solo si está activado
                if (activateClick) {
                    onMapClickListener(point) // Llamar al callback externo
                    clickedPoint = point
                }
                true
            }
        ) {
            // Todo el código previo...

            // Mostrar marcador del punto clickeado solo si no es nulo
            clickedPoint?.let { point ->
                PointAnnotation(point = point) {
                    iconImage = marker // Usar el mismo ícono de marcador
                }
            }
        }

        // Todo el resto del código...
    }
}
```

Esta funcionalidad permite que el usuario haga clic en cualquier punto del mapa y se capture esa ubicación, lo cual es clave para crear nuevos reportes donde la exactitud es importante.

---

## Actividad Práctica

1. **Popup con información del marcador:** Implemente una funcionalidad que muestre un popup o ventana emergente con información adicional cuando el usuario haga clic en un marcador. La información puede incluir imagen, título, descripción y un botón para ver más detalles o navegar a la pantalla de detalles del reporte.

2. **Cambiar color del marcador:** Ajuste el color del marcador según el tipo de reporte. Por ejemplo, use un marcador rojo para reportes de emergencia y uno verde para reportes informativos. Puede también agregar un ícono personalizado por tipo de reporte.

3. **Estilo personalizado del mapa:** Investigue cómo aplicar estilos personalizados al mapa utilizando las opciones de Mapbox. Consulte la [documentación oficial de estilos de Mapbox](https://docs.mapbox.com/android/maps/guides/styles/).
