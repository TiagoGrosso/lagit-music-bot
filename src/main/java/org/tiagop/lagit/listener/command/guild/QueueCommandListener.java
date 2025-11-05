package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.option.QueueCommand;
import org.tiagop.lagit.util.Format;

@Dependent
public class QueueCommandListener extends AbstractGuildCommandListener<QueueCommand.Data, QueueCommand> {

    private final AudioService audioService;

    public QueueCommandListener(
        final QueueCommand command,
        final AudioService audioService
    ) {
        super(command);
        this.audioService = audioService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final QueueCommand.Data data,
        final Guild guild
    ) {
        final var queue = audioService.getQueue(guild);
        if (queue.isEmpty()) {
            event.reply("Queue is empty").queue();
            return;
        }
        final var queueInfo = queue.stream()
            .map(Format::trackInfoString)
            .map("1. %s"::formatted)
            .collect(Collectors.joining("\n"));
        event.reply(queueInfo).queue();
    }
}
