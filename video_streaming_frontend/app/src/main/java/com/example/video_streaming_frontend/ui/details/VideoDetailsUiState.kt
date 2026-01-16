package com.example.video_streaming_frontend.ui.details

data class VideoDetailsUiState(
    val videoId: String,
    val likesCount: Int = 0,
    val liked: Boolean = false,
    val loading: Boolean = false,
    val statusMessage: String? = null,
)
