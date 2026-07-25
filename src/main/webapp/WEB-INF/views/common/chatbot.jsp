<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- MonikaMart AI Chatbot Floating Widget (Feature O4 / Section 11) -->
<div id="chat-widget-container">
    <button id="chat-widget-launcher" title="Ask MonikaMart AI Assistant">
        💬
        <span id="chat-widget-badge">AI</span>
    </button>

    <div id="chat-window">
        <div class="chat-header">
            <div class="chat-header-info">
                <div class="chat-avatar">🤖</div>
                <div>
                    <div class="chat-title">MonikaMart AI</div>
                    <div class="chat-status">
                        <span class="status-dot"></span> Online Assistant
                    </div>
                </div>
            </div>
            <button id="chat-close-btn" class="chat-close-btn" title="Close Chat">&times;</button>
        </div>

        <div id="chat-messages" class="chat-messages">
            <div class="chat-msg msg-bot">
                <p>Hello! I am your MonikaMart AI shopping concierge. How can I help you discover products, track orders, or explain our seller portal today?</p>
                <div class="chat-meta">Offline / Live Gemini Engine</div>
            </div>
        </div>

        <div id="chat-suggestions" class="chat-suggestions">
            <!-- Dynamic suggestion chips populated via JS -->
        </div>

        <div class="chat-input-bar">
            <input type="text" id="chat-input-text" placeholder="Ask about products, orders, returns..." maxlength="500" autocomplete="off">
            <button type="button" id="chat-send-btn" class="chat-send-btn" title="Send Message">➤</button>
        </div>
    </div>
</div>
