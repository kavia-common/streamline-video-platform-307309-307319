package com.example.video_streaming_frontend.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.video_streaming_frontend.data.repository.LikesState
import com.example.video_streaming_frontend.data.repository.VideoLikesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoDetailsViewModel(
    private val repository: VideoLikesRepository,
    private val videoId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoDetailsUiState(videoId = videoId, loading = true))
    val uiState: StateFlow<VideoDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Use cached state immediately (if any) then refresh from backend.
            repository.getCached(videoId)?.let { cached ->
                _uiState.value = _uiState.value.copy(
                    likesCount = cached.count,
                    liked = cached.liked,
                    loading = false,
                )
            }
            runCatching { repository.refresh(videoId) }
                .onSuccess { state ->
                    _uiState.value = _uiState.value.copy(
                        likesCount = state.count,
                        liked = state.liked,
                        loading = false,
                        statusMessage = null,
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        statusMessage = "Failed to load likes",
                    )
                }
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Toggle like/unlike with optimistic UI updates.
     */
    // PUBLIC_INTERFACE
    fun onToggleLike() {
        /** Toggles the like state optimistically and rolls back if the network call fails. */
        val previous = _uiState.value
        val optimistic = reduceOptimisticToggle(previous)

        _uiState.value = optimistic

        viewModelScope.launch {
            // Persist optimistic state to session cache immediately.
            repository.setCached(videoId, LikesState(optimistic.likesCount, optimistic.liked))

            val callResult = runCatching {
                if (optimistic.liked) repository.like(videoId) else repository.unlike(videoId)
            }

            callResult.onSuccess {
                _uiState.value = _uiState.value.copy(statusMessage = null)
            }.onFailure {
                // Roll back UI + cache
                _uiState.value = previous.copy(statusMessage = "Failed to update like")
                repository.setCached(videoId, LikesState(previous.likesCount, previous.liked))
            }
        }
    }

    companion object {
        /**
         * Pure function used for unit testing the optimistic toggle behavior.
         */
        // PUBLIC_INTERFACE
        fun reduceOptimisticToggle(state: VideoDetailsUiState): VideoDetailsUiState {
            /** Returns the optimistic next state after toggling like. */
            val nextLiked = !state.liked
            val nextCount = when {
                nextLiked && !state.liked -> state.likesCount + 1
                !nextLiked && state.liked -> maxOf(0, state.likesCount - 1)
                else -> state.likesCount
            }
            return state.copy(
                liked = nextLiked,
                likesCount = nextCount,
                statusMessage = null,
            )
        }
    }
}

class VideoDetailsViewModelFactory(
    private val repository: VideoLikesRepository,
    private val videoId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoDetailsViewModel::class.java)) {
            return VideoDetailsViewModel(repository, videoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
