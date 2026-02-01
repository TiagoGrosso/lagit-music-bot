package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.SkipCommand;

@ApplicationScoped
public class SkipCommandListener extends AbstractGuildCommandListener<SkipCommand.Data, SkipCommand> {

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
