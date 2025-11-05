package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.List;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final TrackQueue trackQueue;

    public TrackManager(
        final AudioPlayer audioPlayer
    ) {
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.trackQueue = new InMemoryTrackQueue();
    }

    public void queue(AudioTrack track) {
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
