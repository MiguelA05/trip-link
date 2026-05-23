package com.example.triplink.core.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.math.min

interface ImagenCompressionService {
    suspend fun comprimirImagen(
        uri: Uri,
        maxWidth: Int = 1920,
        maxHeight: Int = 1080,
        quality: Int = 80
    ): File?

    suspend fun crearThumbnail(
        archivo: File,
        tamaño: Int = 256
    ): File?
}

class ImagenCompressionServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ImagenCompressionService {

    override suspend fun comprimirImagen(
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): File? = withContext(Dispatchers.Default) {
        try {
            // Decodificar opciones para obtener dimensiones reales
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }

            val imageHeight = options.outHeight
            val imageWidth = options.outWidth

            // Calcular escala
            var inSampleSize = 1
            if (imageHeight > maxHeight || imageWidth > maxWidth) {
                val halfHeight = imageHeight / 2
                val halfWidth = imageWidth / 2

                while (halfHeight / inSampleSize >= maxHeight && halfWidth / inSampleSize >= maxWidth) {
                    inSampleSize *= 2
                }
            }

            // Decodificar con escala
            val scaledOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, scaledOptions)
            } ?: return@withContext null

            // Redimensionar si es necesario
            val finalBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val ratio = min(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            // Guardar comprimido
            val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
            }

            finalBitmap.recycle()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun crearThumbnail(
        archivo: File,
        tamaño: Int
    ): File? = withContext(Dispatchers.Default) {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(archivo.absolutePath, options)

            val imageHeight = options.outHeight
            val imageWidth = options.outWidth

            var inSampleSize = 1
            if (imageHeight > tamaño || imageWidth > tamaño) {
                val halfHeight = imageHeight / 2
                val halfWidth = imageWidth / 2

                while (halfHeight / inSampleSize >= tamaño && halfWidth / inSampleSize >= tamaño) {
                    inSampleSize *= 2
                }
            }

            val scaledOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

            val bitmap = BitmapFactory.decodeFile(archivo.absolutePath, scaledOptions)
                ?: return@withContext null

            val finalBitmap = if (bitmap.width > tamaño || bitmap.height > tamaño) {
                val ratio = min(tamaño.toFloat() / bitmap.width, tamaño.toFloat() / bitmap.height)
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            val file = File(archivo.parent, "thumb_${archivo.name}")
            file.outputStream().use { output ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
            }

            finalBitmap.recycle()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

