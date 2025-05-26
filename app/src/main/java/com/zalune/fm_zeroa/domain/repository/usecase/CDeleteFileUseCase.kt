package com.zalune.fm_zeroa.domain.repository.usecase

import com.zalune.fm_zeroa.domain.model.CloudFile
import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import javax.inject.Inject

class CDeleteFileUseCase @Inject constructor(
    private val repository: ICloudRepository
) {
    /**
     * @param cloudFile archivo a eliminar.
     * @return Result<Unit> éxito o fallo.
     */
    suspend operator fun invoke(cloudFile: CloudFile): Result<Unit> =
        repository.delete(cloudFile)
}