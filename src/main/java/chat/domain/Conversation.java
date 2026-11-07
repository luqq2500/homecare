package chat.domain;

import chat.domain.exception.ChatNotStartedByCustomerException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One ongoing conversation per customer–provider pair.
 * Only the customer can start a chat; the provider can reply.
 * Always open once started.
 */
public class Conversation {

    private final String customerId;
    private final String providerId;
    private final List<Message> messages = new ArrayList<>();
    private Instant startedAt;

    private Conversation(String customerId, String providerId) {
        this.customerId = Objects.requireNonNull(customerId);
        this.providerId = Objects.requireNonNull(providerId);
    }

    public static Conversation between(String customerId, String providerId) {
        return new Conversation(customerId, providerId);
    }

    public void startByCustomer(String text, Instant at) {
        if (startedAt != null) {
            throw new ChatNotStartedByCustomerException("Conversation already started");
        }
        this.startedAt = Objects.requireNonNull(at);
        messages.add(Message.of(customerId, text, at));
    }

    public void replyByProvider(String text, Instant at) {
        if (startedAt == null) {
            throw new ChatNotStartedByCustomerException("Provider cannot start a conversation");
        }
        messages.add(Message.of(providerId, text, at));
    }

    public void replyByCustomer(String text, Instant at) {
        if (startedAt == null) {
            throw new ChatNotStartedByCustomerException("Conversation not started");
        }
        messages.add(Message.of(customerId, text, at));
    }

    public String customerId() { return customerId; }
    public String providerId() { return providerId; }
    public Instant startedAt() { return startedAt; }
    public List<Message> messages() { return Collections.unmodifiableList(messages); }
    public boolean isStarted() { return startedAt != null; }
}
