package org.tiagop.lagit.command;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.tiagop.lagit.command.option.Option;

@ApplicationScoped
public class SkipCommand extends AbstractCommand<SkipCommand.Data> {
    public static final String NAME = "skip";
    public static final String DESCRIPTION = "Skips songs in the queue";
    public static final Option<Integer> SONGS_TO_SKIP_OPTION = Option.intOption(
        "num",
        "How many songs to skip",
        false,
        false,
        Pair.of(1, Integer.MAX_VALUE)
    );

    public SkipCommand() {
        super(NAME, DESCRIPTION, List.of(SONGS_TO_SKIP_OPTION));
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data(event);
    }

    public record Data(Optional<Integer> songsToSkip) {
        public Data(final SlashCommandInteractionEvent event) {
            this(SONGS_TO_SKIP_OPTION.extractValue(event));
        }
    }
}
