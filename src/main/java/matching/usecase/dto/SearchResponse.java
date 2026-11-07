package matching.usecase.dto;

import matching.domain.MatchResult;

import java.util.List;

public record SearchResponse(List<MatchResult> matches) {}
