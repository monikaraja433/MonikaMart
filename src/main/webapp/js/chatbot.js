// MonikaMart Floating AI Chatbot Widget Controller
(function() {
    let isOpen = false;
    let isWaiting = false;

    document.addEventListener('DOMContentLoaded', () => {
        const launcher = document.getElementById('chat-widget-launcher');
        const chatWindow = document.getElementById('chat-window');
        const closeBtn = document.getElementById('chat-close-btn');
        const sendBtn = document.getElementById('chat-send-btn');
        const inputField = document.getElementById('chat-input-text');
        const messagesContainer = document.getElementById('chat-messages');
        const suggestionsContainer = document.getElementById('chat-suggestions');

        if (!launcher || !chatWindow) return;

        function toggleChat() {
            isOpen = !isOpen;
            chatWindow.style.display = isOpen ? 'flex' : 'none';
            if (isOpen) {
                inputField.focus();
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            }
        }

        launcher.addEventListener('click', toggleChat);
        closeBtn.addEventListener('click', toggleChat);

        function addMessage(sender, text, meta) {
            const msgDiv = document.createElement('div');
            msgDiv.className = 'chat-msg ' + (sender === 'user' ? 'msg-user' : 'msg-bot');
            
            const p = document.createElement('p');
            p.textContent = text;
            msgDiv.appendChild(p);

            if (meta) {
                const metaSpan = document.createElement('div');
                metaSpan.className = 'chat-meta';
                metaSpan.textContent = meta;
                msgDiv.appendChild(metaSpan);
            }

            messagesContainer.appendChild(msgDiv);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }

        function showTypingIndicator() {
            const typing = document.createElement('div');
            typing.id = 'chat-typing-indicator';
            typing.className = 'typing-indicator';
            typing.innerHTML = '<span class="typing-dot"></span><span class="typing-dot"></span><span class="typing-dot"></span>';
            messagesContainer.appendChild(typing);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }

        function hideTypingIndicator() {
            const typing = document.getElementById('chat-typing-indicator');
            if (typing) typing.remove();
        }

        function setSuggestions(list) {
            suggestionsContainer.innerHTML = '';
            if (!list || list.length === 0) return;

            list.forEach(item => {
                const chip = document.createElement('button');
                chip.type = 'button';
                chip.className = 'suggestion-chip';
                chip.textContent = item;
                chip.addEventListener('click', () => {
                    inputField.value = item;
                    sendMessage();
                });
                suggestionsContainer.appendChild(chip);
            });
        }

        async function sendMessage() {
            const message = inputField.value.trim();
            if (!message || isWaiting) return;

            addMessage('user', message, null);
            inputField.value = '';
            isWaiting = true;
            showTypingIndicator();

            try {
                const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
                const apiEndpoint = (contextPath.startsWith('/') ? contextPath : '') + '/api/chat';

                const response = await fetch(apiEndpoint, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: JSON.stringify({ message: message })
                });

                const json = await response.json();
                hideTypingIndicator();

                if (json.success && json.data) {
                    addMessage('bot', json.data.reply, json.data.provider);
                    setSuggestions(json.data.suggestions);
                } else {
                    addMessage('bot', json.error || 'Sorry, I could not process your message right now.', 'System Warning');
                }
            } catch (err) {
                hideTypingIndicator();
                addMessage('bot', 'Network or connection issue. Please check your connectivity and try again.', 'Offline Error');
            } finally {
                isWaiting = false;
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            }
        }

        sendBtn.addEventListener('click', sendMessage);
        inputField.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                sendMessage();
            }
        });

        // Preload default quick FAQ suggestions
        setSuggestions([
            'Track My Order',
            'Return Policy',
            'Browse Electronics',
            'How to Sell',
            'Payment Methods'
        ]);
    });
})();
