package chat.usecase.port.in;

import chat.usecase.dto.ReplyRequest;
import chat.usecase.dto.ReplyResponse;

public interface ConversationReplier {
    ReplyResponse reply(ReplyRequest request);
}
