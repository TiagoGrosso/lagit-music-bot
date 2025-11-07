package org.tiagop.lagit.guild.activity;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.embeds.LeaveChannelForInactivityEmbed;

@ApplicationScoped
public class InactivityService {
    public static final Duration INACTIVITY_TIME_LIMIT = Duration.ofMinutes(5);

    private final GuildService guildService;
    private final NavigableSet<Activity> guildInactivity;

    public InactivityService(final GuildService guildService) {
        this.guildService = guildService;
        guildInactivity = new TreeSet<>();
    }

    public void registerGuildInactivity(final String guildId, final Instant activityTime) {
        guildInactivity.add(new Activity(guildId, activityTime));
    }

    public void clearGuildInactivity(final String guildId) {
        // TODO: this runs through the whole set, which is not great
        guildInactivity.removeIf(a -> a.guildId().equals(guildId));
    }

    private Set<String> getInactiveGuilds(final Instant now) {
        return guildInactivity.headSet(new Activity("", now.minus(INACTIVITY_TIME_LIMIT)))
            .stream()
            .map(Activity::guildId)
            .collect(Collectors.toSet());
    }

    @Scheduled(every = "1m")
    public void leaveChannelForInactiveGuilds(final ScheduledExecution execution) {
        final var inactiveGuilds = getInactiveGuilds(execution.getFireTime());
        final var channelManagers = inactiveGuilds.stream()
            .map(guildService::getChannelManager)
            .collect(Collectors.toSet());

        if (channelManagers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("Some channel managers are null. This should never happen.");
        }

        for (final var channelManager : channelManagers) {
            channelManager.leaveChannel();
            channelManager.sendMessageEmbed(new LeaveChannelForInactivityEmbed(INACTIVITY_TIME_LIMIT));
        }
    }
}
