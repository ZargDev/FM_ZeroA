package com.zalune.fm_zeroa.domain.repository.usecase

import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CUploadFileUseCase @Inject constructor(
    private val repository: ICloudRepository
) {
    /**
     * @param filePath ruta local absoluta al archivo a subir.
     * @return Flow<Result<Float>> donde Float es progreso (0–1).
     */
    operator fun invoke(filePath: String): Flow<Result<Float>> =
        repository.upload(filePath)
}