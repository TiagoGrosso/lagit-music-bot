package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.tiagop.lagit.audio.track.TrackRequest;
import org.tiagop.lagit.util.Format;

public record TrackStartedEmbed(
    TrackRequest trackRequest
) implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        final var sourceName = StringUtils.capitalize(trackRequest.track().getSourceManager().getSourceName());
        final var track = trackRequest.track();
        final var trackInfo = track.getInfo();
        return new EmbedBuilder()
            .setColor(Color.GREEN)
            .setThumbnail(trackInfo.artworkUrl)
            .setTitle("Now playing \uD83D\uDCBF")
            .setDescription(Format.trackInfoUrl(track))
            .addField("Requested by:", trackRequest.requestedBy(), false)
            .setFooter("Source: %s".formatted(sourceName)).build();
    }
}
