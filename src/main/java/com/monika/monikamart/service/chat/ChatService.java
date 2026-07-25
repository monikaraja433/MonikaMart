package com.monika.monikamart.service.chat;

import com.monika.monikamart.dto.ChatResponseDTO;
import com.monika.monikamart.exception.ValidationException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    private static final int MAX_INPUT_LENGTH = 500;
    private static final int RATE_LIMIT_MAX_PER_MINUTE = 10;
    private static final long ONE_MINUTE_MILLIS = 60 * 1000L;

    // Per-session question cache: sessionId -> (question -> ChatResponseDTO)
    private final Map<String, Map<String, ChatResponseDTO>> sessionCaches = new ConcurrentHashMap<>();

    // Per-session message timestamps: sessionId -> Queue of timestamps
    private final Map<String, Queue<Long>> sessionRateLimits = new ConcurrentHashMap<>();

    public ChatResponseDTO processMessage(String sessionId, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty.");
        }

        String trimmed = message.trim();
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            throw new ValidationException("Message exceeds maximum allowed length of " + MAX_INPUT_LENGTH + " characters.");
        }

        String safeSessionId = (sessionId != null && !sessionId.isEmpty()) ? sessionId : "anonymous";

        // 1. Enforce per-session rate limit
        checkRateLimit(safeSessionId);

        // 2. Check in-memory session cache for repeated questions
        Map<String, ChatResponseDTO> userCache = sessionCaches.computeIfAbsent(safeSessionId, k -> new ConcurrentHashMap<>());
        String normalizedQuery = trimmed.toLowerCase();
        if (userCache.containsKey(normalizedQuery)) {
            return userCache.get(normalizedQuery);
        }

        // 3. Delegate to configured ChatProvider
        ChatProvider provider = ChatProviderFactory.getProvider();
        ChatResponseDTO response = provider.generateReply(trimmed);

        // 4. Cache response
        userCache.put(normalizedQuery, response);

        return response;
    }

    private void checkRateLimit(String sessionId) {
        long now = System.currentTimeMillis();
        Queue<Long> timestamps = sessionRateLimits.computeIfAbsent(sessionId, k -> new LinkedList<>());

        synchronized (timestamps) {
            // Remove timestamps older than 1 minute
            while (!timestamps.isEmpty() && (now - timestamps.peek()) > ONE_MINUTE_MILLIS) {
                timestamps.poll();
            }

            if (timestamps.size() >= RATE_LIMIT_MAX_PER_MINUTE) {
                throw new ValidationException("Rate limit exceeded: You can send at most " + RATE_LIMIT_MAX_PER_MINUTE + " messages per minute. Please wait a moment.");
            }

            timestamps.add(now);
        }
    }

    public void clearSession(String sessionId) {
        if (sessionId != null) {
            sessionCaches.remove(sessionId);
            sessionRateLimits.remove(sessionId);
        }
    }
}
