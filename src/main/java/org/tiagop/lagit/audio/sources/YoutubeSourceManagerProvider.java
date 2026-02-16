package org.tiagop.lagit.audio.sources;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.YoutubeSourceOptions;
import dev.lavalink.youtube.clients.AndroidMusicWithThumbnail;
import dev.lavalink.youtube.clients.AndroidVrWithThumbnail;
import dev.lavalink.youtube.clients.ClientOptions;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
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

        final var androidMusicOptions = new ClientOptions();
        androidMusicOptions.setPlayback(false);
        androidMusicOptions.setPlaylistLoading(false);
        androidMusicOptions.setSearching(false);
        androidMusicOptions.setVideoLoading(true);

        final var musicOptions = new ClientOptions();
        musicOptions.setPlayback(false);
        musicOptions.setPlaylistLoading(false);
        musicOptions.setSearching(true);
        musicOptions.setVideoLoading(false);

        final var webOptions = new ClientOptions();
        webOptions.setPlayback(false);
        webOptions.setPlaylistLoading(true);
        webOptions.setSearching(true);
        webOptions.setVideoLoading(false);

        final var webEmbedOptions = new ClientOptions();
        webEmbedOptions.setPlayback(false);
        webEmbedOptions.setPlaylistLoading(false);
        webEmbedOptions.setSearching(false);
        webEmbedOptions.setVideoLoading(false);

        final var androidVr = new ClientOptions();
        webEmbedOptions.setPlayback(true);
        webEmbedOptions.setPlaylistLoading(true);
        webEmbedOptions.setSearching(false);
        webEmbedOptions.setVideoLoading(true);

        return new YoutubeAudioSourceManager(options, new AndroidMusicWithThumbnail(androidMusicOptions),
            new MusicWithThumbnail(musicOptions), new WebWithThumbnail(webOptions),
            new WebEmbeddedWithThumbnail(webEmbedOptions), new AndroidVrWithThumbnail(androidVr));
    }
}
