package org.tiagop.lagit.audio.sources;

import com.github.topi314.lavasrc.mirror.DefaultMirroringAudioTrackResolver;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import io.quarkus.arc.lookup.LookupUnlessProperty;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Priority(50)
@LookupUnlessProperty(name = "source.spotify.client.id", stringValue = "disabled")
public class SpotifySourceManagerProvider implements SourceManagerProvider {

    private static final String[] PROVIDERS = {"ytmsearch:\"%ISRC%\"", "ytmsearch:%QUERY%"};

    private final String clientId;
    private final String clientSecret;

    public SpotifySourceManagerProvider(
        @ConfigProperty(name = "source.spotify.client.id") final String clientId,
        @ConfigProperty(name = "source.spotify.client.secret") final String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public AudioSourceManager getSourceManager(final AudioPlayerManager playerManager) {
        return new SpotifySourceManager(
            clientId,
            clientSecret,
            null,
            playerManager,
            new DefaultMirroringAudioTrackResolver(PROVIDERS)
        );
    }
}
