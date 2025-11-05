package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.ClearCommand;

@Dependent
public class ClearCommandListener extends AbstractGuildCommandListener<ClearCommand.Data, ClearCommand> {

    private final AudioService audioService;

    public ClearCommandListener(
        @NotNull final ClearCommand command,
        @NotNull final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
        @NotNull final SlashCommandInteractionEvent event,
        @NotNull final ClearCommand.Data data,
        @NotNull final Guild guild
    ) {
        audioService.clearQueue(guild);
        event.reply("Queue cleared").queue();
    }
}
