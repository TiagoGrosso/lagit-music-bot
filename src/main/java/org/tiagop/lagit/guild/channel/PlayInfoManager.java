package org.tiagop.lagit.guild.channel;

import net.dv8tion.jda.api.entities.Message;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.guild.channel.embeds.TrackStartedEmbed;

public class PlayInfoManager {

    private final ChannelManager channelManager;

    @Nullable
    private Message message;

    public PlayInfoManager(final ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public void updatePlayInfo(final TrackStartedEmbed embed) {
        if (message == null) {
            channelManager.sendMessageEmbed(embed, message -> this.message = message);
            return;
        }
        message.editMessageEmbeds(embed.toMessageEmbed()).queue();
    }
}
