package com.example.smartnotes.data.repository

import com.example.smartnotes.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {

    private val apiService = RetrofitClient.apiService

    // REPLACE THIS WITH YOUR API KEY
    private val API_KEY = "AIzaSyAx-4wODtwPhCYzRuWTUUmJ0Em2zNjWIQY"  // ← PUT YOUR KEY HERE

    suspend fun summarizeText(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Summarize the following note in 2-3 concise bullet points.
                Keep it brief and capture the main ideas:
                
                $text
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                )
            )

            val response = apiService.generateContent(request, API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val summary = response.body()?.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?: "No summary generated"
                Result.success(summary)
            } else {
                Result.failure(Exception("Failed to generate summary: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateFlashcards(text: String, count: Int = 5): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Generate exactly $count flashcards from the following note.
                Format each flashcard as:
                Q: [Question]
                A: [Answer]
                
                Make questions clear and answers concise.
                
                Note content:
                $text
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                )
            )

            val response = apiService.generateContent(request, API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val flashcards = response.body()?.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?: "No flashcards generated"
                Result.success(flashcards)
            } else {
                Result.failure(Exception("Failed to generate flashcards: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}