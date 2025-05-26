package com.zalune.fm_zeroa.presentation.ui.stats

import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.apply
import kotlin.to


class DirectoryStatsBottomSheetFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_PATH = "arg_path"
        fun newInstance(path: String) =
            DirectoryStatsBottomSheetFragment().apply {
                arguments = bundleOf(ARG_PATH to path)
            }
    }
    // En onCreateView / onViewCreated infla tu layout y muestra info de used/free
}
