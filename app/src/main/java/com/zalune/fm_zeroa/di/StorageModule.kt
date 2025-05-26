package com.zalune.fm_zeroa.di

import com.google.firebase.storage.FirebaseStorage
import com.zalune.fm_zeroa.data.repository.CloudRepository
import com.zalune.fm_zeroa.data.source.FirebaseStorageDataSource
import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage =
        FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        storage: FirebaseStorage
    ): FirebaseStorageDataSource =
        FirebaseStorageDataSource(storage)

    @Provides
    @Singleton
    fun provideCloudRepository(
        firebaseDs: FirebaseStorageDataSource
    ): ICloudRepository =
        CloudRepository(firebaseDs)
}