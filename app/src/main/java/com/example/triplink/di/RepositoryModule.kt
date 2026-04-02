package com.example.triplink.di

import com.example.triplink.data.repository.admin.moderation.AdminModerationRepositoryImpl
import com.example.triplink.data.repository.admin.reports.AdminReportsRepositoryImpl
import com.example.triplink.data.repository.user.publications.UserPublicationsRepositoryImpl
import com.example.triplink.domain.repository.admin.moderation.AdminModerationRepository
import com.example.triplink.domain.repository.admin.reports.AdminReportsRepository
import com.example.triplink.domain.repository.user.publications.UserPublicationsRepository
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
    abstract fun bindUserPublicationsRepository(
        impl: UserPublicationsRepositoryImpl
    ): UserPublicationsRepository

    @Binds
    @Singleton
    abstract fun bindAdminModerationRepository(
        impl: AdminModerationRepositoryImpl
    ): AdminModerationRepository

    @Binds
    @Singleton
    abstract fun bindAdminReportsRepository(
        impl: AdminReportsRepositoryImpl
    ): AdminReportsRepository
}

