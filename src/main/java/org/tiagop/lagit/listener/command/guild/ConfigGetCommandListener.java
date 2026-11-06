package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.ConfigGetCommand;
import org.tiagop.lagit.guild.config.ConfigKey;
import org.tiagop.lagit.guild.config.GuildConfigService;

@ApplicationScoped
public class ConfigGetCommandListener extends AbstractGuildCommandListener<ConfigGetCommand.Data, ConfigGetCommand> {

    private final GuildConfigService guildConfigService;

    public ConfigGetCommandListener(final GuildConfigService guildConfigService) {
        this.guildConfigService = guildConfigService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final ConfigGetCommand.Data data,
        final Guild guild
    ) {
        final var key = ConfigKey.fromRaw(data.key());
        if (key == null) {
            event.reply("Unknown config key: %s".formatted(data.key())).setEphemeral(true).queue();
            return;
        }

        final var guildConfig = guildConfigService.getOrCreate(guild.getId());

        // exhaustive switch expression so every new ConfigKey value forces an arm to be added here
        final String message = switch (key) {
            case INACTIVITY_TIMEOUT -> "Inactivity timeout: %d seconds".formatted(
                guildConfig.inactivityTimeLimitSeconds
            );
            case ANNOUNCE_MODE -> "Announce mode: %s".formatted(guildConfig.announceMode);
        };
        event.reply(message).setEphemeral(true).queue();
    }
}
