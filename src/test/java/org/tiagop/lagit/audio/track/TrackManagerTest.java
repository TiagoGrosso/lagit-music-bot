package org.tiagop.lagit.audio.track;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
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
    public void setup() {
        audioPlayer = spy(new DefaultAudioPlayerManager().createPlayer());
        trackManager = new TrackManager(audioPlayer);
        clearInvocations(audioPlayer);
    }

    @Test
    public void itLoadsTracks() {
        // given
        final var firstTrack = new TestAudioTrack();
        final var secondTrack = new TestAudioTrack();

        // when
        trackManager.queue(firstTrack);
        trackManager.queue(secondTrack);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstTrack);
        assertThat(trackManager.getQueue()).containsExactly(secondTrack);
    }

    @Test
    public void itClearsQueue() {
        // given
        final var firstTrack = new TestAudioTrack();
        final var secondTrack = new TestAudioTrack();
        trackManager.queue(firstTrack);
        trackManager.queue(secondTrack);

        // when
        trackManager.clear();

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstTrack);
        assertThat(trackManager.getQueue()).isEmpty();
    }

    @Test
    public void itDoesNotPlayOnNoTrack() {
        // given / when
        trackManager.resumeOrPlayNext();

        // then
        verify(audioPlayer).isPaused();
        verifyNoMoreInteractions(audioPlayer);
    }

    @Test
    public void itPlaysQueuedSong() {
        // given
        final var firstTrack = new TestAudioTrack();
        final var secondTrack = new TestAudioTrack();

        // when
        trackManager.queue(firstTrack);
        trackManager.queue(secondTrack);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(firstTrack);
        assertThat(trackManager.getQueue()).containsExactly(secondTrack);
    }

    @Test
    public void itSkipsOneSong() {
        // given
        final var firstTrack = new TestAudioTrack();
        final var secondTrack = new TestAudioTrack();
        final var thirdTrack = new TestAudioTrack();
        trackManager.queue(firstTrack);
        trackManager.queue(secondTrack);
        trackManager.queue(thirdTrack);

        // when
        trackManager.skip(1);

        // then
        assertThat(trackManager.getCurrentTrack()).isEqualTo(secondTrack);
        assertThat(trackManager.getQueue()).containsExactly(thirdTrack);
    }

    static class TestAudioTrack extends BaseAudioTrack {

        public TestAudioTrack() {
            super(new AudioTrackInfo(
                "",
                "",
                RANDOM.nextLong(100, 1000),
                UUID.randomUUID().toString(),
                false,
                UUID.randomUUID().toString()
            ));
        }

        @Override
        public void process(final LocalAudioTrackExecutor executor) throws Exception {

        }
    }
}