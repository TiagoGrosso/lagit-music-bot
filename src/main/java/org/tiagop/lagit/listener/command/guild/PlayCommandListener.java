package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.audio.manager.ChannelService;
import org.tiagop.lagit.audio.manager.TrackLoadHandler;
import org.tiagop.lagit.command.PlayCommand;

@Dependent
public class PlayCommandListener extends AbstractGuildCommandListener<PlayCommand.Data, PlayCommand> {

    private final AudioService audioService;
    private final ChannelService channelService;

    public PlayCommandListener(
        final PlayCommand command,
        final AudioService audioService,
        final ChannelService channelService
    ) {
        super(command);
        this.audioService = audioService;
        this.channelService = channelService;
    }

    @Override
    protected void handleCommand(
        final SlashCommandInteractionEvent event,
        final PlayCommand.Data data,
        final Guild guild
    ) {
        final var maybeChannel = Optional.ofNullable(event.getMember())
            .map(Member::getVoiceState)
            .map(GuildVoiceState::getChannel)
            .map(AudioChannelUnion::asVoiceChannel);

        maybeChannel.ifPresentOrElse(
            channel -> handleCommand(guild, channel, event, data),
            () -> event.reply("You must be in a voice channel to use this command").queue()
        );
    }

    private void handleCommand(
        final Guild guild,
        final VoiceChannel channel,
        final SlashCommandInteractionEvent event,
        PlayCommand.Data data
    ) {
        data.query().ifPresentOrElse(
            (query) -> loadTrackAndJoinChannel(guild, channel, event, query),
            () -> startPlaying(guild, channel, event)
        );
    }

    private void startPlaying(
        final Guild guild,
        final VoiceChannel channel,
        final SlashCommandInteractionEvent event
    ) {
        audioService.play(guild);
        channelService.joinChannel(channel);
        event.reply("Started playing").queue();
    }

    private void loadTrackAndJoinChannel(
        final Guild guild,
        final VoiceChannel channel,
        final SlashCommandInteractionEvent event,
        String query
    ) {
        event.deferReply().queue();
        audioService.load(
            guild,
            query,
            trackManager -> new TrackLoadHandler(
                trackManager,
                event.getHook(),
                () -> {
                    channelService.joinChannel(channel);
                })
        );
    }
}
