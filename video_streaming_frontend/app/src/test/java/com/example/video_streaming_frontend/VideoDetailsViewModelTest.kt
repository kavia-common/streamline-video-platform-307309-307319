package com.example.video_streaming_frontend

import com.example.video_streaming_frontend.ui.details.VideoDetailsUiState
import com.example.video_streaming_frontend.ui.details.VideoDetailsViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class VideoDetailsViewModelTest {

    @Test
    fun reduceOptimisticToggle_fromUnliked_incrementsCountAndSetsLiked() {
        val initial = VideoDetailsUiState(videoId = "v1", likesCount = 10, liked = false)
        val next = VideoDetailsViewModel.reduceOptimisticToggle(initial)
        assertEquals(true, next.liked)
        assertEquals(11, next.likesCount)
    }

    @Test
    fun reduceOptimisticToggle_fromLiked_decrementsCountAndUnsetsLiked() {
        val initial = VideoDetailsUiState(videoId = "v1", likesCount = 10, liked = true)
        val next = VideoDetailsViewModel.reduceOptimisticToggle(initial)
        assertEquals(false, next.liked)
        assertEquals(9, next.likesCount)
    }

    @Test
    fun reduceOptimisticToggle_neverBelowZero() {
        val initial = VideoDetailsUiState(videoId = "v1", likesCount = 0, liked = true)
        val next = VideoDetailsViewModel.reduceOptimisticToggle(initial)
        assertEquals(false, next.liked)
        assertEquals(0, next.likesCount)
    }
}
