package org.tiagop.lagit.listener.command.guild;

import jakarta.enterprise.context.Dependent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.infra.ExecutorUtils;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.audio.manager.ChannelService;
import org.tiagop.lagit.audio.manager.TrackLoadHandler;
import org.tiagop.lagit.command.PlayCommand;

import java.util.Optional;

@Dependent
public class PlayCommandListener extends AbstractGuildCommandListener<PlayCommand.Data, PlayCommand> {

    private final AudioService audioService;
    private final ExecutorUtils executorUtils;
    private final ChannelService channelService;

    public PlayCommandListener(
            @NotNull final PlayCommand command,
            @NotNull final AudioService audioService,
            @NotNull final ExecutorUtils executorUtils,
            @NotNull final ChannelService channelService
    ) {
        super(command);
        this.audioService = audioService;
        this.executorUtils = executorUtils;
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
                channel -> loadTrackAndJoinChannel(guild, channel, event, data),
                () -> event.reply("You must be in a voice channel to use this command").queue()
        );
    }

    private void loadTrackAndJoinChannel(
            final Guild guild,
            final VoiceChannel channel,
            final SlashCommandInteractionEvent event,
            PlayCommand.Data data) {
        final var query = data.query().orElse("");
        if (StringUtils.isBlank(query)) {
            final var playResult = audioService.tryPlay(guild);
            switch (playResult) {
                case EMPTY_QUEUE -> event.reply("Queue is empty").queue();
                case ALREADY_PLAYING -> event.reply("Already playing").queue();
                case STARTED_PLAYING -> {
                    channelService.joinChannel(channel);
                    event.reply("Playing from the queue").queue();
                }
                case RESUMED_PLAYING -> {
                    channelService.joinChannel(channel);
                    event.reply("Resumed playing").queue();
                }
            }
            return;
        }
        event.deferReply().queue();
        executorUtils.execute(() -> audioService.loadTrack(
                guild,
                query,
                trackManager -> new TrackLoadHandler(
                        trackManager,
                        event.getHook(),
                        () -> {
                            audioService.tryPlay(guild);
                            channelService.joinChannel(channel);
                        }))
        );
    }
}
