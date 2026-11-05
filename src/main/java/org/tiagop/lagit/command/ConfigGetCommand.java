package org.tiagop.lagit.command;

import static org.tiagop.lagit.command.ConfigOptions.CONFIG_KEY;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@ApplicationScoped
public class ConfigGetCommand extends AbstractCommand<ConfigGetCommand.Data> {
    public static final String NAME = "config-get";
    public static final String DESCRIPTION = "Shows the value of a config key for this guild";

    public ConfigGetCommand() {
        super(NAME, DESCRIPTION, List.of(CONFIG_KEY));
    }

    @Override
    public Data parseData(final SlashCommandInteractionEvent event) {
        return new Data(event);
    }

    public record Data(String key) {
        public Data(final SlashCommandInteractionEvent event) {
            this(CONFIG_KEY.extractValue(event).orElseThrow());
        }
    }
}
