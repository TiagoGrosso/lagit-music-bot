package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InMemoryTrackQueue implements TrackQueue {

    private final Queue<AudioTrack> queue;

    public InMemoryTrackQueue() {
        this.queue = new LinkedList<>();
    }

    @Override
    public void queue(@NotNull final AudioTrack track) {
        queue.add(track);
    }

    @Override
    public AudioTrack advance(final int num) {
        assert num > 0;
        if (num > queue.size()) {
            queue.clear();
            return null;
        }
        for (int i = 0; i < num - 1; ++i) {
            queue.poll();
        }
        return queue.poll();
    }

    @Override
    @NotNull
    public List<AudioTrack> list() {
        return List.copyOf(queue);
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
