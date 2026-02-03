package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;
import org.tiagop.lagit.audio.track.TrackRequest;
import org.tiagop.lagit.util.Format;

public record QueueEmbed(
    @Nullable TrackRequest current,
    List<TrackRequest> queue
) implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        final var tracksDescription = queue.stream()
            .map(r -> "1. %s (%s)".formatted(Format.trackInfoUrl(r.track()), r.requestedBy()))
            .collect(Collectors.joining("\n"));
        final var title = current == null
            ? "Currently Stopped"
            : "Now playing: %s".formatted(Format.trackInfoString(current.track()));
        final var builder = new EmbedBuilder()
            .setColor(Color.PINK)
            .setTitle(title)
            .setDescription("Next up:%n%s".formatted(tracksDescription));

        return builder.build();
    }
}
