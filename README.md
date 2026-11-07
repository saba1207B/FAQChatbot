# FAQ Chatbot 🤖

> A modern AI-powered FAQ chatbot application built to demonstrate Natural Language Processing and intelligent question matching.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg)](https://kotlinlang.org/)
[![Jetpack%20Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/compose)
[![NLP](https://img.shields.io/badge/AI-NLP-orange.svg)](https://en.wikipedia.org/wiki/Natural_language_processing)

## 📱 Overview

**FAQ Chatbot** is a native Android application that allows users to ask questions in natural language and receive the most relevant answer from a structured FAQ knowledge base.

The project demonstrates a practical Natural Language Processing workflow using **text preprocessing, TF-IDF vectorization, and cosine similarity** rather than relying on hardcoded question-and-answer rules or a paid AI API.

This project was developed as part of an **Artificial Intelligence Internship — Task 2: Chatbot for FAQs**.

## ✨ Features

### 🤖 Intelligent FAQ Chatbot

- Natural-language question input.
- FAQ matching using TF-IDF and cosine similarity.
- Best-match answer selection.
- Configurable similarity threshold.
- Graceful fallback for unknown questions.
- Suggested FAQ questions when no confident match is found.

### 📚 FAQ Explorer

- Browse the available FAQ knowledge base.
- Search through FAQ content.
- Organised FAQ categories.
- Quick access to common questions.

### 💬 Conversation Experience

- Clean modern chat interface.
- Conversation history during the session.
- Clear-chat functionality.
- Responsive Jetpack Compose UI.

### 🔒 Local & Accessible

- Core NLP matching works locally.
- No paid AI API is required.
- No API key is required for the chatbot engine.
- Designed as an educational and portfolio project.

## 🧠 How It Works

```text
User Question
      ↓
Text Preprocessing
      ↓
TF-IDF Vectorization
      ↓
Cosine Similarity
      ↓
Best FAQ Match
      ↓
Similarity Threshold
   ↙           ↘
Match       No Confident Match
  ↓               ↓
Answer        Suggestions
```

The application converts the user's question and FAQ entries into numerical TF-IDF representations. Cosine similarity is then used to identify the closest FAQ. A threshold prevents weak matches from being presented as confident answers.

## 🛠️ Technology Stack

| Category | Technology |
|---|---|
| Platform | Android |
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material 3 |
| AI / NLP | TF-IDF + Cosine Similarity |
| Architecture | ViewModel-based Android architecture |
| Async Processing | Kotlin Coroutines |
| Reference Implementation | Python + Flask |
| Build System | Gradle + Android Gradle Plugin |
| CI/CD | GitHub Actions |

## 🏗️ Project Structure

```text
FAQChatbot/
├── app/                              # Native Android application
├── FAQChatbot/             # Python/Flask reference implementation
├── public/                           # Project assets
├── .github/
│   └── workflows/
│       └── build-apk.yml             # Automated APK build
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── metadata.json
└── README.md
```

## 🚀 Build the Android App

### Requirements

- Android Studio or a compatible Gradle environment.
- JDK 17.
- Android SDK compatible with the project configuration.

### Build Debug APK

```bash
gradle :app:assembleDebug
```

The generated APK is located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 🤖 GitHub Actions APK Build

The repository includes an automated GitHub Actions workflow that builds the Android application on pushes to `main` and through manual workflow dispatch.

The workflow:

1. Checks out the source code.
2. Configures JDK 17.
3. Installs Gradle 9.3.1.
4. Creates an isolated CI debug keystore.
5. Builds the debug APK.
6. Uploads the APK as a workflow artifact.

This allows the project to be built through GitHub Actions without requiring a local development computer for the APK build.

## 📸 Screenshots

Recommended application screenshots:

```text
screenshots/
├── chatbot.png
├── faq-response.png
├── fallback.png
├── faq-explorer.png
└── search.png
```

## 🎥 Project Demonstration

A short demonstration can show:

1. The internship project introduction.
2. The chatbot interface.
3. A natural-language FAQ question.
4. The matched answer.
5. An unrelated question and fallback response.
6. The FAQ explorer/search experience.
7. The GitHub repository and project structure.

## 🎯 Internship Project

**Program:** Artificial Intelligence Internship  
**Task:** Task 2 — Chatbot for FAQs  
**Project Type:** AI / Natural Language Processing  
**Platform:** Android

The project is intended to demonstrate practical application of NLP concepts in a complete software project suitable for internship evaluation and portfolio presentation.

## 🔮 Future Improvements

- Semantic sentence embeddings.
- Multilingual FAQ support.
- Voice-based questions.
- Analytics for unanswered questions.
- Remote FAQ management.
- More advanced conversational context.

## 👤 Author

**Sabareesh**

GitHub: [@saba1207B](https://github.com/saba1207B)

## 📄 License

MIT License

---

⭐ If you find this project useful, consider starring the repository.