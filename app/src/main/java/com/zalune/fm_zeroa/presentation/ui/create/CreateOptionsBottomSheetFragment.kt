package com.zalune.fm_zeroa.presentation.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zalune.fm_zeroa.databinding.BottomSheetCreateOptionsBinding

class CreateOptionsBottomSheetFragment(
    private val listener: Listener
) : BottomSheetDialogFragment() {

    interface Listener {
        fun onNewFolder()
        fun onNewTxtFile()
    }

    private var _binding: BottomSheetCreateOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.optNewFolder.setOnClickListener {
            listener.onNewFolder()
            dismiss()
        }
        binding.optNewTxtFile.setOnClickListener {
            listener.onNewTxtFile()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
