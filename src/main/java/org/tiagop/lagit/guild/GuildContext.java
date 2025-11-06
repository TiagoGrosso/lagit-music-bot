package org.tiagop.lagit.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.managers.AudioManager;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;

public record GuildContext(
    AudioManager audioManager,
    AudioPlayer audioPlayer,
    TrackManager trackManager,
    AudioPlayerSendHandler audioPlayerSendHandler
) {
}
