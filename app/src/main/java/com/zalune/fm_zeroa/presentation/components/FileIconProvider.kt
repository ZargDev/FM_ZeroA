package com.zalune.fm_zeroa.presentation.components

import androidx.annotation.DrawableRes
import com.zalune.fm_zeroa.R
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
@Singleton
class FileIconProvider @Inject constructor() {

    // Corregido: Quitamos @DrawableRes del tipo del Map
    private val map: MutableMap<String, Int> = mutableMapOf(
        "docx" to R.drawable.ic_word,
        "xlsx" to R.drawable.ic_excel,
        "pptx" to R.drawable.ic_powerpoint,
        "pdf"  to R.drawable.ic_pdf,
        "jpg"  to R.drawable.ic_jpg,
        "jpeg" to R.drawable.ic_jpeg,
        "png"  to R.drawable.ic_png,
        "mp4"  to R.drawable.ic_video,
        "mp3"  to R.drawable.ic_music,
        "wav"  to R.drawable.ic_music,
        "rar"  to R.drawable.ic_rar,
        "zip"  to R.drawable.ic_rar,
        "default" to R.drawable.ic_unknown
    )

    @DrawableRes
    fun getIcon(ext: String, isDirectory: Boolean): Int {
        return if (isDirectory) {
            R.drawable.ic_folder
        } else {
            map[ext.lowercase()] ?: map["default"]!!
        }
    }
}