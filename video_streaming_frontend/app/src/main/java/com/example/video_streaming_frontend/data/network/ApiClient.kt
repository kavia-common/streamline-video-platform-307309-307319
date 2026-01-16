package com.example.video_streaming_frontend.data.network

import com.example.video_streaming_frontend.data.auth.AuthStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Central API client.
 *
 * NOTE: Set BASE_URL to your backend URL. If your project already has an API client,
 * use that instead and remove this file.
 */
object ApiClient {

    // TODO: Wire to real backend URL configuration (e.g., BuildConfig field).
    private const val BASE_URL = "http://10.0.2.2:8000"

    private val authInterceptor = Interceptor { chain ->
        val token = AuthStore.getBearerToken()
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val videoLikesApi: VideoLikesApi by lazy { retrofit.create(VideoLikesApi::class.java) }
}
