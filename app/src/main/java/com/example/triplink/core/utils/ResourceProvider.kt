package com.example.triplink.core.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Interfaz para proporcionar acceso a los recursos de strings
interface ResourceProvider {
    fun getString(id: Int): String
}

class ResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): ResourceProvider{

    override fun getString(id: Int): String {
        // Se obtiene el string a partir del ID de recurso utilizando el contexto de la aplicación
        return context.getString(id)
    }

}