package com.example.smartnotes.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")  // ← CORRECT MODEL
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>
}