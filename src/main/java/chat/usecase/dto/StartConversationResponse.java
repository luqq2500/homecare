package chat.usecase.dto;

public record StartConversationResponse(String customerId, String providerId, boolean started) {}
