package com.zalune.fm_zeroa.presentation.ui.screens.file

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.DialogFileInfoBinding
import com.zalune.fm_zeroa.databinding.FragmentFileActionSheetBinding
import com.zalune.fm_zeroa.domain.repository.usecase.GetFileInfoUseCase
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileActionViewModel
import com.zalune.fm_zeroa.presentation.ui.viewmodel.FileListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class FileActionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFileActionSheetBinding? = null
    private val binding get() = _binding!!

    private val actionVM: FileActionViewModel by viewModels()
    private val listVM: FileListViewModel by activityViewModels()
    @Inject
    lateinit var getInfoUseCase: GetFileInfoUseCase

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
        val parentDir = File(sourcePath).parent.orEmpty()
        binding.tvItemName.text = File(sourcePath).name

        // COPIAR: solo preparamos clipboard
        binding.actionCopy.setOnClickListener {
            listVM.prepareCopy(sourcePath)
            Toast.makeText(requireContext(), "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // CORTAR: preparamos clipboard
        binding.actionCut.setOnClickListener {
            listVM.prepareCut(sourcePath)
            Toast.makeText(requireContext(), "Cortado al portapapeles", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // BORRAR permanente
        binding.actionDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog)
                .setTitle("Confirmar borrado")
                .setMessage("¿Deseas eliminar permanentemente?\n$sourcePath")
                .setPositiveButton("Eliminar") { _, _ ->
                    actionVM.delete(sourcePath)
                    listVM.loadFiles(parentDir)
                    dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // RENOMBRAR
        binding.actionRename.setOnClickListener {
            val input = EditText(requireContext()).apply {
                setText(File(sourcePath).name)
                hint = "Nuevo nombre"
            }
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog)
                .setTitle("Renombrar")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val newName = input.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        actionVM.rename(sourcePath, newName)
                        listVM.loadFiles(parentDir)
                    }
                    dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // INFO
        binding.actionInfo.setOnClickListener {
            dismiss()
            showFileInfoDialog(sourcePath)
        }


        // Observa status de acciones (delete/rename/info)
        actionVM.status.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileInfoDialog(path: String) {
        // 1) Inflar layout binding
        val infoBinding = DialogFileInfoBinding.inflate(layoutInflater)
        // 2) Obtener datos
        lifecycleScope.launch {
            val info = getInfoUseCase(path)
            infoBinding.infoName.text        = info.name
            infoBinding.infoPath.text        = info.path
            infoBinding.infoType.text        = if (info.isDirectory) "Carpeta" else "Archivo"
            infoBinding.infoSize.text        = "${info.sizeBytes} bytes"
            infoBinding.infoModified.text    = Date(info.lastModified).toString()
            infoBinding.infoPerms.text       =
                info.permissions.joinToString("\n") { "${it.first}: ${if (it.second) "✔" else "✘"}" }

            // 3) Construir y mostrar el diálogo
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_FM_ZeroA_MaterialAlertDialog
            )
                .setView(infoBinding.root)
                .setPositiveButton("OK", null)
                .setNeutralButton("Copiar ruta") { _, _ ->
                    // copiar al portapapeles
                    val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                            as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("Ruta", info.path))
                }
                .setCancelable(true)
                .show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
