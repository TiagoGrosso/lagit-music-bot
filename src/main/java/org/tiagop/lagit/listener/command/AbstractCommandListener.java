package org.tiagop.lagit.listener.command;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.listener.AbstractListener;

public abstract class AbstractCommandListener<D, C extends AbstractCommand<D>>
    extends AbstractListener<SlashCommandInteractionEvent> {

    @Inject
    private C command;

    protected AbstractCommandListener() {
        super(SlashCommandInteractionEvent.class);
    }

    @Override
    protected final void processEvent(final SlashCommandInteractionEvent event) {
        if (!command.getName().equals(event.getName())) {
            Log.debugf("Ignoring command %s", event.getName());
            return;
        }
        try {
            event.getChannel().asTextChannel();
            handleCommand(event, command.parseData(event));
        } catch (final Exception e) {
            Log.errorf(e, "Error processing command '%s'", event.getName());
            event.getHook().sendMessage("Error processing command").queue();
        }
    }

    protected abstract void handleCommand(final SlashCommandInteractionEvent event, final D data);

    public C getCommand() {
        return command;
    }
}
