package org.tiagop.lagit.service.search.client;

import static com.github.topi314.lavasearch.result.AudioSearchResult.Type.ALBUM;
import static com.github.topi314.lavasearch.result.AudioSearchResult.Type.TRACK;

import com.github.topi314.lavasearch.SearchManager;
import com.github.topi314.lavasrc.spotify.SpotifyAudioPlaylist;
import com.github.topi314.lavasrc.spotify.SpotifyAudioTrack;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.tiagop.lagit.service.search.SearchResult;

@ApplicationScoped
public class SpotifySearchClient implements SearchClient {

    private final SearchManager searchManager;
    private final boolean enabled;

    public SpotifySearchClient(final SearchManager searchManager) {
        this.searchManager = searchManager;
        enabled = searchManager.getSearchManagers().stream()
            .anyMatch(c -> c instanceof SpotifySourceManager);
    }

    @Override
    public List<SearchResult> search(final String query) {
        if (!enabled) {
            return List.of();
        }
        final var result = searchManager.loadSearch("spsearch:%s".formatted(query), Set.of(ALBUM, TRACK));
        if (result == null) {
            return List.of();
        }
        final var trackStream = result.getTracks().stream()
            .map(track -> (SpotifyAudioTrack) track)
            .map(SearchResult::of)
            .limit(5);
        final var albumStream = result.getAlbums().stream()
            .map(album -> (SpotifyAudioPlaylist) album)
            .map(SearchResult::of)
            .limit(5);

        return Stream.concat(trackStream, albumStream)
            .toList();
    }
}
