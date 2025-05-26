package com.zalune.fm_zeroa.utils

import android.content.Context
import android.widget.Toast
import java.io.File

object FileCreationUtils {

    fun createFolder(context: Context, parentPath: String, folderName: String) {
        val newFolder = File(parentPath, folderName)
        if (!newFolder.exists()) {
            val success = newFolder.mkdirs()
            Toast.makeText(
                context,
                if (success) "Carpeta creada" else "Error al crear carpeta",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(context, "La carpeta ya existe", Toast.LENGTH_SHORT).show()
        }
    }

    fun createTxtFile(context: Context, parentPath: String, fileName: String) {
        val txtFile = File(parentPath, "$fileName.txt")
        if (!txtFile.exists()) {
            val success = txtFile.createNewFile()
            Toast.makeText(
                context,
                if (success) "Archivo creado" else "Error al crear archivo",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(context, "El archivo ya existe", Toast.LENGTH_SHORT).show()
        }
    }
}