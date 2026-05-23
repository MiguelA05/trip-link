package com.example.triplink.core.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface ImagenLocalStorage {
    suspend fun guardarImagen(uri: Uri, nombreLocal: String): File?
    suspend fun obtenerImagen(nombreLocal: String): File?
    suspend fun eliminarImagen(nombreLocal: String): Boolean
    suspend fun limpiarDir(): Boolean
    fun obtenerArchivoLocal(nombreLocal: String): File?
}

class ImagenLocalStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ImagenLocalStorage {
    
    private val imagesDir = File(context.cacheDir, "publication_images")
    
    init {
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
    }
    
    override suspend fun guardarImagen(uri: Uri, nombreLocal: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imagesDir, nombreLocal)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    override suspend fun obtenerImagen(nombreLocal: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imagesDir, nombreLocal)
                if (file.exists()) file else null
            } catch (e: Exception) {
                null
            }
        }
    }
    
    override suspend fun eliminarImagen(nombreLocal: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imagesDir, nombreLocal)
                file.delete()
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override suspend fun limpiarDir(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                imagesDir.deleteRecursively()
                imagesDir.mkdirs()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    override fun obtenerArchivoLocal(nombreLocal: String): File? {
        return try {
            val file = File(imagesDir, nombreLocal)
            if (file.exists()) file else null
        } catch (e: Exception) {
            null
        }
    }
}
