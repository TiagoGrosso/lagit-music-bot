package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import java.time.Duration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.time.DurationFormatUtils;

public record LeaveChannelForInactivityEmbed(
    Duration inactivityDuration
) implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        return new EmbedBuilder()
            .setColor(Color.YELLOW)
            .setTitle("Leaving channel \uD83C\uDFC3")
            .setDescription(
                "Leaving voice channel after %s of inactivity"
                    .formatted(DurationFormatUtils.formatDurationWords(
                        inactivityDuration.toMillis(),
                        true,
                        true
                    )))
            .build();
    }
}
