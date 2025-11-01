package org.tiagop.lagit.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

public final class Format {
    public static final String TRACK_FORMAT = "**%s** - %s [%s]";
    public static final String TRACK_DURATION_FORMAT = "mm:ss";

    private Format() {
    }

    public static String trackInfoString(final AudioTrack track) {
        return TRACK_FORMAT.formatted(
                trimYoutubeTopic(track.getInfo().author),
                track.getInfo().title,
                DurationFormatUtils.formatDuration(track.getDuration(), TRACK_DURATION_FORMAT)
        );
    }

    private static String trimYoutubeTopic(final String author) {
        if (!author.endsWith("- Topic")) {
            return author;
        }
        return StringUtils.substringBeforeLast(author, "- Topic");
    }
}
