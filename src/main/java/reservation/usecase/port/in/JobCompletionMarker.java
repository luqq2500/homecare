package reservation.usecase.port.in;

import reservation.usecase.dto.MarkJobCompletedRequest;
import reservation.usecase.dto.MarkJobCompletedResponse;

public interface JobCompletionMarker {
    MarkJobCompletedResponse markCompleted(MarkJobCompletedRequest request);
}
