package com.zalune.fm_zeroa.data.repositories

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.zalune.fm_zeroa.domain.model.FileItem
import com.zalune.fm_zeroa.domain.repository.IFileExplorerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileExplorerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IFileExplorerRepository {
    override fun listRootDirectories(): List<FileItem> {
        val roots = mutableListOf<FileItem>()
        // Agrega volúmenes montados
        File("/storage").listFiles()?.forEach { file ->
            roots.add(FileItem(file.name, file.toUri(), file.isDirectory, file.length()))
        }
        return roots
    }

    override fun listFiles(path: String): List<FileItem> {
        val dir = File(path)
        return dir.listFiles()?.map { file ->
            FileItem(file.name, file.toUri(), file.isDirectory, file.length())
        }.orEmpty()
    }

    private fun toFileItem(file: File): FileItem = FileItem(
        name = file.name,
        uri = file.toUri(),
        isDirectory = file.isDirectory,
        size = file.length()
    )
}

