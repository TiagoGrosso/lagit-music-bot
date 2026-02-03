package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.guild.channel.PlayInfoManager;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final TrackQueue trackQueue;
    private final PlayInfoManager playInfoManager;
    private final Runnable clearInactivity;
    private final Consumer<Instant> registerInactivity;

    @Nullable
    private TrackRequest current;

    public TrackManager(
        final AudioPlayer audioPlayer,
        final PlayInfoManager playInfoManager,
        final Runnable clearInactivity,
        final Consumer<Instant> registerInactivity
    ) {
        this.audioPlayer = audioPlayer;
        this.playInfoManager = playInfoManager;
        this.clearInactivity = clearInactivity;
        this.registerInactivity = registerInactivity;
        this.audioPlayer.addListener(this);
        this.trackQueue = new InMemoryTrackQueue();
    }

    public void queue(final TrackRequest request) {
        trackQueue.queue(request);
        if (current == null) {
            playNext();
            return;
        }
        playInfoManager.updatePlayInfo(new NowPlayingEmbed(current, getQueue().size()));
    }

    public void skip(final int num) {
        assert num > 0;
        if (num > 1) {
            trackQueue.advance(num - 1);
        }
        playNext();
    }

    public List<TrackRequest> getQueue() {
        return trackQueue.list();
    }

    public void resumeOrPlayNext() {
        if (audioPlayer.isPaused() && current != null) {
            audioPlayer.setPaused(false);
            playInfoManager.updatePlayInfo(new NowPlayingEmbed(current, getQueue().size()));
            return;
        }
        if (audioPlayer.getPlayingTrack() != null) {
            return;
        }
        playNext();
    }

    private void playNext() {
        current = trackQueue.advance(1);
        if (current == null) {
            playInfoManager.updateOnQueueEnded();
            return;
        }
        audioPlayer.playTrack(current.track());
        playInfoManager.updatePlayInfo(new NowPlayingEmbed(current, getQueue().size()));
    }

    public void pause() {
        if (current == null) {
            return;
        }
        audioPlayer.setPaused(true);
        playInfoManager.updatePlayInfo(new NowPlayingEmbed(current, getQueue().size(), true));
    }

    public void stop() {
        audioPlayer.setPaused(false);
        audioPlayer.stopTrack();
        final var queue = getQueue();
        if (queue.isEmpty()) {
            playInfoManager.updateOnQueueEnded();
            return;
        }
        playInfoManager.updatePlayInfo(new NowPlayingEmbed(queue.getFirst(), queue.size(), true));
    }

    public void clear() {
        trackQueue.clear();
        if (current != null) {
            playInfoManager.updatePlayInfo(new NowPlayingEmbed(current, 0));
        }
    }

    @Nullable
    public TrackRequest getCurrentTrack() {
        return current;
    }

    // Listeners

    @Override
    public void onTrackStart(final AudioPlayer player, final AudioTrack track) {
        clearInactivity.run();
    }

    @Override
    public void onPlayerResume(final AudioPlayer player) {
        clearInactivity.run();
    }

    @Override
    public void onPlayerPause(final AudioPlayer player) {
        registerInactivity.accept(Instant.now());
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        registerInactivity.accept(Instant.now());
        if (!endReason.mayStartNext) {
            return;
        }
        playNext();
    }
}
