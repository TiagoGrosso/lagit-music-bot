package org.tiagop.lagit.listener.command.autocomplete;

import static org.tiagop.lagit.command.ConfigSetCommand.CONFIG_KEY_OPTION;

import jakarta.enterprise.context.Dependent;
import java.util.Arrays;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.tiagop.lagit.command.ConfigSetCommand;
import org.tiagop.lagit.command.ConfigSetCommand.ConfigKey;

@Dependent
public class ConfigSetKeyAutocompleteListener extends AbstractAutocompleteListener<ConfigSetCommand> {

    protected ConfigSetKeyAutocompleteListener(
        final ConfigSetCommand command
    ) {
        super(command, CONFIG_KEY_OPTION);
    }

    @Override
    protected void handleAutocomplete(final CommandAutoCompleteInteractionEvent event) {
        final var choices = Arrays.stream(ConfigKey.values())
            .map(key -> new Choice(key.description, key.name()))
            .toList();

        event.replyChoices(choices).queue();
    }
}
