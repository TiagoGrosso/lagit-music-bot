package org.tiagop.lagit.audio;

import static org.assertj.core.api.Assertions.assertThat;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AudioPipelineSpec {

    private static final long TIMEOUT_MILLIS = 15_000;
    private static final long POLL_INTERVAL_MILLIS = 20;
    private static final int MIN_EXPECTED_FRAMES = 200;

    @Inject
    AudioPlayerManager playerManager;

    @Test
    void it_loads_and_decodes_a_track_into_opus_frames() throws IOException, InterruptedException {
        try (final var fixture = new FixtureAudioServer()) {
            // given
            final var track = (AudioTrack) playerManager.loadItemSync(fixture.url());
            assertThat(track).isNotNull();
            assertThat(track.getDuration()).isBetween(4_000L, 6_000L);

            final var player = playerManager.createPlayer();
            final var listener = new EndReasonListener();
            player.addListener(listener);
            final var sendHandler = new AudioPlayerSendHandler(player);

            // when
            player.playTrack(track);
            final var frames = drainFrames(sendHandler, listener);

            // then
            assertThat(listener.getEndReason()).contains(AudioTrackEndReason.FINISHED);
            assertThat(sendHandler.isOpus()).isTrue();
            assertThat(frames).hasSizeGreaterThanOrEqualTo(MIN_EXPECTED_FRAMES);
            assertThat(frames).anySatisfy(frame -> assertThat(containsSignal(frame))
                .as("at least one frame should not be silent")
                .isTrue());
        }
    }

    private static List<byte[]> drainFrames(
        final AudioPlayerSendHandler sendHandler,
        final EndReasonListener listener
    ) throws InterruptedException {
        final var frames = new ArrayList<byte[]>();
        final var deadline = System.currentTimeMillis() + TIMEOUT_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            if (sendHandler.canProvide()) {
                final var frame = Optional.ofNullable(sendHandler.provide20MsAudio());
                assertThat(frame).isPresent();
                frames.add(readFrame(frame.get()));
                continue;
            }
            if (listener.getEndReason().isPresent()) {
                return frames;
            }
            Thread.sleep(POLL_INTERVAL_MILLIS);
        }
        return frames;
    }

    private static byte[] readFrame(final ByteBuffer frame) {
        final var data = new byte[frame.remaining()];
        frame.get(data);
        return data;
    }

    private static boolean containsSignal(final byte[] frame) {
        for (final var sample : frame) {
            if (sample != 0) {
                return true;
            }
        }
        return false;
    }

    private static final class EndReasonListener extends AudioEventAdapter {
        private final AtomicReference<AudioTrackEndReason> endReason = new AtomicReference<>();

        Optional<AudioTrackEndReason> getEndReason() {
            return Optional.ofNullable(endReason.get());
        }

        @Override
        public void onTrackEnd(
            final AudioPlayer player,
            final AudioTrack track,
            final AudioTrackEndReason endReason
        ) {
            this.endReason.set(endReason);
        }
    }
}
