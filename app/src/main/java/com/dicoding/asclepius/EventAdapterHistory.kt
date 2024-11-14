package com.dicoding.asclepius

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.database.HistoryEvent

class EventAdapterHistory(
    private val historyEventViewModel: HistoryEventViewModel
) : ListAdapter<HistoryEvent, EventAdapterHistory.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val favoriteEvent = getItem(position)
        holder.bind(favoriteEvent)
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: TextView = itemView.findViewById(R.id.category)
        private val confidenceScore: TextView = itemView.findViewById(R.id.confidenceScore)
        private val dated: TextView = itemView.findViewById(R.id.dated)
        private val nameScore: TextView = itemView.findViewById(R.id.nameScore)
        private val addAt: TextView = itemView.findViewById(R.id.date)
        private val image: ImageView = itemView.findViewById(R.id.imageLogo)
        private val actionButton: Button = itemView.findViewById(R.id.actionButton)

        fun bind(event: HistoryEvent) {
            category.text = event.category
            confidenceScore.text = "${event.confidentScore}%"
            nameScore.text = "Confident Score: "
            addAt.text = "Add at: "
            dated.text = event.timeAdd.toString()
            Glide.with(itemView.context).load(event.image).into(image)

            actionButton.setOnClickListener {
                deleteItem(event)
            }
        }

        private fun deleteItem(event: HistoryEvent) {
            historyEventViewModel.removeHistory(event) // Remove from database
            val currentList = currentList.toMutableList()
            currentList.remove(event)
            submitList(currentList) // Update adapter list
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryEvent>() {
        override fun areItemsTheSame(oldItem: HistoryEvent, newItem: HistoryEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEvent, newItem: HistoryEvent): Boolean {
            return oldItem == newItem
        }
    }
}
