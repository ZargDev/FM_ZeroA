package com.zalune.fm_zeroa.presentation.ui.screens.cloud

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.databinding.FragmentCloudFilesBinding
import com.zalune.fm_zeroa.presentation.ui.viewmodel.CloudFilesViewModel
import com.zalune.fm_zeroa.presentation.navigation.OnBackPressedHandler

/**
 * A simple [Fragment] subclass.
 * Use the [CloudFilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CloudFilesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_cloud_files, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        if (user == null) {
            // Si no hay sesión, mostramos un AlertDialog
            AlertDialog.Builder(requireContext())
                .setTitle("Acceso a Cloud")
                .setMessage("Para utilizar el servicio de cloud es necesario iniciar sesión.")
                .setPositiveButton("Iniciar sesión") { _, _ ->
                    findNavController().navigate(
                        CloudFilesFragmentDirections
                            .actionCloudFilesFragmentToLoginFragment()
                    )
                }
                .setNegativeButton("Cancelar") { dlg, _ ->
                    dlg.dismiss()
                    // opcional: volver atrás o dejar vacío
                }
                .setCancelable(false)
                .show()
        } else {
            // Ya logueado → vamos al fragmento de contenido cloud
            findNavController().navigate(
                CloudFilesFragmentDirections
                    .actionCloudFilesFragmentToCloudContentFragment()
            )
        }
    }
}
