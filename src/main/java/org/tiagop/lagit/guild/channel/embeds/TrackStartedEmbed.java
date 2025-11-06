package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.tiagop.lagit.audio.track.TrackRequest;
import org.tiagop.lagit.util.Format;

public record TrackStartedEmbed(
    TrackRequest trackRequest
) implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        return new EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle("Now playing \uD83D\uDCBF")
            .setDescription(Format.trackInfoString(trackRequest.track()))
            .setFooter("Requested by @%s".formatted(trackRequest.requestedBy()))
            .build();
    }
}
