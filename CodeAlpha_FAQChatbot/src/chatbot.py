# Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.
#
# This source code is proprietary and confidential.
#
# Unauthorized copying, redistribution, modification, or use — including
# any use for artificial intelligence / machine learning training —
# is strictly prohibited.
#
# See the LICENSE and COPYRIGHT files in the repository root for full terms.

"""
=============================================================================
Project: CodeAlpha_FAQChatbot (CodeAlpha AI Internship)
Module:  chatbot.py
Author:  AI NLP Developer
Purpose: Core FAQ Chatbot Engine utilizing TF-IDF and Cosine Similarity
=============================================================================
"""

import os
import json
import logging
from typing import Dict, Any, List, Optional, Tuple
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from .preprocessing import TextPreprocessor

# Configure logger
logging.basicConfig(level=logging.INFO, format="%(asctime)s - [%(levelname)s] - %(message)s")
logger = logging.getLogger(__name__)


class FAQChatbot:
    """
    AI/NLP FAQ Chatbot engine.
    - Loads structured FAQ dataset from JSON.
    - Fits a TF-IDF vectorizer (with 1-gram and 2-gram support).
    - Transforms user questions into sparse TF-IDF vectors.
    - Computes Cosine Similarity against all FAQ representations.
    - Evaluates against a configurable similarity threshold.
    - Returns structured responses with confidence metrics, matched tags, and fallbacks.
    """

    DEFAULT_FALLBACK = (
        "I'm sorry, I couldn't find a sufficiently close match in our FAQ knowledge base for your question. "
        "Could you please try rephrasing with specific keywords (e.g., 'API rate limits', 'pricing plans', "
        "'password reset', or 'GDPR compliance')? You can also reach our 24/7 human support team at support@codealpha-ai.com."
    )

    def __init__(
        self,
        faq_filepath: Optional[str] = None,
        similarity_threshold: float = 0.22,
        ngram_range: Tuple[int, int] = (1, 2)
    ):
        """
        Initializes the FAQ Chatbot engine.
        :param faq_filepath: Path to the JSON file containing FAQ entries.
        :param similarity_threshold: Minimum cosine score (0.0 to 1.0) to qualify as a valid match.
        :param ngram_range: Unigram & Bigram range for TF-IDF feature extraction.
        """
        self.similarity_threshold = float(similarity_threshold)
        self.preprocessor = TextPreprocessor(use_stemming=False, remove_stopwords=True)
        self.vectorizer = TfidfVectorizer(
            ngram_range=ngram_range,
            sublinear_tf=True,
            norm="l2"
        )
        self.faqs: List[Dict[str, Any]] = []
        self.preprocessed_corpus: List[str] = []
        self.tfidf_matrix = None
        self.is_trained = False

        if faq_filepath and os.path.exists(faq_filepath):
            self.load_dataset(faq_filepath)

    def load_dataset(self, filepath: str) -> bool:
        """
        Loads the FAQ dataset from a JSON file and builds the TF-IDF representation.
        """
        try:
            with open(filepath, "r", encoding="utf-8") as f:
                data = json.load(f)

            if not isinstance(data, list) or len(data) == 0:
                logger.error("Invalid dataset format: Expected non-empty JSON list of FAQ objects.")
                return False

            self.faqs = data
            logger.info(f"Successfully loaded {len(self.faqs)} FAQ records from '{filepath}'.")
            self._fit_vectorizer()
            return True

        except Exception as e:
            logger.exception(f"Error loading FAQ dataset from '{filepath}': {str(e)}")
            return False

    def _fit_vectorizer(self) -> None:
        """
        Preprocesses FAQ questions, tags, and keywords, then trains the TF-IDF matrix.
        """
        self.preprocessed_corpus = []
        for faq in self.faqs:
            # Combine question and tags to create a rich semantic document
            question_text = faq.get("question", "")
            tags_text = " ".join(faq.get("tags", []))
            combined_doc = f"{question_text} {tags_text}"

            # Run NLP preprocessing
            processed_doc = self.preprocessor.preprocess(combined_doc)
            self.preprocessed_corpus.append(processed_doc)

        # Fit TF-IDF matrix
        self.tfidf_matrix = self.vectorizer.fit_transform(self.preprocessed_corpus)
        self.is_trained = True
        logger.info(
            f"TF-IDF Vectorizer fitted with vocabulary size: {len(self.vectorizer.vocabulary_)} terms."
        )

    def get_response(
        self,
        user_query: str,
        threshold: Optional[float] = None
    ) -> Dict[str, Any]:
        """
        Processes user query, computes cosine similarity, and determines the best answer.
        
        Returns a rich response dictionary:
        {
            "query": original user query,
            "processed_query": normalized tokens,
            "matched": bool (True if score >= threshold),
            "similarity_score": float (0.0 to 1.0),
            "confidence_percentage": int (0 to 100),
            "threshold_used": float,
            "answer": answer string,
            "faq_item": matched FAQ dictionary or None,
            "top_suggestions": list of top 3 close matches
        }
        """
        active_threshold = threshold if threshold is not None else self.similarity_threshold

        if not self.is_trained or not self.faqs or self.tfidf_matrix is None:
            return {
                "query": user_query,
                "processed_query": "",
                "matched": False,
                "similarity_score": 0.0,
                "confidence_percentage": 0,
                "threshold_used": active_threshold,
                "answer": "FAQ Knowledge Base is currently uninitialized or empty. Please check server logs.",
                "faq_item": None,
                "top_suggestions": []
            }

        # Step 1: Preprocess user question
        clean_query = self.preprocessor.preprocess(user_query)

        # Step 2: Handle empty query edge case
        if not clean_query.strip():
            return {
                "query": user_query,
                "processed_query": "",
                "matched": False,
                "similarity_score": 0.0,
                "confidence_percentage": 0,
                "threshold_used": active_threshold,
                "answer": "Please enter a valid question or keyword to search our knowledge base.",
                "faq_item": None,
                "top_suggestions": []
            }

        # Step 3: Transform user query to TF-IDF vector
        query_vector = self.vectorizer.transform([clean_query])

        # Step 4: Compute Cosine Similarity between query vector and all FAQ document vectors
        # cosine_similarity returns array of shape (1, num_faqs)
        similarities = cosine_similarity(query_vector, self.tfidf_matrix)[0]

        # Step 5: Rank results by similarity score in descending order
        sorted_indices = np.argsort(similarities)[::-1]
        best_index = int(sorted_indices[0])
        best_score = float(similarities[best_index])

        # Extract top 3 alternatives for suggestions
        top_suggestions = []
        for rank, idx in enumerate(sorted_indices[:3]):
            score = float(similarities[idx])
            if score > 0.05:
                top_suggestions.append({
                    "id": self.faqs[idx].get("id"),
                    "category": self.faqs[idx].get("category"),
                    "question": self.faqs[idx].get("question"),
                    "similarity_score": round(score, 4),
                    "confidence_percentage": int(round(score * 100))
                })

        # Step 6: Threshold evaluation and answer formulation
        is_match = best_score >= active_threshold
        confidence_pct = int(round(best_score * 100))

        if is_match:
            best_faq = self.faqs[best_index]
            response_answer = best_faq.get("answer", "")
            matched_faq_dict = {
                "id": best_faq.get("id"),
                "category": best_faq.get("category"),
                "question": best_faq.get("question"),
                "tags": best_faq.get("tags", [])
            }
        else:
            response_answer = self.DEFAULT_FALLBACK
            matched_faq_dict = None

        return {
            "query": user_query,
            "processed_query": clean_query,
            "matched": is_match,
            "similarity_score": round(best_score, 4),
            "confidence_percentage": confidence_pct,
            "threshold_used": active_threshold,
            "answer": response_answer,
            "faq_item": matched_faq_dict,
            "top_suggestions": top_suggestions
        }

    def get_all_faqs(self) -> List[Dict[str, Any]]:
        """Returns the full FAQ dataset."""
        return self.faqs

    def get_sample_questions(self) -> List[str]:
        """Returns a curated list of representative sample questions."""
        return [
            "How do I reset my password?",
            "What are the API rate limits and quotas?",
            "What pricing plans are available?",
            "How does TF-IDF and Cosine Similarity work?",
            "Is my data used to train AI models?",
            "Can I get a refund on subscription?",
            "Does the platform support Two-Factor Authentication (2FA)?",
            "What payment methods do you accept?"
        ]
