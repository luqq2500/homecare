package discovery.usecase.port.in;

import discovery.usecase.dto.SearchRequest;
import discovery.usecase.dto.SearchResponse;

public interface SearchExecutor {
    SearchResponse search(SearchRequest request);
}
