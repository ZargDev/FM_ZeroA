package com.zalune.fm_zeroa.domain.repository.usecase


import java.io.File
import javax.inject.Inject

/**
 * UseCase para cortar (mover) un archivo o carpeta.
 * @return true si el movimiento fue exitoso.
 */
class CutFileUseCase @Inject constructor() {
    suspend operator fun invoke(sourcePath: String, targetDirPath: String): Boolean {
        return try {
            val source = File(sourcePath)
            val targetDir = File(targetDirPath).apply { if (!exists()) mkdirs() }
            val dest = File(targetDir, source.name)
            val copied = if (source.isDirectory) {
                source.copyRecursively(dest, overwrite = true)
            } else {
                source.copyTo(dest, overwrite = true)
                true
            }
            if (!copied) return false
            val deleted = if (source.isDirectory) source.deleteRecursively() else source.delete()
            deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
