package org.tiagop.lagit.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.command.option.Option;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class AbstractCommand<D> {

    @NotNull private final String name;
    @NotNull private final String description;
    @NotNull private final List<Option<?>> options;

    public abstract D parseData(@NotNull final SlashCommandInteractionEvent event);

    public CommandData toCommandData() {
        return Commands.slash(name, description)
                .addOptions(options.stream().map(Option::toOptionData).toList());
    }
}
