package org.tiagop.lagit.guild.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GuildConfigServiceTest {

    @Inject
    GuildConfigService guildConfigService;

    @Test
    void itCreatesMissingGuildConfig() {
        // given
        final var guildId = "123456789012345678";

        // when
        final var guildConfig = guildConfigService.getOrCreate(guildId);

        // then
        assertThat(guildConfig.guildId).isEqualTo(guildId);
        assertThat(guildConfigService.find("guildId", guildId).count()).isEqualTo(1);
    }

    @Test
    void itReusesExistingGuildConfig() {
        // given
        final var guildId = "876543210987654321";
        guildConfigService.getOrCreate(guildId);

        // when
        final var guildConfig = guildConfigService.getOrCreate(guildId);

        // then
        assertThat(guildConfig.guildId).isEqualTo(guildId);
        assertThat(guildConfigService.find("guildId", guildId).count()).isEqualTo(1);
    }
}
