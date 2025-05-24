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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zalune.fm_zeroa.MainActivity
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.FragmentFileListBinding
import com.zalune.fm_zeroa.domain.model.FileItem
import com.zalune.fm_zeroa.presentation.adapter.FileListAdapter
import com.zalune.fm_zeroa.presentation.components.FileIconProvider
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileListViewModel
import com.zalune.fm_zeroa.utils.OnBackPressedHandler
import com.zalune.fm_zeroa.utils.PermissionManager

import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class FileListFragment : Fragment(), OnBackPressedHandler  {

    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FileListViewModel by viewModels()

    @Inject lateinit var iconProvider: FileIconProvider
    @Inject lateinit var permissionManager: PermissionManager

    private lateinit var adapter: FileListAdapter

    // Lanzador para ir a Ajustes de "Manage All Files"
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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putString("CURRENT_PATH", viewModel.currentPath.value)
        viewModel.getCurrentPath()?.let {
            outState.putString("CURRENT_PATH", it) // ✅ Usa el método seguro del ViewModel
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString("CURRENT_PATH")?.let { savedPath ->
//            viewModel.currentPath.observe(viewLifecycleOwner) { newPath ->
//                Log.d("FLF", "currentPath cambió a: $newPath")
//                (activity as? MainActivity)?.onPathChanged(newPath)
//            }
            if (viewModel.getCurrentPath() != savedPath) {
                viewModel.navigateTo(savedPath) // ✅ Actualiza directamente el ViewModel
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Indicamos que este fragment usa menú (navegacion de carpetas)
        setHasOptionsMenu(true)
        // 1) Registrar custom back handling
//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (viewModel.navigateUp()) {
//                        // navegó al directorio padre
//                    } else {
//                        // estamos en la raíz: deja que el sistema lo maneje
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
//                }
//            }
//        )
        // Configuración del RecyclerView y su adapter
        adapter = FileListAdapter(
            iconProvider,
            onClick = { fileItem ->
                onFileItemClicked(fileItem)
            },
            onLongClick = { fileItem ->
                FileActionBottomSheetFragment
                    .newInstance(fileItem.uri.path!!)
                    .show(childFragmentManager, "FileActions")
            }
        )
        binding.rvFileList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFileList.adapter = adapter

// Observa la ruta y actualiza la barra cada vez que cambie: para el TopAppBar
        viewModel.currentPath.observe(viewLifecycleOwner) { newPath ->
            Log.d("FLF", "currentPath cambió a: $newPath")
            (activity as? MainActivity)?.onPathChanged(newPath)
        }


        initializeListing()

        // Observa los cambios en la lista de archivos
// 1) Observer de lista
        viewModel.fileList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                binding.tvStatus.text =
                    viewModel.errorMsg.value ?: "Carpeta vacía"
                binding.tvStatus.visibility = View.VISIBLE
            } else {
                binding.tvStatus.visibility = View.GONE
            }
        }

// 2) Observer de errores (opcional, para mensajes dinámicos)
        viewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            // Si llegó un mensaje y la lista está vacía, lo mostramos
            if (msg != null && viewModel.fileList.value.isNullOrEmpty()) {
                binding.tvStatus.text = msg
                binding.tvStatus.visibility = View.VISIBLE
            }
        }



        // Flujo de permiso al primer arranque / subsecuentes
        when {
            permissionManager.isFirstRun() -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage(
                        "BananaFile necesita acceso completo al almacenamiento para funcionar correctamente."
                    )
                    .setPositiveButton("Conceder") { _, _ ->
                        permissionManager.markFirstRunDone()
                        requestAllFilesPermission()
                    }
                    .setNegativeButton("Salir") { _, _ ->
                        requireActivity().finish()
                    }
                    .setCancelable(false)
                    .show()
            }
            !permissionManager.hasAllFilesPermission() -> {
                requestAllFilesPermission()
            }
            else -> {
                initializeListing()
            }
        }
    }

    private fun requestAllFilesPermission() {
        val intent = permissionManager.getManageAllFilesIntent(requireContext())
        manageAllFilesLauncher.launch(intent)
    }

    private fun initializeListing() {
        // Ruta raíz del almacenamiento primario
        val defaultPath = Environment.getExternalStorageDirectory().path
        viewModel.initialize(defaultPath)
    }

    private fun onFileItemClicked(item: FileItem) {
        if (item.isDirectory) {
            // Navega a subdirectorio usando la ruta del File
            val dir = File(item.uri.path ?: return)
            viewModel.navigateTo(dir.path)
        } else {
            // Previsualiza archivo convirtiendo uri a File
            val file = File(item.uri.path ?: return)
            FilePreviewFragment.openFile(requireContext(), file)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_file_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = (searchItem.actionView as SearchView).apply {
            queryHint = "Buscar archivos…"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.filterFiles(query)
                    return true
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.filterFiles(newText)
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onBackPressed(): Boolean {
        return viewModel.navigateUp()// Delegamos al ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
