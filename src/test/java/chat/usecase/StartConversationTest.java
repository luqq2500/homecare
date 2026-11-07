package chat.usecase;

import org.junit.jupiter.api.Test;
import chat.domain.Conversation;
import chat.usecase.dto.StartConversationRequest;
import chat.usecase.dto.StartConversationResponse;
import chat.usecase.port.out.ConversationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartConversationTest {

    private final ConversationRepository repository = mock(ConversationRepository.class);
    private final StartConversation usecase = new StartConversation(repository);

    @Test
    void startCreatesAndStartsConversation() {
        when(repository.findByPair("cust-1", "prov-1")).thenReturn(Optional.empty());

        StartConversationResponse response = usecase.start(
                new StartConversationRequest("cust-1", "prov-1", "Hello"));

        assertEquals("cust-1", response.customerId());
        assertEquals("prov-1", response.providerId());
        assertTrue(response.started());
        verify(repository).save(any(Conversation.class));
    }
}
