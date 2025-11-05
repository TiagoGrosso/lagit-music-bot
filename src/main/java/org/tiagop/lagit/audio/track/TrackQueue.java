package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface TrackQueue {

    void queue(@NotNull AudioTrack track);

    AudioTrack advance(int num);

    @NotNull
    List<AudioTrack> list();

    void clear();
}
