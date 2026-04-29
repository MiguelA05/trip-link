package com.example.triplink.core.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Crea un URI temporal en cache para almacenar fotos tomadas con la cámara.
 * Esta función NO es un Composable; es una función normal de Kotlin.
 */
fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "profile_photo_",
        ".jpg",
        context.cacheDir
    ).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

