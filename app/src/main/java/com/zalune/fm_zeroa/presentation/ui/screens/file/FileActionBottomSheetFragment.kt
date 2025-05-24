package com.zalune.fm_zeroa.presentation.ui.screens.file

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zalune.fm_zeroa.databinding.FragmentFileActionSheetBinding
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileActionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FileActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFileActionSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FileActionViewModel by viewModels()

    companion object {
        private const val ARG_PATH = "arg_path"
        fun newInstance(path: String) = FileActionBottomSheetFragment().apply {
            arguments = bundleOf(ARG_PATH to path)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileActionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sourcePath = requireArguments().getString(ARG_PATH)!!
        val parentDirPath = File(sourcePath).parent ?: ""
        val path = requireArguments().getString(ARG_PATH)!!
        // Mostrar el nombre con extensión o solo carpeta
        val displayName = File(path).name
        binding.tvItemName.text = displayName

        // Copiar al mismo directorio padre
        binding.actionCopy.setOnClickListener {
            viewModel.copy(sourcePath, parentDirPath)
            dismiss()
        }
        // Mover (cortar) al mismo directorio padre
        binding.actionCut.setOnClickListener {
            viewModel.cut(sourcePath, parentDirPath)
            dismiss()
        }
        // Borrar permanente
        binding.actionDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar borrado")
                .setMessage("¿Seguro que deseas eliminar permanentemente?\n$sourcePath")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.delete(sourcePath)
                    dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        // Renombrar
        binding.actionRename.setOnClickListener {
            showRenameDialog(sourcePath)
        }
        // Información
        binding.actionInfo.setOnClickListener {
            viewModel.getInfo(sourcePath)
            dismiss()
        }

        // Observa resultados y muestra Toast
        viewModel.status.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRenameDialog(path: String) {
        val input = EditText(requireContext()).apply {
            setText(File(path).name)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Renombrar")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                viewModel.rename(path, input.text.toString())
                dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
