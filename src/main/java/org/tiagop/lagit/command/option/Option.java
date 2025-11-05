package org.tiagop.lagit.command.option;

import java.util.Optional;
import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class Option<T> {
    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autoComplete;
    private final Function<OptionMapping, T> mapper;

    @Nullable
    private final Integer min;
    @Nullable
    private final Integer max;

    private Option(
        final OptionType type,
        final String name,
        final String description,
        final boolean required,
        final boolean autoComplete,
        final Function<OptionMapping, T> mapper,
        @Nullable final Integer min,
        @Nullable final Integer max
    ) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autoComplete = autoComplete;
        this.mapper = mapper;
        this.min = min;
        this.max = max;
    }

    public final Optional<T> extractValue(final SlashCommandInteractionEvent event) {
        return Optional.ofNullable(event.getOption(name))
            .map(mapper);
    }

    public final OptionData toOptionData() {
        final var optionData = new OptionData(type, name, description, required, autoComplete);
        if (max != null) {
            optionData.setMaxValue(max);
        }
        if (min != null) {
            optionData.setMinValue(min);
        }
        return optionData;
    }

    public static Option<String> stringOption(
        final String name,
        final String description,
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
        final String name,
        final String description,
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
        final String name,
        final String description,
        final boolean required,
        final boolean autoComplete,
        final Pair<Integer, Integer> range
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
        final String name,
        final String description,
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

    public OptionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }

    public Function<OptionMapping, T> getMapper() {
        return mapper;
    }

    @Nullable
    public Integer getMin() {
        return min;
    }

    @Nullable
    public Integer getMax() {
        return max;
    }
}
