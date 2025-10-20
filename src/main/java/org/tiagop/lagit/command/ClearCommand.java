package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@ApplicationScoped
public class ClearCommand extends AbstractCommand<ClearCommand.Data> {
    public static final String NAME = "clear";
    public static final String DESCRIPTION = "Clears the queue";

    public ClearCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(@NotNull final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}
