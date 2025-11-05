package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@ApplicationScoped
public class StopCommand extends AbstractCommand<StopCommand.Data> {
    public static final String NAME = "stop";
    public static final String DESCRIPTION = "Stops songs";

    public StopCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}
