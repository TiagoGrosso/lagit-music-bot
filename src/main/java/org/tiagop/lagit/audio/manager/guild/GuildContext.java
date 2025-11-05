package org.tiagop.lagit.audio.manager.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.track.TrackManager;

public record GuildContext(
    @NotNull AudioManager audioManager,
    @NotNull AudioPlayer audioPlayer,
    @NotNull TrackManager trackManager,
    @NotNull AudioPlayerSendHandler audioPlayerSendHandler
) {
}
