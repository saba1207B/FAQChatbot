// Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.
// All rights reserved. No public license is granted.

package com.example.nlp

import java.util.Locale

/**
 * Android NLP text preprocessor.
 * Performs normalization, lowercasing, punctuation removal,
 * tokenization, stopword filtering, and Porter-style stemming.
 */
object TextPreprocessor {

    // Standard English stopwords
    private val STOPWORDS = setOf(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any",
        "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below",
        "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did",
        "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each",
        "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't",
        "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself",
        "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if",
        "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more",
        "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once",
        "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own",
        "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
        "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
        "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll",
        "they're", "they've", "this", "those", "through", "to", "too", "under", "until",
        "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were",
        "weren't", "what", "what's", "when", "when's", "where", "where's", "which",
        "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would",
        "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
        "yourself", "yourselves"
    )

    /**
     * Cleans, tokenizes, removes stopwords, and stems tokens.
     */
    fun preprocess(text: String): List<String> {
        if (text.isBlank()) return emptyList()

        // 1. Lowercase and remove punctuation / non-alphanumeric chars
        val cleaned = text.lowercase(Locale.ROOT)
            .replace(Regex("https?://\\S+|www\\.\\S+"), "")
            .replace(Regex("[^a-z0-9\\s]"), " ")

        // 2. Tokenize by whitespace
        val rawTokens = cleaned.split(Regex("\\s+")).filter { it.isNotBlank() }

        // 3. Stopword removal and stemming
        val filtered = mutableListOf<String>()
        for (token in rawTokens) {
            if (token.length > 1 && token !in STOPWORDS) {
                val stemmed = stem(token)
                filtered.add(stemmed)
            }
        }

        return filtered
    }

    /**
     * Generates unigrams and bigrams from tokens for enriched n-gram matching.
     */
    fun extractNgrams(tokens: List<String>): List<String> {
        if (tokens.isEmpty()) return emptyList()
        val ngrams = mutableListOf<String>()
        // Unigrams
        ngrams.addAll(tokens)
        // Bigrams
        for (i in 0 until tokens.size - 1) {
            ngrams.add("${tokens[i]}_${tokens[i + 1]}")
        }
        return ngrams
    }

    /**
     * Rule-based morphological suffix stemmer.
     */
    private fun stem(word: String): String {
        var w = word
        if (w.endsWith("sses")) return w.substring(0, w.length - 2)
        if (w.endsWith("ies")) return w.substring(0, w.length - 2)
        if (w.endsWith("ss")) return w
        if (w.endsWith("s") && w.length > 3) w = w.substring(0, w.length - 1)

        if (w.endsWith("eed") && w.length > 4) return w.substring(0, w.length - 1)
        if (w.endsWith("ed") && w.length > 4) return w.substring(0, w.length - 2)
        if (w.endsWith("ing") && w.length > 5) return w.substring(0, w.length - 3)

        if (w.endsWith("ational") && w.length > 8) return w.substring(0, w.length - 5) + "e"
        if (w.endsWith("tional") && w.length > 7) return w.substring(0, w.length - 4)
        if (w.endsWith("izer") && w.length > 5) return w.substring(0, w.length - 1)

        return w
    }
}
