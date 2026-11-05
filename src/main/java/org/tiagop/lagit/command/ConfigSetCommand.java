package org.tiagop.lagit.command;

import static org.tiagop.lagit.command.ConfigOptions.CONFIG_KEY;
import static org.tiagop.lagit.command.option.Option.stringOption;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.option.Option;

@ApplicationScoped
public class ConfigSetCommand extends AbstractCommand<ConfigSetCommand.Data> {
    public static final String NAME = "config-set";
    public static final String DESCRIPTION = "Sets the value of a config key for this guild";
    public static final Option<String> CONFIG_VALUE_OPTION = stringOption("value", "The new value", true, false);

    public ConfigSetCommand() {
        super(NAME, DESCRIPTION, List.of(CONFIG_KEY, CONFIG_VALUE_OPTION));
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data(event);
    }

    public record Data(String key, String value) {
        public Data(final SlashCommandInteractionEvent event) {
            this(
                CONFIG_KEY.extractValue(event).orElseThrow(),
                CONFIG_VALUE_OPTION.extractValue(event).orElseThrow()
            );
        }
    }
}
