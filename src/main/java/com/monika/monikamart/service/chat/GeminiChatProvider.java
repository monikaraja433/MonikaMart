package com.monika.monikamart.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.monika.monikamart.dto.ChatResponseDTO;
import com.monika.monikamart.util.DBUtil;
import com.monika.monikamart.util.JsonUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiChatProvider implements ChatProvider {
    private static final Logger LOGGER = Logger.getLogger(GeminiChatProvider.class.getName());
    private static final int TIMEOUT_MILLIS = 8000;

    private final String apiKey;
    private final String model;
    private final MockChatProvider fallbackProvider;

    private static final String SYSTEM_PROMPT = 
        "You are the helpful AI Shopping Assistant for 'MonikaMart', an e-commerce platform built for Anna University R2025. " +
        "Your role is strictly limited to helping users with products, categories (Electronics, Books, Stationery, Home & Office), " +
        "order tracking (Pending -> Confirmed -> Shipped -> Delivered), returns (7-day policy), mock payments, and seller onboarding. " +
        "Keep answers concise, polite, professional, and max 3-4 sentences. " +
        "If a question is outside e-commerce or MonikaMart, politely steer the user back to shopping.";

    public GeminiChatProvider() {
        String envGeminiKey = System.getenv("GEMINI_API_KEY");
        if (envGeminiKey != null && !envGeminiKey.trim().isEmpty()) {
            this.apiKey = envGeminiKey.trim();
        } else {
            this.apiKey = DBUtil.getProperty("ai.chatbot.gemini.key", "");
        }

        String envGeminiModel = System.getenv("GEMINI_MODEL");
        if (envGeminiModel != null && !envGeminiModel.trim().isEmpty()) {
            this.model = envGeminiModel.trim();
        } else {
            this.model = DBUtil.getProperty("ai.chatbot.gemini.model", "gemini-1.5-flash");
        }
        this.fallbackProvider = new MockChatProvider();
    }

    @Override
    public ChatResponseDTO generateReply(String userMessage) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            LOGGER.info("No Gemini API key provided. Falling back to MockChatProvider.");
            return fallbackProvider.generateReply(userMessage);
        }

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(TIMEOUT_MILLIS);
            conn.setReadTimeout(TIMEOUT_MILLIS);
            conn.setDoOutput(true);

            // Construct Gemini REST Payload
            Map<String, Object> requestPayload = new HashMap<>();
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", Arrays.asList(CollectionsMap("text", SYSTEM_PROMPT)));
            requestPayload.put("system_instruction", systemInstruction);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Arrays.asList(CollectionsMap("text", userMessage)));
            requestPayload.put("contents", Arrays.asList(content));

            String jsonInput = JsonUtil.toJson(requestPayload);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                JsonNode root = JsonUtil.getMapper().readTree(response.toString());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                    return new ChatResponseDTO(
                        text,
                        getProviderName(),
                        Arrays.asList("Browse Products", "Check Order Status", "Contact Support")
                    );
                }
            } else {
                LOGGER.warning("Gemini API call returned status: " + responseCode + ". Using graceful fallback.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error calling Gemini API (" + e.getClass().getSimpleName() + "). Returning degraded fallback.");
        }

        // Return graceful degraded response on network/API failure
        ChatResponseDTO fallbackResponse = fallbackProvider.generateReply(userMessage);
        fallbackResponse.setReply(fallbackResponse.getReply() + " (Note: Live AI service momentarily degraded, answering from offline knowledge base)");
        return fallbackResponse;
    }

    @Override
    public String getProviderName() {
        return "GeminiChatProvider (" + model + ")";
    }

    private Map<String, String> CollectionsMap(String k, String v) {
        Map<String, String> m = new HashMap<>();
        m.put(k, v);
        return m;
    }
}
