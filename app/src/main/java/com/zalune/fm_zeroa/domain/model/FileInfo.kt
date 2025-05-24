package com.zalune.fm_zeroa.domain.model

import java.text.DateFormat
import java.io.File

data class FileInfo(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModified: Long,
    val permissions: List<Pair<String, Boolean>>
) {
    fun toDisplayString(): String {
        val type = if (isDirectory) "Carpeta" else "Archivo"
        val sizeKb = sizeBytes / 1024
        val permsText = permissions.joinToString(separator = "\n") { (label, ok) ->
            "$label: ${if (ok) "Sí" else "No"}"
        }
        val date = DateFormat.getDateTimeInstance().format(lastModified)
        return buildString {
            appendLine("Ruta: $path")
            appendLine("Nombre: $name")
            appendLine("Tipo: $type")
            appendLine("Tamaño: ${sizeKb} KB")
            appendLine("Última modificación: $date")
            appendLine("Permisos:")
            append(permsText)
        }
    }
}
