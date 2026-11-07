package chat.usecase.dto;

public record ReplyRequest(String customerId, String providerId, String senderId, String text) {}
