package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.ClearCommand;
import org.tiagop.lagit.guild.GuildService;

@Dependent
public class ClearCommandListener extends AbstractGuildCommandListener<ClearCommand.Data, ClearCommand> {

    private final GuildService guildService;

    public ClearCommandListener(
        final ClearCommand command,
        final GuildService guildService
    ) {
        super(command);
        this.guildService = guildService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final ClearCommand.Data data,
        final Guild guild
    ) {
        guildService.getTrackManager(guild).clear();
        event.reply("Queue cleared").queue();
    }
}
