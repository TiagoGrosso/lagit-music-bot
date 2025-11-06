package org.tiagop.lagit.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;

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
            return new GuildContext(
                guild.getAudioManager(),
                audioPlayer,
                new TrackManager(audioPlayer),
                new AudioPlayerSendHandler(audioPlayer)
            );
        });
    }

    public TrackManager getTrackManager(final Guild guild) {
        return getGuildContext(guild).trackManager();
    }

    public AudioManager getAudioManager(final Guild guild) {
        return getGuildContext(guild).audioManager();
    }

    public AudioPlayerSendHandler getAudioPlayerSendHandler(final Guild guild) {
        return getGuildContext(guild).audioPlayerSendHandler();
    }
}
