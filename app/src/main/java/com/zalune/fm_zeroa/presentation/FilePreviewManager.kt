package com.zalune.fm_zeroa.presentation

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePreviewManager @Inject constructor(
    private val context: Context
) {
    fun openWithIntent(uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    fun loadPdfPage(uri: Uri, pageIndex: Int): PdfRenderer.Page? {
        val pfd: ParcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r") ?: return null
        val renderer = PdfRenderer(pfd)
        if (pageIndex >= renderer.pageCount) return null
        return renderer.openPage(pageIndex)
    }
}
