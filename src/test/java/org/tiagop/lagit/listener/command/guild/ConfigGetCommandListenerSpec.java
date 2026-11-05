package org.tiagop.lagit.listener.command.guild;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.command.ConfigGetCommand;
import org.tiagop.lagit.command.ConfigOptions;
import org.tiagop.lagit.guild.config.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfigService;

@QuarkusTest
class ConfigGetCommandListenerSpec {

    private static final long AWAIT_MILLIS = 15_000;

    private final Guild guild = mock();
    private final AudioManager audioManager = mock();
    private final SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class, RETURNS_DEEP_STUBS);

    @Inject
    ConfigGetCommandListener configGetCommandListener;

    @Inject
    GuildConfigService guildConfigService;

    @Test
    void it_replies_with_the_default_value() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        given(event.getName()).willReturn(ConfigGetCommand.NAME);
        given(event.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);
        given(guild.getId()).willReturn(guildId);
        given(event.getOption(ConfigOptions.CONFIG_KEY.getName()).getAsString())
            .willReturn(ConfigKey.INACTIVITY_TIMEOUT.name());

        // when
        configGetCommandListener.onEvent(event);

        // then
        final var ignored = then(event).should(timeout(AWAIT_MILLIS)).reply(
            "Inactivity timeout: %d seconds".formatted(GuildConfigService.DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds())
        );
    }

    @Test
    void it_replies_with_the_value_set_for_the_guild() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        given(event.getName()).willReturn(ConfigGetCommand.NAME);
        given(event.getGuild()).willReturn(guild);
        given(guild.getAudioManager()).willReturn(audioManager);
        given(guild.getId()).willReturn(guildId);
        given(event.getOption(ConfigOptions.CONFIG_KEY.getName()).getAsString())
            .willReturn(ConfigKey.INACTIVITY_TIMEOUT.name());
        guildConfigService.setInactivityTimeLimit(guildId, 60L);

        // when
        configGetCommandListener.onEvent(event);

        // then
        final var ignored = then(event).should(timeout(AWAIT_MILLIS)).reply("Inactivity timeout: 60 seconds");
    }
}
