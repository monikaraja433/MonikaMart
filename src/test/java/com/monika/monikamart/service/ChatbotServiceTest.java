package com.monika.monikamart.service;

import com.monika.monikamart.dto.ChatResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.service.chat.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatbotServiceTest {
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        this.chatService = new ChatService();
    }

    @Test
    public void testValidMessageResponse() {
        ChatResponseDTO response = chatService.processMessage("session-1", "Where is my order?");
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getReply().toLowerCase().contains("track"));
        Assertions.assertFalse(response.getSuggestions().isEmpty());
    }

    @Test
    public void testCachingForIdenticalQueries() {
        ChatResponseDTO first = chatService.processMessage("session-cache-test", "Return Policy");
        ChatResponseDTO second = chatService.processMessage("session-cache-test", "Return Policy");

        Assertions.assertSame(first, second, "Repeated identical query should return cached object within session");
    }

    @Test
    public void testMessageLengthCap() {
        StringBuilder longMsg = new StringBuilder();
        for (int i = 0; i < 550; i++) {
            longMsg.append("a");
        }

        Assertions.assertThrows(ValidationException.class, () ->
            chatService.processMessage("session-len-test", longMsg.toString())
        );
    }

    @Test
    public void testRateLimitingEnforced() {
        String testSession = "session-rate-limit-test-" + System.currentTimeMillis();

        // 10 messages should succeed
        for (int i = 0; i < 10; i++) {
            chatService.processMessage(testSession, "Question " + i);
        }

        // 11th message within 1 minute must be rejected
        Assertions.assertThrows(ValidationException.class, () ->
            chatService.processMessage(testSession, "Question 11 - Should Exceed Limit")
        );
    }
}
