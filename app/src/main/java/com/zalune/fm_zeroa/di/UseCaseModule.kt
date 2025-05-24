package com.zalune.fm_zeroa.di


import com.zalune.fm_zeroa.domain.repository.usecase.CopyFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CutFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.DeleteFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.GetFileInfoUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.RenameFileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCopyFileUseCase() = CopyFileUseCase()

    @Provides
    fun provideCutFileUseCase() = CutFileUseCase()

    @Provides
    fun provideDeleteFileUseCase() = DeleteFileUseCase()

    @Provides
    fun provideRenameFileUseCase() = RenameFileUseCase()

    @Provides
    fun provideGetFileInfoUseCase() = GetFileInfoUseCase()
}
