package com.zalune.fm_zeroa.data.repository

import com.zalune.fm_zeroa.domain.repository.IFileOperationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.copyRecursively
import kotlin.io.copyTo
import kotlin.io.deleteRecursively

// File: app/src/main/java/com/axelw578/bananafile/data/repository/FileOperationRepository.kt
@Singleton
class FileOperationRepository @Inject constructor(): IFileOperationRepository {
    override suspend fun copy(source: File, targetDir: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!targetDir.exists()) targetDir.mkdirs()
                val dest = File(targetDir, source.name)
                if (source.isDirectory) source.copyRecursively(dest, overwrite = true)
                else source.copyTo(dest, overwrite = true)
                true
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }

    override suspend fun move(source: File, targetDir: File): Boolean =
        withContext(Dispatchers.IO) {
            val ok = copy(source, targetDir)
            if (ok) source.deleteRecursively() else false
        }

    override suspend fun delete(source: File): Boolean = withContext(Dispatchers.IO) {
        if (source.isDirectory) source.deleteRecursively() else source.delete()
    }
}
