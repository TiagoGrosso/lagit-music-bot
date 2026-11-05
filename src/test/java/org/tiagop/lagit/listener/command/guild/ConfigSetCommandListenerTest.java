package org.tiagop.lagit.listener.command.guild;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.command.ConfigSetCommand;
import org.tiagop.lagit.guild.config.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfigService;

class ConfigSetCommandListenerTest {

    private static final String GUILD_ID = "123456789012345678";

    private final GuildConfigService guildConfigService = mock();
    private final ConfigSetCommandListener listener = new ConfigSetCommandListener(guildConfigService);

    private final Guild guild = mock();
    private final SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    private final ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);

    @BeforeEach
    void setUp() {
        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);
        when(guild.getId()).thenReturn(GUILD_ID);
    }

    @Test
    void it_sets_the_inactivity_timeout_and_replies() {
        // given
        final var data = new ConfigSetCommand.Data(ConfigKey.INACTIVITY_TIMEOUT.name(), "60");

        // when
        listener.handleCommand(event, data, guild);

        // then
        verify(guildConfigService).setInactivityTimeLimit(GUILD_ID, 60L);
        final var ignored = verify(event).reply("Inactivity timeout set to 60 seconds");
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void it_replies_with_an_error_when_the_value_is_not_a_whole_number() {
        // given
        final var data = new ConfigSetCommand.Data(ConfigKey.INACTIVITY_TIMEOUT.name(), "soon");

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = verify(event).reply("The inactivity timeout must be a whole number of seconds, got 'soon'");
        verifyNoInteractions(guildConfigService);
    }

    @Test
    void it_replies_with_an_error_when_the_value_is_not_positive() {
        // given
        final var key = ConfigKey.INACTIVITY_TIMEOUT.name();
        final var zeroData = new ConfigSetCommand.Data(key, "0");
        final var negativeData = new ConfigSetCommand.Data(key, "-5");

        // when
        listener.handleCommand(event, zeroData, guild);
        listener.handleCommand(event, negativeData, guild);

        // then
        final var ignoredZero = verify(event).reply("The inactivity timeout must be at least 1 second, got 0");
        final var ignoredNegative = verify(event).reply("The inactivity timeout must be at least 1 second, got -5");
        verifyNoInteractions(guildConfigService);
    }

    @Test
    void it_replies_with_an_error_when_the_key_is_unknown() {
        // given
        final var data = new ConfigSetCommand.Data("NOT_A_KEY", "60");

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = verify(event).reply("Unknown config key: NOT_A_KEY");
        verifyNoInteractions(guildConfigService);
    }
}
