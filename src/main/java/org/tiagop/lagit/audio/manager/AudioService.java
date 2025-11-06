package org.tiagop.lagit.audio.manager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.tiagop.lagit.guild.GuildService;

@ApplicationScoped

public class AudioService {

    private final GuildService guildService;

    public AudioService(final GuildService guildService) {
        this.guildService = guildService;
    }

    public void queue(
        final Guild guild,
        final AudioTrack track
    ) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.queue(track);
    }

    public void play(final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.resumeOrPlayNext();
    }

    public void pause(final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.pause();
    }

    public void clearQueue(final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.clear();
    }

    public void stop(final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.stop();
    }

    public void skip(final Guild guild, final int num) {
        final var trackManager = guildService.getTrackManager(guild);
        trackManager.skip(num);
    }

    public List<AudioTrack> getQueue(final Guild guild) {
        final var trackManager = guildService.getTrackManager(guild);
        return trackManager.getQueue();
    }
}
