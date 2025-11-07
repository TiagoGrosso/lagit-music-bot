package org.tiagop.lagit.guild.activity;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.scheduler.ScheduledExecution;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.embeds.LeaveChannelForInactivityEmbed;

class InactivityServiceTest {

    private final GuildService guildService = mock();
    private final InactivityService inactivityService = new InactivityService(guildService);

    @Test
    void itLeavesChannelForInactiveGuilds() {
        // given
        final var firstGuild = secure().nextAlphanumeric(20);
        final var secondGuild = secure().nextAlphanumeric(20);
        final var thirdGuild = secure().nextAlphanumeric(20);
        final var now = Instant.now();

        inactivityService.registerGuildInactivity(firstGuild, now.minus(Duration.ofMinutes(10)));
        inactivityService.registerGuildInactivity(secondGuild, now.minus(Duration.ofMinutes(4)));
        inactivityService.registerGuildInactivity(thirdGuild, now.minus(Duration.ofMinutes(6)));

        final var firstGuildChannelManager = mock(ChannelManager.class);
        final var thirdGuildChannelManager = mock(ChannelManager.class);
        when(guildService.getChannelManager(firstGuild)).thenReturn(firstGuildChannelManager);
        when(guildService.getChannelManager(thirdGuild)).thenReturn(thirdGuildChannelManager);

        final var scheduledExecution = mock(ScheduledExecution.class);
        when(scheduledExecution.getFireTime()).thenReturn(now);
        // when
        inactivityService.leaveChannelForInactiveGuilds(scheduledExecution);

        // then
        verify(firstGuildChannelManager).leaveChannel();
        verify(firstGuildChannelManager).sendMessageEmbed(any(LeaveChannelForInactivityEmbed.class));
        verify(thirdGuildChannelManager).leaveChannel();
        verify(thirdGuildChannelManager).sendMessageEmbed(any(LeaveChannelForInactivityEmbed.class));
        verify(guildService, never()).getChannelManager(secondGuild);
    }

}