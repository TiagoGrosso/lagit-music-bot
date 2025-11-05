package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@ApplicationScoped
public class PauseCommand extends AbstractCommand<PauseCommand.Data> {
    public static final String NAME = "pause";
    public static final String DESCRIPTION = "Pause a song";

    public PauseCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(@NotNull final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}
