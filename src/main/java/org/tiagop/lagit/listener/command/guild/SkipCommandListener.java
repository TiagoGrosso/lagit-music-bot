package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.SkipCommand;

@Dependent
public class SkipCommandListener extends AbstractGuildCommandListener<SkipCommand.Data, SkipCommand> {

    private final AudioService audioService;

    public SkipCommandListener(
        final SkipCommand command,
        final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final SkipCommand.Data data,
        final Guild guild
    ) {
        audioService.skip(guild, data.songsToSkip().orElse(1));
        event.reply("Skipped " + data.songsToSkip().orElse(1) + " songs").queue();
    }
}
