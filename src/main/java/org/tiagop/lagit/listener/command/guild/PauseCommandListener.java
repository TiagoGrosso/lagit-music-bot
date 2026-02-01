package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.PauseCommand;

@ApplicationScoped
public class PauseCommandListener extends AbstractGuildCommandListener<PauseCommand.Data, PauseCommand> {

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final PauseCommand.Data data,
        final Guild guild
    ) {
        guildService.getTrackManager(guild).pause();
        event.reply("Paused playing").queue();
    }
}
