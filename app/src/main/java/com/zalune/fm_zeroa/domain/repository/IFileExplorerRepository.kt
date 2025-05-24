package com.zalune.fm_zeroa.domain.repository

import android.net.Uri
import com.zalune.fm_zeroa.domain.model.FileItem

interface IFileExplorerRepository {
    fun listRootDirectories(): List<FileItem>
    fun listFiles(path: String): List<FileItem>
}
