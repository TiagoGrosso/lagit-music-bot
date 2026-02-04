package org.tiagop.lagit.audio.track;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.tiagop.lagit.RandomUtils.nextLong;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BaseAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

public class TestAudioTrack extends BaseAudioTrack {

    public TestAudioTrack() {
        super(new AudioTrackInfo(
            secure().nextAlphanumeric(20),
            secure().nextAlphanumeric(20),
            nextLong(100, 1000),
            secure().nextAlphanumeric(20),
            false,
            secure().nextAlphanumeric(20)
        ));
    }

    @Override
    public void process(final LocalAudioTrackExecutor executor) {

    }
}
