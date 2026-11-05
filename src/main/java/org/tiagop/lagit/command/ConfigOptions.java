package org.tiagop.lagit.command;

import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.tiagop.lagit.command.option.Option;
import org.tiagop.lagit.guild.config.ConfigKey;

public final class ConfigOptions {

    public static final Option<String> CONFIG_KEY = Option.stringOption("key", "The config key", true, true);

    private ConfigOptions() {
    }

    public static List<Choice> configKeyChoices() {
        return Arrays.stream(ConfigKey.values())
            .map(key -> new Choice(key.description, key.name()))
            .toList();
    }
}
