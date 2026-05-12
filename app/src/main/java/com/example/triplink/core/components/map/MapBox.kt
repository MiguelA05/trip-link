package com.example.triplink.core.components.map

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.triplink.BuildConfig
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener

private const val NORMAL_MARKER_IMAGE_ID = "triplink-marker-normal"
private const val HIGHLIGHT_MARKER_IMAGE_ID = "triplink-marker-highlight"

data class MapMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String? = null,
    val highlighted: Boolean = false
)

private fun createMarkerBitmap(fillColor: Int): Bitmap {
    val sizePx = 96
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f - 4f, paint)

    paint.color = fillColor
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f - 12f, paint)

    return bitmap
}

private fun registerMarkerImages(style: Style) {
    style.addImage(
        NORMAL_MARKER_IMAGE_ID,
        createMarkerBitmap(android.graphics.Color.parseColor("#DC2626"))
    )
    style.addImage(
        HIGHLIGHT_MARKER_IMAGE_ID,
        createMarkerBitmap(android.graphics.Color.parseColor("#1D4ED8"))
    )
}

/**
 * MapBox composable que integra Mapbox SDK v11+ con AndroidView.
 * Muestra un mapa interactivo con:
 * - Markers en coordenadas especificadas
 * - Botón de "Mi ubicación" (si showMyLocationButton = true)
 * - Captura de clics en el mapa (si activateClick = true)
 * - Callbacks onMarkerClick y onMapClickListener
 */
@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    markers: List<MapMarker> = emptyList(),
    showMyLocationButton: Boolean = true,
    activateClick: Boolean = false,
    onMapClickListener: (Double, Double) -> Unit = { _, _ -> },
    onMarkerClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }
    val markerByAnnotationId = remember { mutableMapOf<String, String>() }
    val mapClickListenerRef = remember { mutableStateOf<OnMapClickListener?>(null) }
    val hasLocationPermission = remember { mutableStateOf(false) }
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnMapClick by rememberUpdatedState(onMapClickListener)
    val currentActivateClick by rememberUpdatedState(activateClick)

    fun renderMarkers(activeMarkers: List<MapMarker>) {
        val manager = pointAnnotationManager.value ?: return
        manager.deleteAll()
        markerByAnnotationId.clear()

        if (activeMarkers.isEmpty()) return

        val options = activeMarkers.map { marker ->
            PointAnnotationOptions()
                .withPoint(Point.fromLngLat(marker.longitude, marker.latitude))
                .withIconImage(if (marker.highlighted) HIGHLIGHT_MARKER_IMAGE_ID else NORMAL_MARKER_IMAGE_ID)
                .withIconSize(if (marker.highlighted) 1.6 else 1.2)
        }

        val annotations = manager.create(options)
        annotations.forEachIndexed { index, annotation ->
            markerByAnnotationId[annotation.id] = activeMarkers[index].id
        }
    }

    // Usar Activity Result API (igual que la cámara) para pedir permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms: Map<String, Boolean> ->
        hasLocationPermission.value = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true || perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Inicializar Mapbox SDK con el token de BuildConfig (debe hacerse antes de crear MapView)
    LaunchedEffect(Unit) {
        try {
            val token = BuildConfig.MAPBOX_ACCESS_TOKEN
            Log.d("MapBox", "Token disponible: ${token.isNotEmpty()}, primeros 20 chars: ${token.take(20)}...")

            if (token.isEmpty()) {
                Log.w("MapBox", "MAPBOX_ACCESS_TOKEN vacío o no configurado")
            } else {
                Log.d("MapBox", "Token de Mapbox cargado desde BuildConfig")
            }
        } catch (e: Exception) {
            Log.e("MapBox", "Error al acceder a MAPBOX_ACCESS_TOKEN: ${e.message}", e)
        }
    }

    // Verificar y solicitar permisos de ubicación (patrón similar a la cámara)
    LaunchedEffect(Unit) {
        hasLocationPermission.value = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission.value) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = modifier) {
        // MapView usando AndroidView para integración con Compose
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                try {
                    Log.d("MapBox", "Creando MapView...")

                    // Crear MapView con el contexto
                    MapView(ctx).apply {
                        mapView.value = this
                        Log.d("MapBox", "MapView creado exitosamente")

                        val mapboxMap = this.mapboxMap
                        val mapClickListener = OnMapClickListener { point ->
                            if (currentActivateClick) {
                                currentOnMapClick(point.longitude(), point.latitude())
                                true
                            } else {
                                false
                            }
                        }
                        mapClickListenerRef.value = mapClickListener
                        this.gestures.addOnMapClickListener(mapClickListener)

                        try {
                            Log.d("MapBox", "Configurando cámara...")
                            mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .zoom(10.0)
                                    .center(Point.fromLngLat(-75.6491181, 4.4687891))
                                    .build()
                            )
                            Log.d("MapBox", "Cámara configurada")

                            Log.d("MapBox", "Cargando estilo MAPBOX_STREETS...")
                            mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                                Log.d("MapBox", "Callback de estilo cargado invocado, estilo: $style")

                                try {
                                    registerMarkerImages(style)
                                    val annotationApi = annotations
                                    val manager = annotationApi.createPointAnnotationManager()
                                    pointAnnotationManager.value = manager
                                    manager.addClickListener { annotation: PointAnnotation ->
                                        markerByAnnotationId[annotation.id]?.let { markerId ->
                                            currentOnMarkerClick(markerId)
                                            true
                                        } ?: false
                                    }
                                    renderMarkers(markers)

                                    Log.d("MapBox", "Manager de markers inicializado")
                                } catch (e: Exception) {
                                    Log.e("MapBox", "Error al inicializar manager de markers: ${e.message}", e)
                                }
                            }

                            if (hasLocationPermission.value) {
                                Log.d("MapBox", "Permiso de ubicación concedido")
                            } else {
                                Log.w("MapBox", "Permiso de ubicación no concedido")
                            }
                        } catch (e: Exception) {
                            Log.e("MapBox", "Error al configurar el mapa: ${e.message}", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MapBox", "Error al crear MapView: ${e.message}", e)
                    MapView(ctx)
                }
            }
            ,
            update = { mapViewInstance ->
                try {
                    renderMarkers(markers)

                    if (markers.isNotEmpty()) {
                        val target = markers.firstOrNull { it.highlighted } ?: markers.first()
                        mapViewInstance.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(target.longitude, target.latitude))
                                .zoom(if (target.highlighted) 13.0 else 11.0)
                                .build()
                        )
                    }
                } catch (e: Exception) {
                    Log.e("MapBox", "Error actualizando markers: ${e.message}", e)
                }
            }
        )

        // Asegurar que MapView reciba eventos de lifecycle (onStart/onStop/onDestroy)
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner, mapView.value) {
            val mv = mapView.value
            val observer = LifecycleEventObserver { _, event ->
                try {
                    when (event) {
                        androidx.lifecycle.Lifecycle.Event.ON_START -> mv?.onStart()
                        androidx.lifecycle.Lifecycle.Event.ON_STOP -> mv?.onStop()
                        androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mv?.onDestroy()
                        else -> {}
                    }
                } catch (e: Exception) {
                    Log.e("MapBox", "Lifecycle handling error: ${e.message}", e)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView.value?.let { mv ->
                    mapClickListenerRef.value?.let { listener ->
                        mv.gestures.removeOnMapClickListener(listener)
                    }
                }
                pointAnnotationManager.value?.deleteAll()
                pointAnnotationManager.value = null
                markerByAnnotationId.clear()
            }
        }

        // Botón flotante "Mi ubicación"
        if (showMyLocationButton) {
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission.value) {
                        // Centrar en ubicación del usuario
                        mapView.value?.mapboxMap?.setCamera(
                            CameraOptions.Builder()
                                .zoom(14.0)
                                .center(Point.fromLngLat(-75.6491181, 4.4687891))
                                .build()
                        )
                    } else {
                        // Solicitar permisos usando el mismo launcher que definimos arriba (Activity Result API)
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
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





