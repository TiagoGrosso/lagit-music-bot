package org.tiagop.lagit.service.search;

import io.quarkus.arc.All;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import org.tiagop.lagit.service.search.client.SearchClient;

@ApplicationScoped
public class SearchService {

    private final List<SearchClient> searchClients;

    public SearchService(@All final List<SearchClient> searchClients) {
        this.searchClients = searchClients;
    }

    @CacheResult(cacheName = "search")
    public List<SearchResult> search(final String query) {
        return searchClients.stream()
            .flatMap(c -> c.search(query).stream().limit(10))
            .limit(25)
            .sorted(Comparator.comparing(r -> r.source().ordinal()))
            .toList();
    }
}
