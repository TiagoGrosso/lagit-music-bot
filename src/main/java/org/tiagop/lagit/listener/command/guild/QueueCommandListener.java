package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.command.option.QueueCommand;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.embeds.QueueEmbed;

@Dependent
public class QueueCommandListener extends AbstractGuildCommandListener<QueueCommand.Data, QueueCommand> {

    private final GuildService guildService;

    public QueueCommandListener(
        final QueueCommand command,
        final GuildService guildService
    ) {
        super(command);
        this.guildService = guildService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final QueueCommand.Data data,
        final Guild guild
    ) {
        final var trackManager = guildService.getTrackManager(guild);
        final var queue = trackManager.getQueue();
        if (queue.isEmpty()) {
            event.reply("Queue is empty").queue();
            return;
        }
        final var current = trackManager.getCurrentTrack();
        event.replyEmbeds(new QueueEmbed(current, queue).toMessageEmbed()).queue();
    }
}
