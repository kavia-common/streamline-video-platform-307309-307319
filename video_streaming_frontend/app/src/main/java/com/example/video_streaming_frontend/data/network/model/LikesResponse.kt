package com.example.video_streaming_frontend.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Response for GET /videos/{id}/likes
 *
 * Backend may return either:
 *  - {"count": 10, "liked": true}
 *  - {"likes": 10, "liked": true}
 */
data class LikesResponse(
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("likes")
    val likes: Int? = null,
    @SerializedName("liked")
    val liked: Boolean? = null,
) {
    fun resolvedCount(): Int = count ?: likes ?: 0
    fun resolvedLiked(): Boolean = liked ?: false
}
