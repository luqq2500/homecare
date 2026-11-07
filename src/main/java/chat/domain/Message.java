package chat.domain;

import java.time.Instant;
import java.util.Objects;

public final class Message {

    private final String senderId;
    private final String text;
    private final Instant sentAt;

    private Message(String senderId, String text, Instant sentAt) {
        this.senderId = Objects.requireNonNull(senderId);
        this.text = Objects.requireNonNull(text);
        if (text.isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank");
        }
        this.sentAt = Objects.requireNonNull(sentAt);
    }

    public static Message of(String senderId, String text, Instant sentAt) {
        return new Message(senderId, text, sentAt);
    }

    public String senderId() { return senderId; }
    public String text() { return text; }
    public Instant sentAt() { return sentAt; }
}
