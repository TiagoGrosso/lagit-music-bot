package org.tiagop.lagit.guild.activity;

import java.time.Instant;

public record Activity(
    String guildId,
    Instant time
) implements Comparable<Activity> {

    @Override
    public int compareTo(final Activity other) {
        return time.compareTo(other.time);
    }
}
