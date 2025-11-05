package org.tiagop.lagit.listener.command.autocomplete;

import io.quarkus.logging.Log;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.command.option.Option;
import org.tiagop.lagit.listener.AbstractListener;

public abstract class AbstractAutocompleteListener<C extends AbstractCommand<?>>
    extends AbstractListener<CommandAutoCompleteInteractionEvent> {

    private final AbstractCommand<?> command;
    private final Option<?> option;

    protected AbstractAutocompleteListener(final AbstractCommand<?> command,
                                           final Option<?> option) {
        super(CommandAutoCompleteInteractionEvent.class);
        this.command = command;
        this.option = option;
    }

    @Override
    protected void processEvent(final CommandAutoCompleteInteractionEvent event) {
        if (!command.getName().equals(event.getName())) {
            Log.debugf("Ignoring command %s", event.getName());
            return;
        }
        if (!option.getName().equals(event.getFocusedOption().getName())) {
            Log.debugf("Ignoring option %s", event.getFocusedOption().getName());
            return;
        }
        handleAutocomplete(event);
    }

    protected abstract void handleAutocomplete(
        final CommandAutoCompleteInteractionEvent event
    );
}
