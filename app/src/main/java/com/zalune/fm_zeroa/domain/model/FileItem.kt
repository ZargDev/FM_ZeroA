package com.zalune.fm_zeroa.domain.model

import android.net.Uri

/**
 * Representa un archivo o carpeta en el explorador.
 */
data class FileItem(
    val name: String,
    val uri: Uri,
    val isDirectory: Boolean,
    val size: Long
)
