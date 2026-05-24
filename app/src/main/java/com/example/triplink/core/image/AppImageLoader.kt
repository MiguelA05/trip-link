package com.example.triplink.core.image

import android.content.Context
import coil3.ImageLoader
import coil3.network.NetworkFetcher
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Lazily create a Coil ImageLoader that uses the app's shared OkHttp client and a disk cache.
 * Use by passing `imageLoader = AppImageLoader.get(context)` to Compose's AsyncImage.
 */
object AppImageLoader {
    @Volatile
    private var instance: ImageLoader? = null

    fun get(context: Context): ImageLoader {
        return instance ?: synchronized(this) {
            instance ?: build(context).also { instance = it }
        }
    }

    private fun build(context: Context): ImageLoader {
        // Create an OkHttp HTTP response cache (disk) that will be applied to the
        // OkHttpClient used by Coil. Use a separate var name to avoid shadowing.
        val cacheDir = File(context.cacheDir, "image_http_cache")
        val cacheSize = 50L * 1024L * 1024L // 50 MB
        val httpCache = try {
            Cache(cacheDir, cacheSize)
        } catch (_: Exception) {
            null
        }

        val okHttpClient = OkHttpClient.Builder().apply {
            httpCache?.let { cache(it) }
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
        }.build()

        // Try to obtain the OkHttpNetworkFetcher factory via reflection so we don't
        // have a hard compile-time dependency on the okhttp module's symbols here.
        val okhttpFactory: NetworkFetcher.Factory? = try {
            val clazz = Class.forName("coil3.network.okhttp.OkHttpNetworkFetcher")
            val method = clazz.getMethod("factory", okhttp3.Call.Factory::class.java)
            @Suppress("UNCHECKED_CAST")
            method.invoke(null, okHttpClient) as? NetworkFetcher.Factory
        } catch (e: Exception) {
            null
        }

        return ImageLoader.Builder(context)
            .components {
                okhttpFactory?.let { add(it) }
            }
            .build()
    }
}






