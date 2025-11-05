package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.SkipCommand;

@Dependent
public class SkipCommandListener extends AbstractGuildCommandListener<SkipCommand.Data, SkipCommand> {

    private final AudioService audioService;

    public SkipCommandListener(
            @NotNull final SkipCommand command,
            @NotNull final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
            @NotNull final SlashCommandInteractionEvent event,
            @NotNull final SkipCommand.Data data,
            @NotNull final Guild guild
    ) {
        audioService.skip(guild, data.songsToSkip().orElse(1));
        event.reply("Skipped " + data.songsToSkip().orElse(1) + " songs").queue();
    }
}
