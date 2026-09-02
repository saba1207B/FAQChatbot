"""
=============================================================================
Project: CodeAlpha_FAQChatbot (CodeAlpha AI Internship)
Module:  preprocessing.py
Author:  AI NLP Developer
Purpose: Text normalization, tokenization, stopword removal, and lemmatization
=============================================================================
"""

import re
import string
from typing import List

# Import NLTK components with resilient automatic downloads and fallback sets
try:
    import nltk
    from nltk.corpus import stopwords
    from nltk.tokenize import word_tokenize
    from nltk.stem import WordNetLemmatizer, PorterStemmer

    # Attempt to download required NLTK corpuses quietly if missing
    for resource in ["punkt", "punkt_tab", "stopwords", "wordnet"]:
        try:
            nltk.download(resource, quiet=True)
        except Exception:
            pass
    
    _lemmatizer = WordNetLemmatizer()
    _stemmer = PorterStemmer()
    try:
        _stop_words = set(stopwords.words("english"))
    except Exception:
        _stop_words = {
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
        }
except ImportError:
    _lemmatizer = None
    _stemmer = None
    _stop_words = {
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any",
        "are", "as", "at", "be", "because", "been", "before", "being", "below", "between",
        "both", "but", "by", "can", "could", "did", "do", "does", "doing", "down", "during",
        "each", "few", "for", "from", "further", "had", "has", "have", "having", "he",
        "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in",
        "into", "is", "it", "its", "itself", "just", "me", "more", "most", "my", "myself",
        "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "our",
        "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so", "some",
        "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then",
        "there", "these", "they", "this", "those", "through", "to", "too", "under", "until",
        "up", "very", "was", "we", "were", "what", "when", "where", "which", "while",
        "who", "whom", "why", "with", "would", "you", "your", "yours", "yourself"
    }


class TextPreprocessor:
    """
    Modular text preprocessor for NLP pipelines.
    Applies lowercasing, punctuation stripping, tokenization,
    stopword filtering, and lemmatization/stemming.
    """

    def __init__(self, use_stemming: bool = False, remove_stopwords: bool = True):
        self.use_stemming = use_stemming
        self.remove_stopwords = remove_stopwords
        self.stop_words = _stop_words

    def clean_text(self, text: str) -> str:
        """
        Removes special characters, URLs, extra whitespace, and converts to lowercase.
        """
        if not text or not isinstance(text, str):
            return ""

        # Lowercase
        cleaned = text.lower()
        # Remove URLs if any
        cleaned = re.sub(r"https?://\S+|www\.\S+", "", cleaned)
        # Remove punctuation and numbers/symbols while keeping alphanumeric words
        cleaned = re.sub(r"[^a-zA-Z0-9\s]", " ", cleaned)
        # Collapse multiple whitespaces
        cleaned = re.sub(r"\s+", " ", cleaned).strip()
        return cleaned

    def tokenize(self, text: str) -> List[str]:
        """
        Splits normalized text into word tokens.
        """
        cleaned = self.clean_text(text)
        if not cleaned:
            return []
        
        # Prefer word_tokenize if NLTK is loaded, else fallback to regex split
        try:
            tokens = word_tokenize(cleaned)
        except Exception:
            tokens = cleaned.split()
            
        return tokens

    def lemmatize_or_stem(self, token: str) -> str:
        """
        Reduces word to its root lemma or morphological stem.
        """
        if _lemmatizer:
            try:
                lemma = _lemmatizer.lemmatize(token)
                if lemma == token and self.use_stemming and _stemmer:
                    return _stemmer.stem(token)
                return lemma
            except Exception:
                pass
        
        if self.use_stemming and _stemmer:
            try:
                return _stemmer.stem(token)
            except Exception:
                pass

        return token

    def preprocess(self, text: str) -> str:
        """
        Complete end-to-end preprocessing pipeline returning a space-separated normalized string.
        """
        tokens = self.tokenize(text)
        filtered_tokens = []

        for token in tokens:
            if len(token) <= 1 and token not in ["c", "r"]: # Filter single characters except programming languages
                continue
            if self.remove_stopwords and token in self.stop_words:
                continue
            normalized_token = self.lemmatize_or_stem(token)
            filtered_tokens.append(normalized_token)

        # Return joined preprocessed string for TF-IDF vectorizer compatibility
        return " ".join(filtered_tokens)


# Singleton preprocessor helper functions for quick usage
_default_preprocessor = TextPreprocessor()

def preprocess_text(text: str) -> str:
    """Convenience helper for preprocessing a single string."""
    return _default_preprocessor.preprocess(text)

def clean_and_tokenize(text: str) -> List[str]:
    """Convenience helper to get token list."""
    return _default_preprocessor.tokenize(text)
