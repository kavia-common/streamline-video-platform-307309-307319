package com.example.video_streaming_frontend.data.repository

import com.example.video_streaming_frontend.data.network.VideoLikesApi
import com.example.video_streaming_frontend.data.network.model.LikesResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class LikesState(
    val count: Int = 0,
    val liked: Boolean = false,
)

/**
 * Repository for likes.
 *
 * Maintains an in-memory cache for the app session (process lifetime).
 */
class VideoLikesRepository(
    private val api: VideoLikesApi,
) {
    private val mutex = Mutex()
    private val cache: MutableMap<String, LikesState> = LinkedHashMap()

    suspend fun getCached(videoId: String): LikesState? = mutex.withLock { cache[videoId] }

    suspend fun setCached(videoId: String, state: LikesState) = mutex.withLock {
        cache[videoId] = state
    }

    suspend fun refresh(videoId: String): LikesState {
        val resp: LikesResponse = api.getLikes(videoId)
        val state = LikesState(
            count = resp.resolvedCount(),
            liked = resp.resolvedLiked(),
        )
        setCached(videoId, state)
        return state
    }

    suspend fun like(videoId: String) {
        api.like(videoId)
    }

    suspend fun unlike(videoId: String) {
        api.unlike(videoId)
    }
}
