package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.audio.manager.ChannelService;
import org.tiagop.lagit.audio.manager.TrackLoadHandler;
import org.tiagop.lagit.command.PlayCommand;

import java.util.Optional;

@Dependent
public class PlayCommandListener extends AbstractGuildCommandListener<PlayCommand.Data, PlayCommand> {

    private final AudioService audioService;
    private final ChannelService channelService;

    public PlayCommandListener(
            @NotNull final PlayCommand command,
            @NotNull final AudioService audioService,
            @NotNull final ChannelService channelService
    ) {
        super(command);
        this.audioService = audioService;
        this.channelService = channelService;
    }

    @Override
    protected void handleCommand(
            @NotNull final SlashCommandInteractionEvent event,
            @NotNull final PlayCommand.Data data,
            @NotNull final Guild guild
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
            @NotNull final Guild guild,
            @NotNull final VoiceChannel channel,
            @NotNull final SlashCommandInteractionEvent event,
            @NotNull PlayCommand.Data data
    ) {
        data.query().ifPresentOrElse(
                (query) -> loadTrackAndJoinChannel(guild, channel, event, query),
                () -> startPlaying(guild, channel, event)
        );
    }

    private void startPlaying(
            @NotNull final Guild guild,
            @NotNull final VoiceChannel channel,
            @NotNull final SlashCommandInteractionEvent event
    ) {
        audioService.play(guild);
        channelService.joinChannel(channel);
        event.reply("Started playing").queue();
    }

    private void loadTrackAndJoinChannel(
            @NotNull final Guild guild,
            @NotNull final VoiceChannel channel,
            @NotNull final SlashCommandInteractionEvent event,
            @NotNull String query
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
