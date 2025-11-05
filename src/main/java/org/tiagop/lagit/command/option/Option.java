package org.tiagop.lagit.command.option;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private final Integer min;
    @Nullable
    private final Integer max;

    @NotNull
    public final Optional<T> extractValue(@NotNull final SlashCommandInteractionEvent event) {
        return Optional.ofNullable(event.getOption(name))
                .map(mapper);
    }

    @NotNull
    public final OptionData toOptionData() {
        final var optionData = new OptionData(type, name, description)
                .setRequired(required)
                .setAutoComplete(autoComplete);
        if (max != null) {
            optionData.setMaxValue(max);
        }
        if (min != null) {
            optionData.setMinValue(min);
        }
        return optionData;
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
                OptionMapping::getAsString,
                null,
                null
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
                OptionMapping::getAsInt,
                null,
                null
        );
    }

    public static Option<Integer> intOption(
            @NotNull final String name,
            @NotNull final String description,
            final boolean required,
            final boolean autoComplete,
            @NotNull final Pair<Integer, Integer> range
    ) {
        return new Option<>(
                OptionType.INTEGER,
                name,
                description,
                required,
                autoComplete,
                OptionMapping::getAsInt,
                range.getLeft(),
                range.getRight()
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
                OptionMapping::getAsBoolean,
                null,
                null
        );
    }
}
