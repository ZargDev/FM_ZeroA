package com.zalune.fm_zeroa.domain.repository.usecase

import java.io.File
import javax.inject.Inject

/**
 * UseCase para copiar un archivo o carpeta.
 * @return true si la copia fue exitosa.
 */
class CopyFileUseCase @Inject constructor() {

    suspend operator fun invoke(sourcePath: String, targetDirPath: String): Boolean {
        return try {
            val source = File(sourcePath)
            val targetDir = File(targetDirPath)
            if (!targetDir.exists()) targetDir.mkdirs()
            val dest = File(targetDir, source.name)
            source.copyRecursively(dest, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
