package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.ClearCommand;

@Dependent
public class ClearCommandListener extends AbstractGuildCommandListener<ClearCommand.Data, ClearCommand> {

    private final AudioService audioService;

    public ClearCommandListener(
        final ClearCommand command,
        final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final ClearCommand.Data data,
        final Guild guild
    ) {
        audioService.clearQueue(guild);
        event.reply("Queue cleared").queue();
    }
}
