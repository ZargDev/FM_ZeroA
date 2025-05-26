package com.zalune.fm_zeroa.domain.repository.usecase

import com.zalune.fm_zeroa.domain.model.CloudFile
import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CDownloadFileUseCase @Inject constructor(
    private val repository: ICloudRepository
) {
    /**
     * @param cloudFile metadatos del archivo en la nube (incluye provider y URL/ID).
     * @return Flow<Result<Float>> donde Float es progreso (0–1).
     */
    operator fun invoke(cloudFile: CloudFile): Flow<Result<Float>> =
        repository.download(cloudFile)
}