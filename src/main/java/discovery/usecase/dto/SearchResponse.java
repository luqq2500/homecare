package discovery.usecase.dto;

import discovery.domain.MatchResult;

import java.util.List;

public record SearchResponse(List<MatchResult> matches) {}
