package org.tiagop.lagit.audio.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.tiagop.lagit.audio.track.TrackManager;

@RequiredArgsConstructor
public class TrackLoadHandler implements AudioLoadResultHandler {

    private final TrackManager trackManager;
    private final InteractionHook interactionHook;
    private final Runnable onSuccessCallback;

    private void loadTrack(final AudioTrack track) {
        Log.infof("Loaded track %s", track.getInfo().title);
        trackManager.queue(track);
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        loadTrack(track);
        final var trackInfo = track.getInfo();
        interactionHook.sendMessage("Added '%s' to queue".formatted(trackInfo.title))
                .queue();
        onSuccessCallback.run();
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().getFirst());
            return;
        }
        for (AudioTrack track : playlist.getTracks()) {
            loadTrack(track);
        }
        final var firstTrackInfo = playlist.getTracks().getFirst().getInfo();
        interactionHook.sendMessage("Added '%s' to queue and %d others".formatted(
                        firstTrackInfo.title,
                        playlist.getTracks().size()))
                .queue();
        onSuccessCallback.run();
    }

    @Override
    public void noMatches() {
        interactionHook.sendMessage("No song found").queue();
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        interactionHook.sendMessage("Error loading song: %s".formatted(exception.getMessage())).queue();
    }
}
