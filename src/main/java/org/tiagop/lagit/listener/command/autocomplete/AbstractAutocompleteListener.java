package org.tiagop.lagit.listener.command.autocomplete;

import io.quarkus.logging.Log;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.command.option.Option;
import org.tiagop.lagit.listener.AbstractListener;

public abstract class AbstractAutocompleteListener<C extends AbstractCommand<?>>
    extends AbstractListener<CommandAutoCompleteInteractionEvent> {

    private final AbstractCommand<?> command;
    private final Option<?> option;

    protected AbstractAutocompleteListener(@NotNull final AbstractCommand<?> command,
                                           @NotNull final Option<?> option) {
        super(CommandAutoCompleteInteractionEvent.class);
        this.command = command;
        this.option = option;
    }

    @Override
    protected void processEvent(@NotNull final CommandAutoCompleteInteractionEvent event) {
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
        @NotNull final CommandAutoCompleteInteractionEvent event
    );
}
