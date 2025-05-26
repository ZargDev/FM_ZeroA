/* File: app/src/main/java/com/axelw578/bananafile/presentation/navigation/NavigationManager.kt */
package com.zalune.fm_zeroa.presentation.navigation

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestiona la navegación entre directorios como una pila (stack) de URIs.
 */
@Singleton
class NavigationManager @Inject constructor(
    private val context: Context
) {
    private val stack: ArrayDeque<Uri> = ArrayDeque()

    /** URI actual (último en la pila) */
    val currentUri: Uri?
        get() = stack.lastOrNull()

    /** Navega a un nuevo directorio agregando su URI a la pila */
    fun navigateTo(uri: Uri) {
        stack.addLast(uri)
    }

    /**
     * Retrocede un nivel en la navegación.
     * @return la URI actual tras retroceder, o null si no hay más.
     */
    fun navigateUp(): Uri? {
        if (stack.size > 1) {
            stack.removeLast()
        }
        return currentUri
    }

    /**
     * Genera la lista de breadcrumbs a partir de la pila de URIs.
     */
    fun getBreadcrumbs(): List<NavigationItem> = stack.map { uri ->
        val doc = DocumentFile.fromTreeUri(context, uri)
        val name = doc?.name ?: uri.lastPathSegment.orEmpty()
        NavigationItem(label = name, uri = uri)
    }

    /**
     * Inicializa la navegación con la URI raíz.
     */
    fun resetToRoot(rootUri: Uri) {
        stack.clear()
        stack.addLast(rootUri)
    }
}
