package org.tiagop.lagit.service.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public record SearchResult(
        @NotNull Source source,
        @NotNull String artist,
        @NotNull String name,
        @NotNull Type type,
        @NotNull String id
) {

    @RequiredArgsConstructor
    @Getter
    public enum Source {
        YOUTUBE("Youtube", "ytmsearch");

        private final String sourceName;
        private final String searchPrefix;
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
