package com.example.triplink.core.components.imagepicker

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.triplink.core.image.AppImageLoader
import androidx.compose.ui.platform.LocalContext
import com.example.triplink.R

/**
 * Componente que muestra la foto de perfil de forma circular.
 * Si no hay foto, muestra un icono.
 * En modo edición, muestra botón de cámara superpuesto.
 */
@Composable
fun ProfileImage(
    photoUri: Uri?,
    isEditMode: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    val imageSize = 140.dp
    Box(contentAlignment = Alignment.Center) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                imageLoader = AppImageLoader.get(LocalContext.current),
                contentDescription = stringResource(R.string.permissions_profile_image_description),
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .then(if (isEditMode) Modifier.clickable { onEditClick() } else Modifier),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = stringResource(R.string.permissions_profile_image_description),
                modifier = Modifier
                    .size(imageSize)
                    .then(if (isEditMode) Modifier.clickable { onEditClick() } else Modifier),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isEditMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = stringResource(R.string.permissions_change_photo),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

