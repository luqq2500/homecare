package chat.usecase.dto;

public record ReplyResponse(String customerId, String providerId, int messageCount) {}
