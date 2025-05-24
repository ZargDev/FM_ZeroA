/* File: app/src/main/java/com/axelw578/bananafile/presentation/navigation/NavigationItem.kt */
package com.zalune.fm_zeroa.presentation.navigation

import android.net.Uri

/**
 * Representa un elemento de la ruta de navegación (breadcrumb).
 */
data class NavigationItem(
    val label: String,
    val uri: Uri
)