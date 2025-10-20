package org.tiagop.lagit.command.option;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.AbstractCommand;

import java.util.Collections;

@ApplicationScoped
public class QueueCommand extends AbstractCommand<QueueCommand.Data> {
    public static final String NAME = "queue";
    public static final String DESCRIPTION = "Gets the current queue";

    public QueueCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(@NotNull final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}