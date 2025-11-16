package org.tiagop.lagit.service.search.client;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TrackSearcher {

    private final AudioPlayerManager playerManager;

    public TrackSearcher(final AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public List<AudioTrack> search(final String prefix, final String query) {
        final var searchResult = playerManager.loadItemSync("%s: %s".formatted(prefix, query));
        return switch (searchResult) {
            case AudioTrack track -> List.of(track);
            case AudioPlaylist playlist -> playlist.getTracks();
            case null, default -> List.<AudioTrack>of();
        };
    }
}
