package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.embeds.TrackStartedEmbed;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final TrackQueue trackQueue;
    private final ChannelManager channelManager;
    private final Runnable clearInactivity;
    private final Consumer<Instant> registerInactivity;

    public TrackManager(
        final AudioPlayer audioPlayer,
        final ChannelManager channelManager,
        final Runnable clearInactivity,
        final Consumer<Instant> registerInactivity
    ) {
        this.audioPlayer = audioPlayer;
        this.channelManager = channelManager;
        this.clearInactivity = clearInactivity;
        this.registerInactivity = registerInactivity;
        this.audioPlayer.addListener(this);
        this.trackQueue = new InMemoryTrackQueue();
    }

    public void queue(final TrackRequest request) {
        trackQueue.queue(request);
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

    public List<TrackRequest> getQueue() {
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
        audioPlayer.playTrack(next.track());
        channelManager.sendMessageEmbed(new TrackStartedEmbed(next));
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
