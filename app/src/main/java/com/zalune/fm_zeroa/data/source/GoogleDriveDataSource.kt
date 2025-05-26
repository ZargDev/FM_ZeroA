package com.zalune.fm_zeroa.data.source


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class GoogleDriveDataSource @Inject constructor(
//    private val driveService: Drive
//) {
//    /**
//     * Sube un archivo local a Google Drive.
//     * Por simplicidad, aquí sólo emitimos 0% al inicio y 100% al final.
//     */
//    fun upload(filePath: String): Flow<Result<Float>> = flow {
//        emit(Result.success(0.0f))
//        val localFile = File(filePath)
//        val metadata = DriveFile().apply {
//            name = localFile.name
//            // opcional: parents = listOf("appDataFolder") u otra carpeta
//        }
//        val content = FileContent(null, localFile)
//        val created = driveService.files()
//            .create(metadata, content)
//            .setFields("id,name,size")
//            .execute()
//        // al terminar, reportamos 100%
//        emit(Result.success(1.0f))
//    }.flowOn(Dispatchers.IO)
//        .catch { e -> emit(Result.failure(e)) }
//
//    /**
//     * Descarga un archivo remoto a un Uri local.
//     */
//    fun download(fileId: String, destUri: Uri): Flow<Result<Float>> = flow {
//        emit(Result.success(0.0f))
//        val outputStream = driveService.files()
//            .get(fileId)
//            .executeMediaAsInputStream()
//            .use { input ->
//                kotlin.runCatching {
//                    android.content.ContextWrapper(null) // remplazar por context real
//                        .contentResolver
//                        .openOutputStream(destUri)!!
//                        .use { out -> input.copyTo(out) }
//                }
//            }
//        emit(Result.success(1.0f))
//    }.flowOn(Dispatchers.IO)
//        .catch { e -> emit(Result.failure(e)) }
//
//    /**
//     * Lista todos los archivos del usuario en Drive.
//     */
//    suspend fun listAll(): Result<List<DriveEntry>> = runCatching {
//        val result = driveService.files()
//            .list()
//            .setFields("files(id,name,size,webViewLink)")
//            .execute()
//        result.files.map { f ->
//            DriveEntry(
//                id = f.id,
//                name = f.name,
//                size = f.size ?: 0,
//                downloadUrl = f.webViewLink
//            )
//        }
//    }
//
//    /**
//     * Borra un archivo remoto en Drive.
//     */
//    suspend fun delete(fileId: String): Result<Unit> = runCatching {
//        driveService.files().delete(fileId).execute()
//    }
//}
//
///**
// * Data class interna para Drive — luego lo mapearás a tu `CloudFile`.
// */
//data class DriveEntry(
//    val id: String,
//    val name: String,
//    val size: Long,
//    val downloadUrl: String
//)