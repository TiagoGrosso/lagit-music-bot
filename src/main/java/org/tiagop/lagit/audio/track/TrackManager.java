package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Supplier<TextChannel> defaultChannelSupplier;
    private final TrackQueue trackQueue;

    @Nullable
    private AudioTrack currentTrack;

    public TrackManager(
            @NotNull final AudioPlayer audioPlayer,
            @NotNull final Supplier<TextChannel> defaultChannelSupplier
    ) {
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.trackQueue = new InMemoryTrackQueue(20);
        this.defaultChannelSupplier = defaultChannelSupplier;
    }

    public void queue(@NotNull AudioTrack track) {
        trackQueue.queue(track);
    }

    public void advance() {
        currentTrack = trackQueue.advance();
        if (currentTrack == null) {
            throw new IllegalStateException("No more tracks to play");
        }
        tryPlay();
    }

    @NotNull
    public List<AudioTrack> getQueue() {
        return Stream.concat(
                Stream.of(currentTrack),
                        trackQueue.list().stream())
                .filter(Objects::nonNull)
                .toList();
    }

    public TryPlayResult tryPlay() {
        if (audioPlayer.isPaused()) {
            resume();
            return TryPlayResult.RESUMED_PLAYING;
        }
        if (currentTrack != null) {
            return TryPlayResult.ALREADY_PLAYING;
        }
        currentTrack = trackQueue.advance();
        if (currentTrack == null) {
            return TryPlayResult.EMPTY_QUEUE;
        }
        audioPlayer.playTrack(currentTrack);
        return TryPlayResult.STARTED_PLAYING;
    }

    public void pause() {
        audioPlayer.setPaused(true);
    }

    public void resume() {
        audioPlayer.setPaused(false);
    }

    public void stop() {
        audioPlayer.setPaused(false);
        audioPlayer.stopTrack();
    }

    public void clear() {
        trackQueue.clear();
    }

    // Listeners

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        currentTrack = null;
        if (!endReason.mayStartNext) {
            return;
        }
        advance();
    }

}
