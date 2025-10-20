package org.tiagop.lagit.command.option;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Option<T> {
    @NotNull
    private final OptionType type;
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    private final boolean required;
    private final boolean autoComplete;
    private final Function<OptionMapping, T> mapper;

    @NotNull
    public final Optional<T> extractValue(@NotNull final SlashCommandInteractionEvent event) {
        return Optional.ofNullable(event.getOption(name))
                .map(mapper);
    }

    @NotNull
    public final OptionData toOptionData() {
        return new OptionData(type, name, description).setRequired(required).setAutoComplete(autoComplete);
    }

    public static Option<String> stringOption(
            @NotNull final String name,
            @NotNull final String description,
            final boolean required,
            final boolean autoComplete
    ) {
        return new Option<>(
                OptionType.STRING,
                name,
                description,
                required,
                autoComplete,
                OptionMapping::getAsString
        );
    }

    public static Option<Integer> intOption(
            @NotNull final String name,
            @NotNull final String description,
            final boolean required,
            final boolean autoComplete
    ) {
        return new Option<>(
                OptionType.INTEGER,
                name,
                description,
                required,
                autoComplete,
                OptionMapping::getAsInt
        );
    }

    public static Option<Boolean> booleanOption(
            @NotNull final String name,
            @NotNull final String description,
            final boolean required,
            final boolean autoComplete
    ) {
        return new Option<>(
                OptionType.BOOLEAN,
                name,
                description,
                required,
                autoComplete,
                OptionMapping::getAsBoolean
        );
    }
}
