package com.example.smartnotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General"
) {
    fun getTimeAgo(): String {
        val diff = System.currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }
}

// Keep MockData for now - we'll migrate later
object MockData {
    val sampleNotes = mutableListOf(
        Note(
            1,
            "Machine Learning Basics",
            "Neural networks are computing systems inspired by biological neural networks. They consist of layers of interconnected nodes that process information using dynamic state responses to external inputs.",
            System.currentTimeMillis() - (2 * 60 * 60 * 1000),
            "Study"
        ),
        Note(
            2,
            "Project Meeting Notes",
            "Discussed Q1 deliverables and timelines. Need to finalize API documentation by next week. Team suggested moving to microservices architecture.",
            System.currentTimeMillis() - (5 * 60 * 60 * 1000),
            "Work"
        ),
        Note(
            3,
            "Kotlin Coroutines",
            "Coroutines are lightweight threads that allow you to write asynchronous code in a sequential manner. They're perfect for handling long-running tasks without blocking the main thread.",
            System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000),
            "Study"
        ),
        Note(
            4,
            "App Ideas",
            "1. Fitness tracker with AR workouts\n2. Recipe app with AI meal planning\n3. Study buddy matching platform\n4. Smart expense tracker with OCR",
            System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000),
            "Ideas"
        ),
        Note(
            5,
            "Birthday Gift Ideas",
            "Mom's birthday coming up:\n- Smartwatch\n- Kindle Paperwhite\n- Spa voucher\n- Personalized photo album",
            System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000),
            "Personal"
        )
    )
}