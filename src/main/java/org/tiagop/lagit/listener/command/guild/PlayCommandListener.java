package org.tiagop.lagit.listener.command.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.enterprise.context.Dependent;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.tiagop.lagit.audio.manager.AudioService;
import org.tiagop.lagit.command.PlayCommand;
import org.tiagop.lagit.guild.channel.ChannelService;
import org.tiagop.lagit.util.Format;

@Dependent
public class PlayCommandListener extends AbstractGuildCommandListener<PlayCommand.Data, PlayCommand> {

    private final AudioService audioService;
    private final ChannelService channelService;
    private final AudioPlayerManager audioPlayerManager;

    public PlayCommandListener(
        final PlayCommand command,
        final AudioService audioService,
        final ChannelService channelService,
        final AudioPlayerManager audioPlayerManager
    ) {
        super(command);
        this.audioService = audioService;
        this.channelService = channelService;
        this.audioPlayerManager = audioPlayerManager;
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
        final PlayCommand.Data data
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
        final String query
    ) {
        event.deferReply().queue();
        final var loaded = audioPlayerManager.loadItemSync(query);
        switch (loaded) {
            case AudioTrack track -> {
                addSingleTrackToQueue(guild, track, channel, event);
            }
            case AudioPlaylist playlist -> {
                if (playlist.isSearchResult()) {
                    addSingleTrackToQueue(guild, playlist.getTracks().getFirst(), channel, event);
                    return;
                }
                final var tracks = playlist.getTracks();
                addMultipleTracksToQueue(guild, channel, event, tracks);
            }
            case null, default -> event.getHook().sendMessage("No matches found").queue();
        }
    }

    private void addMultipleTracksToQueue(
        final Guild guild,
        final VoiceChannel channel,
        final SlashCommandInteractionEvent event,
        final List<AudioTrack> tracks
    ) {
        for (final var track : tracks) {
            audioService.queue(guild, track);
        }
        channelService.joinChannel(channel);
        event.getHook()
            .sendMessage("Added '%s' to queue and %d others".formatted(
                Format.trackInfoString(tracks.getFirst()),
                tracks.size()))
            .queue();
    }

    private void addSingleTrackToQueue(
        final Guild guild,
        final AudioTrack track,
        final VoiceChannel channel,
        final SlashCommandInteractionEvent event
    ) {
        audioService.queue(guild, track);
        channelService.joinChannel(channel);
        event.getHook()
            .sendMessage("Added '%s' to queue".formatted(Format.trackInfoString(track)))
            .queue();
    }
}
