# CodeAlpha FAQ Chatbot 🤖

**CodeAlpha Artificial Intelligence Internship — Task 2: Chatbot for FAQs**

A professional Android FAQ assistant built with Kotlin and Jetpack Compose, backed by a real local NLP pipeline using **text preprocessing, TF-IDF vectorization, and cosine similarity**. The repository also contains the Python reference implementation used to demonstrate the same NLP workflow.

## ✨ Highlights

- Real FAQ matching — no rigid `if/else` question matching
- Local NLP processing with no paid API or API key requirement
- TF-IDF feature representation and cosine-similarity ranking
- Configurable similarity threshold and graceful fallback
- FAQ categories, search and suggestions
- Conversation history and clear-chat support
- Responsive modern Jetpack Compose interface
- Python/Flask reference implementation included under `CodeAlpha_FAQChatbot/`
- GitHub Actions workflow for automatic Android debug APK builds

## 🎯 CodeAlpha Task Alignment

This project implements **Task 2 — Chatbot for FAQs**. The application collects a structured FAQ knowledge base, preprocesses natural-language questions, compares a user's query with the FAQ corpus, and returns the closest answer when the similarity score reaches the configured threshold.

| Requirement | Implementation |
|---|---|
| FAQ dataset | Local structured FAQ dataset |
| NLP preprocessing | Tokenization, cleaning, stopword handling and n-gram extraction |
| Similarity matching | TF-IDF + cosine similarity |
| Best answer | Highest-scoring FAQ |
| Unknown questions | Threshold-based fallback + suggestions |
| User interface | Android Jetpack Compose |
| External paid API | Not required |

## 🧠 How It Works

```text
User Question
     ↓
Text Preprocessing
     ↓
Token / N-gram Extraction
     ↓
TF-IDF Vector Representation
     ↓
Cosine Similarity Against FAQ Corpus
     ↓
Highest Similarity Score
     ↓
Threshold Check
   ↙       ↘
Match     Fallback
   ↓         ↓
Answer    Suggestions
```

### TF-IDF

TF-IDF assigns greater weight to terms that are informative within the FAQ corpus and lower weight to terms that occur broadly across documents.

### Cosine Similarity

The query vector is compared with every FAQ vector. The FAQ with the highest similarity score is selected, provided it meets the configured threshold.

## 📱 Android Application

The Android application is implemented with:

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Lifecycle/ViewModel
- Kotlin Coroutines
- Local FAQ dataset
- Local TF-IDF engine

The main Android entry point is `app/src/main/java/com/example/MainActivity.kt`, with the chatbot state and matching workflow handled by `ChatViewModel`.

## 🐍 Python Reference Implementation

A complete Python/Flask version is retained in:

```text
CodeAlpha_FAQChatbot/
├── app.py
├── requirements.txt
├── data/
├── src/
├── templates/
└── static/
```

It provides a browser-based demonstration of the same NLP concept and is useful for technical inspection and experimentation.

## 📁 Repository Structure

```text
CodeAlpha_FAQChatbot/
├── app/                         # Android application module
├── CodeAlpha_FAQChatbot/        # Python/Flask reference implementation
├── public/                      # Project assets
├── gradle/                      # Gradle configuration
├── .github/
│   └── workflows/
│       └── build-apk.yml        # Automatic APK build
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── metadata.json
└── README.md
```

## 🚀 Build the Android App

### Requirements

- Android Studio or a compatible Gradle environment
- JDK 17
- Android SDK with the project's configured compile SDK

### Local build

```bash
gradle :app:assembleDebug
```

The APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## ⚙️ GitHub Actions APK Build

Every push to `main` and every manual workflow dispatch runs the Android build workflow.

The workflow:

1. Checks out the repository.
2. Installs JDK 17.
3. Installs Gradle 9.3.1.
4. Creates an isolated CI debug keystore.
5. Builds `:app:assembleDebug`.
6. Publishes the APK as a GitHub Actions artifact.

No private signing key is stored in the repository.

## 🧪 Testing Checklist

- Launch the app.
- Ask a known FAQ question.
- Confirm that the most relevant FAQ answer is returned.
- Try a differently worded question.
- Adjust the similarity threshold.
- Try an unrelated question and verify graceful fallback.
- Open the FAQ explorer/search interface.
- Clear the conversation and start again.

## 📸 Screenshots

Add final demonstration screenshots to a `screenshots/` directory when available. Recommended captures:

1. Main chatbot screen
2. High-similarity FAQ response
3. Threshold/fallback response
4. FAQ browser/search interface

## 🎥 LinkedIn Demonstration

Recommended 60–90 second flow:

1. Introduce the CodeAlpha Task 2 project.
2. Show the Android chatbot interface.
3. Ask a natural-language FAQ question.
4. Show the similarity/confidence result and returned answer.
5. Demonstrate an unrelated question and fallback.
6. Briefly show the TF-IDF/cosine-similarity implementation.
7. End with the GitHub repository link.

## 🔐 Privacy & Dependencies

The core FAQ matching workflow is local and does not require a paid translation/LLM API. No API key is needed for the NLP matching engine.

## 📌 Limitations

- Retrieval quality depends on the FAQ knowledge base.
- TF-IDF is lexical rather than deep semantic understanding.
- Questions containing concepts absent from the dataset may fall back even when a human would understand the intent.

## 🔮 Future Improvements

- Sentence-transformer semantic embeddings
- Multilingual FAQ support
- Voice input
- Analytics for unanswered questions
- Remote FAQ synchronization
- Automated FAQ ingestion

## 👤 Author

**Sabareesh**

CodeAlpha Artificial Intelligence Internship — Task 2

## 📄 License

MIT License
