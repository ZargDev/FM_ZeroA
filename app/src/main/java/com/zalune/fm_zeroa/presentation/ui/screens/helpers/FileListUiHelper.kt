package com.zalune.fm_zeroa.presentation.ui.screens.helpers

import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zalune.fm_zeroa.MainActivity
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.FragmentFileListBinding
import com.zalune.fm_zeroa.presentation.adapter.FileListAdapter
import com.zalune.fm_zeroa.presentation.ui.topbar.TopAppBarCustom
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileListViewModel
import com.zalune.fm_zeroa.utils.PermissionManager

object FileListUiHelper {

    fun initRecycler(
        binding: FragmentFileListBinding,
        adapter: FileListAdapter
    ) {
        binding.rvFileList.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvFileList.adapter = adapter
    }

    fun observeViewModel(
        fragment: Fragment,
        binding: FragmentFileListBinding,
        adapter: FileListAdapter,
        viewModel: FileListViewModel
    ) {
        viewModel.currentPath.observe(fragment.viewLifecycleOwner) { path ->
            (fragment.activity as? MainActivity)?.onPathChanged(path)
        }
        viewModel.fileList.observe(fragment.viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvStatus.isVisible = list.isEmpty()
            if (list.isEmpty()) {
                binding.tvStatus.text = viewModel.errorMsg.value ?: "Carpeta vacía"
            }
        }
        viewModel.errorMsg.observe(fragment.viewLifecycleOwner) { err ->
            val shouldShow = viewModel.fileList.value.isNullOrEmpty()
            binding.tvStatus.apply {
                isVisible = shouldShow
                text = err ?: text
            }
        }
    }

    fun setupToolbar(
        fragment       : Fragment,
        topBar         : TopAppBarCustom,
        onSearch       : () -> Unit,
        onViewMode     : () -> Unit,
        onMultiSelect  : () -> Unit,
        onCreateFolder : () -> Unit,
        onCreateTxt    : () -> Unit,
        onSettings     : () -> Unit,
        onAbout        : () -> Unit,
        onExit         : () -> Unit,
        onPathClick    : () -> Unit
    ) {
        topBar.setOnActionsClickListener(
            onSearch    = onSearch,
            onViewMode  = onViewMode,
            onMore      = onMore@{
                // detectamos el anchor (el icono “Más”)
                val anchor = topBar.findViewById<View>(R.id.action_more)
                if (anchor == null) return@onMore

                PopupMenu(fragment.requireContext(), anchor).apply {
                    menuInflater.inflate(R.menu.menu_more_options, menu)
                    MenuCompat.setGroupDividerEnabled(menu, true)
                    setOnMenuItemClickListener { mi ->
                        when (mi.itemId) {
                            R.id.action_more_multiselect -> onMultiSelect()
                            R.id.action_more_create -> {
                                // segundo popup para “Crear”
                                PopupMenu(fragment.requireContext(), anchor).apply {
                                    menuInflater.inflate(R.menu.menu_create_options, menu)
                                    setOnMenuItemClickListener { ci ->
                                        when (ci.itemId) {
                                            R.id.action_create_folder -> onCreateFolder()
                                            R.id.action_create_txt    -> onCreateTxt()
                                            else -> return@setOnMenuItemClickListener false
                                        }
                                        true
                                    }
                                }.show()
                            }
                            R.id.action_more_settings -> onSettings()
                            R.id.action_more_about    -> onAbout()
                            R.id.action_more_exit     -> onExit()
                        }
                        true
                    }
                }.show()
            },
            onPathClick = onPathClick
        )
    }

    fun setupBackHandling(
        fragment    : Fragment,
        viewModel   : FileListViewModel,
        initialPath : String
    ) {
        var lastBackPress = 0L
        fragment.requireActivity()
            .onBackPressedDispatcher
            .addCallback(fragment.viewLifecycleOwner, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 1) Si estamos en modo búsqueda, salimos de él y restauramos la carpeta previa
                    if (viewModel.isInSearchMode()) {
                        viewModel.exitSearchMode()
                        return
                    }
                    // 2) Si no estamos en la carpeta raíz, subimos un nivel
                    val current = viewModel.currentPath.value.orEmpty()
                    if (current != initialPath) {
                        val parent = current.substringBeforeLast('/', initialPath)
                        viewModel.loadFiles(parent)
                        return
                    }
                    // 3) Ya en la raíz: doble toque para salir
                    val now = System.currentTimeMillis()
                    if (now - lastBackPress < 2000) {
                        fragment.requireActivity().finish()
                    } else {
                        Toast.makeText(
                            fragment.requireContext(),
                            "Presiona dos veces para salir de la aplicación",
                            Toast.LENGTH_SHORT
                        ).show()
                        lastBackPress = now
                    }
                }
            })
    }

    fun setupStoragePermissionFlow(
        fragment         : Fragment,
        permissionManager: PermissionManager,
        launcher         : ActivityResultLauncher<android.content.Intent>,
        onReady          : () -> Unit
    ) {
        when {
            permissionManager.isFirstRun() -> {
                AlertDialog.Builder(fragment.requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("BananaFile necesita acceso completo al almacenamiento para funcionar correctamente.")
                    .setPositiveButton("Conceder") { _, _ ->
                        permissionManager.markFirstRunDone()
                        launcher.launch(permissionManager.getManageAllFilesIntent(fragment.requireContext()))
                    }
                    .setNegativeButton("Salir") { _, _ -> fragment.requireActivity().finish() }
                    .setCancelable(false)
                    .show()
            }
            !permissionManager.hasAllFilesPermission() -> {
                launcher.launch(permissionManager.getManageAllFilesIntent(fragment.requireContext()))
            }
            else -> onReady()
        }
    }
}
