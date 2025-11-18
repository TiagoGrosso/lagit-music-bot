package org.tiagop.lagit.service.search;

public enum SearchSource {
    YOUTUBE("Youtube", 1),
    SPOTIFY("Spotify", 2);

    public final String sourceName;
    public final int priority;

    SearchSource(final String sourceName, final int priority) {
        this.sourceName = sourceName;
        this.priority = priority;
    }
}
