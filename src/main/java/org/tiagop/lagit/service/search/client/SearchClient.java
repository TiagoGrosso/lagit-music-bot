package org.tiagop.lagit.service.search.client;

import java.util.List;
import org.tiagop.lagit.service.search.SearchResult;

public interface SearchClient {
    List<SearchResult> search(String query);
}
