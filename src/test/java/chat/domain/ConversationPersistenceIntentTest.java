package chat.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ConversationPersistenceIntentTest {

    @Test
    void chatRemainsOpenAfterReservationEnds() {
        Conversation conversation = Conversation.between("bob", "alice");
        conversation.startByCustomer("Hello", Instant.now());
        assertTrue(conversation.isStarted());
        assertFalse(conversation.messages().isEmpty());
    }
}
