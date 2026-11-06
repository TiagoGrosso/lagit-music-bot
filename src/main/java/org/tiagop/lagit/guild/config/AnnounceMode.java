package org.tiagop.lagit.guild.config;

import org.jetbrains.annotations.Nullable;

public enum AnnounceMode {
    NEW_MESSAGE,
    EDIT_MESSAGE,
    SILENT,
    ;

    @Nullable
    public static AnnounceMode fromRaw(final String raw) {
        for (final var mode : values()) {
            if (mode.name().equalsIgnoreCase(raw)) {
                return mode;
            }
        }
        return null;
    }
}
