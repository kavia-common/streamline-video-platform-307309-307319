package com.example.video_streaming_frontend.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.video_streaming_frontend.R
import com.example.video_streaming_frontend.ui.details.VideoDetailsFragment

/**
 * Simple Browse/List screen for Android TV.
 *
 * This app template currently doesn't have a full browse grid; this fragment provides
 * a minimal TV-friendly list to demonstrate navigation into VideoDetailsFragment
 * with a real videoId.
 */
class VideoBrowseFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoBrowseAdapter

    private var lastSelectedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastSelectedPosition = savedInstanceState?.getInt(STATE_LAST_SELECTED_POSITION) ?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_LAST_SELECTED_POSITION, lastSelectedPosition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_video_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = view.findViewById<TextView>(R.id.browse_title)
        title.text = getString(R.string.browse_title)

        recyclerView = view.findViewById(R.id.videos_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        // Demo dataset: IDs are real values passed through navigation to details.
        // Replace with real backend-driven listing later.
        val items = listOf(
            VideoItem(id = "video-1", title = "Video 1"),
            VideoItem(id = "video-2", title = "Video 2"),
            VideoItem(id = "video-3", title = "Video 3"),
            VideoItem(id = "video-4", title = "Video 4"),
            VideoItem(id = "video-5", title = "Video 5"),
        )

        adapter = VideoBrowseAdapter(
            items = items,
            onItemClicked = { position, item ->
                // Remember focus position so that back returns to the right item.
                lastSelectedPosition = position

                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.main_fragment_container,
                        VideoDetailsFragment().apply {
                            arguments = Bundle().apply {
                                putString(VideoDetailsFragment.ARG_VIDEO_ID, item.id)
                            }
                        },
                    )
                    .addToBackStack(BACKSTACK_DETAILS)
                    .commit()
            },
            onItemFocused = { position ->
                lastSelectedPosition = position
            },
        )
        recyclerView.adapter = adapter

        // Restore focus to the last-selected item after returning from details.
        recyclerView.post {
            val target = lastSelectedPosition.coerceIn(0, maxOf(0, adapter.itemCount - 1))
            recyclerView.scrollToPosition(target)
            recyclerView.findViewHolderForAdapterPosition(target)?.itemView?.requestFocus()
                ?: run {
                    // If the ViewHolder isn't laid out yet, try again on next frame.
                    recyclerView.post {
                        recyclerView.findViewHolderForAdapterPosition(target)?.itemView?.requestFocus()
                    }
                }
        }
    }

    companion object {
        const val TAG = "VideoBrowseFragment"
        private const val STATE_LAST_SELECTED_POSITION = "state_last_selected_position"
        private const val BACKSTACK_DETAILS = "details"
    }
}
