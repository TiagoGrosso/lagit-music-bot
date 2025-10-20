package org.tiagop.lagit.service.search;

import java.util.List;

public interface SearchClient {
    List<SearchResult> search(String query);
}
