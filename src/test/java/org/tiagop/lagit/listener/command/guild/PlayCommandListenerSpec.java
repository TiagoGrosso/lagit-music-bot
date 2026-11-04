package org.tiagop.lagit.listener.command.guild;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.tiagop.lagit.audio.AudioPlayerSendHandler;
import org.tiagop.lagit.audio.FixtureAudioServer;
import org.tiagop.lagit.command.PlayCommand;
import org.tiagop.lagit.guild.GuildService;

@QuarkusTest
class PlayCommandListenerSpec {

    private static final long AWAIT_MILLIS = 15_000;
    private static final long POLL_INTERVAL_MILLIS = 20;
    private static final String GUILD_ID = "987654321098765432";

    private final Guild guild = mock();
    private final AudioManager audioManager = mock();
    private final SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class, RETURNS_DEEP_STUBS);

    @Inject
    PlayCommandListener playCommandListener;

    @Inject
    GuildService guildService;

    @Test
    void it_plays_the_requested_track_in_the_requesters_voice_channel() throws IOException, InterruptedException {
        try (final var fixture = new FixtureAudioServer()) {
            // given
            final var voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
            final var deferReplyAction = event.deferReply();
            final var hook = event.getHook();
            final var nowPlayingChannel = event.getChannel().asTextChannel();

            given(event.getName()).willReturn(PlayCommand.NAME);
            given(event.getGuild()).willReturn(guild);
            given(guild.getId()).willReturn(GUILD_ID);
            given(guild.getAudioManager()).willReturn(audioManager);
            given(voiceChannel.getGuild()).willReturn(guild);
            given(event.getOption("query").getAsString()).willReturn(fixture.url());
            given(event.getUser().getAsMention()).willReturn("<@%s>".formatted(GUILD_ID));

            // when
            playCommandListener.onEvent(event);

            // then
            then(audioManager).should(timeout(AWAIT_MILLIS)).openAudioConnection(voiceChannel);

            final var sendHandlerCaptor = ArgumentCaptor.forClass(AudioPlayerSendHandler.class);
            then(audioManager).should().setSendingHandler(sendHandlerCaptor.capture());
            final var sendHandler = sendHandlerCaptor.getValue();

            awaitFirstFrame(sendHandler);
            assertThat(sendHandler.isOpus()).isTrue();
            assertThat(sendHandler.provide20MsAudio()).isNotNull();

            final var currentTrack = Optional.ofNullable(guildService.getTrackManager(guild).getCurrentTrack());
            assertThat(currentTrack).isPresent();
            assertThat(currentTrack.get().track().getInfo().uri).isEqualTo(fixture.url());
            assertThat(currentTrack.get().requestedBy()).isEqualTo("<@%s>".formatted(GUILD_ID));

            final var ignored = then(hook).should().sendMessage(startsWith("Added '"));
            then(deferReplyAction).should().queue();
            final var ignoredEmbeds = then(nowPlayingChannel).should().sendMessageEmbeds(any(MessageEmbed.class));
        }
    }

    private static void awaitFirstFrame(final AudioPlayerSendHandler sendHandler) throws InterruptedException {
        final var deadline = System.currentTimeMillis() + AWAIT_MILLIS;
        while (System.currentTimeMillis() < deadline && !sendHandler.canProvide()) {
            Thread.sleep(POLL_INTERVAL_MILLIS);
        }
        assertThat(sendHandler.canProvide()).as("send handler should provide audio frames").isTrue();
    }
}
