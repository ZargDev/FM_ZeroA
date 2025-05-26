package com.zalune.fm_zeroa.domain.repository.usecase

import com.zalune.fm_zeroa.domain.model.CloudFile
import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import javax.inject.Inject

class CListFilesUseCase @Inject constructor(
    private val repository: ICloudRepository
) {
    /**
     * @return Result<List<CloudFile>> con todos los archivos en la nube.
     */
    suspend operator fun invoke(): Result<List<CloudFile>> =
        repository.listAll()
}