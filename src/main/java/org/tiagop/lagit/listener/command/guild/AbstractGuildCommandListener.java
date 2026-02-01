package org.tiagop.lagit.listener.command.guild;

import jakarta.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.AbstractCommand;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.listener.command.AbstractCommandListener;

public abstract class AbstractGuildCommandListener<D, C extends AbstractCommand<D>>
    extends AbstractCommandListener<D, C> {

    @Inject
    GuildService guildService;

    @Override
    protected final void handleCommand(final SlashCommandInteractionEvent event, final D data) {
        final var guild = event.getGuild();
        if (guild == null) {
            throw new IllegalStateException("Command can only be used in a guild");
        }
        guildService.getChannelManager(guild).setLastTextChannelUsed(event.getChannel().asTextChannel());
        handleCommand(event, data, guild);
    }

    protected abstract void handleCommand(
        final SlashCommandInteractionEvent event,
        final D data,
        final Guild guild
    );
}
