package com.zalune.fm_zeroa.domain.usecase

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import javax.inject.Inject

class FileOperationsUseCase @Inject constructor(
    private val context: Context
) {
    suspend fun copyFile(source: DocumentFile, destDir: DocumentFile): Boolean {
        val inStream = context.contentResolver.openInputStream(source.uri) ?: return false
        val outFile = destDir.createFile(source.type ?: "*/*", source.name!!) ?: return false
        val outStream = context.contentResolver.openOutputStream(outFile.uri) ?: return false
        inStream.use { input -> outStream.use { output -> input.copyTo(output) } }
        return true
    }

    suspend fun moveFile(source: DocumentFile, destDir: DocumentFile): Boolean {
        val copied = copyFile(source, destDir)
        return if (copied) source.delete() else false
    }

    suspend fun deleteFile(file: DocumentFile): Boolean {
        return file.delete()
    }

    suspend fun createDirectory(parent: DocumentFile, name: String): DocumentFile? {
        return parent.createDirectory(name)
    }
}
