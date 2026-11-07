package chat.domain;

import org.junit.jupiter.api.Test;
import chat.domain.exception.ChatNotStartedByCustomerException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ConversationIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");

    @Test
    void customerInitiatesChatCreatesConversation() {
        Conversation conversation = Conversation.between("bob", "alice");
        conversation.startByCustomer("Do you bring eco-friendly cleaning solutions?", NOW);
        assertTrue(conversation.isStarted());
        assertEquals(1, conversation.messages().size());
        assertEquals("bob", conversation.messages().get(0).senderId());
    }

    @Test
    void providerCannotInitiateChatFirst() {
        Conversation conversation = Conversation.between("bob", "alice");
        assertThrows(ChatNotStartedByCustomerException.class,
                () -> conversation.replyByProvider("Hi", NOW));
        assertFalse(conversation.isStarted());
    }
}
