package org.tiagop.lagit.service.search;

import io.quarkus.arc.All;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.tiagop.lagit.service.search.client.SearchClient;
import org.tiagop.lagit.util.Executor;

@ApplicationScoped
public class SearchService {

    private final List<SearchClient> searchClients;

    public SearchService(@All final List<SearchClient> searchClients) {
        this.searchClients = searchClients;
    }

    @CacheResult(cacheName = "search")
    public List<SearchResult> search(final String query) {
        return searchClients.stream()
            .map(client -> Executor.executeInVirtualThread(() -> client.search(query)))
            .map(CompletableFuture::join)
            .flatMap(Collection::stream)
            .limit(25)
            .sorted(Comparator.comparingInt(r -> -r.source().priority))
            .toList();
    }
}
