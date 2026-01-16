package com.example.video_streaming_frontend.data.auth

/**
 * Simple in-memory auth token store for the current app session.
 *
 * Replace/wire this into the app's real auth store if one exists.
 */
object AuthStore {
    @Volatile
    private var bearerToken: String? = null

    // PUBLIC_INTERFACE
    fun setBearerToken(token: String?) {
        /** Sets the bearer token for this process/session. */
        bearerToken = token
    }

    // PUBLIC_INTERFACE
    fun getBearerToken(): String? {
        /** Returns the bearer token for this process/session, if available. */
        return bearerToken
    }
}
