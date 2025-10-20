package org.tiagop.lagit.service.search;

import io.quarkus.arc.All;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class SearchService {

    private final List<SearchClient> searchClients;

    public SearchService(@NotNull @All final List<SearchClient> searchClients) {
        this.searchClients = searchClients;
    }

    @CacheResult(cacheName = "search")
    public List<SearchResult> search(final String query) {
        return searchClients.stream()
                .map(c -> c.search(query))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(SearchResult::type).thenComparing(SearchResult::name))
                .toList();
    }
}
