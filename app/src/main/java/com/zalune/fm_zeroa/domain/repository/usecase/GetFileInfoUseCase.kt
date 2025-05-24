package com.zalune.fm_zeroa.domain.repository.usecase

import com.zalune.fm_zeroa.domain.model.FileInfo
import java.io.File
import javax.inject.Inject

/**
 * UseCase para obtener información detallada de un archivo o carpeta.
 */
class GetFileInfoUseCase @Inject constructor() {

    suspend operator fun invoke(path: String): FileInfo {
        val file = File(path)
        return FileInfo(
            path = file.absolutePath,
            name = file.name,
            isDirectory = file.isDirectory,
            sizeBytes = if (file.isFile) file.length() else file.walk().map { it.length() }.sum(),
            lastModified = file.lastModified(),
            permissions = listOf(
                "Readable" to file.canRead(),
                "Writable" to file.canWrite(),
                "Executable" to file.canExecute()
            )
        )
    }
}
