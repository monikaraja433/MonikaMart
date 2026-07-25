package com.monika.monikamart.dto;

import java.util.List;

public class ChatResponseDTO {
    private String reply;
    private String provider;
    private List<String> suggestions;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String reply, String provider, List<String> suggestions) {
        this.reply = reply;
        this.provider = provider;
        this.suggestions = suggestions;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
