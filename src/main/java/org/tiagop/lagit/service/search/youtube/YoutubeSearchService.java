package org.tiagop.lagit.service.search.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.service.search.SearchClient;
import org.tiagop.lagit.service.search.SearchResult;

@ApplicationScoped
public class YoutubeSearchService implements SearchClient {

    private final YoutubeSearchClient youtubeSearchClient;

    public YoutubeSearchService(
        @NotNull @RestClient final YoutubeSearchClient youtubeSearchClient
    ) {
        this.youtubeSearchClient = youtubeSearchClient;
    }

    public List<SearchResult> search(@NotNull final String query) {
        final var raw = youtubeSearchClient.getSuggestions(new YoutubeSearchClient.MusicSuggestionsRequestBody(query));
        final var tracks = extractTracks(raw);

        return tracks.stream()
            .map(t -> new SearchResult(
                SearchResult.Source.YOUTUBE,
                t.artist,
                t.title,
                SearchResult.Type.UNKNOWN,
                t.videoId))
            .toList();
    }

    private static List<TrackInfo> extractTracks(JsonNode root) {
        final var maybeItems =
            root.at("/contents/tabbedSearchResultsRenderer/tabs/0/tabRenderer/content/sectionListRenderer/contents")
                .valueStream()
                .filter(item -> item.hasNonNull("musicShelfRenderer"))
                .findFirst()
                .map(item -> item.get("musicShelfRenderer").get("contents"));

        if (maybeItems.isEmpty()) {
            return List.of();
        }

        final var items = maybeItems.get();
        final var tracks = new LinkedList<TrackInfo>();

        for (final var track : items.valueStream().toList()) {
            final var columns = track.at("/musicResponsiveListItemRenderer/flexColumns");

            if (columns.isMissingNode()) {
                continue;
            }
            final var metadata = columns.at("/0/musicResponsiveListItemFlexColumnRenderer/text/runs/0");
            final var title = metadata.get("text").textValue();
            final var videoIdNode = metadata.at("/navigationEndpoint/watchEndpoint/videoId");

            if (videoIdNode.isMissingNode()) {
                // If the track is not available on YouTube Music, videoId will be empty
                continue;
            }

            final var videoId = videoIdNode.textValue();

            final var runs = columns.at("/1/musicResponsiveListItemFlexColumnRenderer/text/runs")
                .valueStream()
                .toList();

            final var isSong = runs.getFirst().get("text").textValue().toLowerCase().contains("song");

            if (!isSong) {
                continue;
            }

            final var authorNode = runs.getLast().path("text");
            final var author = authorNode.isMissingNode() ? "Unknown Artist" : authorNode.textValue();

            tracks.add(new TrackInfo(videoId, title, author));
        }

        return tracks;
    }

    record TrackInfo(
        String videoId,
        String title,
        String artist
    ) {
        public TrackInfo(String videoId, String title, String artist) {
            this.videoId = videoId;
            this.title = title;
            this.artist = artist;
        }
    }
}
