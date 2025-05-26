package com.zalune.fm_zeroa.data.source

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {

    private fun rootRef(): StorageReference =
        storage.reference.child("user_files")

    /**
     * Sube un archivo local (filePath) a Firebase Storage,
     * emitiendo el progreso (0.0–1.0) y luego cerrando con éxito o error.
     */
    fun upload(filePath: String): Flow<Result<Float>> = callbackFlow {
        val fileUri = Uri.parse(filePath)
        // usa el timestamp + nombre original para evitar colisiones
        val destRef = rootRef().child("${System.currentTimeMillis()}_${fileUri.lastPathSegment}")
        val uploadTask = destRef.putFile(fileUri)

        val progressListener = { snapshot: com.google.firebase.storage.UploadTask.TaskSnapshot ->
            val progress = snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount
            trySend(Result.success(progress))
            Unit // Añade esto para retornar Unit explícitamente
        }
        uploadTask.addOnProgressListener(progressListener)
        uploadTask
            .addOnSuccessListener {
                trySend(Result.success(1.0f)) // 100%
                close()
            }
            .addOnFailureListener { e ->
                trySend(Result.failure(e))
                close(e)
            }

        awaitClose {
            uploadTask.removeOnProgressListener(progressListener)
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    /**
     * Descarga un archivo remoto (por su ruta en storagePath) a un archivo local
     * (destFileUri) y emite progreso.
     *
     * @param storagePath ruta completa dentro de Firebase Storage (p.ej. "user_files/12345_img.png")
     * @param destFileUri Uri de destino en almacenamiento interno/external
     */
    fun download(storagePath: String, destFileUri: Uri): Flow<Result<Float>> = callbackFlow {
        val srcRef = storage.reference.child(storagePath)
        val downloadTask = srcRef.getFile(destFileUri)

        val progressListener = { snapshot: com.google.firebase.storage.FileDownloadTask.TaskSnapshot ->
            val progress = snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount
            trySend(Result.success(progress))
            Unit // Añade esto para retornar Unit explícitamente
        }
        downloadTask.addOnProgressListener(progressListener)
        downloadTask
            .addOnSuccessListener {
                trySend(Result.success(1.0f))
                close()
            }
            .addOnFailureListener { e ->
                trySend(Result.failure(e))
                close(e)
            }

        awaitClose {
            downloadTask.removeOnProgressListener(progressListener)
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    /**
     * Lista todos los archivos bajo el nodo "user_files/".
     */
    suspend fun listAll(): Result<List<FirebaseCloudEntry>> {
        return try {
            val result = rootRef().listAll().await()
            // Para cada ítem obtenemos metadata y URL de descarga
            val files = result.items.map { ref ->
                val meta = ref.metadata.await()
                val url = ref.downloadUrl.await().toString()
                FirebaseCloudEntry(
                    storagePath = ref.path,
                    name = ref.name,
                    size = meta.sizeBytes,
                    downloadUrl = url
                )
            }
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un archivo remoto.
     */
    suspend fun delete(storagePath: String): Result<Unit> {
        return try {
            storage.reference.child(storagePath).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Data class interna para representar un archivo en Firebase.
 * Luego lo adaptaremos a nuestro modelo de dominio `CloudFile`.
 */
data class FirebaseCloudEntry(
    val storagePath: String,
    val name: String,
    val size: Long,
    val downloadUrl: String
)