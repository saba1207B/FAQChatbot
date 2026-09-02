"""
=============================================================================
Project: CodeAlpha_FAQChatbot (CodeAlpha AI Internship)
Module:  app.py
Author:  AI NLP Developer
Purpose: Flask REST API and Web Application Server for FAQ Chatbot
=============================================================================
"""

import os
import logging
from flask import Flask, render_template, request, jsonify

from src.chatbot import FAQChatbot

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
logger = logging.getLogger("CodeAlpha_FAQChatbot")

app = Flask(__name__)

# Locate FAQ dataset relative to project root
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
FAQ_DATA_PATH = os.path.join(BASE_DIR, "data", "faqs.json")

# Initialize Chatbot Engine
chatbot_engine = FAQChatbot(
    faq_filepath=FAQ_DATA_PATH,
    similarity_threshold=0.22
)


@app.route("/")
def home():
    """Renders the main Chatbot interface."""
    sample_questions = chatbot_engine.get_sample_questions()
    total_faqs = len(chatbot_engine.get_all_faqs())
    return render_template(
        "index.html",
        sample_questions=sample_questions,
        total_faqs=total_faqs,
        default_threshold=chatbot_engine.similarity_threshold
    )


@app.route("/api/chat", methods=["POST"])
def chat_endpoint():
    """
    Main API endpoint for chatbot communication.
    Accepts JSON payload:
    {
        "message": "User query string",
        "threshold": 0.25 (optional float)
    }
    """
    try:
        data = request.get_json(force=True)
        if not data or "message" not in data:
            return jsonify({
                "status": "error",
                "message": "Missing 'message' field in JSON request body."
            }), 400

        user_message = str(data.get("message", "")).strip()
        custom_threshold = data.get("threshold", None)

        if custom_threshold is not None:
            try:
                custom_threshold = float(custom_threshold)
                if not (0.0 <= custom_threshold <= 1.0):
                    custom_threshold = None
            except (ValueError, TypeError):
                custom_threshold = None

        # Execute NLP query processing and TF-IDF cosine matching
        result = chatbot_engine.get_response(user_message, threshold=custom_threshold)

        return jsonify({
            "status": "success",
            "data": result
        }), 200

    except Exception as e:
        logger.exception("Error processing /api/chat request")
        return jsonify({
            "status": "error",
            "message": f"Internal server error: {str(e)}"
        }), 500


@app.route("/api/faqs", methods=["GET"])
def get_faqs_endpoint():
    """Returns all FAQ dataset entries grouped by category."""
    faqs = chatbot_engine.get_all_faqs()
    return jsonify({
        "status": "success",
        "total": len(faqs),
        "data": faqs
    }), 200


@app.route("/api/samples", methods=["GET"])
def get_samples_endpoint():
    """Returns quick test sample questions for demonstration."""
    samples = chatbot_engine.get_sample_questions()
    return jsonify({
        "status": "success",
        "samples": samples
    }), 200


@app.route("/api/health", methods=["GET"])
def health_check():
    """Service health verification endpoint."""
    return jsonify({
        "status": "healthy",
        "service": "CodeAlpha_FAQChatbot",
        "faqs_loaded": len(chatbot_engine.get_all_faqs()),
        "model_trained": chatbot_engine.is_trained
    }), 200


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    logger.info(f"Starting CodeAlpha FAQ Chatbot on http://127.0.0.1:{port}")
    app.run(host="0.0.0.0", port=port, debug=True)
