package org.tiagop.lagit.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import io.quarkus.arc.All;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.tiagop.lagit.audio.sources.SourceManagerProvider;

@ApplicationScoped
public class AudioPlayerManagerProducer {
    private final AudioPlayerManager playerManager;

    @SuppressWarnings("deprecation")
    public AudioPlayerManagerProducer(@All final List<SourceManagerProvider> sourceManagerProviders) {
        this.playerManager = new DefaultAudioPlayerManager();

        for (final var sourceManagerProvider : sourceManagerProviders) {
            final var sourceManager = sourceManagerProvider.getSourceManager(playerManager);
            Log.infof("Registering source manager for %s", StringUtils.capitalize(sourceManager.getSourceName()));
            playerManager.registerSourceManager(sourceManager);
        }

        AudioSourceManagers.registerRemoteSources(
            playerManager,
            com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class
        );
    }

    @Produces
    public AudioPlayerManager getAudioPlayerManager() {
        return playerManager;
    }
}
