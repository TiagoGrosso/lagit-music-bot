package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@ApplicationScoped
public class QueueCommand extends AbstractCommand<QueueCommand.Data> {
    public static final String NAME = "queue";
    public static final String DESCRIPTION = "Gets the current queue";

    public QueueCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}