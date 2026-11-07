package search.usecase.port.in;

import search.usecase.dto.SearchRequest;
import search.usecase.dto.SearchResponse;

public interface SearchExecutor {
    SearchResponse search(SearchRequest request);
}
