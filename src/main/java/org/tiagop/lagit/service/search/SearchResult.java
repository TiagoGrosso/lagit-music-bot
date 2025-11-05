package org.tiagop.lagit.service.search;

import org.apache.commons.lang3.StringUtils;

public record SearchResult(
    Source source,
    String artist,
    String name,
    Type type,
    String id
) {

    public enum Source {
        YOUTUBE("Youtube", "ytmsearch");

        private final String sourceName;
        private final String searchPrefix;

        Source(final String sourceName, final String searchPrefix) {
            this.sourceName = sourceName;
            this.searchPrefix = searchPrefix;
        }

        public String getSearchPrefix() {
            return searchPrefix;
        }

        public String getSourceName() {
            return sourceName;
        }
    }

    public enum Type {
        SINGLE,
        ALBUM,
        PLAYLIST,
        UNKNOWN
    }

    public String choiceName() {
        return StringUtils.truncate(
            "[%s] %s - %s%s".formatted(
                source.sourceName,
                StringUtils.truncate(artist, 20),
                StringUtils.truncate(name, 50),
                type == Type.UNKNOWN ? "" : " (" + type + ") "),
            100);
    }

    public String choiceValue() {
        return "%s: %s".formatted(source.searchPrefix, name);
    }

}
