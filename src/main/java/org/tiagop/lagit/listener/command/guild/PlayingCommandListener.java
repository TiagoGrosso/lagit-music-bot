package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.PlayingCommand;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;

@ApplicationScoped
public class PlayingCommandListener extends AbstractGuildCommandListener<PlayingCommand.Data, PlayingCommand> {

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final PlayingCommand.Data data,
        final Guild guild
    ) {
        final var trackManager = guildService.getTrackManager(guild);
        final var currentTrack = trackManager.getCurrentTrack();
        if (currentTrack == null) {
            event.reply("Nothing is playing").queue();
            return;
        }
        event.reply("Displaying the current track").queue();
        final var queueSize = trackManager.getQueue().size();
        guildService.getPlayInfoManager(guild)
            .updatePlayInfo(new NowPlayingEmbed(currentTrack, queueSize), true);
    }
}
