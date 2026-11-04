package org.tiagop.lagit.guild.channel.embeds;

import static java.awt.Color.YELLOW;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationWords;

import java.time.Duration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record LeaveChannelForInactivityEmbed(
    Duration inactivityDuration
) implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        return new EmbedBuilder()
            .setColor(YELLOW)
            .setTitle("Leaving channel \uD83C\uDFC3")
            .setDescription(
                "Leaving voice channel after %s of inactivity"
                    .formatted(formatDurationWords(
                        inactivityDuration.toMillis(),
                        true,
                        true
                    )))
            .build();
    }
}
