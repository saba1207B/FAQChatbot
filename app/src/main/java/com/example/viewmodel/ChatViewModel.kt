// Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.
// All rights reserved. No public license is granted.

package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FaqDataset
import com.example.model.ChatMessage
import com.example.model.FaqItem
import com.example.model.FaqSuggestion
import com.example.model.Sender
import com.example.nlp.TextPreprocessor
import com.example.nlp.TfidfEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val allFaqs = FaqDataset.ALL_FAQS
    private val tfidfEngine: TfidfEngine

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _threshold = MutableStateFlow(0.22f)
    val threshold: StateFlow<Float> = _threshold.asStateFlow()

    private val _showMathDialog = MutableStateFlow(false)
    val showMathDialog: StateFlow<Boolean> = _showMathDialog.asStateFlow()

    private val _showFaqDrawer = MutableStateFlow(false)
    val showFaqDrawer: StateFlow<Boolean> = _showFaqDrawer.asStateFlow()

    private val _searchFilter = MutableStateFlow("")
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val vocabularySize: Int
    val totalFaqsCount: Int = allFaqs.size
    val categories: List<String> = listOf("All") + allFaqs.map { it.category }.distinct()

    init {
        // Preprocess corpus documents: question + tags
        val preprocessedCorpus = allFaqs.map { faq ->
            val docText = "${faq.question} ${faq.tags.joinToString(" ")}"
            val tokens = TextPreprocessor.preprocess(docText)
            TextPreprocessor.extractNgrams(tokens)
        }

        tfidfEngine = TfidfEngine(preprocessedCorpus)
        vocabularySize = tfidfEngine.vocabulary.size

        // Initial welcome message
        _messages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.BOT,
                text = "Hello! 👋 Welcome to CodeAlpha AI FAQ Assistant.\n\nI can answer questions regarding our platform, API integration, pricing, security, billing, and NLP architecture using TF-IDF vector matching and Cosine Similarity.",
                timestamp = getCurrentTime(),
                matched = true,
                similarityScore = 1.0f,
                confidencePercentage = 100,
                processedTokens = "welcome ai assistant nlp"
            )
        )
    }

    fun sendMessage(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val userTime = getCurrentTime()
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = Sender.USER,
            text = trimmed,
            timestamp = userTime
        )

        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            _isTyping.value = true
            delay(400) // Brief computational simulation delay for typing indicator

            val tokens = TextPreprocessor.preprocess(trimmed)
            val ngrams = TextPreprocessor.extractNgrams(tokens)
            val tokenString = tokens.joinToString(", ")

            if (ngrams.isEmpty()) {
                val botMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = Sender.BOT,
                    text = "Please enter a valid question or keyword to search our knowledge base.",
                    timestamp = getCurrentTime(),
                    matched = false,
                    similarityScore = 0f,
                    confidencePercentage = 0,
                    processedTokens = ""
                )
                _messages.value = _messages.value + botMsg
                _isTyping.value = false
                return@launch
            }

            // Compute similarity rankings
            val rankings = tfidfEngine.computeSimilarities(ngrams)
            val bestMatch = rankings.firstOrNull()
            val currentThreshold = _threshold.value

            val topSuggestions = rankings.take(3)
                .filter { it.second > 0.05f }
                .map { (idx, score) ->
                    val faq = allFaqs[idx]
                    FaqSuggestion(
                        id = faq.id,
                        category = faq.category,
                        question = faq.question,
                        score = score,
                        confidencePercentage = (score * 100).toInt()
                    )
                }

            if (bestMatch != null && bestMatch.second >= currentThreshold) {
                val matchedFaq = allFaqs[bestMatch.first]
                val score = bestMatch.second
                val botMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = Sender.BOT,
                    text = matchedFaq.answer,
                    timestamp = getCurrentTime(),
                    matched = true,
                    similarityScore = score,
                    confidencePercentage = (score * 100).toInt(),
                    processedTokens = tokenString,
                    matchedFaq = matchedFaq,
                    topSuggestions = topSuggestions
                )
                _messages.value = _messages.value + botMsg
            } else {
                val fallbackText = "I'm sorry, I couldn't find a sufficiently close match in our FAQ knowledge base for your question.\n\nCould you please try rephrasing with specific keywords (e.g., 'API rate limits', 'pricing plans', 'password reset', or 'GDPR compliance')? You can also reach our 24/7 human support team at support@codealpha-ai.com."
                val score = bestMatch?.second ?: 0f
                val botMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = Sender.BOT,
                    text = fallbackText,
                    timestamp = getCurrentTime(),
                    matched = false,
                    similarityScore = score,
                    confidencePercentage = (score * 100).toInt(),
                    processedTokens = tokenString,
                    topSuggestions = topSuggestions
                )
                _messages.value = _messages.value + botMsg
            }

            _isTyping.value = false
        }
    }

    fun updateThreshold(value: Float) {
        _threshold.value = value.coerceIn(0.05f, 0.80f)
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.BOT,
                text = "Chat conversation cleared.\n\nAsk any question about the platform, pricing, API rate limits, authentication, or NLP algorithms!",
                timestamp = getCurrentTime(),
                matched = true,
                similarityScore = 1.0f,
                confidencePercentage = 100
            )
        )
    }

    fun toggleMathDialog(show: Boolean) {
        _showMathDialog.value = show
    }

    fun toggleFaqDrawer(show: Boolean) {
        _showFaqDrawer.value = show
    }

    fun setSearchFilter(query: String) {
        _searchFilter.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun getFilteredFaqs(): List<FaqItem> {
        val query = _searchFilter.value.trim().lowercase(Locale.ROOT)
        val cat = _selectedCategory.value
        return allFaqs.filter { faq ->
            val matchCat = cat == null || cat == "All" || faq.category.equals(cat, ignoreCase = true)
            val matchText = query.isEmpty() ||
                    faq.question.lowercase(Locale.ROOT).contains(query) ||
                    faq.answer.lowercase(Locale.ROOT).contains(query) ||
                    faq.tags.any { it.lowercase(Locale.ROOT).contains(query) }
            matchCat && matchText
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
