package com.dicoding.asclepius

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventAdapter(private var events: List<ArticlesItem>, private val onItemClick: (ArticlesItem) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.title)
        private val eventAuthor: TextView = itemView.findViewById(R.id.author)
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)

        fun bind(ArticlesItem: ArticlesItem, onItemClick: (ArticlesItem) -> Unit) {
            val TAG ="a"
            Log.d(TAG, "bind: $ArticlesItem" )
            eventName.text = ArticlesItem.title ?: "Unnamed Event"
            eventAuthor.text = ArticlesItem.author ?: "Unnamed Event"
            Glide.with(itemView.context)
                .load(ArticlesItem.urlToImage)
                .into(imageLogo)
            eventName.setOnClickListener {
                val url = ArticlesItem.url
                // membuat intent untuk membuka URL di browser
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                // memulai activity untuk intent
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], onItemClick)
    }

    override fun getItemCount(): Int = events.size

    fun updateData(newEvents: List<ArticlesItem>) {
        events = newEvents
        notifyDataSetChanged()
    }
}