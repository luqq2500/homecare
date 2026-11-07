package reservation.usecase.port.in;

import reservation.usecase.dto.FileDisputeRequest;
import reservation.usecase.dto.FileDisputeResponse;

public interface DisputeFiler {
    FileDisputeResponse file(FileDisputeRequest request);
}
