package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InMemoryTrackQueue implements TrackQueue {

    private final Queue<AudioTrack> queue;
    private final int maxSize;

    public InMemoryTrackQueue(
            @ConfigProperty(name = "lagit.queue.max-size", defaultValue = "20") final int maxSize
    ) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

    @Override
    public void queue(@NotNull final AudioTrack track) {
        if (queue.size() + 1 >= maxSize) {
            throw new IllegalStateException("Queue is full");
        }
        queue.add(track);
    }

    @Override
    @Nullable
    public AudioTrack advance() {
        return queue.poll();
    }

    @Override
    @NotNull
    public List<AudioTrack> list() {
        return List.copyOf(queue);
    }

    @Override
    @Nullable
    public AudioTrack skip(final int num) {
        assert num > 0;
        if (num >= queue.size()) {
            queue.clear();
            return null;
        }
        for (int i = 0; i < num; i++) {
            queue.poll();
        }
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
