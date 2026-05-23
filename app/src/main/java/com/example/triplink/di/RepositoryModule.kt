package com.example.triplink.di

import com.example.triplink.data.repository.remote.admin.ModerationRepositoryImpl
import com.example.triplink.data.repository.remote.admin.ReportRepositoryImpl
import com.example.triplink.data.repository.remote.user.AuthRepositoryImpl
import com.example.triplink.data.repository.remote.user.BadgeRepositoryImpl
import com.example.triplink.data.repository.remote.user.CommentRepositoryImpl
import com.example.triplink.data.repository.remote.user.CommentModerationRepositoryImpl
import com.example.triplink.data.repository.remote.user.FavoriteRepositoryImpl
import com.example.triplink.data.repository.remote.user.PublicationRepositoryImpl
import com.example.triplink.data.repository.remote.user.UserProfileRepositoryImpl
import com.example.triplink.domain.repository.admin.ModerationRepository
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.user.AuthRepository
import com.example.triplink.domain.repository.user.BadgeRepository
import com.example.triplink.domain.repository.user.CommentModerationRepository
import com.example.triplink.domain.repository.user.CommentRepository
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
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
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindPublicationRepository(
        impl: PublicationRepositoryImpl
    ): PublicationRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        impl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindCommentModerationRepository(
        impl: CommentModerationRepositoryImpl
    ): CommentModerationRepository

    @Binds
    @Singleton
    abstract fun bindBadgeRepository(
        impl: BadgeRepositoryImpl
    ): BadgeRepository

    @Binds
    @Singleton
    abstract fun bindModerationRepository(
        impl: ModerationRepositoryImpl
    ): ModerationRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository
}
