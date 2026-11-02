package org.tiagop.lagit.guild.config;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GuildConfigService implements PanacheRepository<GuildConfig> {

    @Transactional
    public GuildConfig getOrCreate(final String guildId) {
        return find("guildId", guildId)
            .firstResultOptional()
            .orElseGet(() -> {
                final var guildConfig = new GuildConfig();
                guildConfig.guildId = guildId;
                persist(guildConfig);
                return guildConfig;
            });
    }
}
