package org.tiagop.lagit.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.guild.activity.InactivityService;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.PlayInfoManager;
import org.tiagop.lagit.guild.config.GuildConfigService;

@ApplicationScoped
public class GuildService {

    private final Map<String, GuildContext> guildContexts;

    private final AudioPlayerManager playerManager;
    private final InactivityService inactivityService;
    private final GuildConfigService guildConfigService;

    public GuildService(
        final AudioPlayerManager playerManager,
        final InactivityService inactivityService,
        final GuildConfigService guildConfigService
    ) {
        this.playerManager = playerManager;
        this.inactivityService = inactivityService;
        this.guildConfigService = guildConfigService;
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
            final var playInfoManager = new PlayInfoManager(guild.getId(), guildConfigService, channelManager);
            return new GuildContext(
                new TrackManager(
                    audioPlayer,
                    playInfoManager,
                    () -> inactivityService.clearGuildInactivity(guild.getId()),
                    (time) -> inactivityService.registerGuildInactivity(guild.getId(), time)
                ),
                channelManager,
                playInfoManager
            );
        });
    }

    public TrackManager getTrackManager(final Guild guild) {
        return getGuildContext(guild).trackManager();
    }

    public ChannelManager getChannelManager(final Guild guild) {
        return getGuildContext(guild).channelManager();
    }

    @Nullable
    public ChannelManager getChannelManager(final String guildId) {
        final var guildContext = guildContexts.get(guildId);
        return guildContext == null ? null : guildContext.channelManager();
    }

    public PlayInfoManager getPlayInfoManager(final Guild guild) {
        return getGuildContext(guild).playInfoManager();
    }
}
