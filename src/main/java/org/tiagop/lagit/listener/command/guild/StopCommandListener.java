package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.StopCommand;

@ApplicationScoped
public class StopCommandListener extends AbstractGuildCommandListener<StopCommand.Data, StopCommand> {

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final StopCommand.Data data,
        final Guild guild
    ) {
        guildService.getTrackManager(guild).stop();
        guildService.getChannelManager(guild).leaveChannel();
        event.reply("Stopped playing").queue();
    }
}
