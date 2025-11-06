package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.PauseCommand;
import org.tiagop.lagit.guild.GuildService;

@Dependent
public class PauseCommandListener extends AbstractGuildCommandListener<PauseCommand.Data, PauseCommand> {

    private final GuildService guildService;

    public PauseCommandListener(
        final PauseCommand command,
        final GuildService guildService
    ) {
        super(command);
        this.guildService = guildService;
    }

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
