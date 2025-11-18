package org.tiagop.lagit.audio.sources;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.YoutubeSourceOptions;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail;
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
        return new YoutubeAudioSourceManager(options, new WebEmbeddedWithThumbnail(), new MusicWithThumbnail());
    }
}
