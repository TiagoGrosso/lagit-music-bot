package org.tiagop.lagit.service.search.client;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.track.YoutubeAudioTrack;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.tiagop.lagit.service.search.SearchResult;

@ApplicationScoped
public class YoutubeSearchClient implements SearchClient {

    private final AudioPlayerManager playerManager;

    public YoutubeSearchClient(final AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public List<SearchResult> search(final String query) {
        final var searchResult = playerManager.loadItemSync("ytmsearch: %s".formatted(query));
        final var tracks = switch (searchResult) {
            case AudioTrack track -> List.of(track);
            case AudioPlaylist playlist -> playlist.getTracks();
            case null, default -> List.<AudioTrack>of();
        };
        return tracks.stream()
            .map(track -> (YoutubeAudioTrack) track)
            .map(SearchResult::of)
            .limit(5)
            .toList();
    }
}
