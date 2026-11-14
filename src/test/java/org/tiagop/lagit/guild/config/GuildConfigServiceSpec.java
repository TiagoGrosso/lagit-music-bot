package org.tiagop.lagit.guild.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.guild.config.AnnounceMode;

@QuarkusTest
class GuildConfigServiceSpec {

    @Inject
    GuildConfigService guildConfigService;

    @Test
    void it_creates_missing_guild_config() {
        // given
        final var guildId = "123456789012345678";

        // when
        final var guildConfig = guildConfigService.getOrCreate(guildId);

        // then
        assertThat(guildConfig.guildId).isEqualTo(guildId);
        assertThat(guildConfig.inactivityTimeLimitSeconds)
            .isEqualTo(GuildConfigService.DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds());
        assertThat(guildConfig.announceMode).isEqualTo(AnnounceMode.EDIT_MESSAGE);
        assertThat(guildConfigService.find("guildId", guildId).count()).isEqualTo(1);
    }

    @Test
    void it_reuses_existing_guild_config() {
        // given
        final var guildId = "876543210987654321";
        guildConfigService.getOrCreate(guildId);

        // when
        final var guildConfig = guildConfigService.getOrCreate(guildId);

        // then
        assertThat(guildConfig.guildId).isEqualTo(guildId);
        assertThat(guildConfigService.find("guildId", guildId).count()).isEqualTo(1);
    }

    @Test
    void it_sets_the_inactivity_time_limit_of_a_guild() {
        // given
        final var guildId = "123450987654321098";
        guildConfigService.getOrCreate(guildId);

        // when
        guildConfigService.setInactivityTimeLimit(guildId, Duration.ofMinutes(1).toSeconds());

        // then
        assertThat(guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds).isEqualTo(60L);
    }

    @Test
    void it_sets_the_announce_mode_of_a_guild() {
        // given
        final var guildId = "987650123456789012";
        guildConfigService.getOrCreate(guildId);

        // when
        guildConfigService.setAnnounceMode(guildId, AnnounceMode.SILENT);

        // then
        assertThat(guildConfigService.getOrCreate(guildId).announceMode).isEqualTo(AnnounceMode.SILENT);
    }
}
