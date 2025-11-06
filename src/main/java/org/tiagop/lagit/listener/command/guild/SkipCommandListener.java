package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.SkipCommand;
import org.tiagop.lagit.guild.GuildService;

@Dependent
public class SkipCommandListener extends AbstractGuildCommandListener<SkipCommand.Data, SkipCommand> {

    private final GuildService guildService;

    public SkipCommandListener(
        final SkipCommand command,
        final GuildService guildService
    ) {
        super(command);
        this.guildService = guildService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final SkipCommand.Data data,
        final Guild guild
    ) {
        guildService.getTrackManager(guild).skip(data.songsToSkip().orElse(1));
        event.reply("Skipped " + data.songsToSkip().orElse(1) + " songs").queue();
    }
}
