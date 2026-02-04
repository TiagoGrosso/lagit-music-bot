package org.tiagop.lagit.guild.channel;

import net.dv8tion.jda.api.entities.Message;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;
import org.tiagop.lagit.guild.channel.embeds.QueueEndedEmbed;

public class PlayInfoManager {

    private final ChannelManager channelManager;

    @Nullable
    private Message message;

    public PlayInfoManager(final ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public void updatePlayInfo(final NowPlayingEmbed embed) {
        updatePlayInfo(embed, false);
    }

    public void updatePlayInfo(
        final NowPlayingEmbed embed,
        final boolean forceNewMessage
    ) {
        if (message == null || forceNewMessage) {
            channelManager.sendMessageEmbed(embed, message -> this.message = message);
            return;
        }
        message.editMessageEmbeds(embed.toMessageEmbed()).queue();
    }

    public void updateOnQueueEnded() {
        if (message == null) {
            return;
        }
        message.editMessageEmbeds(new QueueEndedEmbed().toMessageEmbed()).queue();
        this.message = null;
    }
}
