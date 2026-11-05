package org.tiagop.lagit.listener.command.autocomplete;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.command.ConfigGetCommand;
import org.tiagop.lagit.guild.config.ConfigKey;

class ConfigGetKeyAutocompleteListenerTest {

    @Test
    void it_offers_every_config_key_as_a_choice() {
        // given
        final var listener = new ConfigGetKeyAutocompleteListener(new ConfigGetCommand());
        final var event = mock(CommandAutoCompleteInteractionEvent.class);
        final var callbackAction = mock(AutoCompleteCallbackAction.class);
        when(event.replyChoices(anyCollection())).thenReturn(callbackAction);

        // when
        listener.handleAutocomplete(event);

        // then
        final var ignored = verify(event).replyChoices(List.of(
            new Choice(ConfigKey.INACTIVITY_TIMEOUT.description, ConfigKey.INACTIVITY_TIMEOUT.name()),
            new Choice(ConfigKey.ANNOUNCE_MODE.description, ConfigKey.ANNOUNCE_MODE.name())
        ));
        verify(callbackAction).queue();
    }
}
