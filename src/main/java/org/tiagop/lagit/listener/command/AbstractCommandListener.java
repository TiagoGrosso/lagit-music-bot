package org.tiagop.lagit.listener.command;

import io.quarkus.logging.Log;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.listener.AbstractListener;

public abstract class AbstractCommandListener<D, C extends AbstractCommand<D>>
    extends AbstractListener<SlashCommandInteractionEvent> {

    private final C command;

    protected AbstractCommandListener(final C command) {
        super(SlashCommandInteractionEvent.class);
        this.command = command;
    }

    @Override
    protected final void processEvent(final SlashCommandInteractionEvent event) {
        if (!command.getName().equals(event.getName())) {
            Log.debugf("Ignoring command %s", event.getName());
            return;
        }
        handleCommand(event, command.parseData(event));
    }

    protected abstract void handleCommand(final SlashCommandInteractionEvent event, final D data);

    public C getCommand() {
        return command;
    }
}
