package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.ClearCommand;

@ApplicationScoped
public class ClearCommandListener extends AbstractGuildCommandListener<ClearCommand.Data, ClearCommand> {

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final ClearCommand.Data data,
        final Guild guild
    ) {
        guildService.getTrackManager(guild).clear();
        event.reply("Queue cleared").queue();
    }
}
