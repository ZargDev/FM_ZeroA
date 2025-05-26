package com.zalune.fm_zeroa.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zalune.fm_zeroa.R
import com.zalune.fm_zeroa.domain.model.CloudFile
import java.text.DecimalFormat

class CloudFileAdapter(
    private val onItemClick: (CloudFile) -> Unit
) : ListAdapter<CloudFile, CloudFileAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cloud_file, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onItemClick: (CloudFile) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvSize: TextView = itemView.findViewById(R.id.tvFileSize)
        private val formatter = DecimalFormat("#,##0.#")

        fun bind(file: CloudFile) {
            tvName.text = file.name
            tvSize.text = file.size?.let { readableFileSize(it) } ?: "--"

            itemView.setOnClickListener { onItemClick(file) }
        }

        private fun readableFileSize(size: Long): String {
            // convierte bytes a KB, MB, etc.
            if (size <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return "${formatter.format(size / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CloudFile>() {
            override fun areItemsTheSame(old: CloudFile, new: CloudFile): Boolean =
                old.id == new.id

            override fun areContentsTheSame(old: CloudFile, new: CloudFile): Boolean =
                old == new
        }
    }
}