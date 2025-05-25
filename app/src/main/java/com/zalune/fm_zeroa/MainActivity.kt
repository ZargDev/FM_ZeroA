package com.zalune.fm_zeroa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.zalune.fm_zeroa.presentation.ui.topbar.TopAppBarCustom
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.FragmentNavigator
import com.zalune.fm_zeroa.utils.OnBackPressedHandler
import com.zalune.fm_zeroa.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var permissionManager: PermissionManager
    private lateinit var topBar: TopAppBarCustom
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHostView: FrameLayout
    private var downX = 0f
    private val SWIPE_MIN_DISTANCE = 150

    // Mapas para gestionar títulos e íconos del BottomNavigationView
    private val originalTitles = mutableMapOf<Int, CharSequence>()
    private val iconColors = mapOf(
        R.id.menu_categories_fragment to R.color.bnv_icon_color_home_selected,
        R.id.file_list_fragment      to R.color.bnv_icon_color_files_selected,
        R.id.cloud_files_fragment    to R.color.bnv_icon_color_cloud_selected
    )

    private var currentTabId: Int = R.id.menu_categories_fragment
    // En las funciones onSwipeLeft() y onSwipeRight()
    private val tabs = listOf(
        R.id.file_list_fragment,       // Archivos (izquierda)
        R.id.menu_categories_fragment, // Menú (centro)
        R.id.cloud_files_fragment      // Nube (derecha)
    )
    // Variables para controlar el doble tap para salir
    private var backPressedOnce = false
    private val backPressHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostView = findViewById(R.id.nav_host_fragment)
        bottomNav    = findViewById(R.id.bottom_nav)
        // Configuración de la TopBar

        topBar = findViewById(R.id.topAppBar)
        topBar.setOnActionsClickListener(
            onSearch    = { /* ... */ },
            onViewMode  = { /* ... */ },
            onMore      = { /* ... */ },
            onPathClick = { /* ... */ }
        )
        // Configuración de Navigation Component
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        // Configuración de BottomNavigationView

        bottomNav.setupWithNavController(navController)
        bottomNav.itemIconTintList = null
        bottomNav.itemTextColor = null
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
        // Guarda títulos originales y los oculta
        bottomNav.menu.forEach { item ->
            originalTitles[item.itemId] = item.title.toString()
            item.title = ""
        }
        // Destaca la pestaña inicial y guarda su ID
        currentTabId = bottomNav.selectedItemId
        highlightMenuItem(bottomNav, currentTabId)
        // Evita recarga/animación si vuelven a pulsar la misma pestaña
        bottomNav.setOnItemReselectedListener { /* ... */ }
        // Manejador de selección de pestañas
        bottomNav.setOnItemSelectedListener { menuItem ->
            val destId = menuItem.itemId
            if (destId == currentTabId) return@setOnItemSelectedListener true

            val currentIndex = tabs.indexOf(currentTabId)
            val destIndex    = tabs.indexOf(destId)
            val isMovingToRight = destIndex > currentIndex

            // Aquí definimos los cuatro anims según la dirección
            val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = if (isMovingToRight) {
                // Navegación hacia la derecha
                listOf(
                    R.anim.slide_in_right_bounce,  // nueva entra desde la derecha
                    R.anim.slide_out_left_bounce,  // actual sale hacia la izquierda
                    R.anim.slide_in_left_bounce,   // al hacer POP entra desde la izquierda
                    R.anim.slide_out_right_bounce  // al hacer POP sale hacia la derecha
                )
            } else {
                // Navegación hacia la izquierda
                listOf(
                    R.anim.slide_in_left_bounce,   // nueva entra desde la izquierda
                    R.anim.slide_out_right_bounce, // actual sale hacia la derecha
                    R.anim.slide_in_right_bounce,  // al hacer POP entra desde la derecha
                    R.anim.slide_out_left_bounce   // al hacer POP sale hacia la izquierda
                )
            }

            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)               // Evita múltiples instancias
                .setRestoreState(true)                  // Restaura estado guardado
                .setPopUpTo(
                    navController.graph.startDestinationId,
                    /* inclusive = */ false,
                    /* saveState = */ true
                )
                .setEnterAnim(enterAnim)
                .setExitAnim (exitAnim)
                .setPopEnterAnim(popEnterAnim)
                .setPopExitAnim (popExitAnim)
                .build()

            highlightMenuItem(bottomNav, destId)
            navController.navigate(destId, null, options)
            currentTabId = destId
            true
        }

        // Listener de cambios de destino para TopBar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            highlightMenuItem(bottomNav, destination.id)
            when (destination.id) {
                R.id.menu_categories_fragment -> {
                    topBar.setTitleText("Menú")
                    topBar.showPathSection(false)
                }
                R.id.file_list_fragment -> {
                    topBar.setTitleText("")
                    topBar.showPathSection(true)
                }
                R.id.file_preview_fragment -> topBar.showPathSection(false)
                R.id.cloud_files_fragment -> {
                    topBar.setTitleText("Nube")
                    topBar.showPathSection(false)
                }
                else -> topBar.showPathSection(false)
            }
        }
        navController.graph.forEach { node ->
            if (node.id in tabs) {
                // Configurar cada tab como un destino raíz independiente
                node as FragmentNavigator.Destination
                node.label = null
            }
        }
        // Callback personalizado para manejar gesto Back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.nav_host_fragment)
                val currentDestId = navController.currentDestination?.id

                // Lista de IDs de los fragmentos principales
                val mainFragments = setOf(
                    R.id.menu_categories_fragment,
                    R.id.file_list_fragment,
                    R.id.cloud_files_fragment
                )

                // 1. Verificar si el fragmento actual maneja el back
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                if (currentFragment is OnBackPressedHandler && currentFragment.onBackPressed()) {
                    return
                }

                // 2. Si está en un fragmento principal, manejar salida
                if (currentDestId in mainFragments) {
                    if (backPressedOnce) {
                        finish()
                    } else {
                        backPressedOnce = true
                        Toast.makeText(
                            this@MainActivity,
                            "Presiona otra vez para salir",
                            Toast.LENGTH_SHORT
                        ).show()
                        backPressHandler.postDelayed({ backPressedOnce = false }, 2000)
                    }
                } else {
                    // 3. Si no es un fragmento principal, navegar hacia atrás
                    if (!navController.popBackStack()) {
                        // Si no hay más retroceso, también manejar salida
                        if (backPressedOnce) {
                            finish()
                        } else {
                            backPressedOnce = true
                            Toast.makeText(
                                this@MainActivity,
                                "Presiona otra vez para salir",
                                Toast.LENGTH_SHORT
                            ).show()
                            backPressHandler.postDelayed({ backPressedOnce = false }, 2000)
                        }
                    }
                }
            }
        })
    }
    // Funcon para la navegacion de fragments usando gestos
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                return super.dispatchTouchEvent(ev)
            }
            MotionEvent.ACTION_UP -> {
                val deltaX = ev.x - downX
                if (abs(deltaX) > SWIPE_MIN_DISTANCE) {
                    if (deltaX > 0) {
                        onSwipeRight() // Deslizó a la derecha
                    } else {
                        onSwipeLeft()  // Deslizó a la izquierda
                    }
                }
                return super.dispatchTouchEvent(ev)
            }
            else -> return super.dispatchTouchEvent(ev)
        }
    }
    private fun highlightMenuItem(
        bottomNav: BottomNavigationView,
        itemId: Int
    ) {
        bottomNav.menu.forEach { item ->
            if (item.itemId == itemId) {
                val title = originalTitles[itemId] ?: ""
                val color = ContextCompat.getColor(this, iconColors[itemId]!!)
                item.title = SpannableString(title).apply {
                    setSpan(ForegroundColorSpan(color), 0, length, 0)
                }
            } else {
                item.title = ""
            }
        }
    }
    private fun onSwipeLeft() {
        val idx = tabs.indexOf(currentTabId)
        if (idx < tabs.lastIndex) {
            bottomNav.selectedItemId = tabs[idx + 1] // Swipe izquierdo mueve a la derecha
        } else {
            bounce(navHostView, -50f)
        }
    }

    private fun onSwipeRight() {
        val idx = tabs.indexOf(currentTabId)
        if (idx > 0) {
            bottomNav.selectedItemId = tabs[idx - 1] // Swipe derecho mueve a la izquierda
        } else {
            bounce(navHostView, +50f)
        }
    }
    private fun bounce(view: View, offset: Float) {
        view.animate()
            .translationX(offset)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .translationX(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()

    fun onPathChanged(newPath: String) {
        Log.d("MAIN", "onPathChanged: $newPath")
        topBar.updatePath(newPath)
    }
}