package org.tiagop.lagit.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public record TrackRequest(
    AudioTrack track,
    String requestedBy
) {
}
