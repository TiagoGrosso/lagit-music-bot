package org.tiagop.lagit.guild.channel;

import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.guild.channel.embeds.Embed;

public class ChannelManager {
    private final Guild guild;
    private final AudioManager guildAudioManager;
    @Nullable
    private TextChannel lastTextChannelUsed;

    public ChannelManager(
        final Guild guild,
        final AudioManager guildAudioManager,
        final AudioPlayerSendHandler audioPlayerSendHandler
    ) {
        this.guild = guild;
        this.guildAudioManager = guildAudioManager;
        guildAudioManager.setSendingHandler(audioPlayerSendHandler);
    }

    public void leaveChannel() {
        guildAudioManager.closeAudioConnection();
    }

    public void joinChannel(final VoiceChannel voiceChannel) {
        if (!voiceChannel.getGuild().getId().equals(guild.getId())) {
            throw new IllegalStateException("Voice channel is not in the same guild as the channel manager");
        }
        guildAudioManager.openAudioConnection(voiceChannel);
    }

    public void sendMessageEmbed(final Embed embed) {
        final var channel = Optional.ofNullable(lastTextChannelUsed)
            .orElseGet(this::getFirstTextChannel);
        channel.sendMessageEmbeds(embed.toMessageEmbed()).queue();
    }

    public void setLastTextChannelUsed(@NonNull final TextChannel channel) {
        this.lastTextChannelUsed = channel;
    }

    private TextChannel getFirstTextChannel() {
        return guild.getTextChannels()
            .stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("No text channels found in guild %s".formatted(guild.getId())));
    }
}
