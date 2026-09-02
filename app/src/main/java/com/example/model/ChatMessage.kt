package com.example.model

enum class Sender {
    USER,
    BOT
}

/**
 * Represents a chat message in the conversation history.
 */
data class ChatMessage(
    val id: String,
    val sender: Sender,
    val text: String,
    val timestamp: String,
    val matched: Boolean = false,
    val similarityScore: Float = 0f,
    val confidencePercentage: Int = 0,
    val processedTokens: String = "",
    val matchedFaq: FaqItem? = null,
    val topSuggestions: List<FaqSuggestion> = emptyList()
)
