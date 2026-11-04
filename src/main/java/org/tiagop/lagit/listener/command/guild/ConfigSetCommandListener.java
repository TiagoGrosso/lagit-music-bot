package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;
import org.tiagop.lagit.command.ConfigSetCommand;
import org.tiagop.lagit.command.ConfigSetCommand.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfigService;

@ApplicationScoped
public class ConfigSetCommandListener extends AbstractGuildCommandListener<ConfigSetCommand.Data, ConfigSetCommand> {

    private final GuildConfigService guildConfigService;

    public ConfigSetCommandListener(final GuildConfigService guildConfigService) {
        this.guildConfigService = guildConfigService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final ConfigSetCommand.Data data,
        final Guild guild
    ) {
        final var key = configKey(data.key());
        if (key == null) {
            event.reply("Unknown config key: %s".formatted(data.key())).setEphemeral(true).queue();
            return;
        }

        // exhaustive switch expression so every new ConfigKey value forces an arm to be added here
        final String message = switch (key) {
            case INACTIVITY_TIMEOUT -> setInactivityTimeout(guild.getId(), data.value());
        };
        event.reply(message).setEphemeral(true).queue();
    }

    private String setInactivityTimeout(final String guildId, final String rawValue) {
        final long seconds;
        try {
            seconds = Long.parseLong(rawValue);
        } catch (final NumberFormatException e) {
            return "The inactivity timeout must be a whole number of seconds, got '%s'".formatted(rawValue);
        }
        if (seconds <= 0) {
            return "The inactivity timeout must be at least 1 second, got %d".formatted(seconds);
        }
        guildConfigService.setInactivityTimeLimit(guildId, seconds);
        return "Inactivity timeout set to %d seconds".formatted(seconds);
    }

    @Nullable
    private ConfigKey configKey(final String raw) {
        try {
            return ConfigKey.valueOf(raw);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
