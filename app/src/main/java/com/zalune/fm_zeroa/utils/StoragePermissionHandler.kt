// File: app/src/main/java/com/axelw578/bananafile/utils/StoragePermissionHandler.kt
package com.zalune.fm_zeroa.utils

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class StoragePermissionHandler(
    private val fragment: Fragment,
    private val permissionManager: PermissionManager,
    private val onGranted: () -> Unit
) {
    private val launcher = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (permissionManager.hasAllFilesPermission()) {
            onGranted()
        } else {
            Toast.makeText(
                fragment.requireContext(),
                "Sin este permiso no podemos listar tus archivos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun checkAndRequest() {
        val ctx = fragment.requireContext()
        when {
            permissionManager.isFirstRun() -> {
                // Aquí muestras tu propio diálogo confirmando
                // y tras aceptarlo:
                permissionManager.markFirstRunDone()
                launcher.launch(permissionManager.getManageAllFilesIntent(ctx))
            }
            !permissionManager.hasAllFilesPermission() -> {
                launcher.launch(permissionManager.getManageAllFilesIntent(ctx))
            }
            else -> onGranted()
        }
    }
}
