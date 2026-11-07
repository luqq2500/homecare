package chat.usecase;

import chat.domain.Conversation;
import chat.usecase.dto.ReplyRequest;
import chat.usecase.dto.ReplyResponse;
import chat.usecase.exception.ConversationNotFoundException;
import chat.usecase.port.in.ConversationReplier;
import chat.usecase.port.out.ConversationRepository;

import java.time.Instant;

public class ReplyToConversation implements ConversationReplier {

    private final ConversationRepository repository;

    public ReplyToConversation(ConversationRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReplyResponse reply(ReplyRequest request) {
        Conversation conversation = repository.findByPair(request.customerId(), request.providerId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));

        Instant now = Instant.now();
        if (request.senderId().equals(request.customerId())) {
            conversation.replyByCustomer(request.text(), now);
        } else {
            conversation.replyByProvider(request.text(), now);
        }
        repository.save(conversation);

        return new ReplyResponse(request.customerId(), request.providerId(), conversation.messages().size());
    }
}
