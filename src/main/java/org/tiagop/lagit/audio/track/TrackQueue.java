package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface TrackQueue {

    void queue(AudioTrack track);

    @Nullable
    AudioTrack advance(int num);

    List<AudioTrack> list();

    void clear();
}
