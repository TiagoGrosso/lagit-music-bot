package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Supplier<TextChannel> defaultChannelSupplier;
    private final TrackQueue trackQueue;

    public TrackManager(
        @NotNull final AudioPlayer audioPlayer,
        @NotNull final Supplier<TextChannel> defaultChannelSupplier
    ) {
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.trackQueue = new InMemoryTrackQueue();
        this.defaultChannelSupplier = defaultChannelSupplier;
    }

    public void queue(@NotNull AudioTrack track) {
        trackQueue.queue(track);
        if (getCurrentTrack() == null) {
            playNext();
        }
    }

    public void skip(final int num) {
        assert num > 0;
        if (num > 1) {
            trackQueue.advance(num - 1);
        }
        playNext();
    }

    @NotNull
    public List<AudioTrack> getQueue() {
        return trackQueue.list();
    }

    public void resumeOrPlayNext() {
        if (audioPlayer.isPaused()) {
            audioPlayer.setPaused(false);
            return;
        }
        playNext();
    }

    private void playNext() {
        final var next = trackQueue.advance(1);
        if (next == null) {
            return;
        }
        audioPlayer.playTrack(next);
    }

    public void pause() {
        audioPlayer.setPaused(true);
    }

    public void stop() {
        audioPlayer.setPaused(false);
        audioPlayer.stopTrack();
    }

    public void clear() {
        trackQueue.clear();
    }

    public AudioTrack getCurrentTrack() {
        return audioPlayer.getPlayingTrack();
    }

    // Listeners

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) {
            return;
        }
        playNext();
    }
}
