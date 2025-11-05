package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.option.Option;

@ApplicationScoped
public class PlayCommand extends AbstractCommand<PlayCommand.Data> {
    public static final String NAME = "play";
    public static final String DESCRIPTION = "Play a song";
    public static final Option<String> QUERY_OPTION = Option.stringOption(
        "query",
        "Query to search for",
        false,
        true
    );

    public PlayCommand() {
        super(NAME, DESCRIPTION, List.of(QUERY_OPTION));
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data(event);
    }

    public record Data(Optional<String> query) {
        public Data(final SlashCommandInteractionEvent event) {
            this(QUERY_OPTION.extractValue(event));
        }
    }
}
