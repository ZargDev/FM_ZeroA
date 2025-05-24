package com.zalune.fm_zeroa.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zalune.fm_zeroa.databinding.ItemFileBinding
import com.zalune.fm_zeroa.domain.model.FileItem
import com.zalune.fm_zeroa.presentation.components.FileIconProvider

import javax.inject.Inject

class FileListAdapter @Inject constructor(
    private val iconProvider: FileIconProvider,
    private val onClick: (FileItem) -> Unit,
    private val onLongClick: (FileItem) -> Unit    // ← nuevo callback
) : ListAdapter<FileItem, FileListAdapter.FileVH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FileItem>() {
            override fun areItemsTheSame(a: FileItem, b: FileItem) = a.uri == b.uri
            override fun areContentsTheSame(a: FileItem, b: FileItem) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileVH {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileVH(binding)
    }

    override fun onBindViewHolder(holder: FileVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FileVH(private val b: ItemFileBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: FileItem) {
            // Nombre e ícono
            b.tvName.text = item.name
            val ext = item.name.substringAfterLast('.', "")
            b.ivIcon.setImageResource(
                iconProvider.getIcon(ext, item.isDirectory)
            )

            // Click normal
            b.root.setOnClickListener {
                onClick(item)
            }
            // Long-press
            b.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }
}
