package com.zalune.fm_zeroa.presentation.ui.fabmenu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.FabActionsMenuBinding

class FloatingActionsMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding: FabActionsMenuBinding = FabActionsMenuBinding.inflate(
        LayoutInflater.from(context),
        this,
        true // attach to parent so root = this
    )
    private var expanded = false

    init {
        // Overlay captura toques fuera de los fabs
        binding.overlay.setOnClickListener { hideMenu() }
        binding.fabMain.setOnClickListener { toggleMenu() }
    }

    private fun toggleMenu() {
        if (expanded) hideMenu() else showMenu()
    }

    private fun showMenu() {
        expanded = true
        binding.overlay.visibility = View.VISIBLE
        binding.fabCreateFolder.visibility = View.VISIBLE
        binding.fabCreateTxt.visibility = View.VISIBLE

        // Calcular desplazamientos dinámicos
        val mainSizePx = binding.fabMain.height.toFloat()
        val marginPx = 16 * context.resources.displayMetrics.density
        val offsetFolder = -(mainSizePx + marginPx)
        val offsetTxt    = -(2 * mainSizePx + 2 * marginPx)

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.fabCreateFolder, "translationY", 0f, offsetFolder),
                ObjectAnimator.ofFloat(binding.fabCreateTxt,    "translationY", 0f, offsetTxt)
            )
            duration = 200
            start()
        }
    }

    private fun hideMenu() {
        expanded = false
        binding.overlay.visibility = View.GONE

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.fabCreateFolder, "translationY", binding.fabCreateFolder.translationY, 0f),
                ObjectAnimator.ofFloat(binding.fabCreateTxt,    "translationY", binding.fabCreateTxt.translationY,    0f)
            )
            duration = 200
            addListener(onEnd = {
                binding.fabCreateFolder.visibility = View.GONE
                binding.fabCreateTxt.visibility    = View.GONE
            })
            start()
        }
    }

    /** Asigna callback para crear carpeta */
    fun setOnCreateFolder(onClick: () -> Unit) {
        binding.fabCreateFolder.setOnClickListener {
            onClick()
            hideMenu()
        }
    }

    /** Asigna callback para crear archivo txt */
    fun setOnCreateTxt(onClick: () -> Unit) {
        binding.fabCreateTxt.setOnClickListener {
            onClick()
            hideMenu()
        }
    }

    /** Cambia FAB principal a modo "pegar" */
    fun showPasteMode(onPaste: ()->Unit) {
        binding.overlay.visibility = View.VISIBLE
        binding.fabCreateFolder.visibility = View.GONE
        binding.fabCreateTxt.visibility    = View.GONE
        binding.fabMain.setImageResource(android.R.drawable.ic_menu_save)
        binding.fabMain.setOnClickListener { onPaste() }
    }

    // En FloatingActionsMenu.kt
    fun hidePasteMode() {
        binding.overlay.visibility = View.GONE
        // restaurar icono y comportamiento original:
        binding.fabMain.setImageResource(R.drawable.ic_add)
        binding.fabMain.setOnClickListener { toggleMenu() }
    }

}