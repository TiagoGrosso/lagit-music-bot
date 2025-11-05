package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TrackQueue {

    void queue(@NotNull AudioTrack track);

    AudioTrack advance(int num);

    @NotNull
    List<AudioTrack> list();

    void clear();
}
