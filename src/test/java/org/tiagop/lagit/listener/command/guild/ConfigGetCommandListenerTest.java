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
import org.tiagop.lagit.command.ConfigGetCommand;
import org.tiagop.lagit.guild.config.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfig;
import org.tiagop.lagit.guild.config.GuildConfigService;

class ConfigGetCommandListenerTest {

    private static final String GUILD_ID = "123456789012345678";

    private final GuildConfigService guildConfigService = mock();
    private final ConfigGetCommandListener listener = new ConfigGetCommandListener(guildConfigService);

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
    void it_replies_with_the_configured_value() {
        // given
        final var guildConfig = new GuildConfig();
        guildConfig.inactivityTimeLimitSeconds = 60L;
        when(guildConfigService.getOrCreate(GUILD_ID)).thenReturn(guildConfig);
        final var data = new ConfigGetCommand.Data(ConfigKey.INACTIVITY_TIMEOUT.name());

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = verify(event).reply("Inactivity timeout: 60 seconds");
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void it_replies_with_an_error_when_the_key_is_unknown() {
        // given
        final var data = new ConfigGetCommand.Data("NOT_A_KEY");

        // when
        listener.handleCommand(event, data, guild);

        // then
        final var ignored = verify(event).reply("Unknown config key: NOT_A_KEY");
        verifyNoInteractions(guildConfigService);
    }
}
