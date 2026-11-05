package org.tiagop.lagit.listener.command.autocomplete;

import static org.tiagop.lagit.command.ConfigOptions.CONFIG_KEY;
import static org.tiagop.lagit.command.ConfigOptions.configKeyChoices;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.tiagop.lagit.command.ConfigSetCommand;

@Dependent
public class ConfigSetKeyAutocompleteListener extends AbstractAutocompleteListener<ConfigSetCommand> {

    protected ConfigSetKeyAutocompleteListener(
        final ConfigSetCommand command
    ) {
        super(command, CONFIG_KEY);
    }

    @Override
    protected void handleAutocomplete(final CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(configKeyChoices()).queue();
    }
}
