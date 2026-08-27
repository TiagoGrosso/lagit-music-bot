package org.tiagop.lagit.audio.sources;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.YoutubeSourceOptions;
import dev.lavalink.youtube.clients.Android;
import dev.lavalink.youtube.clients.AndroidMusic;
import dev.lavalink.youtube.clients.AndroidVr;
import dev.lavalink.youtube.clients.MWeb;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.TvHtml5Simply;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.WebEmbedded;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Priority(100)
public class YoutubeSourceManagerProvider implements SourceManagerProvider {

    @Override
    public AudioSourceManager getSourceManager(final AudioPlayerManager playerManager) {
        final var options = new YoutubeSourceOptions()
            .setRemoteCipher("https://cipher.kikkia.dev/", null, null)
            .setAllowSearch(true);

        return new YoutubeAudioSourceManager(options,
            new Android(),
            new Music(),
            new Web(),
            new MWeb(),
            new WebEmbedded(),
            new AndroidMusic(),
            new AndroidVr(),
            new TvHtml5Simply()
        );
    }
}
