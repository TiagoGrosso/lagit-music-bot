package org.tiagop.lagit.audio.track;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BaseAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tiagop.lagit.guild.channel.PlayInfoManager;
import org.tiagop.lagit.guild.channel.embeds.NowPlayingEmbed;

class TrackManagerTest {
    private static final Random RANDOM = new Random();

    private final AudioPlayer audioPlayer = spy(new DefaultAudioPlayerManager().createPlayer());
    private final PlayInfoManager playInfoManager = mock();
    private final Runnable clearInactivity = mock();
    private final Consumer<Instant> registerInactivity = mock();
    private final TrackManager trackManager = new TrackManager(
        audioPlayer,
        playInfoManager,
        clearInactivity,
        registerInactivity
    );

    @BeforeEach
    void setup() {
        clearInvocations(audioPlayer, playInfoManager);
    }

    @Test
    void it_loads_tracks() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
        then(audioPlayer).should().playTrack(firstRequest.track());

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void it_clears_queue() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // when
        trackManager.clear();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        assertThat(trackManager.getQueue()).isEmpty();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void it_does_not_play_on_no_track() {
        // given / when
        trackManager.resumeOrPlayNext();

        // then
        then(audioPlayer).should().isPaused();
        then(audioPlayer).should().getPlayingTrack();
        then(audioPlayer).shouldHaveNoMoreInteractions();
        assertThat(trackManager.getCurrentTrack()).isNull();
        then(playInfoManager).should().updateOnQueueEnded();
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void it_skips_one_song() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        final var thirdRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.queue(thirdRequest);

        // when
        trackManager.skip(1);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondRequest);
        assertThat(audioPlayer.getPlayingTrack()).isEqualTo(secondRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(thirdRequest);

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 2));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(secondRequest, 1));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void pause_playing_does_nothing_on_no_track() {
        // when
        trackManager.pause();

        // then
        then(audioPlayer).shouldHaveNoInteractions();
        then(playInfoManager).shouldHaveNoInteractions();
    }

    @Test
    void pauses_playing() {
        // given
        final var firstRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.pause();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        assertThat(audioPlayer.isPaused()).isTrue();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0, true));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void resumes_playing() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.pause();
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
        assertThat(audioPlayer.isPaused()).isFalse();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder, times(1)).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1, true));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void plays_next_song() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.stop();
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondRequest);
        assertThat(trackManager.getQueue()).isEmpty();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(secondRequest, 1, true)); // stop
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(secondRequest, 0)); // resume
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void does_nothing_if_already_playing() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void skips_multiple_songs() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        final var thirdRequest = testRequest();
        final var fourthRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.queue(thirdRequest);
        trackManager.queue(fourthRequest);

        // when
        trackManager.skip(3);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(fourthRequest);
        assertThat(trackManager.getQueue()).isEmpty();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 2));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 3));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(fourthRequest, 0));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void stops_when_queue_empty() {
        // given
        final var firstRequest = testRequest();
        trackManager.queue(firstRequest);

        // when
        trackManager.stop();

        // then
        assertThat(trackManager.getQueue()).isEmpty();

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updateOnQueueEnded();
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void stops_when_queue_not_empty() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // when
        trackManager.stop();

        // then
        assertThat(trackManager.getQueue()).hasSize(1);
        assertThat(trackManager.getQueue().getFirst()).isEqualTo(secondRequest);

        final var inOrder = inOrder(playInfoManager);
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 0));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(firstRequest, 1));
        then(playInfoManager).should(inOrder).updatePlayInfo(new NowPlayingEmbed(secondRequest, 1, true));
        then(playInfoManager).shouldHaveNoMoreInteractions();
    }

    @Test
    void handles_track_start_event() {
        // when
        trackManager.onTrackStart(audioPlayer, mock());

        // then
        then(clearInactivity).should().run();
    }

    @Test
    void handles_player_resume_event() {
        // when
        trackManager.onPlayerResume(audioPlayer);

        // then
        then(clearInactivity).should().run();
    }

    @Test
    void it_handles_player_pause_event() {
        // when
        trackManager.onPlayerPause(audioPlayer);

        // then
        then(registerInactivity).should().accept(any());
    }

    @Test
    void it_handles_track_end_event_may_start_next() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // when
        trackManager.onTrackEnd(
            audioPlayer,
            firstRequest.track(),
            AudioTrackEndReason.FINISHED
        );

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondRequest);
        then(registerInactivity).should(times(2)).accept(any());
    }

    @Test
    void handles_track_end_event_not_may_start_next() {
        // given
        final var firstRequest = testRequest();
        trackManager.queue(firstRequest);

        // when
        trackManager.onTrackEnd(
            audioPlayer,
            firstRequest.track(),
            AudioTrackEndReason.CLEANUP
        );

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest);
        then(registerInactivity).should().accept(any());
    }

    private static TrackRequest testRequest() {
        return new TrackRequest(new TestAudioTrack(), UUID.randomUUID().toString());
    }

    static class TestAudioTrack extends BaseAudioTrack {

        public TestAudioTrack() {
            super(new AudioTrackInfo(
                secure().nextAlphanumeric(20),
                secure().nextAlphanumeric(20),
                RANDOM.nextLong(100, 1000),
                secure().nextAlphanumeric(20),
                false,
                secure().nextAlphanumeric(20)
            ));
        }

        @Override
        public void process(final LocalAudioTrackExecutor executor) {

        }
    }
}