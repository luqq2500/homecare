package chat.usecase.dto;

public record StartConversationRequest(String customerId, String providerId, String text) {}
