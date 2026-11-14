package org.tiagop.lagit.guild.config;

import org.jetbrains.annotations.Nullable;

public enum ConfigKey {
    INACTIVITY_TIMEOUT("Inactivity timeout (seconds)"),
    ANNOUNCE_MODE("Announce mode (NEW_MESSAGE, EDIT_MESSAGE or SILENT)"),
    ;

    public final String description;

    ConfigKey(final String description) {
        this.description = description;
    }

    @Nullable
    public static ConfigKey fromRaw(final String raw) {
        try {
            return valueOf(raw);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
