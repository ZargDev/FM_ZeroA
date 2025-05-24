package com.zalune.fm_zeroa.domain.repository.usecase

import java.io.File
import javax.inject.Inject

/**
 * UseCase para renombrar un archivo o carpeta.
 * @return true si el renombrado fue exitoso.
 */
class RenameFileUseCase @Inject constructor() {

    suspend operator fun invoke(oldPath: String, newName: String): Boolean {
        return try {
            val file = File(oldPath)
            val newFile = File(file.parentFile, newName)
            file.renameTo(newFile)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
