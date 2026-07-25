package com.monika.monikamart.service.chat;

import com.monika.monikamart.dto.ChatResponseDTO;

public interface ChatProvider {
    ChatResponseDTO generateReply(String userMessage);
    String getProviderName();
}
