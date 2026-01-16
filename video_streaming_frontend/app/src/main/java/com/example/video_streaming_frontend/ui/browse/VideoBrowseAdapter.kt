package com.example.video_streaming_frontend.ui.browse

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.video_streaming_frontend.R

class VideoBrowseAdapter(
    private val items: List<VideoItem>,
    private val onItemClicked: (position: Int, item: VideoItem) -> Unit,
    private val onItemFocused: (position: Int) -> Unit,
) : RecyclerView.Adapter<VideoBrowseAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_browse, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) onItemFocused(position)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(position, item)
        }

        // TV remote OK/ENTER should also activate.
        holder.itemView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                onItemClicked(position, item)
                true
            } else {
                false
            }
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.video_item_title)
    }
}
