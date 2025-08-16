package com.savana.ui.activities.main.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savana.R
import com.savana.databinding.ViewHistoryEntryBinding
import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status

class DiffCallBack : DiffUtil.ItemCallback<HistoryEntry>() {
    override fun areItemsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryEntry, newItem: HistoryEntry): Boolean {
        return oldItem == newItem
    }
}

class HistoryAdapter : ListAdapter<HistoryEntry, HistoryAdapter.HistoryViewHolder>(DiffCallBack()) {

    private var onItemClickCallback: ((HistoryEntry)-> Unit)? = null

    inner class HistoryViewHolder(private val binding: ViewHistoryEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyEntry: HistoryEntry) {
            binding.title.text = historyEntry.label

            val statusIconRes = when (historyEntry.status) {
                Status.Success -> R.drawable.ic_success
                Status.Deny -> R.drawable.ic_deny
                Status.Analyzing -> R.drawable.ic_waiting
            }
            binding.statusIcon.setImageResource(statusIconRes)

            binding.root.setOnClickListener {
                onItemClickCallback?.invoke(historyEntry)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ViewHistoryEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyEntry = getItem(position)
        holder.bind(historyEntry)
    }

    fun setOnItemClickCallback(callback: (HistoryEntry)-> Unit){
        onItemClickCallback = callback
    }

}