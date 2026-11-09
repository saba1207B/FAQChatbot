// Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.
// All rights reserved. No public license is granted.

package com.example.model

/**
 * Represents a single FAQ knowledge entry.
 */
data class FaqItem(
    val id: Int,
    val category: String,
    val question: String,
    val answer: String,
    val tags: List<String>
)

/**
 * Top recommendation item when confidence is moderate or fallback is returned.
 */
data class FaqSuggestion(
    val id: Int,
    val category: String,
    val question: String,
    val score: Float,
    val confidencePercentage: Int
)
