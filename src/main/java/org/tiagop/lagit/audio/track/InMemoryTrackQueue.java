package org.tiagop.lagit.audio.track;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import org.jetbrains.annotations.Nullable;

public class InMemoryTrackQueue implements TrackQueue {

    private final Queue<TrackRequest> queue;

    public InMemoryTrackQueue() {
        this.queue = new ArrayDeque<>();
    }

    @Override
    public void queue(final TrackRequest track) {
        queue.add(track);
    }

    @Override
    @Nullable
    public TrackRequest advance(final int num) {
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

    public List<TrackRequest> list() {
        return List.copyOf(queue);
    }

    @Override
    public void clear() {
        queue.clear();
    }
}
