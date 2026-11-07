package chat.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void ofAcceptsNonBlankText() {
        Instant now = Instant.parse("2026-11-07T12:00:00Z");
        Message message = Message.of("cust-1", "Hello", now);
        assertEquals("cust-1", message.senderId());
        assertEquals("Hello", message.text());
        assertEquals(now, message.sentAt());
    }

    @Test
    void ofRejectsBlankText() {
        Instant now = Instant.parse("2026-11-07T12:00:00Z");
        assertThrows(IllegalArgumentException.class, () -> Message.of("cust-1", "", now));
        assertThrows(IllegalArgumentException.class, () -> Message.of("cust-1", "   ", now));
    }
}
