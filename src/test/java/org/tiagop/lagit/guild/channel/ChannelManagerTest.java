package org.tiagop.lagit.guild.channel;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.apache.commons.lang3.function.Consumers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.guild.channel.embeds.Embed;

class ChannelManagerTest {

    private final Guild guild = mock();
    private final AudioManager audioManager = mock();
    private final ChannelManager channelManager = new ChannelManager(guild, audioManager, mock());

    private final TextChannel channel = mock();
    private final Embed embed = mock();
    private final MessageEmbed messageEmbed = mock();
    private final MessageCreateAction messageCreateAction = mock();

    @BeforeEach
    void setup() {
        given(embed.toMessageEmbed()).willReturn(messageEmbed);
        given(channel.sendMessageEmbeds(messageEmbed)).willReturn(messageCreateAction);
    }

    @Test
    void sends_message_embed_to_last_used_channel() {
        // given
        channelManager.setLastTextChannelUsed(channel);

        // when
        channelManager.sendMessageEmbed(embed);

        // then
        then(guild).shouldHaveNoMoreInteractions();
        then(messageCreateAction).should().queue(Consumers.nop());
    }

    @Test
    void sends_message_embed_with_callback() {
        // given
        channelManager.setLastTextChannelUsed(channel);
        final Consumer<Message> callback = mock();

        // when
        channelManager.sendMessageEmbed(embed, callback);

        // then
        then(guild).shouldHaveNoMoreInteractions();
        then(messageCreateAction).should().queue(callback);
    }

    @Test
    void sends_message_embed_to_first_channel_found_when_no_last_used_channel_exists() {
        // given
        given(guild.getTextChannels()).willReturn(List.of(channel, mock()));

        // when
        channelManager.sendMessageEmbed(embed);

        // then
        then(guild).should().getTextChannels();
        then(guild).shouldHaveNoMoreInteractions();
        then(messageCreateAction).should().queue(Consumers.nop());
    }

    @Test
    void throws_sending_message_embed_when_no_channels_found() {
        // given
        final var guildId = randomUUID().toString();
        given(guild.getTextChannels()).willReturn(List.of());
        given(guild.getId()).willReturn(guildId);

        // when
        final var exception = catchException(() -> channelManager.sendMessageEmbed(embed));

        // then
        assertThat(exception)
            .isExactlyInstanceOf(IllegalStateException.class)
            .hasMessage("No text channels found in guild %s".formatted(guildId));
        then(guild).should().getTextChannels();
        then(guild).should().getId();
        then(guild).shouldHaveNoMoreInteractions();
        then(messageCreateAction).shouldHaveNoInteractions();
    }

}