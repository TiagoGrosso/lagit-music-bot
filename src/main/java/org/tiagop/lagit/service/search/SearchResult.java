package org.tiagop.lagit.service.search;

import com.github.topi314.lavasrc.spotify.SpotifyAudioPlaylist;
import com.github.topi314.lavasrc.spotify.SpotifyAudioTrack;
import dev.lavalink.youtube.track.YoutubeAudioTrack;
import org.apache.commons.lang3.StringUtils;

public record SearchResult(
    SearchSource source,
    Type type,
    String artist,
    String title,
    String id
) {

    public static final String CHOICE_NAME_FORMAT = "[%s] %s %s - %s";

    public String choiceName() {
        return StringUtils.truncate(
            CHOICE_NAME_FORMAT.formatted(
                source.name,
                type.emoji,
                StringUtils.truncate(artist, 20),
                StringUtils.truncate(title, 50)),
            100);
    }

    public String choiceValue() {
        return id;
    }

    public static SearchResult of(final YoutubeAudioTrack track) {
        return new SearchResult(
            SearchSource.YOUTUBE,
            Type.TRACK,
            track.getInfo().author,
            track.getInfo().title,
            track.getIdentifier()
        );
    }

    public static SearchResult of(final SpotifyAudioTrack track) {
        return new SearchResult(
            SearchSource.SPOTIFY,
            Type.TRACK,
            track.getInfo().author,
            track.getInfo().title,
            track.getInfo().uri
        );
    }

    public static SearchResult of(final SpotifyAudioPlaylist album) {
        return new SearchResult(SearchSource.SPOTIFY,
            Type.ALBUM,
            album.getAuthor(),
            album.getName(),
            album.getUrl()
        );
    }

    enum Type {
        ALBUM("\uD83D\uDCBF"), TRACK("\uD83C\uDFB5");

        public final String emoji;

        Type(final String emoji) {
            this.emoji = emoji;
        }
    }
}
