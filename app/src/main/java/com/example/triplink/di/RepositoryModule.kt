package com.example.triplink.di

import com.example.triplink.data.repository.admin.AdminRepositoryImpl
import com.example.triplink.data.repository.user.UserRepositoryImpl
import com.example.triplink.domain.repository.admin.AdminRepository
import com.example.triplink.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository
}
