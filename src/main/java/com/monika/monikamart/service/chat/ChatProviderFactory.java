package com.monika.monikamart.service.chat;

import com.monika.monikamart.util.DBUtil;

public class ChatProviderFactory {
    private static ChatProvider instance;

    private ChatProviderFactory() {}

    public static synchronized ChatProvider getProvider() {
        if (instance == null) {
            String providerType = DBUtil.getProperty("ai.chatbot.provider", "mock");
            if ("gemini".equalsIgnoreCase(providerType)) {
                instance = new GeminiChatProvider();
            } else {
                instance = new MockChatProvider();
            }
        }
        return instance;
    }

    public static synchronized void setProvider(ChatProvider customProvider) {
        instance = customProvider;
    }
}
