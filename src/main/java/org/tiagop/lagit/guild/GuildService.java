package org.tiagop.lagit.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.entities.Guild;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.guild.channel.ChannelManager;

@ApplicationScoped
public class GuildService {

    private final Map<String, GuildContext> guildContexts;

    private final AudioPlayerManager playerManager;

    public GuildService(final AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.guildContexts = new ConcurrentHashMap<>();
    }

    private GuildContext getGuildContext(final Guild guild) {
        return guildContexts.computeIfAbsent(guild.getId(), id -> {
            final var audioPlayer = playerManager.createPlayer();
            final var channelManager = new ChannelManager(
                guild,
                guild.getAudioManager(),
                new AudioPlayerSendHandler(audioPlayer)
            );
            return new GuildContext(
                new TrackManager(audioPlayer, channelManager),
                channelManager
            );
        });
    }

    public TrackManager getTrackManager(final Guild guild) {
        return getGuildContext(guild).trackManager();
    }

    public ChannelManager getChannelManager(final Guild guild) {
        return getGuildContext(guild).channelManager();
    }

}
