package org.tiagop.lagit.service.search;

public enum SearchSource {
    YOUTUBE("Youtube", 1),
    SPOTIFY("Spotify", 2);

    public final String name;
    public final int priority;

    SearchSource(final String name, final int priority) {
        this.name = name;
        this.priority = priority;
    }
}
