package com.zalune.fm_zeroa.presentation.ui.topbar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.zalune.fm_zeroa.R
import com.google.android.material.appbar.MaterialToolbar

class TopAppBarCustom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.toolbarStyle
) : MaterialToolbar(context, attrs, defStyleAttr) {

    // Variables inicializadas con lateinit
    private lateinit var tvPath: TextView
    private lateinit var tvStorageStats: TextView

    // Estado del panel
    private var isPanelExpanded = false
    private var currentFullPath: String = ""

    // Callbacks
    private var onSearch: (() -> Unit)? = null
    private var onViewMode: (() -> Unit)? = null
    private var onMore: (() -> Unit)? = null
    private var onPathClick: (() -> Unit)? = null


    init {
        // Inflamos sólo el <merge> con los TextView encima de este Toolbar
        LayoutInflater.from(context)
            .inflate(R.layout.top_app_bar_custom, this, true)

        // Inflamos el menú de iconos
        inflateMenu(R.menu.top_app_bar_menu)

        // Referencia de la ruta resumida
        tvPath = findViewById(R.id.tvPath)
        tvPath.setOnClickListener { showFullPathPopup() }

        // Listener de menú
        setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_search    -> onSearch?.invoke()
                R.id.action_view_mode -> onViewMode?.invoke()
                R.id.action_more -> {
                    Log.d("TOPBAR","Se pulsó ‘Más’")
                    onMore?.invoke()
                }
                else                  -> return@setOnMenuItemClickListener false
            }
            true
        }
    }

    fun updatePath(fullPath: String) {
        currentFullPath = fullPath
        tvPath.text = fullPath.split("/")
            .takeIf { it.size > 3 }
            ?.let { "../${it.takeLast(3).joinToString("/")}" }
            ?: fullPath
    }

    /** Z-ADD Título simple en lugar de ruta */
    fun setTitleText(text: String) {
        // Si tu layout tiene un TextView para título en vez de tvPath:
        tvPath.text = text
    }
    /** Z-ADD Oculta la sección ruta+stats */
    fun showPathSection(show: Boolean) {
        val vis = if (show) View.VISIBLE else View.GONE
        tvPath.visibility = vis

    }

    fun setOnActionsClickListener(
        onSearch: () -> Unit,
        onViewMode: () -> Unit,
        onMore: () -> Unit,
        onPathClick: () -> Unit
    ) {
        this.onSearch = onSearch
        this.onViewMode = onViewMode
        this.onMore = onMore
        this.onPathClick = onPathClick
    }

    fun updateStorageStats(used: String, free: String) {
        if (::tvStorageStats.isInitialized) {
            tvStorageStats.text = "$used • $free"
        }
    }

    /** Muestra un PopupWindow con la ruta completa + botón copiar */
    private fun showFullPathPopup() {
        // 1) Inflar layout
        val popupView = LayoutInflater.from(context)
            .inflate(R.layout.popup_full_path, null, false)

        // 2) Rellenar datos
        val tvPopup = popupView.findViewById<TextView>(R.id.tvPopupFullPath)
        tvPopup.text = currentFullPath
        popupView.findViewById<ImageButton>(R.id.btnCopyPopup)
            .setOnClickListener {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE)
                        as ClipboardManager
                cm.setPrimaryClip(
                    ClipData.newPlainText("Ruta completa", currentFullPath)
                )
                Toast.makeText(context,
                    R.string.copy_success, Toast.LENGTH_SHORT
                ).show()
            }

        // 3) Crear PopupWindow
        val popup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 8f
            animationStyle = android.R.style.Animation_Dialog
        }

        // 4) Mostrar justo debajo de tvPath
        popup.showAsDropDown(tvPath, 0, 8)
    }

    fun enterCopyMode(onCancel: ()->Unit) {
        setNavigationIcon(R.drawable.ic_close)
        setNavigationOnClickListener { onCancel() }
    }
    fun exitCopyMode() {
        navigationIcon = null
        setNavigationOnClickListener(null)
    }


    /** Modo “cancelar copia” */
    fun showCancelSelection(onCancel: ()->Unit) {
        // Cambia el icono de la izquierda por una X y asigna onClick
        setNavigationIcon(R.drawable.ic_close)
        setNavigationOnClickListener { onCancel() }
    }

    /** Sale del modo “cancelar copia” */
    fun hideCancelSelection() {
        navigationIcon = null
        setNavigationOnClickListener(null)
    }

    /** (si lo necesitas) */
    fun updateSelectionCount(n: Int) {
        title = "$n seleccionados"
    }

}


