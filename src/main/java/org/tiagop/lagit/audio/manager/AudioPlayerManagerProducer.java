package org.tiagop.lagit.audio.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.YoutubeSourceOptions;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.WebEmbedded;
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AudioPlayerManagerProducer {
    private final AudioPlayerManager playerManager;

    public AudioPlayerManagerProducer() {
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(youtubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
    }

    @Produces
    public AudioPlayerManager getAudioPlayerManager() {
        return playerManager;
    }

    private static YoutubeAudioSourceManager youtubeAudioSourceManager() {
        final var options = new YoutubeSourceOptions()
                .setRemoteCipher("https://cipher.kikkia.dev/", null, null)
                .setAllowSearch(true);
        return new YoutubeAudioSourceManager(options, new WebEmbeddedWithThumbnail(), new Music());
    }
}
