package com.zalune.fm_zeroa.data.mapper

import androidx.documentfile.provider.DocumentFile
import com.zalune.fm_zeroa.domain.model.FileItem
import javax.inject.Inject

class DocumentFileMapper @Inject constructor() {
    fun map(doc: DocumentFile): FileItem? {
        val uri = doc.uri ?: return null
        return FileItem(
            name = doc.name.orEmpty(),
            uri = uri,
            isDirectory = doc.isDirectory,
            size = doc.length()
        )
    }
}

