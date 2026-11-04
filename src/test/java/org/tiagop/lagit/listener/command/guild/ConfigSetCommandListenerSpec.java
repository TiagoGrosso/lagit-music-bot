package org.tiagop.lagit.listener.command.guild;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.mock;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.command.ConfigSetCommand;
import org.tiagop.lagit.command.ConfigSetCommand.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfigService;

@QuarkusTest
class ConfigSetCommandListenerSpec {

    private static final long AWAIT_MILLIS = 15_000;

    private final Guild guild = mock();
    private final AudioManager audioManager = mock();
    private final SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class, RETURNS_DEEP_STUBS);

    @Inject
    ConfigSetCommandListener configSetCommandListener;

    @Inject
    GuildConfigService guildConfigService;

    @Test
    void it_sets_the_inactivity_timeout_for_the_guild() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        given(event.getName()).willReturn(ConfigSetCommand.NAME);
        given(event.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);
        given(guild.getId()).willReturn(guildId);
        given(event.getOption(ConfigSetCommand.CONFIG_KEY_OPTION.getName()).getAsString())
            .willReturn(ConfigKey.INACTIVITY_TIMEOUT.name());
        given(event.getOption(ConfigSetCommand.CONFIG_VALUE_OPTION.getName()).getAsString()).willReturn("60");

        // when
        configSetCommandListener.onEvent(event);

        // then
        final var ignored = then(event).should(timeout(AWAIT_MILLIS)).reply("Inactivity timeout set to 60 seconds");
        assertThat(guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds).isEqualTo(60L);
    }

    @Test
    void it_replies_with_an_error_and_keeps_the_config_when_the_value_is_invalid() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        given(event.getName()).willReturn(ConfigSetCommand.NAME);
        given(event.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);
        given(guild.getId()).willReturn(guildId);
        given(event.getOption(ConfigSetCommand.CONFIG_KEY_OPTION.getName()).getAsString())
            .willReturn(ConfigKey.INACTIVITY_TIMEOUT.name());
        given(event.getOption(ConfigSetCommand.CONFIG_VALUE_OPTION.getName()).getAsString()).willReturn("soon");

        // when
        configSetCommandListener.onEvent(event);

        // then
        final var ignored = then(event).should(timeout(AWAIT_MILLIS)).reply(startsWith("The inactivity timeout must be"));
        assertThat(guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds)
            .isEqualTo(GuildConfigService.DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds());
    }

    @Test
    void it_replies_with_an_error_when_the_key_is_unknown() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        given(event.getName()).willReturn(ConfigSetCommand.NAME);
        given(event.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);
        given(guild.getId()).willReturn(guildId);
        given(event.getOption(ConfigSetCommand.CONFIG_KEY_OPTION.getName()).getAsString()).willReturn("nope");
        given(event.getOption(ConfigSetCommand.CONFIG_VALUE_OPTION.getName()).getAsString()).willReturn("60");

        // when
        configSetCommandListener.onEvent(event);

        // then
        final var ignored = then(event).should(timeout(AWAIT_MILLIS)).reply(startsWith("Unknown config key"));
        assertThat(guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds)
            .isEqualTo(GuildConfigService.DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds());
    }
}
