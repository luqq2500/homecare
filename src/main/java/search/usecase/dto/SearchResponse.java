package search.usecase.dto;

import search.domain.MatchResult;

import java.util.List;

public record SearchResponse(List<MatchResult> matches) {}
