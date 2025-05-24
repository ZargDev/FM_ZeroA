package com.zalune.fm_zeroa.domain.repository.usecase


import java.io.File
import javax.inject.Inject

/**
 * UseCase para borrar un archivo o carpeta de forma permanente.
 * @return true si el borrado fue exitoso.
 */
class DeleteFileUseCase @Inject constructor() {

    suspend operator fun invoke(path: String): Boolean {
        return try {
            val file = File(path)
            file.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
