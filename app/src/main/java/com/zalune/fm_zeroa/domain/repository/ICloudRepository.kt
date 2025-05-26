package com.zalune.fm_zeroa.domain.repository

import com.zalune.fm_zeroa.domain.model.CloudFile
import kotlinx.coroutines.flow.Flow

interface ICloudRepository {

    /**
     * Sube un archivo a la nube.
     * @param filePath Ruta local absoluta del archivo.
     * @return flujo que emite progreso (0.0f - 1.0f), o error.
     */
    fun upload(filePath: String): Flow<Result<Float>>

    /**
     * Descarga un archivo desde la nube.
     * @param cloudFile Archivo remoto a descargar.
     * @return flujo que emite progreso (0.0f - 1.0f), o error.
     */
    fun download(cloudFile: CloudFile): Flow<Result<Float>>

    /**
     * Lista todos los archivos disponibles en la nube.
     */
    suspend fun listAll(): Result<List<CloudFile>>

    /**
     * Elimina un archivo de la nube.
     */
    suspend fun delete(cloudFile: CloudFile): Result<Unit>
}