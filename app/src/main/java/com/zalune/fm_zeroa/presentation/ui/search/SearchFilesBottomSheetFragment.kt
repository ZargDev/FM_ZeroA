package com.zalune.fm_zeroa.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zalune.fm_zeroa.databinding.BottomSheetSearchBinding
import kotlin.text.isNotEmpty
import kotlin.text.trim
import kotlin.toString

class SearchFilesBottomSheetFragment(
    private val onSearch: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSearchBinding? = null
    private val b get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSearchBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.etAdvancedSearch.setOnClickListener {
            val query = b.etAdvancedSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                onSearch(query)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
