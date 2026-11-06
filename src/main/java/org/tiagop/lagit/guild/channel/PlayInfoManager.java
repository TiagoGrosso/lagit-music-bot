package org.tiagop.lagit.guild.channel;

import net.dv8tion.jda.api.entities.Message;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;
import org.tiagop.lagit.guild.channel.embeds.QueueEndedEmbed;
import org.tiagop.lagit.guild.config.AnnounceMode;
import org.tiagop.lagit.guild.config.GuildConfigService;

public class PlayInfoManager {

    private final String guildId;
    private final GuildConfigService guildConfigService;
    private final ChannelManager channelManager;

    @Nullable
    private Message message;

    public PlayInfoManager(
        final String guildId,
        final GuildConfigService guildConfigService,
        final ChannelManager channelManager
    ) {
        this.guildId = guildId;
        this.guildConfigService = guildConfigService;
        this.channelManager = channelManager;
    }

    public void updatePlayInfo(final NowPlayingEmbed embed) {
        updatePlayInfo(embed, false);
    }

    public void updatePlayInfo(
        final NowPlayingEmbed embed,
        final boolean forceNewMessage
    ) {
        final var announceMode = announceMode();
        if (announceMode == AnnounceMode.SILENT) {
            return;
        }
        if (announceMode == AnnounceMode.NEW_MESSAGE || forceNewMessage || message == null) {
            channelManager.sendMessageEmbed(embed, msg -> this.message = msg);
            return;
        }
        message.editMessageEmbeds(embed.toMessageEmbed()).queue();
    }

    public void updateOnQueueEnded() {
        if (announceMode() == AnnounceMode.SILENT || message == null) {
            return;
        }
        message.editMessageEmbeds(new QueueEndedEmbed().toMessageEmbed()).queue();
        this.message = null;
    }

    private AnnounceMode announceMode() {
        return guildConfigService.getOrCreate(guildId).announceMode;
    }
}
