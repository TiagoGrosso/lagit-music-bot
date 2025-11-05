package org.tiagop.lagit.audio.manager;

import jakarta.enterprise.context.ApplicationScoped;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.tiagop.lagit.audio.manager.guild.GuildService;

@ApplicationScoped
public class ChannelService {

    private final GuildService guildService;

    public ChannelService(final GuildService guildService) {
        this.guildService = guildService;
    }

    public void joinChannel(final VoiceChannel voiceChannel) {
        final var guild = voiceChannel.getGuild();
        final var audioManager = guildService.getAudioManager(guild);
        audioManager.openAudioConnection(voiceChannel);
        final var audioSendHandler = guildService.getAudioPlayerSendHandler(guild);
        audioManager.setSendingHandler(audioSendHandler);
    }

    public void leaveChannel(final Guild guild) {
        final var audioManager = guildService.getAudioManager(guild);
        audioManager.closeAudioConnection();
    }
}
