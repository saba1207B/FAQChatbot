/*
Copyright (c) 2026 Sabareesh M (saba1207B). All Rights Reserved.

This source code is proprietary and confidential.

Unauthorized copying, redistribution, modification, or use — including
any use for artificial intelligence / machine learning training —
is strictly prohibited.

See the LICENSE and COPYRIGHT files in the repository root for full terms.
*/

/**
 * CodeAlpha FAQ Chatbot - Frontend Client Logic
 * Handles real-time messaging, similarity threshold tuning,
 * API communication, and FAQ library browsing.
 */

// Format current clock time (HH:MM AM/PM)
function getCurrentTime() {
    const now = new Date();
    return now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// Initial initialization
document.addEventListener('DOMContentLoaded', () => {
    const welcomeTimeEl = document.getElementById('welcome-time');
    if (welcomeTimeEl) {
        welcomeTimeEl.textContent = getCurrentTime();
    }

    // Threshold slider listener
    const thresholdSlider = document.getElementById('threshold-slider');
    const thresholdVal = document.getElementById('threshold-val');
    if (thresholdSlider && thresholdVal) {
        thresholdSlider.addEventListener('input', (e) => {
            thresholdVal.textContent = parseFloat(e.target.value).toFixed(2);
        });
    }
});

// Sends user message or sample question to the Flask backend
async function sendUserMessage(messageText) {
    if (!messageText || !messageText.trim()) return;

    const messagesContainer = document.getElementById('messages-container');
    const typingIndicator = document.getElementById('typing-indicator');
    const thresholdSlider = document.getElementById('threshold-slider');
    const activeThreshold = thresholdSlider ? parseFloat(thresholdSlider.value) : 0.22;

    // 1. Append User Message
    appendMessage({
        sender: 'user',
        text: messageText,
        time: getCurrentTime()
    });

    // Clear input
    const inputEl = document.getElementById('user-input');
    if (inputEl) inputEl.value = '';

    // 2. Show typing indicator
    if (typingIndicator) typingIndicator.classList.add('active');
    scrollToBottom();

    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                message: messageText,
                threshold: activeThreshold
            })
        });

        const json = await response.json();
        
        // Hide typing indicator
        if (typingIndicator) typingIndicator.classList.remove('active');

        if (response.ok && json.status === 'success') {
            const data = json.data;
            appendMessage({
                sender: 'bot',
                text: data.answer,
                matched: data.matched,
                score: data.similarity_score,
                confidence: data.confidence_percentage,
                queryTokens: data.processed_query,
                faqItem: data.faq_item,
                topSuggestions: data.top_suggestions,
                time: getCurrentTime()
            });
        } else {
            appendMessage({
                sender: 'bot',
                text: json.message || "An unexpected error occurred while processing your request.",
                matched: false,
                score: 0.0,
                time: getCurrentTime()
            });
        }
    } catch (err) {
        if (typingIndicator) typingIndicator.classList.remove('active');
        appendMessage({
            sender: 'bot',
            text: "Network error: Unable to connect to the FAQ chatbot server. Please ensure app.py is running.",
            matched: false,
            score: 0.0,
            time: getCurrentTime()
        });
        console.error("Chat API error:", err);
    }

    scrollToBottom();
}

// Appends message DOM node
function appendMessage(msg) {
    const messagesContainer = document.getElementById('messages-container');
    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${msg.sender}-message`;

    if (msg.sender === 'user') {
        msgDiv.innerHTML = `
            <div class="msg-avatar"><i class="fa-solid fa-user"></i></div>
            <div class="msg-content">
                <div class="msg-bubble">${escapeHtml(msg.text)}</div>
                <span class="msg-time">${msg.time}</span>
            </div>
        `;
    } else {
        // Formulate score badge & breakdown
        let badgeHtml = '';
        let breakdownHtml = '';

        if (msg.score !== undefined && msg.score > 0) {
            const badgeClass = msg.matched ? (msg.score >= 0.50 ? 'match-high' : 'match-mid') : 'match-fallback';
            const badgeText = msg.matched
                ? `<i class="fa-solid fa-circle-check"></i> ${msg.confidence}% Similarity Match`
                : `<i class="fa-solid fa-circle-xmark"></i> Below Threshold (${(msg.score * 100).toFixed(0)}%)`;

            badgeHtml = `<div class="match-meta-pill ${badgeClass}">${badgeText}</div>`;

            if (msg.faqItem) {
                breakdownHtml = `
                    <div class="nlp-breakdown-card">
                        <div><strong>Matched FAQ:</strong> "${escapeHtml(msg.faqItem.question)}"</div>
                        <div><strong>Category:</strong> ${escapeHtml(msg.faqItem.category)} • <strong>Tokens:</strong> <code>${escapeHtml(msg.queryTokens)}</code></div>
                    </div>
                `;
            }
        }

        // Top suggestions if fallback
        let suggestionsHtml = '';
        if (!msg.matched && msg.topSuggestions && msg.topSuggestions.length > 0) {
            const chips = msg.topSuggestions.map(s => 
                `<button class="quick-chip" onclick="sendSampleQuestion('${escapeHtml(s.question)}')">${escapeHtml(s.question)} (${s.confidence_percentage}%)</button>`
            ).join('');
            
            suggestionsHtml = `
                <div class="sample-chips-container">
                    <span>Did you mean one of these?</span>
                    <div class="chips-row">${chips}</div>
                </div>
            `;
        }

        msgDiv.innerHTML = `
            <div class="msg-avatar"><i class="fa-solid fa-robot"></i></div>
            <div class="msg-content">
                <div class="msg-bubble">
                    ${badgeHtml}
                    <p>${escapeHtml(msg.text)}</p>
                    ${breakdownHtml}
                    ${suggestionsHtml}
                </div>
                <span class="msg-time">${msg.time}</span>
            </div>
        `;
    }

    messagesContainer.appendChild(msgDiv);
    scrollToBottom();
}

// Form submit handler
function handleChatSubmit(e) {
    e.preventDefault();
    const inputEl = document.getElementById('user-input');
    if (inputEl && inputEl.value.trim()) {
        sendUserMessage(inputEl.value.trim());
    }
}

// Quick sample question click
function sendSampleQuestion(question) {
    const inputEl = document.getElementById('user-input');
    if (inputEl) inputEl.value = question;
    sendUserMessage(question);
}

// Auto scroll helper
function scrollToBottom() {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}

// Clear conversation
function clearChat() {
    const messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) {
        messagesContainer.innerHTML = `
            <div class="message bot-message">
                <div class="msg-avatar"><i class="fa-solid fa-robot"></i></div>
                <div class="msg-content">
                    <div class="msg-bubble">
                        <p><strong>Chat conversation cleared.</strong></p>
                        <p>Ask any question about the platform, pricing, API rate limits, authentication, or NLP algorithms!</p>
                    </div>
                    <span class="msg-time">${getCurrentTime()}</span>
                </div>
            </div>
        `;
    }
}

// FAQ Modal Drawer Logic
let cachedFaqs = [];

async function openFaqModal() {
    const modal = document.getElementById('faq-modal');
    if (modal) modal.classList.add('active');

    if (cachedFaqs.length === 0) {
        try {
            const res = await fetch('/api/faqs');
            const json = await res.json();
            if (json.status === 'success') {
                cachedFaqs = json.data;
                renderFaqList(cachedFaqs);
            }
        } catch (e) {
            document.getElementById('faq-list-container').innerHTML = '<p>Failed to load FAQ list.</p>';
        }
    } else {
        renderFaqList(cachedFaqs);
    }
}

function closeFaqModal() {
    const modal = document.getElementById('faq-modal');
    if (modal) modal.classList.remove('active');
}

function renderFaqList(faqs) {
    const container = document.getElementById('faq-list-container');
    if (!container) return;

    if (faqs.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #9ca3af;">No FAQs match your search query.</p>';
        return;
    }

    container.innerHTML = faqs.map(faq => `
        <div class="faq-item-card" onclick="sendSampleQuestion('${escapeHtml(faq.question)}'); closeFaqModal();">
            <span class="faq-category-tag">${escapeHtml(faq.category)}</span>
            <div class="faq-item-q">${escapeHtml(faq.question)}</div>
            <div class="faq-item-a">${escapeHtml(faq.answer)}</div>
        </div>
    `).join('');
}

function filterFaqList() {
    const query = (document.getElementById('modal-filter').value || '').toLowerCase();
    const filtered = cachedFaqs.filter(faq => 
        faq.question.toLowerCase().includes(query) ||
        faq.answer.toLowerCase().includes(query) ||
        faq.category.toLowerCase().includes(query) ||
        (faq.tags && faq.tags.some(t => t.toLowerCase().includes(query)))
    );
    renderFaqList(filtered);
}

// Utility HTML escape
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
