package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.PauseCommand;

@Dependent
public class PauseCommandListener extends AbstractGuildCommandListener<PauseCommand.Data, PauseCommand> {

    private final AudioService audioService;

    public PauseCommandListener(
        final PauseCommand command,
        final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final PauseCommand.Data data,
        final Guild guild
    ) {
        audioService.pause(guild);
        event.reply("Paused playing").queue();
    }
}
