package com.example.video_streaming_frontend.data.network

import com.example.video_streaming_frontend.data.network.model.LikesResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VideoLikesApi {

    @GET("/videos/{id}/likes")
    suspend fun getLikes(@Path("id") videoId: String): LikesResponse

    @POST("/videos/{id}/like")
    suspend fun like(@Path("id") videoId: String): Response<Unit>

    @DELETE("/videos/{id}/like")
    suspend fun unlike(@Path("id") videoId: String): Response<Unit>
}
