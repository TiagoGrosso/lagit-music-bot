package org.tiagop.lagit.audio.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.manager.guild.GuildService;
import org.tiagop.lagit.audio.track.TrackManager;
import org.tiagop.lagit.audio.track.TryPlayResult;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

@ApplicationScoped

public class AudioService {

    private final GuildService guildService;
    private final AudioPlayerManager audioPlayerManager;

    public AudioService(@NotNull final GuildService guildService,
                        @NotNull final AudioPlayerManager audioPlayerManager) {
        this.guildService = guildService;
        this.audioPlayerManager = audioPlayerManager;
    }

    public Future<Void> loadTrack(
            @NotNull final Guild guild,
            @NotNull final String url,
            @NotNull Function<TrackManager, AudioLoadResultHandler> trackLoadHandlerBuilder
    ) {
        final var trackManager = guildService.getTrackManager(guild);
        return audioPlayerManager.loadItem(url, trackLoadHandlerBuilder.apply(trackManager));
    }

    public TryPlayResult tryPlay(@NotNull final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        return trackManager.tryPlay();
    }

    public void pause(@NotNull final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.pause();
    }

    public void clearQueue(@NotNull final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.clear();
    }

    public void stopPlaying(@NotNull final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.stop();
    }

    public List<AudioTrack> getQueue(@NotNull final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        return trackManager.getQueue();
    }
}
