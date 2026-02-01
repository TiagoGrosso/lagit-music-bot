package org.tiagop.lagit.guild;

import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.PlayInfoManager;

public record GuildContext(
    TrackManager trackManager,
    ChannelManager channelManager,
    PlayInfoManager playInfoManager
) {
}
