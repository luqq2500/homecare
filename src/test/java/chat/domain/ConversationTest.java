package chat.domain;

import org.junit.jupiter.api.Test;
import chat.domain.exception.ChatNotStartedByCustomerException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");

    @Test
    void betweenCreatesUnstartedConversation() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        assertEquals("cust-1", conversation.customerId());
        assertEquals("prov-1", conversation.providerId());
        assertFalse(conversation.isStarted());
        assertTrue(conversation.messages().isEmpty());
    }

    @Test
    void startByCustomerStartsConversation() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        conversation.startByCustomer("Hello", NOW);
        assertTrue(conversation.isStarted());
        assertEquals(NOW, conversation.startedAt());
        assertEquals(1, conversation.messages().size());
        assertEquals("cust-1", conversation.messages().get(0).senderId());
    }

    @Test
    void startByCustomerTwiceIsRejected() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        conversation.startByCustomer("Hello", NOW);
        assertThrows(ChatNotStartedByCustomerException.class,
                () -> conversation.startByCustomer("Again", NOW.plusSeconds(60)));
    }

    @Test
    void providerCannotStartConversation() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        assertThrows(ChatNotStartedByCustomerException.class,
                () -> conversation.replyByProvider("Hi", NOW));
    }

    @Test
    void providerCanReplyAfterCustomerStarts() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        conversation.startByCustomer("Hello", NOW);
        conversation.replyByProvider("Hi back", NOW.plusSeconds(60));
        assertEquals(2, conversation.messages().size());
        assertEquals("prov-1", conversation.messages().get(1).senderId());
    }

    @Test
    void customerCanReplyAfterStart() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        conversation.startByCustomer("Hello", NOW);
        conversation.replyByCustomer("More info", NOW.plusSeconds(120));
        assertEquals(2, conversation.messages().size());
    }
}
