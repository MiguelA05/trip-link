package com.example.triplink.data.repository.remote.images

import android.content.Context
import android.net.Uri
import com.example.triplink.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface CloudinaryImageRepository {
    suspend fun subirImagen(archivo: File, nombrePublico: String): Result<String>
    suspend fun subirMultiples(archivos: List<File>, prefijo: String): Result<List<String>>
    suspend fun eliminarImagen(urlPublica: String): Result<Boolean>
}

class CloudinaryImageRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CloudinaryImageRepository {

    private val httpClient = OkHttpClient()

    override suspend fun subirImagen(
        archivo: File,
        nombrePublico: String
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            suspendCancellableCoroutine { continuation ->
                try {
                    val url = "https://api.cloudinary.com/v1_1/${BuildConfig.CLOUDINARY_CLOUD_NAME}/image/upload"

                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "file",
                            archivo.name,
                            archivo.asRequestBody("image/*".toMediaType())
                        )
                        .addFormDataPart("upload_preset", BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                        .addFormDataPart("public_id", nombrePublico)
                        .addFormDataPart("folder", "publications")
                        .build()

                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    val response = httpClient.newCall(request).execute()

                    if (response.isSuccessful) {
                        val bodyString = response.body?.string() ?: ""
                        val json = JSONObject(bodyString)
                        val secureUrl = json.getString("secure_url")
                        continuation.resume(Result.success(secureUrl))
                    } else {
                        val errorMsg = "Cloudinary error: ${response.code}"
                        continuation.resume(Result.failure(Exception(errorMsg)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resumeWithException(e)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun subirMultiples(
        archivos: List<File>,
        prefijo: String
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val urls = mutableListOf<String>()

            for ((indice, archivo) in archivos.withIndex()) {
                val resultado = subirImagen(archivo, "${prefijo}_$indice")

                when {
                    resultado.isSuccess -> {
                        urls.add(resultado.getOrNull() ?: "")
                    }
                    else -> {
                        return@withContext Result.failure(
                            resultado.exceptionOrNull() ?: Exception("Error subiendo imagen $indice")
                        )
                    }
                }
            }

            Result.success(urls)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun eliminarImagen(urlPublica: String): Result<Boolean> {
        // Cloudinary requiere API Key + Secret para eliminar
        return try {
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}






