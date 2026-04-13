package com.example.triplink.di

import com.example.triplink.data.repository.admin.ModerationRepositoryImpl
import com.example.triplink.data.repository.admin.ReportRepositoryImpl
import com.example.triplink.data.repository.user.AuthRepositoryImpl
import com.example.triplink.data.repository.user.BadgeRepositoryImpl
import com.example.triplink.data.repository.user.CommentRepositoryImpl
import com.example.triplink.data.repository.user.FavoriteRepositoryImpl
import com.example.triplink.data.repository.user.PublicationRepositoryImpl
import com.example.triplink.data.repository.user.UserProfileRepositoryImpl
import com.example.triplink.domain.repository.admin.ModerationRepository
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.auth.AuthRepository
import com.example.triplink.domain.repository.badge.BadgeRepository
import com.example.triplink.domain.repository.comment.CommentRepository
import com.example.triplink.domain.repository.favorite.FavoriteRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
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
