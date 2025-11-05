package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.audio.manager.ChannelService;
import org.tiagop.lagit.command.StopCommand;

@Dependent
public class StopCommandListener extends AbstractGuildCommandListener<StopCommand.Data, StopCommand> {

    private final AudioService audioService;
    private final ChannelService channelService;

    public StopCommandListener(
        @NotNull final StopCommand command,
        @NotNull final AudioService audioService,
        @NotNull final ChannelService channelService
    ) {
        super(command);
        this.audioService = audioService;
        this.channelService = channelService;
    }

    @Override
    protected void handleCommand(
        @NotNull final SlashCommandInteractionEvent event,
        @NotNull final StopCommand.Data data,
        @NotNull final Guild guild
    ) {
        audioService.stop(guild);
        channelService.leaveChannel(guild);
        event.reply("Stopped playing").queue();
    }
}
