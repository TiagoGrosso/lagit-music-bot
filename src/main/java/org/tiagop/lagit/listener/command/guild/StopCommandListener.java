package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.StopCommand;
import org.tiagop.lagit.guild.channel.ChannelService;

@Dependent
public class StopCommandListener extends AbstractGuildCommandListener<StopCommand.Data, StopCommand> {

    private final AudioService audioService;
    private final ChannelService channelService;

    public StopCommandListener(
        final StopCommand command,
        final AudioService audioService,
        final ChannelService channelService
    ) {
        super(command);
        this.audioService = audioService;
        this.channelService = channelService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final StopCommand.Data data,
        final Guild guild
    ) {
        audioService.stop(guild);
        channelService.leaveChannel(guild);
        event.reply("Stopped playing").queue();
    }
}
