// app/src/main/java/com/axelw578/bananafile/di/DataModule.kt
package com.zalune.fm_zeroa.di

import com.zalune.fm_zeroa.data.repositories.FileExplorerRepository
import com.zalune.fm_zeroa.domain.repository.IFileExplorerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindFileExplorerRepository(
        impl: FileExplorerRepository
    ): IFileExplorerRepository
}
