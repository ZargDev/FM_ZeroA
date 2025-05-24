package com.zalune.fm_zeroa.presentation.ui.screens.cloud

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
import com.zalune.fm_zeroa.presentation.ui.screens.auth.RegisterFragmentDirections
import com.zalune.fm_zeroa.presentation.ui.viewmodel.CloudFilesViewModel
import com.zalune.fm_zeroa.utils.OnBackPressedHandler


/**
 * A simple [Fragment] subclass.
 * Use the [CloudFilesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CloudFilesFragment : Fragment(), OnBackPressedHandler {
    // TODO: Rename and change types of parameters
    private val viewModel: CloudFilesViewModel by viewModels()
    private var _binding: FragmentCloudFilesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onBackPressed(): Boolean {
        // Lógica específica del fragmento
        return false // O true si maneja el evento
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCloudFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTest.setOnClickListener {
            viewModel.incrementCounter()
        }

        // Observador del contador
        viewModel.testCounter.observe(viewLifecycleOwner) { count ->
            binding.tvCounter.text = "Contador: $count"
        }

        // Log para ver creación del fragment
        Log.d("PERSISTENCE_TEST", "CloudFragment creado: ${this.hashCode()}")
        val user = auth.currentUser
        if (user == null) {
            // No autenticado: mostrar mensaje y ofrecer ir al login
            binding.llNotLogged.visibility = View.VISIBLE
            binding.llLogged.visibility   = View.GONE

            // Opcional: navegar al LoginFragment al tocar el mensaje
            binding.tvPleaseLogin.setOnClickListener {
                val action = CloudFilesFragmentDirections
                        .actionCloudFilesFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        } else {
            // Autenticado: mostrar nombre de usuario y FAB
            binding.llNotLogged.visibility = View.GONE
            binding.llLogged.visibility   = View.VISIBLE

            // Mostrar nombre del usuario (email o displayName)
            binding.tvUsername.text = user.displayName
                ?: user.email
                        ?: "Usuario"

            // El FAB de momento no hace nada
            binding.fabAdd.setOnClickListener {
                // TODO: implementar acción de agregar archivo
            }

            binding.btnLogout.setOnClickListener {
                auth.signOut()
                // Regresa al mismo fragment para mostrar mensaje de login
                val action = CloudFilesFragmentDirections
                    .actionCloudFilesFragmentSelf()
                findNavController().navigate(action)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}