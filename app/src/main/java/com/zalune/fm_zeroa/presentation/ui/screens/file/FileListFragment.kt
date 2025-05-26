package com.zalune.fm_zeroa.presentation.ui.screens.file


import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zalune.fm_zeroa.MainActivity
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.FragmentFileListBinding
import com.zalune.fm_zeroa.domain.model.FileItem
import com.zalune.fm_zeroa.presentation.adapter.FileListAdapter
import com.zalune.fm_zeroa.presentation.components.FileIconProvider
import com.zalune.fm_zeroa.presentation.ui.fabmenu.FloatingActionsMenu
import com.zalune.fm_zeroa.presentation.ui.screens.helpers.FileListActionsHelper
import com.zalune.fm_zeroa.presentation.ui.screens.helpers.FileListUiHelper
import com.zalune.fm_zeroa.presentation.ui.search.SearchFilesBottomSheetFragment
import com.zalune.fm_zeroa.presentation.ui.stats.DirectoryStatsBottomSheetFragment
import com.zalune.fm_zeroa.presentation.ui.topbar.TopAppBarCustom
import com.zalune.fm_zeroa.presentation.ui.viewmodel.ClipboardState
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileListViewModel
import com.zalune.fm_zeroa.utils.PermissionManager

import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import kotlin.getValue



@AndroidEntryPoint
class FileListFragment : Fragment() {

    private var _binding: FragmentFileListBinding? = null
    val binding get() = _binding!!

    private lateinit var adapter: FileListAdapter
    private val viewModel: FileListViewModel by activityViewModels()
    private lateinit var fabMenu: FloatingActionsMenu

    @Inject lateinit var iconProvider: FileIconProvider
    @Inject lateinit var permissionManager: PermissionManager

    private val manageAllFilesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (permissionManager.hasAllFilesPermission()) {
            initializeListing()
        } else {
            Toast.makeText(
                requireContext(),
                "Sin este permiso no podemos listar tus archivos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val main = activity as? MainActivity ?: return

        // 0) Inicializa tu adapter
        adapter = FileListAdapter(
            iconProvider,
            onClick = { item -> onFileItemClicked(item) },
            onLongClick = { item ->
                FileActionBottomSheetFragment
                    .newInstance(item.uri.path!!)
                    .show(childFragmentManager, "FileActions")
            }
        )

        // 1) Recycler
        FileListUiHelper.initRecycler(binding, adapter)

        // 2) Toolbar
        FileListUiHelper.setupToolbar(
            fragment        = this,
            topBar          = main.topBar,
            onSearch        = {
                SearchFilesBottomSheetFragment { q -> viewModel.filterFiles(q) }
                    .show(childFragmentManager, "SearchSheet")
            },
            onViewMode      = { toggleViewMode() },
            onMultiSelect   = { enterMultiSelectMode() },
            onCreateFolder  = {
                FileListActionsHelper.showCreateFolderDialog(
                    requireContext(),
                    viewModel.currentPath.value.orEmpty()
                ) {
                    // recarga
                    viewModel.loadFiles(viewModel.currentPath.value.orEmpty())
                }
            },
            onCreateTxt     = {
                FileListActionsHelper.showCreateTxtDialog(
                    requireContext(),
                    viewModel.currentPath.value.orEmpty()
                ) {
                    viewModel.loadFiles(viewModel.currentPath.value.orEmpty())
                }
            },
            onSettings      = { /* navegamos más tarde */ },
            onAbout         = { FileListActionsHelper.showAboutDialog(requireContext()) },
            onExit          = { requireActivity().finish() },
            onPathClick     = { showStats() }
        )


        // 3) Observadores
        FileListUiHelper.observeViewModel(this, binding, adapter, viewModel)

        // 4) Back button
        FileListUiHelper.setupBackHandling(
            fragment    = this,
            viewModel   = viewModel,
            initialPath = Environment.getExternalStorageDirectory().path
        )

        // 5) Inicio de listado y permisos
        FileListUiHelper.setupStoragePermissionFlow(
            fragment        = this,
            permissionManager = permissionManager,
            launcher        = manageAllFilesLauncher,
            onReady         = { initializeListing() }
        )

        // 6) Hook al FAB menu:
        val fabMenu = main.fabMenu
        val topBar = requireActivity()
            .findViewById<TopAppBarCustom>(R.id.topAppBar)
        val currentPath = viewModel.currentPath.value.orEmpty()

        fabMenu.setOnCreateFolder {
            FileListActionsHelper.showCreateFolderDialog(
                requireContext(),
                viewModel.currentPath.value.orEmpty()
            ) {
                // recarga
                viewModel.loadFiles(viewModel.currentPath.value.orEmpty())
            }
        }

        fabMenu.setOnCreateTxt {
            FileListActionsHelper.showCreateTxtDialog(
                requireContext(),
                viewModel.currentPath.value.orEmpty()
            ) {
                viewModel.loadFiles(viewModel.currentPath.value.orEmpty())
            }
        }

// escucha cambios en el portapapeles

        viewModel.clipboard.observe(viewLifecycleOwner) { cb ->
            Log.d("FileListFragment", "clipboard changed → $cb")
            when (cb) {
                is ClipboardState.None -> {
                    topBar.exitCopyMode()
                    fabMenu.hidePasteMode()
                }
                is ClipboardState.Copy,
                is ClipboardState.Cut -> {
                    topBar.enterCopyMode { viewModel.clearClipboard() }
                    fabMenu.showPasteMode {
                        viewModel.currentPath.value?.let { target ->
                            viewModel.paste(target)
                        }
                    }
                }
            }
        }
        if (savedInstanceState == null) {
            // Primera vez que se crea la vista: inicializa el VM
            initializeListing()
        }
    }

    private fun initializeListing() {
        val defaultPath = Environment.getExternalStorageDirectory().path
        viewModel.initialize(defaultPath)
    }

    private fun onFileItemClicked(item: FileItem) {
        if (item.isDirectory) {

            // Cargamos el nuevo directorio
            viewModel.loadFiles(File(item.uri.path!!).path)
        } else {
            FilePreviewFragment.openFile(requireContext(), File(item.uri.path!!))
        }
    }

    private var isGrid = false
    private fun toggleViewMode() {
        isGrid = !isGrid
        binding.rvFileList.layoutManager =
            if (isGrid) GridLayoutManager(requireContext(), 3)
            else LinearLayoutManager(requireContext())
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_file_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as? SearchView)?.apply {
            queryHint = "Buscar archivos…"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) =
                    viewModel.filterFiles(query).let { true }
                override fun onQueryTextChange(newText: String) =
                    viewModel.filterFiles(newText).let { true }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun enterMultiSelectMode() {
        // TODO: activa tu selección múltiple (o llama a tu helper)
    }

    private fun openSettings() {
        findNavController().navigate(R.id.settingsFragment)
    }

    private fun showAbout() {
        FileListActionsHelper.showAboutDialog(requireContext())
    }

    private fun showStats() {
        DirectoryStatsBottomSheetFragment
            .newInstance(viewModel.currentPath.value.orEmpty())
            .show(childFragmentManager, "Stats")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
