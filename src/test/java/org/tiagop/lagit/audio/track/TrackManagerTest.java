package org.tiagop.lagit.audio.track;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BaseAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrackManagerTest {
    private static final Random RANDOM = new Random();

    private AudioPlayer audioPlayer;
    private TrackManager trackManager;

    @BeforeEach
    void setup() {
        audioPlayer = spy(new DefaultAudioPlayerManager().createPlayer());
        trackManager = new TrackManager(audioPlayer, mock(), () -> {
        }, (ign) -> {
        });
        clearInvocations(audioPlayer);
    }

    @Test
    void itLoadsTracks() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
    }

    @Test
    void itClearsQueue() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // when
        trackManager.clear();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(trackManager.getQueue()).isEmpty();
    }

    @Test
    void itDoesNotPlayOnNoTrack() {
        // given / when
        trackManager.resumeOrPlayNext();

        // then
        verify(audioPlayer).isPaused();
        verify(audioPlayer).getPlayingTrack();
        verifyNoMoreInteractions(audioPlayer);
        assertThat(trackManager.getCurrentTrack()).isNull();
    }

    @Test
    void itPlaysQueuedSong() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
    }

    @Test
    void itSkipsOneSong() {
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
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(thirdRequest);
    }

    @Test
    void itPausesPlaying() {
        // given
        final var firstRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.pause();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(audioPlayer.isPaused()).isTrue();
    }

    @Test
    void itResumesPlaying() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.pause();
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
        assertThat(audioPlayer.isPaused()).isFalse();
    }

    @Test
    void itPlaysNextSong() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.stop();
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondRequest.track());
        assertThat(trackManager.getQueue()).isEmpty();
    }

    @Test
    void itDoesNothingIfAlreadyPlaying() {
        // given
        final var firstRequest = testRequest();
        final var secondRequest = testRequest();

        // when
        trackManager.queue(firstRequest);
        trackManager.queue(secondRequest);
        trackManager.resumeOrPlayNext();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstRequest.track());
        assertThat(trackManager.getQueue()).containsExactly(secondRequest);
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
        public void process(final LocalAudioTrackExecutor executor) throws Exception {

        }
    }
}