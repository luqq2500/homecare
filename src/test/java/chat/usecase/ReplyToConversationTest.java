package chat.usecase;

import org.junit.jupiter.api.Test;
import chat.domain.Conversation;
import chat.usecase.dto.ReplyRequest;
import chat.usecase.dto.ReplyResponse;
import chat.usecase.exception.ConversationNotFoundException;
import chat.usecase.port.out.ConversationRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReplyToConversationTest {

    private final ConversationRepository repository = mock(ConversationRepository.class);
    private final ReplyToConversation usecase = new ReplyToConversation(repository);

    @Test
    void replyByProviderAfterStartIncreasesCount() {
        Conversation conversation = Conversation.between("cust-1", "prov-1");
        conversation.startByCustomer("Hello", Instant.now());
        when(repository.findByPair("cust-1", "prov-1")).thenReturn(Optional.of(conversation));

        ReplyResponse response = usecase.reply(
                new ReplyRequest("cust-1", "prov-1", "prov-1", "Hi back"));

        assertEquals(2, response.messageCount());
        verify(repository).save(conversation);
    }

    @Test
    void replyOnNonExistentConversationThrowsNotFound() {
        when(repository.findByPair("cust-1", "prov-1")).thenReturn(Optional.empty());

        assertThrows(ConversationNotFoundException.class,
                () -> usecase.reply(new ReplyRequest("cust-1", "prov-1", "prov-1", "Hi")));
    }
}
