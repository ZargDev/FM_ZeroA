package com.zalune.fm_zeroa.presentation.ui.screens.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.zalune.fm_zeroa.R
import java.io.File

class FilePreviewFragment : Fragment(R.layout.fragment_file_preview) {
    // Tu lógica de previsualización existente...

    companion object {
        /**
         * Lanza ACTION_VIEW para el File dado, usando FileProvider
         * asegúrate de tener configurado el provider en el Manifest y file_paths.xml.
         */
        fun openFile(context: Context, file: File) {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val mime = context.contentResolver.getType(uri) ?: "*/*"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

}
