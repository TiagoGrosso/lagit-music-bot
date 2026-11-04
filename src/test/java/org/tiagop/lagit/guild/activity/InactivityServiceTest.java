package org.tiagop.lagit.guild.activity;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.scheduler.ScheduledExecution;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.embeds.LeaveChannelForInactivityEmbed;
import org.tiagop.lagit.guild.config.GuildConfig;
import org.tiagop.lagit.guild.config.GuildConfigService;

class InactivityServiceTest {

    private final GuildService guildService = mock();
    private final GuildConfigService guildConfigService = mock();
    private final InactivityService inactivityService = new InactivityService(guildService, guildConfigService);

    @Test
    void itLeavesChannelForInactiveGuilds() {
        // given
        final var firstGuild = secure().nextAlphanumeric(20);
        final var secondGuild = secure().nextAlphanumeric(20);
        final var thirdGuild = secure().nextAlphanumeric(20);
        final var now = Instant.now();

        givenGuildInactivityLimit(firstGuild, Duration.ofMinutes(5));
        givenGuildInactivityLimit(secondGuild, Duration.ofMinutes(5));
        givenGuildInactivityLimit(thirdGuild, Duration.ofMinutes(5));

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

    @Test
    void itUsesEachGuildsInactivityTimeLimit() {
        // given
        final var shortLimitGuild = secure().nextAlphanumeric(20);
        final var longLimitGuild = secure().nextAlphanumeric(20);
        final var now = Instant.now();

        givenGuildInactivityLimit(shortLimitGuild, Duration.ofMinutes(1));
        givenGuildInactivityLimit(longLimitGuild, Duration.ofMinutes(15));

        inactivityService.registerGuildInactivity(shortLimitGuild, now.minus(Duration.ofMinutes(4)));
        inactivityService.registerGuildInactivity(longLimitGuild, now.minus(Duration.ofMinutes(4)));

        final var shortLimitGuildChannelManager = mock(ChannelManager.class);
        when(guildService.getChannelManager(shortLimitGuild)).thenReturn(shortLimitGuildChannelManager);

        final var scheduledExecution = mock(ScheduledExecution.class);
        when(scheduledExecution.getFireTime()).thenReturn(now);
        // when
        inactivityService.leaveChannelForInactiveGuilds(scheduledExecution);

        // then
        final var embedCaptor = ArgumentCaptor.forClass(LeaveChannelForInactivityEmbed.class);
        verify(shortLimitGuildChannelManager).leaveChannel();
        verify(shortLimitGuildChannelManager).sendMessageEmbed(embedCaptor.capture());
        assertThat(embedCaptor.getValue().inactivityDuration()).isEqualTo(Duration.ofMinutes(1));
        verify(guildService, never()).getChannelManager(longLimitGuild);
    }

    private void givenGuildInactivityLimit(final String guildId, final Duration limit) {
        final var guildConfig = new GuildConfig();
        guildConfig.inactivityTimeLimitSeconds = limit.toSeconds();
        when(guildConfigService.getOrCreate(guildId)).thenReturn(guildConfig);
    }
}
