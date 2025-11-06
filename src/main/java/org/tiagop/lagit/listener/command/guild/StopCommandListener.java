package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.StopCommand;
import org.tiagop.lagit.guild.GuildService;

@Dependent
public class StopCommandListener extends AbstractGuildCommandListener<StopCommand.Data, StopCommand> {

    private final GuildService guildService;

    public StopCommandListener(
        final StopCommand command,
        final GuildService guildService
    ) {
        super(command);
        this.guildService = guildService;
    }

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
