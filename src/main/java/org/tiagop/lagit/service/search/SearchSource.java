package org.tiagop.lagit.service.search;

public enum SearchSource {
    YOUTUBE("Youtube");

    private final String sourceName;

    SearchSource(final String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}
