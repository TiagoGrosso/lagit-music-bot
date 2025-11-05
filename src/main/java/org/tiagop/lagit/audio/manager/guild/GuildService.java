package org.tiagop.lagit.audio.manager.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;

@ApplicationScoped
public class GuildService {

    private final Map<String, GuildContext> guildContexts;

    private final AudioPlayerManager playerManager;

    public GuildService(@NotNull final AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.guildContexts = new ConcurrentHashMap<>();
    }

    private GuildContext getGuildContext(final Guild guild) {
        return guildContexts.computeIfAbsent(guild.getId(), id -> {
            final Supplier<TextChannel> textChannelSupplier = () -> {
                final var channel = guild.getDefaultChannel();
                if (channel == null) {
                    throw new IllegalStateException("Guild has no default channel");
                }
                return channel.asTextChannel();
            };
            final var audioPlayer = playerManager.createPlayer();
            return new GuildContext(
                guild.getAudioManager(),
                audioPlayer,
                new TrackManager(audioPlayer, textChannelSupplier),
                new AudioPlayerSendHandler(audioPlayer)
            );
        });
    }

    @NotNull
    public TrackManager getTrackManager(@NotNull final Guild guild) {
        return getGuildContext(guild).trackManager();
    }

    @NotNull
    public AudioManager getAudioManager(@NotNull final Guild guild) {
        return getGuildContext(guild).audioManager();
    }

    @NotNull
    public AudioPlayerSendHandler getAudioPlayerSendHandler(@NotNull final Guild guild) {
        return getGuildContext(guild).audioPlayerSendHandler();
    }
}
