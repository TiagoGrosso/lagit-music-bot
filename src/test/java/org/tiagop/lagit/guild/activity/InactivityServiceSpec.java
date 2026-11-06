package org.tiagop.lagit.guild.activity;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.scheduler.ScheduledExecution;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.tiagop.lagit.guild.GuildService;
import org.tiagop.lagit.guild.channel.ChannelManager;
import org.tiagop.lagit.guild.channel.embeds.LeaveChannelForInactivityEmbed;
import org.tiagop.lagit.guild.config.AnnounceMode;
import org.tiagop.lagit.guild.config.GuildConfigService;

@QuarkusTest
class InactivityServiceSpec {

    @InjectSpy
    GuildService guildService;

    @Inject
    InactivityService inactivityService;

    @Inject
    GuildConfigService guildConfigService;

    @Test
    void it_leaves_channels_after_the_default_inactivity_time_limit() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        final var activeGuildId = secure().nextAlphanumeric(20);
        final var now = Instant.now();

        inactivityService.registerGuildInactivity(guildId, now.minus(Duration.ofMinutes(10)));
        inactivityService.registerGuildInactivity(activeGuildId, now.minus(Duration.ofMinutes(4)));

        final var channelManager = mock(ChannelManager.class);
        doReturn(channelManager).when(guildService).getChannelManager(guildId);

        final var scheduledExecution = mock(ScheduledExecution.class);
        when(scheduledExecution.getFireTime()).thenReturn(now);
        // when
        inactivityService.leaveChannelForInactiveGuilds(scheduledExecution);

        // then
        verify(channelManager).leaveChannel();

        final var embedCaptor = ArgumentCaptor.forClass(LeaveChannelForInactivityEmbed.class);
        verify(channelManager).sendMessageEmbed(embedCaptor.capture());
        final var publishedMessage = embedCaptor.getValue().toMessageEmbed();
        assertThat(publishedMessage.getTitle()).isEqualTo("Leaving channel \uD83C\uDFC3");
        assertThat(publishedMessage.getDescription()).isEqualTo("Leaving voice channel after 5 minutes of inactivity");

        verify(guildService, never()).getChannelManager(activeGuildId);
        assertThat(guildConfigService.getOrCreate(guildId).inactivityTimeLimitSeconds)
            .isEqualTo(GuildConfigService.DEFAULT_INACTIVITY_TIME_LIMIT.toSeconds());
    }

    @Test
    void it_uses_each_guilds_configured_inactivity_time_limit() {
        // given
        final var shortLimitGuildId = secure().nextAlphanumeric(20);
        final var defaultLimitGuildId = secure().nextAlphanumeric(20);
        final var now = Instant.now();

        guildConfigService.setInactivityTimeLimit(shortLimitGuildId, Duration.ofMinutes(1).toSeconds());

        inactivityService.registerGuildInactivity(shortLimitGuildId, now.minus(Duration.ofMinutes(4)));
        inactivityService.registerGuildInactivity(defaultLimitGuildId, now.minus(Duration.ofMinutes(4)));

        final var channelManager = mock(ChannelManager.class);
        doReturn(channelManager).when(guildService).getChannelManager(shortLimitGuildId);

        final var scheduledExecution = mock(ScheduledExecution.class);
        when(scheduledExecution.getFireTime()).thenReturn(now);
        // when
        inactivityService.leaveChannelForInactiveGuilds(scheduledExecution);

        // then
        final var embedCaptor = ArgumentCaptor.forClass(LeaveChannelForInactivityEmbed.class);
        verify(channelManager).leaveChannel();
        verify(channelManager).sendMessageEmbed(embedCaptor.capture());
        assertThat(embedCaptor.getValue().inactivityDuration()).isEqualTo(Duration.ofMinutes(1));
        assertThat(embedCaptor.getValue().toMessageEmbed().getDescription())
            .isEqualTo("Leaving voice channel after 1 minute of inactivity");
        verify(guildService, never()).getChannelManager(defaultLimitGuildId);
    }

    @Test
    void it_does_not_announce_the_leave_in_silent_mode() {
        // given
        final var guildId = secure().nextAlphanumeric(20);
        final var now = Instant.now();
        guildConfigService.setAnnounceMode(guildId, AnnounceMode.SILENT);

        inactivityService.registerGuildInactivity(guildId, now.minus(Duration.ofMinutes(10)));

        final var channelManager = mock(ChannelManager.class);
        doReturn(channelManager).when(guildService).getChannelManager(guildId);

        final var scheduledExecution = mock(ScheduledExecution.class);
        when(scheduledExecution.getFireTime()).thenReturn(now);
        // when
        inactivityService.leaveChannelForInactiveGuilds(scheduledExecution);

        // then
        verify(channelManager).leaveChannel();
        verify(channelManager, never()).sendMessageEmbed(any(LeaveChannelForInactivityEmbed.class));
    }
}
