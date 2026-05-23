package com.example.triplink.di

import com.example.triplink.core.services.ImagenCompressionService
import com.example.triplink.core.services.ImagenCompressionServiceImpl
import com.example.triplink.core.storage.ImagenLocalStorage
import com.example.triplink.core.storage.ImagenLocalStorageImpl
import com.example.triplink.data.repository.remote.images.CloudinaryImageRepository
import com.example.triplink.data.repository.remote.images.CloudinaryImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageServicesModule {
    
    @Binds
    abstract fun bindImagenLocalStorage(
        impl: ImagenLocalStorageImpl
    ): ImagenLocalStorage
    
    @Binds
    abstract fun bindImagenCompressionService(
        impl: ImagenCompressionServiceImpl
    ): ImagenCompressionService
    
    @Binds
    abstract fun bindCloudinaryImageRepository(
        impl: CloudinaryImageRepositoryImpl
    ): CloudinaryImageRepository
}
