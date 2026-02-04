package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@ApplicationScoped
public class PlayingCommand extends AbstractCommand<PlayingCommand.Data> {
    public static final String NAME = "playing";
    public static final String DESCRIPTION = "Shows the song currently playing";

    public PlayingCommand() {
        super(NAME, DESCRIPTION, Collections.emptyList());
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data();
    }

    public record Data() {
    }
}