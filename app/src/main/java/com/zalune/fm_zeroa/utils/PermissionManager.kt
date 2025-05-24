package com.zalune.fm_zeroa.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Ya existente...

    /** Comprueba si tenemos el permiso MANAGE_EXTERNAL_STORAGE (All files access) */
    fun hasAllFilesPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.os.Environment.isExternalStorageManager()
        } else {
            // En API < 30, basta con READ/WRITE
            context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    /** Construye el Intent para llevar al usuario a la pantalla de Ajustes de “Gestionar todos los archivos” */
    fun getManageAllFilesIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
                // no añadir FLAG_ACTIVITY_NEW_TASK aquí
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }

    private companion object {
        const val PREFS_NAME = "banana_file_prefs"
        const val KEY_FIRST_RUN = "first_run_done"
    }

    /** Devuelve true si es la primera vez que se abre la app */
    fun isFirstRun(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    /** Marca que ya no es primer arranque */
    fun markFirstRunDone() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_FIRST_RUN, false)
            .apply()
    }


}
