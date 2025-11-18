package org.tiagop.lagit.service.search.client;

import static org.tiagop.lagit.service.search.SearchSource.SPOTIFY;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.tiagop.lagit.service.search.SearchResult;

@ApplicationScoped
public class SpotifySearchClient implements SearchClient {

    private final TrackSearcher trackSearcher;

    public SpotifySearchClient(final TrackSearcher trackSearcher) {
        this.trackSearcher = trackSearcher;
    }

    @Override
    public List<SearchResult> search(final String query) {
        final var tracks = trackSearcher.search("spsearch", query);
        return tracks.stream()
            .map(t -> new SearchResult(SPOTIFY, t.getInfo().author, t.getInfo().title, t.getInfo().uri))
            .toList();
    }
}
