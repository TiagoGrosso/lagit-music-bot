package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TrackQueue {

    void queue(@NotNull AudioTrack track);

    @Nullable
    AudioTrack advance();

    @NotNull
    List<AudioTrack> list();

    @Nullable
    AudioTrack skip(int num);

    void clear();
}
