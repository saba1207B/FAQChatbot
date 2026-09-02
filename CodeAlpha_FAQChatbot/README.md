# CodeAlpha_FAQChatbot 🤖
### AI & NLP-Powered FAQ Assistant Engine
**CodeAlpha Artificial Intelligence Internship — Task 1 Submission**

![Python](https://img.shields.io/badge/Python-3.9%2B-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Flask](https://img.shields.io/badge/Flask-3.0.3-000000?style=for-the-badge&logo=flask&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/scikit--learn-1.4.2-F7931E?style=for-the-badge&logo=scikit-learn&logoColor=white)
![NLTK](https://img.shields.io/badge/NLTK-3.8.1-154f5b?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

---

## 📌 Project Overview
**CodeAlpha_FAQChatbot** is an intelligent, lightweight, and completely local Natural Language Processing (NLP) conversational agent. Designed specifically to automate customer and developer support for a cloud AI SaaS platform, this project demonstrates real computational text matching using **Term Frequency-Inverse Document Frequency (TF-IDF)** and **Cosine Similarity** without relying on paid or rate-limited third-party APIs.

Unlike basic if/else pattern matchers or regex bots, this system builds a sparse mathematical vector space representation of all frequently asked questions. When a user enters a query, the text undergoes multi-stage NLP cleaning, is mapped into the TF-IDF feature space, and evaluated via vector dot products to find the semantically closest answer.

---

## 🎯 CodeAlpha Internship Task Scope
- **Domain:** Artificial Intelligence & Natural Language Processing (NLP)
- **Objective:** Develop an end-to-end conversational FAQ system capable of tokenizing unstructured input, calculating similarity scores against a knowledge base, returning the highest-confidence match, and handling out-of-domain questions gracefully with configurable thresholds.

---

## 🌟 Key Features
- **📊 30+ Structured FAQ Knowledge Base:** Realistic dataset covering General Information, Authentication, API & SDK Integration, Rate Limits, Billing, Security & GDPR, and Troubleshooting.
- **🧠 Real Mathematical NLP Matching:** Full end-to-end pipeline implementing lowercasing, punctuation stripping, tokenization, stopword filtering, unigram/bigram TF-IDF vectorization, and L2-normalized cosine similarity.
- **⚙️ Configurable Similarity Threshold:** Interactive slider allowing dynamic adjustment of the matching threshold (default `0.22`) with real-time confidence badges.
- **🛡️ Graceful Fallback & Smart Suggestions:** If a user query does not meet the confidence threshold, the bot returns an informative fallback response along with top nearest question suggestions.
- **🎨 Glassmorphic Modern UI:** Dark-mode responsive interface with real-time typing indicators, message timestamps, conversation history clearing, and quick sample question pills.
- **⚡ 100% Offline & Private:** Operates entirely locally with zero API keys or external server dependencies.

---

## 🔬 AI & NLP Methodology

### 1. NLP Preprocessing Pipeline
Every input query and FAQ document undergoes standard NLP normalization:
```
Raw User Text ➡️ Lowercasing ➡️ URL & Punctuation Removal ➡️ Tokenization ➡️ Stopword Filtering ➡️ Lemmatization / Stemming ➡️ Clean Token Sequence
```

### 2. How TF-IDF Vectorization Works
**TF-IDF** (Term Frequency-Inverse Document Frequency) quantifies word importance within a document relative to an entire corpus.

$$\text{TF}(t, d) = \frac{f_{t, d}}{\sum_{t' \in d} f_{t', d}}$$

$$\text{IDF}(t, D) = \ln\left(\frac{1 + |D|}{1 + |\{d \in D : t \in d\}|}\right) + 1$$

$$\text{TF-IDF}(t, d, D) = \text{TF}(t, d) \times \text{IDF}(t, D)$$

- **Term Frequency (TF):** Measures how frequently a word $t$ appears in document $d$.
- **Inverse Document Frequency (IDF):** Penalizes ubiquitous terms (e.g. *what*, *how*, *is*) and assigns higher weights to informative keywords (e.g. *pricing*, *rate-limit*, *encryption*).

### 3. How Cosine Similarity Works
Cosine similarity computes the cosine of the angle between the query vector $\mathbf{q}$ and FAQ document vector $\mathbf{d}_i$:

$$\text{Cosine Similarity}(\mathbf{q}, \mathbf{d}_i) = \frac{\mathbf{q} \cdot \mathbf{d}_i}{\|\mathbf{q}\|_2 \|\mathbf{d}_i\|_2} = \frac{\sum_{j=1}^{V} q_j d_{ij}}{\sqrt{\sum_{j=1}^V q_j^2} \sqrt{\sum_{j=1}^V d_{ij}^2}}$$

- Scores range from **`0.0`** (completely orthogonal / zero overlap) to **`1.0`** (identical vector orientation).
- If $\max_i(\text{Score}_i) \ge \text{Threshold}$, the corresponding answer is returned; otherwise, the fallback handler is triggered.

---

## 🛠️ Technology Stack
| Layer | Technologies |
|---|---|
| **Programming Language** | Python 3.9+ |
| **Backend Web Framework** | Flask 3.0.3, Jinja2, Werkzeug |
| **Machine Learning & NLP** | scikit-learn (TfidfVectorizer, cosine_similarity), NLTK, NumPy |
| **Frontend Interface** | HTML5, CSS3 (Glassmorphism & Flexbox), Modern JavaScript (ES6+ Fetch API) |
| **Deployment & WSGI** | Gunicorn |

---

## 📁 Project Structure
```
CodeAlpha_FAQChatbot/
├── app.py                     # Flask web server and REST API endpoints
├── requirements.txt           # Python dependencies
├── README.md                  # Comprehensive documentation and guide
├── .gitignore                 # Git ignore configuration
├── data/
│   └── faqs.json              # 30+ structured FAQ knowledge dataset
├── src/
│   ├── __init__.py            # Package initializer
│   ├── preprocessing.py       # Tokenization, cleaning, stopword filtering
│   └── chatbot.py             # TF-IDF & Cosine Similarity chatbot engine
├── templates/
│   └── index.html             # Chatbot UI template
├── static/
│   ├── style.css              # Modern glassmorphism UI styles
│   └── script.js              # Real-time chat client, fetch API, DOM handling
└── screenshots/               # Demo screenshots for documentation
```

---

## 🚀 Installation Instructions

### Prerequisites
- Python 3.9 or higher installed on your system.
- Git (optional, for cloning).

### 1. Clone or Navigate to Project
```bash
cd CodeAlpha_FAQChatbot
```

### 2. Create and Activate a Virtual Environment
```bash
# On Linux / macOS
python3 -m venv venv
source venv/bin/activate

# On Windows
python -m venv venv
venv\Scripts\activate
```

### 3. Install Dependencies
```bash
pip install -r requirements.txt
```

---

## 💻 How to Run the Chatbot

Start the Flask application server:
```bash
python app.py
```

Open your browser and navigate to:
```
http://127.0.0.1:5000
```

---

## 🧪 Example Questions to Test

| Category | Example Question | Expected Match |
|---|---|---|
| **Authentication** | *"How can I reset my forgotten password?"* | Password reset instructions |
| **API & Quotas** | *"What is the rate limit for free tier accounts?"* | 60 RPM & 10,000 TPM limit details |
| **Pricing** | *"What subscription tiers and pricing plans do you offer?"* | Developer, Pro & Enterprise tiers |
| **NLP Math** | *"How does cosine similarity calculate vectors?"* | Mathematical angle formula explanation |
| **Security** | *"Is customer data used to train AI models?"* | Zero data retention privacy policy |
| **Fallback Test** | *"What is the recipe for chocolate chip cookies?"* | Graceful out-of-scope fallback & suggestions |

---

## 📸 Screenshots & LinkedIn Demonstration Guide

### Suggested Screenshots for Submission / LinkedIn:
1. **Hero View:** Clean UI with the sidebar configuration and initial welcome greeting.
2. **High Similarity Query:** A technical question (e.g. *"What are the API rate limits?"*) showing the green **92% Similarity Match** badge and matched token metadata.
3. **NLP Threshold Fallback:** An unrelated query showing the fallback response and suggested relevant topics.
4. **Knowledge Base Modal:** The FAQ dataset explorer drawer displaying categorized cards.

### Recommended LinkedIn Post Caption:
> 🚀 Excited to share my latest Artificial Intelligence project completed during my **CodeAlpha AI Internship**: **CodeAlpha_FAQChatbot**!
> 
> 🧠 Built a full-stack conversational NLP chatbot using **Python, Flask, NLTK, and scikit-learn**. Instead of rigid keyword rules, this bot transforms unstructured natural language into **TF-IDF sparse vector representations** and computes **Cosine Similarity** to instantly retrieve high-precision answers from a knowledge base.
> 
> Key features:
> ✅ 30+ structured FAQ knowledge base
> ✅ Real TF-IDF & Cosine Similarity mathematical matching
> ✅ Configurable similarity threshold & graceful fallback
> ✅ Glassmorphic responsive web interface
> ✅ 100% offline & zero API key dependency
> 
> #ArtificialIntelligence #NLP #MachineLearning #Python #Flask #CodeAlpha #Internship #DataScience

---

## 🔮 Future Improvements
- [ ] Integration of dense sentence embeddings (e.g. `all-MiniLM-L6-v2` via Sentence-Transformers) for hybrid sparse-dense retrieval.
- [ ] Voice input & Speech-to-Text (STT) integration via Web Speech API.
- [ ] Admin panel for real-time CRUD management of the FAQ dataset.
- [ ] Multi-lingual support using NLTK translation & multilingual tokenizers.

---

## 👤 Author
- **Intern:** AI/NLP Engineering Intern
- **Program:** CodeAlpha Artificial Intelligence Internship
- **Project:** CodeAlpha_FAQChatbot
