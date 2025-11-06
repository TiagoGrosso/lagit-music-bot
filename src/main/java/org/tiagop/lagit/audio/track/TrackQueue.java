package org.tiagop.lagit.audio.track;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface TrackQueue {

    void queue(TrackRequest track);

    @Nullable
    TrackRequest advance(int num);

    List<TrackRequest> list();

    void clear();
}
