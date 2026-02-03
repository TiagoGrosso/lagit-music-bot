package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.tiagop.lagit.audio.track.TrackRequest;
import org.tiagop.lagit.util.Format;

public record NowPlayingEmbed(
    TrackRequest trackRequest,
    int queueSize,
    boolean isPaused
) implements Embed {

    public NowPlayingEmbed(final TrackRequest trackRequest, final int queueSize) {
        this(trackRequest, queueSize, false);
    }

    @Override
    public MessageEmbed toMessageEmbed() {
        final var sourceName = StringUtils.capitalize(trackRequest.track().getSourceManager().getSourceName());
        final var track = trackRequest.track();
        final var trackInfo = track.getInfo();
        final var title = isPaused
            ? "Paused \uD83D\uDD07"
            : "Now playing \uD83D\uDCBF";
        return new EmbedBuilder()
            .setColor(Color.GREEN)
            .setThumbnail(trackInfo.artworkUrl)
            .setTitle(title)
            .setDescription(Format.trackInfoUrl(track))
            .addField("Requested by:", trackRequest.requestedBy(), false)
            .addField("Queue size:", "%d".formatted(queueSize), true)
            .setFooter("Source: %s".formatted(sourceName)).build();
    }
}
