package com.zalune.fm_zeroa.di

import com.zalune.fm_zeroa.domain.model.CloudProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudModule {

    @Provides
    @Singleton
    @AppQualifier
    fun provideCloudProvider(): CloudProvider {
        // Aquí decides el valor por defecto o lees de DataStore/SharedPrefs
        return CloudProvider.FIREBASE
    }
}