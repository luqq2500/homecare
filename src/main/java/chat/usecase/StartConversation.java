package chat.usecase;

import chat.domain.Conversation;
import chat.usecase.dto.StartConversationRequest;
import chat.usecase.dto.StartConversationResponse;
import chat.usecase.port.in.ConversationStarter;
import chat.usecase.port.out.ConversationRepository;

import java.time.Instant;

public class StartConversation implements ConversationStarter {

    private final ConversationRepository repository;

    public StartConversation(ConversationRepository repository) {
        this.repository = repository;
    }

    @Override
    public StartConversationResponse start(StartConversationRequest request) {
        Conversation conversation = repository.findByPair(request.customerId(), request.providerId())
                .orElseGet(() -> Conversation.between(request.customerId(), request.providerId()));

        conversation.startByCustomer(request.text(), Instant.now());
        repository.save(conversation);

        return new StartConversationResponse(request.customerId(), request.providerId(), conversation.isStarted());
    }
}
