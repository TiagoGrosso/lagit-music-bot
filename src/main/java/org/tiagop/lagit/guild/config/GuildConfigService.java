package org.tiagop.lagit.guild.config;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Duration;

@ApplicationScoped
public class GuildConfigService implements PanacheRepository<GuildConfig> {

    public static final Duration DEFAULT_INACTIVITY_TIME_LIMIT = Duration.ofMinutes(5);

    @Transactional
    public GuildConfig getOrCreate(final String guildId) {
        return find("guildId", guildId)
            .firstResultOptional()
            .orElseGet(() -> {
                final var guildConfig = new GuildConfig();
                guildConfig.guildId = guildId;
                guildConfig.inactivityTimeLimitSeconds = DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds();
                guildConfig.announceMode = AnnounceMode.EDIT_MESSAGE;
                persist(guildConfig);
                return guildConfig;
            });
    }

    @Transactional
    public void setInactivityTimeLimit(final String guildId, final long inactivityTimeLimitSeconds) {
        getOrCreate(guildId).inactivityTimeLimitSeconds = inactivityTimeLimitSeconds;
    }

    @Transactional
    public void setAnnounceMode(final String guildId, final AnnounceMode announceMode) {
        getOrCreate(guildId).announceMode = announceMode;
    }
}
