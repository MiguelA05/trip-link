package com.example.triplink.core.components.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@Composable
fun LocationPickerMapField(
    modifier: Modifier = Modifier,
    currentLatitude: Double?,
    currentLongitude: Double?,
    showMyLocationButton: Boolean = true,
    onLocationConfirmed: (longitude: Double, latitude: Double) -> Unit
) {
    var pendingLongitude by remember { mutableStateOf<Double?>(null) }
    var pendingLatitude by remember { mutableStateOf<Double?>(null) }

    val hasSelectedLocation = currentLatitude != null && currentLongitude != null
    val hasPendingLocation = pendingLatitude != null && pendingLongitude != null

    val selectedLocationLabel = if (hasPendingLocation) {
        stringResource(
            R.string.component_map_location_coordinates_format,
            pendingLatitude!!,
            pendingLongitude!!
        )
    } else if (hasSelectedLocation) {
        stringResource(
            R.string.component_map_location_coordinates_format,
            currentLatitude!!,
            currentLongitude!!
        )
    } else {
        stringResource(R.string.component_map_location_not_selected)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MapBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                markers = listOfNotNull(
                    if (hasPendingLocation) {
                        MapMarker(
                            id = "pending_location",
                            latitude = pendingLatitude!!,
                            longitude = pendingLongitude!!,
                            highlighted = true
                        )
                    } else if (hasSelectedLocation) {
                        MapMarker(
                            id = "selected_location",
                            latitude = currentLatitude,
                            longitude = currentLongitude,
                            highlighted = true
                        )
                    } else {
                        null
                    }
                ),
                showMyLocationButton = showMyLocationButton,
                activateClick = true,
                centerCameraOnUpdate = !hasPendingLocation, // Only center automatically if not currently picking
                onMapClickListener = { longitude, latitude ->
                    pendingLongitude = longitude
                    pendingLatitude = latitude
                },
                onDeviceLocation = { longitude, latitude ->
                    // When MapBox obtains device location, set pending so user can confirm
                    pendingLongitude = longitude
                    pendingLatitude = latitude
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = selectedLocationLabel,
                        color = TextColors.Muted,
                        style = TextTokens.body()
                    )
                }
            }
        }
    }

    val longitude = pendingLongitude
    val latitude = pendingLatitude
    if (longitude != null && latitude != null) {
        GeneralAlertDialog(
            onDismissRequest = {
                pendingLongitude = null
                pendingLatitude = null
            },
            onConfirm = {
                onLocationConfirmed(longitude, latitude)
                pendingLongitude = null
                pendingLatitude = null
            },
            title = stringResource(R.string.component_map_location_confirm_title),
            message = stringResource(
                R.string.component_map_location_confirm_message,
                latitude,
                longitude
            ),
            icon = Icons.Default.LocationOn,
            buttonText = stringResource(R.string.component_map_location_confirm_action),
            dismissButtonText = stringResource(R.string.component_map_location_cancel_action),
            onDismissAction = {
                pendingLongitude = null
                pendingLatitude = null
            }
        )
    }
}

