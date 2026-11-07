package chat.usecase.port.in;

import chat.usecase.dto.StartConversationRequest;
import chat.usecase.dto.StartConversationResponse;

public interface ConversationStarter {
    StartConversationResponse start(StartConversationRequest request);
}
