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
            val targetDir = File(targetDirPath).apply { if (!exists()) mkdirs() }
            val dest = File(targetDir, source.name)
            val success = if (source.isDirectory) {
                source.copyRecursively(dest, overwrite = true)
            } else {
                source.copyTo(dest, overwrite = true)
                true
            }
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
