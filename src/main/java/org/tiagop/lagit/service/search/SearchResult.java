package org.tiagop.lagit.service.search;

import org.apache.commons.lang3.StringUtils;

public record SearchResult(
    SearchSource source,
    String artist,
    String title,
    String id
) {

    public static final String CHOICE_NAME_FORMAT = "[%s] %s - %s";

    public String choiceName() {
        return StringUtils.truncate(
            CHOICE_NAME_FORMAT.formatted(
                source.getSourceName(),
                StringUtils.truncate(artist, 20),
                StringUtils.truncate(title, 50)),
            100);
    }

    public String choiceValue() {
        return id;
    }

}
