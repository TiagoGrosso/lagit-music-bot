package org.tiagop.lagit.guild.channel.embeds;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record QueueEndedEmbed() implements Embed {
    @Override
    public MessageEmbed toMessageEmbed() {
        return new EmbedBuilder()
            .setColor(Color.gray)
            .setTitle("Queue ended")
            .setDescription("There are no more songs left in the queue")
            .build();
    }
}
