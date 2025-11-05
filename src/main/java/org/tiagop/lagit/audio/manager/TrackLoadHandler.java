package org.tiagop.lagit.audio.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.util.Format;

@RequiredArgsConstructor
public class TrackLoadHandler implements AudioLoadResultHandler {

    private final TrackManager trackManager;
    private final InteractionHook interactionHook;
    private final Runnable onSuccessCallback;

    private void loadTrack(final AudioTrack track) {
        trackManager.queue(track);
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        loadTrack(track);
        interactionHook.sendMessage("Added '%s' to queue".formatted(Format.trackInfoString(track)))
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
        interactionHook.sendMessage("Added '%s' to queue and %d others".formatted(
                Format.trackInfoString(playlist.getTracks().getFirst()),
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
