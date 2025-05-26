// File: app/src/main/java/com/axelw578/bananafile/presentation/ui/viewmodel/ClipboardState.kt
package com.zalune.fm_zeroa.presentation.ui.viewmodel

sealed class ClipboardState {
    object None : ClipboardState()
    data class Copy(val items: List<String>) : ClipboardState()
    data class Cut(val items: List<String>)  : ClipboardState()
}
