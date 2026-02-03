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

class PlayInfoManagerTest {

    private final ChannelManager channelManager = mock();
    private final PlayInfoManager playInfoManager = new PlayInfoManager(channelManager);

    private final NowPlayingEmbed embed = mock();
    private final Message message = mock();
    private final MessageEmbed messageEmbed = mock();
    private final MessageEditAction messageEditAction = mock();

    @Test
    void updates_play_info_by_sending_new_message_when_no_message_exists() {
        // when
        playInfoManager.updatePlayInfo(embed);

        // then
        then(channelManager).should().sendMessageEmbed(eq(embed), any());
    }

    @Test
    void updates_play_info_by_editing_existing_message() {
        // given
        given(embed.toMessageEmbed()).willReturn(messageEmbed);
        given(message.editMessageEmbeds(messageEmbed)).willReturn(messageEditAction);

        // Simulate the first call that sets the message
        doAnswer(invocation -> {
            final Consumer<Message> callback = invocation.getArgument(1);
            callback.accept(message);
            return null;
        }).when(channelManager).sendMessageEmbed(eq(embed), any());
        playInfoManager.updatePlayInfo(embed);
        given(message.editMessageEmbeds(messageEmbed)).willReturn(messageEditAction);

        // when
        playInfoManager.updatePlayInfo(embed);

        // then
        then(messageEditAction).should().queue();
    }

    @Test
    void does_nothing_when_no_message_exists_on_queue_ended() {
        // when
        playInfoManager.updateOnQueueEnded();

        // then
        then(message).shouldHaveNoInteractions();
    }

    @Test
    void edits_and_clears_message_on_queue_ended() {
        // given
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

}