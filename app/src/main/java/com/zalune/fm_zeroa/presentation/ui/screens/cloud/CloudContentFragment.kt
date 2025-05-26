package com.zalune.fm_zeroa.presentation.ui.screens.cloud

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.zalune.fm_zeroa.databinding.FragmentCloudContentBinding
import com.zalune.fm_zeroa.presentation.adapter.CloudFileAdapter
import com.zalune.fm_zeroa.presentation.ui.viewmodel.CloudFilesViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CloudContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CloudContentFragment : Fragment() {
    private var _binding: FragmentCloudContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val viewModel: CloudFilesViewModel by viewModels()

    private lateinit var adapter: CloudFileAdapter
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.upload(it.toString()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCloudContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // 1) Header: mostrar nombre de usuario
        val user = auth.currentUser!!
        binding.tvUsername.text = user.displayName ?: user.email ?: "Usuario"

        // 2) Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            // Navega al menú principal
            findNavController().navigate(
                CloudContentFragmentDirections
                    .actionCloudContentFragmentToMenuCategoriesFragment()
            )
        }

        // 3) RecyclerView
        adapter = CloudFileAdapter { file ->
            // aquí descargar o borrar
            viewModel.download(file)
        }
        binding.rvFiles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CloudContentFragment.adapter
        }

        // 4) Observers
        viewModel.files.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
        viewModel.progress.observe(viewLifecycleOwner) { p ->
            // podrías mostrar un ProgressBar o Snackbar
        }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // 5) Subir archivo
        binding.fabUpload.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }

        // 6) cargar lista inicial
        viewModel.refreshList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
