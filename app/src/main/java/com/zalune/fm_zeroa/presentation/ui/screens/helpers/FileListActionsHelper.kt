package com.zalune.fm_zeroa.presentation.ui.screens.helpers

import android.content.Context
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.utils.FileCreationUtils

object FileListActionsHelper {

    fun showCreateFolderDialog(
        context: Context,
        parentPath: String,
        onCreated: () -> Unit
    ) {
        val input = EditText(context).apply {
            hint = "Nombre de la carpeta"
        }
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog)
            .setTitle("Crear carpeta")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    FileCreationUtils.createFolder(context, parentPath, name)
                    onCreated()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun showCreateTxtDialog(
        context: Context,
        parentPath: String,
        onCreated: () -> Unit
    ) {
        val input = EditText(context).apply {
            hint = "Nombre del archivo"
        }
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog)
            .setTitle("Crear archivo .txt")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    FileCreationUtils.createTxtFile(context, parentPath, name)
                    onCreated()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun showAboutDialog(context: Context) {
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog)
            .setTitle("Acerca de BananaFile")
            .setMessage("BananaFile v1.0\nDesarrollado por PedrosAndCompanyDesune…")
            .setPositiveButton("OK", null)
            .show()
    }
}