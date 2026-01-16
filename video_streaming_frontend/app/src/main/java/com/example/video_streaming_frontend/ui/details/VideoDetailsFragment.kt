package com.example.video_streaming_frontend.ui.details

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.video_streaming_frontend.R
import com.example.video_streaming_frontend.data.network.ApiClient
import com.example.video_streaming_frontend.data.repository.VideoLikesRepository
import kotlinx.coroutines.launch

/**
 * Simple Video Details UI for Android TV.
 *
 * Integrates a like count and a like/unlike toggle with D-pad support.
 */
class VideoDetailsFragment : Fragment() {

    private lateinit var likesCountText: TextView
    private lateinit var likeToggleButton: Button
    private lateinit var statusText: TextView

    private lateinit var viewModel: VideoDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For now, use a static video id. If the app has navigation arguments, replace this.
        val videoId = arguments?.getString(ARG_VIDEO_ID) ?: "demo-video-1"

        val repo = VideoLikesRepository(ApiClient.videoLikesApi)
        val factory = VideoDetailsViewModelFactory(repo, videoId)
        viewModel = factory.create(VideoDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_video_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        likesCountText = view.findViewById(R.id.likes_count)
        likeToggleButton = view.findViewById(R.id.like_toggle_button)
        statusText = view.findViewById(R.id.status_text)

        // Ensure initial focus on TV
        likeToggleButton.requestFocus()

        likeToggleButton.setOnClickListener {
            viewModel.onToggleLike()
        }

        // TV remote OK/ENTER toggles like (in case click isn't dispatched by OEM)
        likeToggleButton.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                viewModel.onToggleLike()
                true
            } else {
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    likesCountText.text = getString(R.string.likes_count, state.likesCount)
                    likeToggleButton.text = if (state.liked) {
                        getString(R.string.like_button_unlike)
                    } else {
                        getString(R.string.like_button_like)
                    }
                    statusText.text = state.statusMessage.orEmpty()
                }
            }
        }
    }

    companion object {
        const val ARG_VIDEO_ID = "video_id"
    }
}
