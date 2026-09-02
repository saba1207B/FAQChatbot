package com.example.nlp

import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Pure Kotlin implementation of TF-IDF Vectorizer and Cosine Similarity.
 * - Fits vocabulary and computes smoothed inverse document frequency IDF(t)
 * - Computes L2-normalized sparse vector dot products for cosine similarity.
 */
class TfidfEngine(
    private val corpus: List<List<String>>
) {
    // Maps term/ngram -> unique feature column index
    val vocabulary: Map<String, Int>

    // Precalculated IDF values for each vocabulary feature
    private val idfArray: DoubleArray

    // Precalculated L2-normalized TF-IDF vector for each document in the corpus
    val documentVectors: List<DoubleArray>

    init {
        val totalDocs = corpus.size.toDouble()
        val vocabMap = mutableMapOf<String, Int>()
        val docFrequency = mutableMapOf<String, Int>()

        // 1. Build vocabulary and calculate document frequencies
        var featureIdx = 0
        for (doc in corpus) {
            val uniqueTermsInDoc = doc.toSet()
            for (term in uniqueTermsInDoc) {
                docFrequency[term] = (docFrequency[term] ?: 0) + 1
                if (!vocabMap.containsKey(term)) {
                    vocabMap[term] = featureIdx++
                }
            }
        }

        vocabulary = vocabMap
        val vocabSize = vocabMap.size

        // 2. Compute smoothed IDF: ln((N + 1) / (df + 1)) + 1
        idfArray = DoubleArray(vocabSize)
        for ((term, idx) in vocabulary) {
            val df = (docFrequency[term] ?: 1).toDouble()
            idfArray[idx] = ln((totalDocs + 1.0) / (df + 1.0)) + 1.0
        }

        // 3. Vectorize all corpus documents
        val docVecs = mutableListOf<DoubleArray>()
        for (doc in corpus) {
            docVecs.add(transformToVector(doc))
        }
        documentVectors = docVecs
    }

    /**
     * Transforms a list of tokens/ngrams into an L2-normalized TF-IDF vector.
     */
    fun transformToVector(tokens: List<String>): DoubleArray {
        val vector = DoubleArray(vocabulary.size)
        if (tokens.isEmpty() || vocabulary.isEmpty()) return vector

        // Calculate term frequencies in this document/query
        val tfCounts = mutableMapOf<Int, Double>()
        for (token in tokens) {
            val idx = vocabulary[token]
            if (idx != null) {
                tfCounts[idx] = (tfCounts[idx] ?: 0.0) + 1.0
            }
        }

        // Sublinear TF-IDF calculation: (1 + ln(tf)) * idf
        var sumSquares = 0.0
        for ((idx, count) in tfCounts) {
            val tf = 1.0 + ln(count)
            val tfidf = tf * idfArray[idx]
            vector[idx] = tfidf
            sumSquares += tfidf * tfidf
        }

        // L2 Unit vector normalization
        val norm = sqrt(sumSquares)
        if (norm > 0.0) {
            for (i in vector.indices) {
                vector[i] /= norm
            }
        }

        return vector
    }

    /**
     * Computes cosine similarity between two unit vectors (dot product).
     */
    fun cosineSimilarity(vecA: DoubleArray, vecB: DoubleArray): Float {
        if (vecA.size != vecB.size || vecA.isEmpty()) return 0f
        var dot = 0.0
        for (i in vecA.indices) {
            val valA = vecA[i]
            if (valA != 0.0) {
                dot += valA * vecB[i]
            }
        }
        return dot.coerceIn(0.0, 1.0).toFloat()
    }

    /**
     * Scores a user query against all documents in the corpus.
     * Returns a list of Pair(documentIndex, similarityScore).
     */
    fun computeSimilarities(queryTokens: List<String>): List<Pair<Int, Float>> {
        val queryVector = transformToVector(queryTokens)
        val results = mutableListOf<Pair<Int, Float>>()

        for (i in documentVectors.indices) {
            val score = cosineSimilarity(queryVector, documentVectors[i])
            results.add(Pair(i, score))
        }

        // Sort descending by score
        return results.sortedByDescending { it.second }
    }
}
