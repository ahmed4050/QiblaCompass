package com.qibla.compass.di

import android.content.Context
import com.qibla.compass.data.QiblaRepositoryImpl
import com.qibla.compass.domain.QiblaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideQiblaRepository(
        @ApplicationContext context: Context
    ): QiblaRepository = QiblaRepositoryImpl(
        context = context
    )
}