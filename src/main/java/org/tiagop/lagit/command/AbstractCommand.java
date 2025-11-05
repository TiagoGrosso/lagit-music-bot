package org.tiagop.lagit.command;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.tiagop.lagit.command.option.Option;

public abstract class AbstractCommand<D> {

    private final String name;
    private final String description;
    private final List<Option<?>> options;

    protected AbstractCommand(
        final String name,
        final String description,
        final List<Option<?>> options
    ) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public abstract D parseData(final SlashCommandInteractionEvent event);

    public CommandData toCommandData() {
        return Commands.slash(name, description)
            .addOptions(options.stream().map(Option::toOptionData).toList());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Option<?>> getOptions() {
        return options;
    }
}
