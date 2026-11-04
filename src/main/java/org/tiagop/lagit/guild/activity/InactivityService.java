package org.tiagop.lagit.guild.activity;

import static java.util.stream.Collectors.toSet;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.embeds.LeaveChannelForInactivityEmbed;
import org.tiagop.lagit.guild.config.GuildConfigService;

@ApplicationScoped
public class InactivityService {

    private final GuildService guildService;
    private final GuildConfigService guildConfigService;
    private final NavigableSet<Activity> guildInactivity;

    public InactivityService(final GuildService guildService, final GuildConfigService guildConfigService) {
        this.guildService = guildService;
        this.guildConfigService = guildConfigService;
        guildInactivity = new TreeSet<>();
    }

    public void registerGuildInactivity(final String guildId, final Instant activityTime) {
        final var inactivityTimeLimitSeconds = guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds;
        guildInactivity.add(new Activity(guildId, activityTime.plusSeconds(inactivityTimeLimitSeconds)));
    }

    public void clearGuildInactivity(final String guildId) {
        // TODO: this runs through the whole set, which is not great
        guildInactivity.removeIf(a -> a.guildId().equals(guildId));
    }

    private Set<String> getInactiveGuilds(final Instant now) {
        return guildInactivity.headSet(new Activity("", now))
            .stream()
            .map(Activity::guildId)
            .collect(toSet());
    }

    @Scheduled(every = "10s")
    public void leaveChannelForInactiveGuilds(final ScheduledExecution execution) {
        final var inactiveGuilds = getInactiveGuilds(execution.getFireTime());
        final var pairs = inactiveGuilds.stream()
            .map(guild -> Pair.of(guild, guildService.getChannelManager(guild)))
            .collect(toSet());

        if (pairs.stream().map(Pair::getRight).anyMatch(Objects::isNull)) {
            throw new IllegalStateException("Some channel managers are null. This should never happen.");
        }

        for (final var pair : pairs) {
            final var inactivityTimeLimit = Duration.ofSeconds(
                guildConfigService.getOrCreate(pair.getLeft()).inactivityTimeLimitSeconds
            );
            pair.getRight().leaveChannel();
            pair.getRight().sendMessageEmbed(new LeaveChannelForInactivityEmbed(inactivityTimeLimit));
            clearGuildInactivity(pair.getLeft());
        }
    }

    record Activity(
        String guildId,
        Instant clearAt
    ) implements Comparable<Activity> {

        @Override
        public int compareTo(final Activity other) {
            return clearAt.compareTo(other.clearAt);
        }
    }
}
