package chat.usecase.port.out;

import chat.domain.Conversation;

import java.util.Optional;

public interface ConversationRepository {
    void save(Conversation conversation);
    Optional<Conversation> findByPair(String customerId, String providerId);
}
