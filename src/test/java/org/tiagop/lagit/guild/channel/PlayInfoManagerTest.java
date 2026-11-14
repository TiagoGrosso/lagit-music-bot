package org.tiagop.lagit.guild.channel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.guild.channel.embeds.QueueEndedEmbed;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;
import org.tiagop.lagit.guild.config.AnnounceMode;
import org.tiagop.lagit.guild.config.GuildConfig;
import org.tiagop.lagit.guild.config.GuildConfigService;

class PlayInfoManagerTest {

    private static final String GUILD_ID = "123456789012345678";

    private final ChannelManager channelManager = mock();
    private final GuildConfigService guildConfigService = mock();
    private final PlayInfoManager playInfoManager = new PlayInfoManager(GUILD_ID, guildConfigService, channelManager);

    private final NowPlayingEmbed embed = mock();
    private final Message message = mock();
    private final MessageEmbed messageEmbed = mock();
    private final MessageEditAction messageEditAction = mock();

    @Test
    void updates_play_info_by_sending_new_message_when_no_message_exists() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);

        // when
        playInfoManager.updatePlayInfo(embed);

        // then
        then(channelManager).should().sendMessageEmbed(eq(embed), any());
    }

    @Test
    void updates_play_info_by_sending_new_message_when_force_new_message() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);
        given(embed.toMessageEmbed()).willReturn(messageEmbed);
        given(message.editMessageEmbeds(messageEmbed)).willReturn(messageEditAction);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());
        playInfoManager.updatePlayInfo(embed);

        // when
        playInfoManager.updatePlayInfo(embed, true);

        // then
        then(channelManager).should(times(2)).sendMessageEmbed(eq(embed), any());
        then(messageEditAction).shouldHaveNoInteractions();
    }

    @Test
    void updates_play_info_by_editing_existing_message() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);
        given(embed.toMessageEmbed()).willReturn(messageEmbed);
        given(message.editMessageEmbeds(messageEmbed)).willReturn(messageEditAction);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());
        playInfoManager.updatePlayInfo(embed);

        // when
        playInfoManager.updatePlayInfo(embed);

        // then
        then(messageEditAction).should().queue();
    }

    @Test
    void sends_a_new_message_per_play_info_in_new_message_mode() {
        // given
        givenAnnounceMode(AnnounceMode.NEW_MESSAGE);
        given(embed.toMessageEmbed()).willReturn(messageEmbed);
        given(message.editMessageEmbeds(messageEmbed)).willReturn(messageEditAction);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());

        // when
        playInfoManager.updatePlayInfo(embed);
        playInfoManager.updatePlayInfo(embed);

        // then
        then(channelManager).should(times(2)).sendMessageEmbed(eq(embed), any());
        then(messageEditAction).shouldHaveNoInteractions();
    }

    @Test
    void sends_no_play_info_in_silent_mode() {
        // given
        givenAnnounceMode(AnnounceMode.SILENT);

        // when
        playInfoManager.updatePlayInfo(embed);

        // then
        then(channelManager).shouldHaveNoInteractions();
    }

    @Test
    void does_nothing_when_no_message_exists_on_queue_ended() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);

        // when
        playInfoManager.updateOnQueueEnded();

        // then
        then(message).shouldHaveNoInteractions();
    }

    @Test
    void edits_and_clears_message_on_queue_ended() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);
        given(message.editMessageEmbeds(new QueueEndedEmbed().toMessageEmbed())).willReturn(messageEditAction);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());
        playInfoManager.updatePlayInfo(embed);

        // when
        playInfoManager.updateOnQueueEnded();
        playInfoManager.updatePlayInfo(embed);

        // then
        then(messageEditAction).should().queue();

        then(channelManager).should(times(2)).sendMessageEmbed(eq(embed), any());
    }

    @Test
    void edits_no_message_on_queue_ended_in_silent_mode() {
        // given
        givenAnnounceMode(AnnounceMode.EDIT_MESSAGE);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());
        playInfoManager.updatePlayInfo(embed);

        givenAnnounceMode(AnnounceMode.SILENT);

        // when
        playInfoManager.updateOnQueueEnded();

        // then
        then(message).shouldHaveNoInteractions();
        then(messageEditAction).shouldHaveNoInteractions();
    }

    private void givenAnnounceMode(final AnnounceMode announceMode) {
        final var guildConfig = new GuildConfig();
        guildConfig.announceMode = announceMode;
        given(guildConfigService.getOrCreate(GUILD_ID)).willReturn(guildConfig);
    }
}
