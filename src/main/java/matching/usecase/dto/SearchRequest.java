package matching.usecase.dto;

import java.time.Instant;
import java.util.List;

public record SearchRequest(List<String> jobs, Instant start, int durationHours, int helperCount) {}
