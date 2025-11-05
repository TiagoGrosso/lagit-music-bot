package org.tiagop.lagit.command;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.option.Option;

public abstract class AbstractCommand<D> {

    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final List<Option<?>> options;

    protected AbstractCommand(
        @NotNull final String name,
        @NotNull final String description,
        @NotNull final List<Option<?>> options
    ) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public abstract D parseData(@NotNull final SlashCommandInteractionEvent event);

    public CommandData toCommandData() {
        return Commands.slash(name, description)
            .addOptions(options.stream().map(Option::toOptionData).toList());
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @NotNull List<Option<?>> getOptions() {
        return options;
    }
}
