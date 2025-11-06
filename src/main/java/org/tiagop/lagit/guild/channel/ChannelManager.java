package org.tiagop.lagit.guild.channel;

import io.quarkus.logging.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.guild.channel.embeds.Embed;

public class ChannelManager {
    private final Guild guild;
    private final AudioManager guildAudioManager;

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
        final var channels = guild.getTextChannels();
        if (channels.isEmpty()) {
            Log.warn("No text channels found for guild %s".formatted(guild.getName()));
            return;
        }
        final var channel = channels.getFirst();
        channel.sendMessageEmbeds(embed.toMessageEmbed()).queue();
    }
}
