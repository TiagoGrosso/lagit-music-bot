package org.tiagop.lagit.service.search.client;

import static org.tiagop.lagit.service.search.SearchSource.YOUTUBE;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.tiagop.lagit.service.search.SearchResult;

@ApplicationScoped
public class YoutubeSearchClient implements SearchClient {

    private final TrackSearcher trackSearcher;

    public YoutubeSearchClient(final TrackSearcher trackSearcher) {
        this.trackSearcher = trackSearcher;
    }

    @Override
    public List<SearchResult> search(final String query) {
        final var tracks = trackSearcher.search("ytmsearch", query);
        return tracks.stream()
            .map(t -> new SearchResult(YOUTUBE, t.getInfo().author, t.getInfo().title, t.getIdentifier()))
            .toList();
    }
}
