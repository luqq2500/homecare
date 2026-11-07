package matching.usecase.port.in;

import matching.usecase.dto.SearchRequest;
import matching.usecase.dto.SearchResponse;

public interface SearchExecutor {
    SearchResponse search(SearchRequest request);
}
