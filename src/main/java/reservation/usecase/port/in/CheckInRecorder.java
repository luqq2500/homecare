package reservation.usecase.port.in;

import reservation.usecase.dto.RecordCheckInRequest;
import reservation.usecase.dto.RecordCheckInResponse;

public interface CheckInRecorder {
    RecordCheckInResponse record(RecordCheckInRequest request);
}
