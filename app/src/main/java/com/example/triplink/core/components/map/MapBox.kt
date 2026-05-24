package com.example.triplink.core.components.map

import android.annotation.SuppressLint
import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    markers: List<MapMarker> = emptyList(),
    showMyLocationButton: Boolean = true,
    activateClick: Boolean = false,
    centerCameraOnUpdate: Boolean = true,
    /**
     * Callback invoked when the device location is obtained by FusedLocationProvider.
     * Order: longitude, latitude to be consistent with map click callbacks used elsewhere.
     */
    onDeviceLocation: (Double, Double) -> Unit = { _, _ -> },
    onMapClickListener: (Double, Double) -> Unit = { _, _ -> },
    onMarkerClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }
    val markerByAnnotationId = remember { mutableMapOf<String, String>() }
    val mapClickListenerRef = remember { mutableStateOf<OnMapClickListener?>(null) }
    val hasLocationPermission = remember { mutableStateOf(false) }
    val isRequestingLocation = remember { mutableStateOf(false) }
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnMapClick by rememberUpdatedState(onMapClickListener)
    val currentActivateClick by rememberUpdatedState(activateClick)
    val currentCenterCameraOnUpdate by rememberUpdatedState(centerCameraOnUpdate)
    
    // Determinar el estilo según el tema del sistema (Claro/Oscuro)
    val isDarkTheme = isSystemInDarkTheme()
    val mapStyle = if (isDarkTheme) Style.DARK else Style.MAPBOX_STREETS

    // Manejar carga de estilo y registro de recursos de forma reactiva al cambio de tema
    LaunchedEffect(mapStyle, mapView.value) {
        val mv = mapView.value ?: return@LaunchedEffect
        mv.mapboxMap.loadStyle(mapStyle) { style ->
            try {
                registerMarkerImages(style)
                val manager = pointAnnotationManager.value ?: mv.annotations.createPointAnnotationManager().also { 
                    pointAnnotationManager.value = it 
                }
                
                // Configurar click listener de markers una sola vez
                manager.deleteAll() // Limpiar para el nuevo estilo
                manager.addClickListener { annotation: PointAnnotation ->
                    markerByAnnotationId[annotation.id]?.let { markerId ->
                        currentOnMarkerClick(markerId)
                        true
                    } ?: false
                }
                
                // Definir lógica local de renderizado para el callback inicial
                val options = markers.map { marker ->
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(marker.longitude, marker.latitude))
                        .withIconImage(if (marker.highlighted) HIGHLIGHT_MARKER_IMAGE_ID else NORMAL_MARKER_IMAGE_ID)
                        .withIconSize(if (marker.highlighted) 1.6 else 1.2)
                }

                val annotations = manager.create(options)
                annotations.forEachIndexed { index, annotation ->
                    markerByAnnotationId[annotation.id] = markers[index].id
                }
                
                // Centrar cámara inicialmente
                if (markers.isNotEmpty()) {
                    val target = markers.firstOrNull { it.highlighted } ?: markers.first()
                    mv.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(target.longitude, target.latitude))
                            .zoom(if (target.highlighted) 13.0 else 11.0)
                            .build()
                    )
                }
            } catch (e: Exception) {
                Log.e("MapBox", "Error al cargar recursos en el estilo: ${e.message}", e)
            }
        }
    }

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

                        // Solucionar conflicto de scroll con contenedores padres (ej. verticalScroll)
                        this.setOnTouchListener { v, event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    // Bloquear el scroll del padre cuando se toca el mapa
                                    v.parent.requestDisallowInterceptTouchEvent(true)
                                }
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                    // Liberar el scroll del padre
                                    v.parent.requestDisallowInterceptTouchEvent(false)
                                }
                            }
                            false // Retornar false para que los gestos de Mapbox sigan funcionando
                        }

                        val mapboxMap = this.mapboxMap
                        
                        try {
                            Log.d("MapBox", "Configurando cámara...")
                            mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .zoom(10.0)
                                    .center(Point.fromLngLat(-75.6491181, 4.4687891))
                                    .build()
                            )
                            Log.d("MapBox", "Cámara configurada")

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

                    // Ensure listener is attached
                    val mapClickListener = mapClickListenerRef.value ?: OnMapClickListener { point ->
                        if (currentActivateClick) {
                            currentOnMapClick(point.longitude(), point.latitude())
                            true
                        } else {
                            false
                        }
                    }.also { mapClickListenerRef.value = it }
                    
                    mapViewInstance.gestures.removeOnMapClickListener(mapClickListener)
                    mapViewInstance.gestures.addOnMapClickListener(mapClickListener)

                    if (markers.isNotEmpty() && currentCenterCameraOnUpdate) {
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
                    if (isRequestingLocation.value) return@FloatingActionButton
                    if (hasLocationPermission.value) {
                        try {
                            isRequestingLocation.value = true
                            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                            val cts = CancellationTokenSource()
                            // Request a current high-accuracy location; fallback to lastLocation if returns null
                            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                                .addOnSuccessListener { location ->
                                    try {
                                        if (location != null) {
                                            val lon = location.longitude
                                            val lat = location.latitude
                                            Log.d("MapBox", "Device location obtained: $lat,$lon")
                                            // Notify parent and center camera
                                            onDeviceLocation(lon, lat)
                                            mapView.value?.mapboxMap?.setCamera(
                                                CameraOptions.Builder()
                                                    .zoom(14.0)
                                                    .center(Point.fromLngLat(lon, lat))
                                                    .build()
                                            )
                                        } else {
                                            Log.w("MapBox", "getCurrentLocation returned null, trying lastLocation")
                                            fusedClient.lastLocation.addOnSuccessListener { last ->
                                                if (last != null) {
                                                    val lon = last.longitude
                                                    val lat = last.latitude
                                                    onDeviceLocation(lon, lat)
                                                    mapView.value?.mapboxMap?.setCamera(
                                                        CameraOptions.Builder()
                                                            .zoom(14.0)
                                                            .center(Point.fromLngLat(lon, lat))
                                                            .build()
                                                    )
                                                } else {
                                                    Log.w("MapBox", "lastLocation also null")
                                                }
                                            }.addOnFailureListener { e ->
                                                Log.e("MapBox", "Error getting lastLocation: ${e.message}", e)
                                            }.addOnCompleteListener {
                                                isRequestingLocation.value = false
                                                cts.cancel()
                                            }
                                        }
                                    } finally {
                                        // ensure flag reset when getCurrentLocation completes
                                        isRequestingLocation.value = false
                                        cts.cancel()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("MapBox", "Error getting current location: ${e.message}", e)
                                    isRequestingLocation.value = false
                                    try { cts.cancel() } catch (_: Exception) {}
                                }
                        } catch (e: Exception) {
                            Log.e("MapBox", "Exception requesting device location: ${e.message}", e)
                            isRequestingLocation.value = false
                        }
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
                if (isRequestingLocation.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación"
                    )
                }
            }
        }
    }
}





